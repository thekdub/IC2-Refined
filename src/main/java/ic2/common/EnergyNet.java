package ic2.common;

import ic2.api.*;
import ic2.platform.Platform;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;

import java.util.*;
import java.util.Map.Entry;

public final class EnergyNet {
  public static final double minConductionLoss = 1.0E-4D;
  private static final Map<World, EnergyNet> worldToEnergyNetMap = new HashMap<>();
  private final World world;
  private final HashMap<IEnergySource, List<EnergyPath>> energySourceToEnergyPathMap = new HashMap<>();
  private final HashMap<EntityLiving, Integer> entityLivingToShockEnergyMap = new HashMap<>();
  
  private EnergyNet(World world) {
    this.world = world;
  }
  
  public static EnergyNet getForWorld(World world) {
    if (world == null) {
      System.out.println("[IC2] EnergyNet.getForWorld: world = null, bad things may happen..");
      return null;
    }
    else {
      if (!worldToEnergyNetMap.containsKey(world)) {
        worldToEnergyNetMap.put(world, new EnergyNet(world));
      }
      return worldToEnergyNetMap.get(world);
    }
  }
  
  public static void onTick(World world) { //Shock entities
    Platform.profilerStartSection("Shocking");
    EnergyNet energyNet = getForWorld(world);
    for (EntityLiving entityLiving : energyNet.entityLivingToShockEnergyMap.keySet()) {
      int i = (energyNet.entityLivingToShockEnergyMap.get(entityLiving) + 63) / 64;
      if (entityLiving.isAlive()) {
        entityLiving.damageEntity(IC2DamageSource.electricity, i);
      }
    }
    energyNet.entityLivingToShockEnergyMap.clear();
    Platform.profilerEndSection();
  }
  
  public void addTileEntity(TileEntity tileentity) {
    if (tileentity instanceof IEnergyTile && !((IEnergyTile) tileentity).isAddedToEnergyNet()) {
      if (tileentity instanceof IEnergyAcceptor) {
        List<EnergyPath> list = discover(tileentity, true, Integer.MAX_VALUE);
        for (EnergyPath energyPath : list) {
          IEnergySource ienergysource = (IEnergySource) energyPath.target;
          if (energySourceToEnergyPathMap.containsKey(ienergysource) &&
              (double) ienergysource.getMaxEnergyOutput() > energyPath.loss) {
            energySourceToEnergyPathMap.remove(ienergysource);
          }
        }
      }
    }
    else {
      if (tileentity instanceof IEnergyTile) {
        ((IEnergyTile) tileentity).isAddedToEnergyNet();
      }
    }
  }
  
  public void removeTileEntity(TileEntity tileEntity) {
    if (tileEntity instanceof IEnergyTile && ((IEnergyTile) tileEntity).isAddedToEnergyNet()) {
      if (tileEntity instanceof IEnergyAcceptor) {
        for (EnergyPath energyPath : discover(tileEntity, true, Integer.MAX_VALUE)) {
          IEnergySource iEnergySource = (IEnergySource) energyPath.target;
          if (energySourceToEnergyPathMap.containsKey(iEnergySource) &&
              iEnergySource.getMaxEnergyOutput() > energyPath.loss) {
            energySourceToEnergyPathMap.remove(iEnergySource);
          }
        }
        List<EnergyPath> energyPathList = discover(tileEntity, true, Integer.MAX_VALUE);
        Iterator<EnergyPath> energyPathIterator = energyPathList.iterator();
        label55:
        while (true) {
          while (true) {
            EnergyPath energyPath;
            IEnergySource iEnergySource;
            do {
              do {
                if (!energyPathIterator.hasNext()) {
                  break label55;
                }
                energyPath = energyPathIterator.next();
                iEnergySource = (IEnergySource) energyPath.target;
              } while (!energySourceToEnergyPathMap.containsKey(iEnergySource));
            } while ((double) iEnergySource.getMaxEnergyOutput() <= energyPath.loss);
            if (tileEntity instanceof IEnergyConductor) {
              energySourceToEnergyPathMap.remove(iEnergySource);
            }
            else {
              Iterator<EnergyPath> iterator1 = energySourceToEnergyPathMap.get(iEnergySource).iterator();
              while (iterator1.hasNext()) {
                if ((iterator1.next()).target == tileEntity) {
                  iterator1.remove();
                  break;
                }
              }
            }
          }
        }
      }
    }
    else if (tileEntity instanceof IEnergySource) {
      energySourceToEnergyPathMap.remove(tileEntity);
    }
  }
  
