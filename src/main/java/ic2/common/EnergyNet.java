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

  public int emitEnergyFrom(IEnergySource energySource, int energyAvailable) {
    if (Runtime.getRuntime().freeMemory() < 1073741824L)
      return energyAvailable;
    if (!energySource.isAddedToEnergyNet())
      return energyAvailable;
    if (!this.energySourceToEnergyPathMap.containsKey(energySource))
      this.energySourceToEnergyPathMap.put(energySource, discover((TileEntity)energySource, false, energySource.getMaxEnergyOutput()));
    int energySpent = 0;
    Vector<EnergyPath> energyPaths = new Vector<>();
    double energyLossRatio = 0.0D;
    for (EnergyPath energypath : this.energySourceToEnergyPathMap.get(energySource)) {
      assert energypath.target instanceof IEnergySink;
      IEnergySink ienergysink = (IEnergySink)energypath.target;
      if (ienergysink.demandsEnergy()) {
        energyLossRatio += 1.0D / energypath.loss;
        if (!energyPaths.contains(energypath))
          energyPaths.add(energypath);
        if (energyPaths.size() >= energyAvailable)
          break;
      }
    }
    Iterator<EnergyPath> energyPathIterator = energyPaths.iterator();
    while (true) {
      if (!energyPathIterator.hasNext())
        return energyAvailable - energySpent;
      EnergyPath energyPath = energyPathIterator.next();
      IEnergySink energySink = (IEnergySink)energyPath.target;
      int energyRemaining = (int) (Math.round(energyAvailable / energyLossRatio / energyPath.loss * 100000.0D) / 100000.0D);
      int energyLoss = (int) energyPath.loss;
      if (energyRemaining > energyLoss) {
        int energyInjected = energySink.injectEnergy(energyPath.targetDirection, energyRemaining - energyLoss);
        energySpent += energyRemaining - energyInjected;
        int conducted = energyRemaining - energyLoss - energyInjected;
        energyPath.totalEnergyConducted += conducted;
        if (conducted > 0) {
          energyPath.addPacket(conducted);
        }
        if (conducted > energyPath.minInsulationEnergyAbsorption) {
          List<EntityLiving> list = this.world.a(EntityLiving.class, AxisAlignedBB.a((energyPath.minX - 1), (energyPath.minY - 1), (energyPath.minZ - 1), (energyPath.maxX + 2), (energyPath.maxY + 2), (energyPath.maxZ + 2)));
          for (EntityLiving entityLiving : list) {
            int k1 = 0;
            for (IEnergyConductor iEnergyConductor : energyPath.conductors) {
              TileEntity tileentity = (TileEntity)iEnergyConductor;
              if (entityLiving.boundingBox.a(AxisAlignedBB.a((tileentity.x - 1), (tileentity.y - 1), (tileentity.z - 1), (tileentity.x + 2), (tileentity.y + 2), (tileentity.z + 2)))) {
                int l1 = conducted - iEnergyConductor.getInsulationEnergyAbsorption();
                if (l1 > k1)
                  k1 = l1;
                if (iEnergyConductor.getInsulationEnergyAbsorption() == energyPath.minInsulationEnergyAbsorption)
                  break;
              }
            }
            if (this.entityLivingToShockEnergyMap.containsKey(entityLiving)) {
              this.entityLivingToShockEnergyMap.put(entityLiving, Integer.valueOf(this.entityLivingToShockEnergyMap.get(entityLiving).intValue() + k1));
              continue;
            }
            this.entityLivingToShockEnergyMap.put(entityLiving, Integer.valueOf(k1));
          }
          if (conducted >= energyPath.minInsulationBreakdownEnergy)
            for (IEnergyConductor iEnergyConductor : energyPath.conductors) {
              if (conducted >= iEnergyConductor.getInsulationBreakdownEnergy()) {
                iEnergyConductor.removeInsulation();
                if (iEnergyConductor.getInsulationEnergyAbsorption() < energyPath.minInsulationEnergyAbsorption)
                  energyPath.minInsulationEnergyAbsorption = iEnergyConductor.getInsulationEnergyAbsorption();
              }
            }
        }
        if (conducted >= energyPath.minConductorBreakdownEnergy)
          for (IEnergyConductor iEnergyConductor : energyPath.conductors) {
            if (conducted >= iEnergyConductor.getConductorBreakdownEnergy())
              iEnergyConductor.removeConductor();
          }
      }
    }
  }

  /**
   * Deliver energy from {@code energySource} to the sinks it is connected to.
   * <p>
   * The routine repeatedly re‑computes the source amount that each active
   * sink should receive and then sends a packet to every sink that
   * respects the {@code packetSize} limit.  The loop terminates when
   * no more energy can be sent (either because all sinks are satisfied
   * or because the remaining amount is too small to give each sink at
   * least one EU).
   * </p>
   *
   * @param energySource     the source of the energy
   * @param packetSize       maximum EU that may be injected into a sink
   *                         in a single round (must be > 0)
   * @param energyAvailable amount of EU that can be spent
   * @return the amount of EU that could not be spent
   */
  public int emitAllEnergyFrom(IEnergySource energySource,
                               int packetSize,
                               int energyAvailable) {

    /* ---------- sanity checks ---------- */
    if (Runtime.getRuntime().freeMemory() < 1_073_741_824L) { // 1GB
      return energyAvailable;
    }
    if (!energySource.isAddedToEnergyNet()) {
      return energyAvailable;
    }
    if (packetSize <= 0) {
      throw new IllegalArgumentException("packetSize must be > 0");
    }
    if (energyAvailable < packetSize) {
      return energyAvailable;
    }

    /* ---------- cache / discover energy paths ---------- */
    @SuppressWarnings("unchecked")
    List<EnergyPath> allPaths = this.energySourceToEnergyPathMap
        .computeIfAbsent(energySource, src -> discover((TileEntity) src, false, src.getMaxEnergyOutput()));

    /* ---------- main distribution loop ---------- */
    int remainingEnergy = energyAvailable;

    while (true) {
      /* active paths that actually demand energy */
      List<EnergyPath> activePaths = new ArrayList<>();
      for (EnergyPath ep : allPaths) {
        if (((IEnergySink) ep.target).demandsEnergy()) {
          activePaths.add(ep);
        }
      }
      if (activePaths.isEmpty()) {
        break; // nothing left to deliver
      }

      /* if we cannot give at least 1 EU to each sink, stop */
      if ((double) remainingEnergy / activePaths.size() < 1.0) {
        break;
      }

      /* loss‑sum used to compute the proportional source amount */
      double lossSum = 0.0D;
      for (EnergyPath ep : activePaths) {
        lossSum += 1.0D / ep.loss;
      }

      /* ------- pre‑compute how much source energy to send to each path -------
       * The original code calculates the source amount for a path as
       *   sourceToSend = floor(round(remainingEnergy / lossSum / ep.loss * 100000) / 100000)
       * We compute that for every active path *before* we start sending.
       * The packet is capped to {@code packetSize} at this point.
       */
      List<Integer> sourceToSendList = new ArrayList<>(activePaths.size());
      for (EnergyPath ep : activePaths) {
        int rawSource = (int) (Math.round(remainingEnergy / lossSum / ep.loss * 100_000.0D) / 100_000.0D);
        int sourceToSend = Math.min(rawSource, packetSize);
        sourceToSendList.add(sourceToSend);
      }

      /* ------- send a packet to each active sink ------- */
      boolean anyInjectedThisRound = false;

      for (int i = 0; i < activePaths.size(); i++) {
        EnergyPath ep = activePaths.get(i);
        IEnergySink sink = (IEnergySink) ep.target;
        int sourceToSend = sourceToSendList.get(i);

        /* nothing to send on this path (e.g. all loss) */
        if (sourceToSend <= ep.loss) {
          continue;
        }

        anyInjectedThisRound = true;

        /* perform the actual injection */
        int injected = sink.injectEnergy(ep.targetDirection, (int) Math.ceil(sourceToSend - ep.loss));

        /* update path statistics */
        int conducted = (int) Math.ceil(sourceToSend - ep.loss - injected);
        ep.totalEnergyConducted += conducted;
        if (conducted > 0) {
          ep.addPacket(conducted);
        }

        /* ----- insulation absorption & shocking ----- */
        if (conducted > ep.minInsulationEnergyAbsorption) {
          AxisAlignedBB pathBB = AxisAlignedBB.a(ep.minX - 1, ep.minY - 1, ep.minZ - 1, ep.maxX + 2, ep.maxY + 2, ep.maxZ + 2);

          List<EntityLiving> entities = this.world.a(EntityLiving.class, pathBB);

          for (EntityLiving entity : entities) {
            int maxShock = 0;
            for (IEnergyConductor ec : ep.conductors) {
              TileEntity te = (TileEntity) ec;
              AxisAlignedBB conBB = AxisAlignedBB.a(
                  te.x - 1, te.y - 1, te.z - 1,
                  te.x + 2, te.y + 2, te.z + 2);

              if (entity.boundingBox.a(conBB)) {
                int shock = conducted - ec.getInsulationEnergyAbsorption();
                if (shock > maxShock) {
                  maxShock = shock;
                }
                if (ec.getInsulationEnergyAbsorption() == ep.minInsulationEnergyAbsorption) {
                  break; // first conductor that limits the shock
                }
              }
            }

            /* accumulate shock values for this entity */
            this.entityLivingToShockEnergyMap.merge(entity, maxShock, Integer::sum);
          }

          /* insulation breakdown */
          if (conducted >= ep.minInsulationBreakdownEnergy) {
            for (IEnergyConductor ec : ep.conductors) {
              if (conducted >= ec.getInsulationBreakdownEnergy()) {
                ec.removeInsulation();
                if (ec.getInsulationEnergyAbsorption() < ep.minInsulationEnergyAbsorption) {
                  ep.minInsulationEnergyAbsorption = ec.getInsulationEnergyAbsorption();
                }
              }
            }
          }
        }

        /* ----- conductor breakdown ----- */
        if (conducted >= ep.minConductorBreakdownEnergy) {
          for (IEnergyConductor ec : ep.conductors) {
            if (conducted >= ec.getConductorBreakdownEnergy()) {
              ec.removeConductor();
            }
          }
        }

        /* deduct the source energy that was actually sent this round */
        remainingEnergy -= sourceToSend;
      }

      /* if nothing was injected this round we are stuck – exit */
      if (!anyInjectedThisRound) {
        break;
      }

      /* stop if we have less than packetSize left – the next round will exit */
      if (remainingEnergy < packetSize) {
        break;
      }
    }

    /* return whatever energy could not be spent */
    return remainingEnergy;
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