  public int emitEnergyFrom(IEnergySource ienergysource, int i) {
    if (ienergysource == null || !ienergysource.isAddedToEnergyNet()) {
      return i;
    }
    else {
      if (!energySourceToEnergyPathMap.containsKey(ienergysource)) {
        energySourceToEnergyPathMap
            .put(ienergysource, discover((TileEntity) ienergysource, false, ienergysource.getMaxEnergyOutput()));
      }
      if (energySourceToEnergyPathMap.get(ienergysource).isEmpty()) { // Remove empty entries
        energySourceToEnergyPathMap.remove(ienergysource);
        return i;
      }
      else {
        energySourceToEnergyPathMap.get(ienergysource).removeIf(energyPath -> energyPath.conductors == null
            || energyPath.conductors.isEmpty());
      }
      int j = 0;
      Vector<EnergyPath> vector = new Vector<>();
      double d = 0.0D;
      for (EnergyPath energypath : energySourceToEnergyPathMap.get(ienergysource)) {
        if (!EnergyNet.class.desiredAssertionStatus() && !(energypath.target instanceof IEnergySink)) {
          throw new AssertionError();
        }
        IEnergySink ienergysink = (IEnergySink) energypath.target;
        if (ienergysink.demandsEnergy()) {
          d += 1.0D / energypath.loss;
          if (!vector.contains(energypath)) //Added to prevent duplicates.
          {
            vector.add(energypath);
          }
          if (vector.size() >= i) {
            break;
          }
        }
      }
      Iterator<EnergyPath> iterator1 = vector.iterator();
      while (true) {
        int conducted;
        EnergyPath energypath1;
        do {
          IEnergySink ienergysink1;
          int k;
          int l;
          do {
            if (!iterator1.hasNext()) {
              return i - j;
            }
            energypath1 = iterator1.next();
            ienergysink1 = (IEnergySink) energypath1.target;
            k = (int) Math.floor((double) Math.round((double) i / d / energypath1.loss * 100000.0D) / 100000.0D);
            l = (int) Math.floor(energypath1.loss);
          } while (k <= l);
          int i1 = ienergysink1.injectEnergy(energypath1.targetDirection, k - l);
          j += k - i1;
          conducted = k - l - i1;
          energypath1.totalEnergyConducted += conducted;
          // Added for the statistic tracking of energy for EU Reader
          if (conducted > 0) {
            energypath1.addPacket(conducted);
          }
          if (conducted > energypath1.minInsulationEnergyAbsorption) {
            List<EntityLiving> list = world.a(EntityLiving.class, AxisAlignedBB
                .a(energypath1.minX - 1, energypath1.minY - 1, energypath1.minZ - 1,
                    energypath1.maxX + 2, energypath1.maxY + 2, energypath1.maxZ + 2));
            for (EntityLiving entityLiving : list) {
              int k1 = 0;

              for (IEnergyConductor iEnergyConductor : energypath1.conductors) {
                TileEntity tileentity = (TileEntity) iEnergyConductor;
                if (entityLiving.boundingBox.a(AxisAlignedBB
                    .a(tileentity.x - 1, tileentity.y - 1, tileentity.z - 1,
                        tileentity.x + 2, tileentity.y + 2, tileentity.z + 2))) {
                  int l1 = conducted - iEnergyConductor.getInsulationEnergyAbsorption();
                  if (l1 > k1) {
                    k1 = l1;
                  }
                  if (iEnergyConductor.getInsulationEnergyAbsorption() == energypath1.minInsulationEnergyAbsorption) {
                    break;
                  }
                }
              }
              if (entityLivingToShockEnergyMap.containsKey(entityLiving)) {
                entityLivingToShockEnergyMap.put(entityLiving, entityLivingToShockEnergyMap.get(entityLiving) + k1);
              }
              else {
                entityLivingToShockEnergyMap.put(entityLiving, k1);
              }
            }
            if (conducted >= energypath1.minInsulationBreakdownEnergy) {
              for (IEnergyConductor iEnergyConductor : energypath1.conductors) {
                if (conducted >= iEnergyConductor.getInsulationBreakdownEnergy()) {
                  iEnergyConductor.removeInsulation();
                  if (iEnergyConductor.getInsulationEnergyAbsorption() < energypath1.minInsulationEnergyAbsorption) {
                    energypath1.minInsulationEnergyAbsorption = iEnergyConductor.getInsulationEnergyAbsorption();
                  }
                }
              }
            }
          }
        } while (conducted < energypath1.minConductorBreakdownEnergy);
        for (IEnergyConductor iEnergyConductor : energypath1.conductors) {
          if (conducted >= iEnergyConductor.getConductorBreakdownEnergy()) {
            iEnergyConductor.removeConductor();
          }
        }
      }
    }
  }
  
  public long getTotalEnergyConducted(TileEntity tileentity) {
    long l = 0L;
    if (tileentity instanceof IEnergyConductor || tileentity instanceof IEnergySink) {
      List list = discover(tileentity, true, Integer.MAX_VALUE);
      Iterator iterator1 = list.iterator();
  
      label56:
      while (true) {
        EnergyPath energypath1;
        IEnergySource ienergysource;
        do {
          do {
            if (!iterator1.hasNext()) {
              break label56;
            }
  
            energypath1 = (EnergyPath) iterator1.next();
            ienergysource = (IEnergySource) energypath1.target;
          } while (!energySourceToEnergyPathMap.containsKey(ienergysource));
        } while ((double) ienergysource.getMaxEnergyOutput() <= energypath1.loss);
  
        Iterator iterator2 = ((List) energySourceToEnergyPathMap.get(ienergysource)).iterator();
  
        while (true) {
          EnergyPath energypath2;
          do {
            if (!iterator2.hasNext()) {
              continue label56;
            }
      
            energypath2 = (EnergyPath) iterator2.next();
          } while ((!(tileentity instanceof IEnergySink) || energypath2.target != tileentity) &&
              (!(tileentity instanceof IEnergyConductor) || !energypath2.conductors.contains(tileentity)));
    
          l += energypath2.totalEnergyConducted;
        }
      }
    }
    
    EnergyPath energypath;
    if (tileentity instanceof IEnergySource && energySourceToEnergyPathMap.containsKey(tileentity)) {
      for (Iterator iterator = ((List) energySourceToEnergyPathMap.get(tileentity)).iterator(); iterator.hasNext();
            l += energypath.totalEnergyConducted) {
        energypath = (EnergyPath) iterator.next();
      }
    }
  
    return l;
  }

  /**
   * Sends up to {@code totalEnergy} EU from {@code source} to the reachable
   * sinks, with the following constraints:
   *
   * 1. At most {@code 512} sinks are considered (or fewer if the source
   *    has < 512EU).  This keeps path‑finding efficient.
   * 2. Every packet sent to a sink is capped at {@code maxPacketSize}
   *    (the transformer’s packet size).
   * 3. Shock logic, insulation breakdown and conductor breakdown are
   *    preserved exactly as in the original implementation.
   * 4. The method returns the amount of energy that actually reached
   *    the sinks (post‑loss).
   *
   * @param source          the energy source (e.g. a tile entity)
   * @param maxPacketSize   the maximum EU that can be sent to a single
   *                        sink in one packet
   * @param totalEnergy     the amount of energy the source is ready to
   *                        distribute this tick
   * @return the total EU that was delivered to sinks this tick
   */
  public int emitEnergyFrom(IEnergySource source, int maxPacketSize, int totalEnergy) {

    if (source == null || !source.isAddedToEnergyNet()) {
      return 0;
    }

    /* ---- 1.  Make sure we have a path list ---- */
    if (!energySourceToEnergyPathMap.containsKey(source)) {
      energySourceToEnergyPathMap.put(source,
          discover((TileEntity) source, false, source.getMaxEnergyOutput()));
    }
    List<EnergyPath> allPaths = energySourceToEnergyPathMap.get(source);
    if (allPaths.isEmpty()) {
      energySourceToEnergyPathMap.remove(source);
      return 0;
    }
    // remove paths with no conductors (they are dead)
    allPaths.removeIf(p -> p.conductors == null || p.conductors.isEmpty());

    /* ---- 2.  Build a sink vector (max 512 or fewer) ---- */
    int sinkLimit = Math.min(512, totalEnergy);   // 512‑sink cap
    Vector<EnergyPath> sinks = new Vector<>();
    double d = 0.0D;                               // 1/loss weighting
    for (EnergyPath p : allPaths) {
      if (!(p.target instanceof IEnergySink)) continue;
      IEnergySink sink = (IEnergySink) p.target;
      if (!sink.demandsEnergy()) continue;

      d += 1.0D / p.loss;
      if (!sinks.contains(p)) sinks.add(p);
      if (sinks.size() >= sinkLimit) break;     // reached the limit
    }
    if (sinks.isEmpty()) return 0;

    /* ---- 3.  Main loop – one wave per tick ---- */
    int remaining   = totalEnergy;          // energy still to send
    int deliveredSum = 0;                   // energy actually delivered

    while (!sinks.isEmpty() && remaining / sinks.size() >= 1) {

      int energyPortion = Math.min(remaining / sinks.size(), maxPacketSize);
      if (energyPortion < 1) {
        break;
      }

      boolean anyDeliveredThisWave = false;
      Iterator<EnergyPath> it = sinks.iterator();

      while (it.hasNext() && remaining >= energyPortion) {
        EnergyPath path = it.next();
        IEnergySink sink = (IEnergySink) path.target;

        int received = energyPortion - ((int) path.loss);

        if (received <= 0) {
          continue;
        }

        /* ---- 3d.  Reject energy that the sink cannot accept ---- */
        int rejected = sink.injectEnergy(path.targetDirection, received);
        int conducted = energyPortion - rejected;
        int delivered = received - rejected;   // EU actually accepted

        /* ---- 3e.  If the sink is now full, drop it from future waves ---- */
        if (!sink.demandsEnergy()) it.remove();

        /* ---- 3f.  Update counters only if something was accepted ---- */
        if (delivered > 0) {
          anyDeliveredThisWave = true;
          deliveredSum += delivered;
          remaining   -= conducted;

          path.totalEnergyConducted += delivered;
          path.addPacket(delivered);
        }

        /* ---- 3g.  Shock logic – exactly as original ---- */
				// energy that actually travelled
				if (conducted > path.minInsulationEnergyAbsorption) {
          List<EntityLiving> list = world.a(EntityLiving.class,
              AxisAlignedBB.a(path.minX - 1, path.minY - 1, path.minZ - 1,
                  path.maxX + 2, path.maxY + 2, path.maxZ + 2));
          for (EntityLiving entityLiving : list) {
            int maxDamage = 0;
            for (IEnergyConductor con : path.conductors) {
              TileEntity t = (TileEntity) con;
              if (entityLiving.boundingBox.a(
                  AxisAlignedBB.a(t.x - 1, t.y - 1, t.z - 1,
                      t.x + 2, t.y + 2, t.z + 2))) {
                int damage = conducted - con.getInsulationEnergyAbsorption();
                if (damage > maxDamage) maxDamage = damage;
                if (con.getInsulationEnergyAbsorption()
                    == path.minInsulationEnergyAbsorption) break;
              }
            }
            entityLivingToShockEnergyMap.merge(entityLiving, maxDamage, Integer::sum);
          }

          /* ---- 3h.  Insulation breakdown if enough energy was conducted ---- */
          if (conducted >= path.minInsulationBreakdownEnergy) {
            for (IEnergyConductor con : path.conductors) {
              if (conducted >= con.getInsulationBreakdownEnergy()) {
                con.removeInsulation();
                if (con.getInsulationEnergyAbsorption()
                    < path.minInsulationEnergyAbsorption) {
                  path.minInsulationEnergyAbsorption =
                      con.getInsulationEnergyAbsorption();
                }
              }
            }
          }
        }

        /* ---- 3i.  Conductors that break because of over‑current ---- */
        if (conducted >= path.minConductorBreakdownEnergy) {
          for (IEnergyConductor con : path.conductors) {
            if (conducted >= con.getConductorBreakdownEnergy()) {
              con.removeConductor();
            }
          }
        }
      }

      /* ---- 4.  Prevent infinite loop – no sink can accept ≥ 1 EU this wave ---- */
      if (!anyDeliveredThisWave) break;
    }

    /* ---- 5.  Return the energy that reached the sinks ---- */
    return deliveredSum;
  }


  private List<EnergyPath> discover(TileEntity tileEntity, boolean flag, int i) {
    //newDiscover(tileEntity, flag, i); // Todo: Remove this
    HashMap<TileEntity, EnergyBlockLink> tileEntityEnergyBlockLinkHashMap = new HashMap<>();
    LinkedList<TileEntity> tileEntityLinkedList = new LinkedList<>();
    tileEntityLinkedList.add(tileEntity);
    
    discover_1:
    while (true) {
      TileEntity tileEntity1;
      do {
        if (tileEntityLinkedList.isEmpty()) {
          LinkedList<EnergyPath> energyPaths = new LinkedList<>();
          Iterator<Entry<TileEntity, EnergyBlockLink>> tileEntityEnergyBlockLinkIterator =
                tileEntityEnergyBlockLinkHashMap.entrySet().iterator();
          
          while (true) {
            discover_2:
            while (true) {
              Entry<TileEntity, EnergyBlockLink> tileEntityEnergyBlockLinkEntry;
              TileEntity energyPathTarget;
              do {
                if (!tileEntityEnergyBlockLinkIterator.hasNext()) {
                  return energyPaths;
                }
                
                tileEntityEnergyBlockLinkEntry = tileEntityEnergyBlockLinkIterator.next();
                energyPathTarget = tileEntityEnergyBlockLinkEntry.getKey();
              } while ((flag || !(energyPathTarget instanceof IEnergySink)) &&
                    (!flag || !(energyPathTarget instanceof IEnergySource)));
              
              EnergyBlockLink energyBlockLink = tileEntityEnergyBlockLinkEntry.getValue();
              EnergyPath energyPath = new EnergyPath();
              energyPath.loss = Math.max(energyBlockLink.loss, 0.1D);
              
              energyPath.target = energyPathTarget;
              energyPath.targetDirection = energyBlockLink.direction;
              
              if (!flag && tileEntity instanceof IEnergySource) {
                while (true) {
                  assert energyBlockLink != null;
                  energyPathTarget = energyBlockLink.direction.applyToTileEntity(energyPathTarget);
                  if (energyPathTarget == tileEntity) {
                    break;
                  }
                  
                  if (!(energyPathTarget instanceof IEnergyConductor)) {
                    if (energyPathTarget != null) {
                      System.out.println("EnergyNet: EnergyBlockLink corrupted (" + energyPath.target + " [" +
                            energyPath.target.x + " " + energyPath.target.y + " " + energyPath.target.z + "] -> " +
                            energyPathTarget + " [" + energyPathTarget.x + " " + energyPathTarget.y + " " +
                            energyPathTarget.z + "] -> " + tileEntity + " [" + tileEntity.x + " " + tileEntity.y +
                            " " + tileEntity.z + "])");
                    }
                    continue discover_2;
                  }
                  
                  IEnergyConductor iEnergyConductor = (IEnergyConductor) energyPathTarget;
                  
                  energyPath.minX = Math.min(energyPath.minX, energyPathTarget.x);
                  energyPath.minY = Math.min(energyPath.minY, energyPathTarget.y);
                  energyPath.minZ = Math.min(energyPath.minZ, energyPathTarget.z);
                  energyPath.maxX = Math.max(energyPath.maxX, energyPathTarget.x);
                  energyPath.maxY = Math.max(energyPath.maxY, energyPathTarget.y);
                  energyPath.maxZ = Math.max(energyPath.maxZ, energyPathTarget.z);
                  
                  energyPath.conductors.add(iEnergyConductor);
                  
                  energyPath.minInsulationEnergyAbsorption = Math.min(iEnergyConductor.getInsulationEnergyAbsorption(),
                        energyPath.minInsulationEnergyAbsorption);
                  energyPath.minInsulationBreakdownEnergy = Math.min(iEnergyConductor.getInsulationBreakdownEnergy(),
                        energyPath.minInsulationBreakdownEnergy);
                  energyPath.minConductorBreakdownEnergy = Math.min(iEnergyConductor.getConductorBreakdownEnergy(),
                        energyPath.minConductorBreakdownEnergy);
                  
                  energyBlockLink = tileEntityEnergyBlockLinkHashMap.get(energyPathTarget);
                  if (energyBlockLink == null) {
                    Platform.displayError("An energy network pathfinding entry is corrupted.\nThis could happen " +
                          "due to incorrect Minecraft behavior or a bug.\n\n(Technical information: energyBlockLink, " +
                          "tile entities below)\nE: " + tileEntity + " (" + tileEntity.x + "," + tileEntity.y + "," +
                          tileEntity.z + ")\n" + "C: " + energyPathTarget + " (" + energyPathTarget.x + "," +
                          energyPathTarget.y + "," + energyPathTarget.z + ")\n" + "R: " + energyPath.target +
                          " (" + energyPath.target.x + "," + energyPath.target.y + "," + energyPath.target.z + ")");
                  }
                }
              }
              
              energyPaths.add(energyPath);
            }
          }
        }
        
        tileEntity1 = tileEntityLinkedList.remove();
      } while (tileEntity1.l());
      
      double energyBlockLinkLoss = 0.0D;
      if (tileEntity1 != tileEntity) {
        energyBlockLinkLoss = tileEntityEnergyBlockLinkHashMap.get(tileEntity1).loss;
      }
      
      List<EnergyNet.EnergyTarget> energyTargetList = getValidReceivers(tileEntity1, flag);
      Iterator<EnergyNet.EnergyTarget> energyTargetIterator = energyTargetList.iterator();
      
      while (true) {
        EnergyTarget energyTarget;
        double energyConductorConductionLoss;
        do { // ... while tileEntityEnergyBlockLinkHashMap contains energyTarget.tileEntity AND
          //            tileEntityEnergyBlockLinkHashMap.get(energyTarget.tileEntity).loss
          //                <= energyBlockLinkLoss + energyConductorConductionLoss
          
          do { // ... while energyBlockLinkLoss + energyConductorConductionLoss >= 1
            
            do { // ... while energyTarget.tileEntity == tileEntity (from method args)
              if (!energyTargetIterator.hasNext()) {
                continue discover_1;
              }
              energyTarget = energyTargetIterator.next();
            } while (energyTarget.tileEntity == tileEntity);
            
            energyConductorConductionLoss = 0.0D;
            if (!(energyTarget.tileEntity instanceof IEnergyConductor)) {
              break;
            }
            energyConductorConductionLoss = ((IEnergyConductor) energyTarget.tileEntity).getConductionLoss();
            energyConductorConductionLoss = Math.max(energyConductorConductionLoss, 1.0E-4D);
          } while (energyBlockLinkLoss + energyConductorConductionLoss >= i);
          
        } while (tileEntityEnergyBlockLinkHashMap.containsKey(energyTarget.tileEntity) &&
              tileEntityEnergyBlockLinkHashMap.get(energyTarget.tileEntity).loss
                    <= energyBlockLinkLoss + energyConductorConductionLoss);
        
        tileEntityEnergyBlockLinkHashMap.put(energyTarget.tileEntity,
              new EnergyBlockLink(energyTarget.direction, energyBlockLinkLoss + energyConductorConductionLoss));
        
        if (energyTarget.tileEntity instanceof IEnergyConductor) {
          tileEntityLinkedList.remove(energyTarget.tileEntity);
          tileEntityLinkedList.add(energyTarget.tileEntity);
        }
      }
    }
  }
  
  private LinkedList<EnergyTarget> getValidReceivers(TileEntity tileentity, boolean flag) {
    LinkedList<EnergyTarget> linkedlist = new LinkedList<>();
    Direction[] adirection = Direction.values();
    int i = adirection.length;
    
    for (int j = 0; j < i; ++j) {
      Direction direction = adirection[j];
      TileEntity tileentity1 = direction.applyToTileEntity(tileentity);
      if (tileentity1 instanceof IEnergyTile && ((IEnergyTile) tileentity1).isAddedToEnergyNet()) {
        Direction direction1 = direction.getInverse();
        if ((!flag && tileentity instanceof IEnergyEmitter &&
            ((IEnergyEmitter) tileentity).emitsEnergyTo(tileentity1, direction) ||
            flag && tileentity instanceof IEnergyAcceptor &&
                ((IEnergyAcceptor) tileentity).acceptsEnergyFrom(tileentity1, direction)) &&
            (!flag && tileentity1 instanceof IEnergyAcceptor &&
                ((IEnergyAcceptor) tileentity1).acceptsEnergyFrom(tileentity, direction1) ||
                flag && tileentity1 instanceof IEnergyEmitter &&
                    ((IEnergyEmitter) tileentity1).emitsEnergyTo(tileentity, direction1))) {
          linkedlist.add(new EnergyTarget(tileentity1, direction1));
        }
      }
    }
    
    return linkedlist;
  }
  
  static class EnergyBlockLink {
    Direction direction;
    double loss;
    
    EnergyBlockLink(Direction direction1, double d) {
      direction = direction1;
      loss = d;
    }
  }
  
  static class EnergyPath {
    TileEntity target = null;
    Direction targetDirection;
    Set<IEnergyConductor> conductors = new HashSet<>();
    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int minZ = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int maxY = Integer.MIN_VALUE;
    int maxZ = Integer.MIN_VALUE;
    double loss = 0.0D;
    int minInsulationEnergyAbsorption = Integer.MAX_VALUE;
    int minInsulationBreakdownEnergy = Integer.MAX_VALUE;
    int minConductorBreakdownEnergy = Integer.MAX_VALUE;
    long totalEnergyConducted = 0L;
    
    @Override
    public boolean equals(Object o) {
      if (o == this) {
        return true;
      }
      if (!(o instanceof EnergyPath)) {
        return false;
      }
      EnergyPath ep = (EnergyPath) o;
      return ep.target.equals(target) && ep.targetDirection.equals(targetDirection) && ep.minX == minX &&
          ep.minY == minY
          && ep.minZ == minZ && ep.maxX == maxX && ep.maxY == maxY && ep.maxZ == maxZ;
    }

    // src/main/java/ic2/common/EnergyNet.java  (inside the EnergyPath class)

    int packetCount = 0;           // number of packets that successfully reached a sink
    long totalPacketSize = 0L;     // cumulative energy of those packets
    int minPacketSize = Integer.MAX_VALUE; // smallest packet size seen
    int maxPacketSize = Integer.MIN_VALUE; // largest packet size seen

    // Called from `emitEnergyFrom` after a successful transmission
    void addPacket(int delivered) {
      if (delivered <= 0) return;
      packetCount++;
      totalPacketSize += delivered;
      minPacketSize = Math.min(minPacketSize, delivered);
      maxPacketSize = Math.max(maxPacketSize, delivered);
    }
  }
  
  static class EnergyTarget {
    TileEntity tileEntity;
    Direction direction;
    
    EnergyTarget(TileEntity tileentity, Direction direction1) {
      tileEntity = tileentity;
      direction = direction1;
    }
  }



  // -----------------------------------------------------------------------------
  //  NEW: packet‑statistics DTO
  // -----------------------------------------------------------------------------
  public static final class EnergyPacketStats {
    /** Total EU that has ever reached the target tile entity. */
    public final long totalEnergy;

    /** Total number of delivered packets. */
    public final long totalPackets;

    /** Sum of the sizes of all delivered packets (EU). */
    public final long totalPacketSize;

    /** Minimum delivered packet size seen (EU). */
    public final int minPacketSize;

    /** Maximum delivered packet size seen (EU). */
    public final int maxPacketSize;

    private EnergyPacketStats(long energy, long packets, long sum,
                              int min, int max) {
      this.totalEnergy = energy;
      this.totalPackets = packets;
      this.totalPacketSize = sum;
      this.minPacketSize = min;
      this.maxPacketSize = max;
    }

    /* NEW: Helper to tell whether the measured power is zero. */
    public boolean hasZeroPower() {
      return this.totalPackets > 0 && this.totalEnergy == 0L;
    }
  }

// -----------------------------------------------------------------------------
//  NEW: helper that aggregates packet statistics for a given tile entity
// -----------------------------------------------------------------------------
  /**
   * Walks the entire energy‑network and extracts the packet‑level statistics
   * that belong to {@code te}.  The method performs **no state changes**;
   * it only reads the cumulative packet data stored in each {@link EnergyPath}.
   *
   * @param te the tile entity to query
   * @return a populated {@link EnergyPacketStats} instance
   */
  public EnergyPacketStats getEnergyPacketStats(TileEntity te) {
    long energy      = 0L;
    long packets     = 0L;
    long sumSize     = 0L;
    int  minSize     = Integer.MAX_VALUE;
    int  maxSize     = Integer.MIN_VALUE;

    // Walk every source‑to‑paths list in the network
    for (List<EnergyPath> paths : energySourceToEnergyPathMap.values()) {
      for (EnergyPath p : paths) {
        // Does this path touch the queried tile entity?
        boolean hits = false;
        if (p.target == te) {
          hits = true;
        } else if (te instanceof IEnergyConductor && p.conductors.contains(te)) {
          hits = true;
        }
        if (!hits) continue;

        // Accumulate packet statistics from this path
        energy   += p.totalEnergyConducted;   // legacy value kept for compatibility
        packets  += p.packetCount;
        sumSize   += p.totalPacketSize;
        minSize   = Math.min(minSize, p.minPacketSize);
        maxSize   = Math.max(maxSize, p.maxPacketSize);
      }
    }

    // If no path touched the entity, reset min/max to zero
    if (packets == 0L) {
      minSize = 0;
      maxSize = 0;
    }

    // NEW: Reset min/max when *no* power is being delivered
    EnergyPacketStats result = new EnergyPacketStats(energy, packets, sumSize, minSize, maxSize);
    if (result.hasZeroPower()) {
      result = new EnergyPacketStats(energy, packets, sumSize, 0, 0);
    }

    return result;
  }

}
