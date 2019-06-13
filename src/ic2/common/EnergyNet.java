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
	static final boolean $assertionsDisabled = !EnergyNet.class.desiredAssertionStatus();
	private static Map worldToEnergyNetMap = new HashMap();
	private World world;
	private Map energySourceToEnergyPathMap = new HashMap();
	private Map entityLivingToShockEnergyMap = new HashMap();

	private EnergyNet(World world1) {
		this.world = world1;
	}

	public static EnergyNet getForWorld(World world1) {
		if (world1 == null) {
			System.out.println("[IC2] EnergyNet.getForWorld: world = null, bad things may happen..");
			return null;
		}
		else {
			if (!worldToEnergyNetMap.containsKey(world1)) {
				worldToEnergyNetMap.put(world1, new EnergyNet(world1));
			}

			return (EnergyNet) worldToEnergyNetMap.get(world1);
		}
	}

	public static void onTick(World world1) {
		Platform.profilerStartSection("Shocking");
		EnergyNet energynet = getForWorld(world1);
		Iterator iterator = energynet.entityLivingToShockEnergyMap.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry entry = (Entry) iterator.next();
			EntityLiving entityliving = (EntityLiving) entry.getKey();
			int i = ((Integer) entry.getValue() + 63) / 64;
			if (entityliving.isAlive()) {
				entityliving.damageEntity(IC2DamageSource.electricity, i);
			}
		}

		energynet.entityLivingToShockEnergyMap.clear();
		Platform.profilerEndSection();
	}

	public void addTileEntity(TileEntity tileentity) {
		if (tileentity instanceof IEnergyTile && !((IEnergyTile) tileentity).isAddedToEnergyNet()) {
			if (tileentity instanceof IEnergyAcceptor) {
				List list = this.discover(tileentity, true, Integer.MAX_VALUE);
				Iterator iterator = list.iterator();

				while (iterator.hasNext()) {
					EnergyPath energypath = (EnergyPath) iterator.next();
					IEnergySource ienergysource = (IEnergySource) energypath.target;
					if (this.energySourceToEnergyPathMap.containsKey(ienergysource) && (double) ienergysource.getMaxEnergyOutput() > energypath.loss) {
						this.energySourceToEnergyPathMap.remove(ienergysource);
					}
				}
			}

			if (!(tileentity instanceof IEnergySource)) {
			}

		}
		else {
			if (tileentity instanceof IEnergyTile) {
				((IEnergyTile) tileentity).isAddedToEnergyNet();
			}
			else {
				boolean var10000 = false;
			}

		}
	}

	public void removeTileEntity(TileEntity tileentity) {
		if (tileentity instanceof IEnergyTile && ((IEnergyTile) tileentity).isAddedToEnergyNet()) {
			if (tileentity instanceof IEnergyAcceptor) {
				List list = this.discover(tileentity, true, Integer.MAX_VALUE);
				Iterator iterator = list.iterator();

				label55:
				while (true) {
					while (true) {
						EnergyPath energypath;
						IEnergySource ienergysource;
						do {
							do {
								if (!iterator.hasNext()) {
									break label55;
								}

								energypath = (EnergyPath) iterator.next();
								ienergysource = (IEnergySource) energypath.target;
							} while (!this.energySourceToEnergyPathMap.containsKey(ienergysource));
						} while ((double) ienergysource.getMaxEnergyOutput() <= energypath.loss);

						if (tileentity instanceof IEnergyConductor) {
							this.energySourceToEnergyPathMap.remove(ienergysource);
						}
						else {
							Iterator iterator1 = ((List) this.energySourceToEnergyPathMap.get(ienergysource)).iterator();

							while (iterator1.hasNext()) {
								if (((EnergyPath) iterator1.next()).target == tileentity) {
									iterator1.remove();
									break;
								}
							}
						}
					}
				}
			}

			if (tileentity instanceof IEnergySource) {
				this.energySourceToEnergyPathMap.remove(tileentity);
			}

		}
		else {
			boolean var10000;
			if (tileentity instanceof IEnergyTile) {
				var10000 = !((IEnergyTile) tileentity).isAddedToEnergyNet();
			}
			else {
				var10000 = true;
			}

		}
	}

	public int emitEnergyFrom(IEnergySource ienergysource, int i) {
		if (!ienergysource.isAddedToEnergyNet()) {
			return i;
		}
		else {
			if (!this.energySourceToEnergyPathMap.containsKey(ienergysource)) {
				this.energySourceToEnergyPathMap.put(ienergysource, this.discover((TileEntity) ienergysource, false, ienergysource.getMaxEnergyOutput()));
			}

			int j = 0;
			Vector vector = new Vector();
			double d = 0.0D;
			Iterator iterator = ((List) this.energySourceToEnergyPathMap.get(ienergysource)).iterator();

			while (iterator.hasNext()) {
				EnergyPath energypath = (EnergyPath) iterator.next();
				if (!$assertionsDisabled && !(energypath.target instanceof IEnergySink)) {
					throw new AssertionError();
				}

				IEnergySink ienergysink = (IEnergySink) energypath.target;
				if (ienergysink.demandsEnergy()) {
					d += 1.0D / energypath.loss;
					vector.add(energypath);
					if (vector.size() >= i) {
						break;
					}
				}
			}

			Iterator iterator1 = vector.iterator();

			while (true) {
				int j1;
				EnergyPath energypath1;
				do {
					IEnergySink ienergysink1;
					int k;
					int l;
					do {
						if (!iterator1.hasNext()) {
							return i - j;
						}

						energypath1 = (EnergyPath) iterator1.next();
						ienergysink1 = (IEnergySink) energypath1.target;
						k = (int) Math.floor((double) Math.round((double) i / d / energypath1.loss * 100000.0D) / 100000.0D);
						l = (int) Math.floor(energypath1.loss);
					} while (k <= l);

					int i1 = ienergysink1.injectEnergy(energypath1.targetDirection, k - l);
					j += k - i1;
					j1 = k - l - i1;
					energypath1.totalEnergyConducted += (long) j1;
					if (j1 > energypath1.minInsulationEnergyAbsorption) {
						List list = this.world.a(EntityLiving.class, AxisAlignedBB.a((double) (energypath1.minX - 1), (double) (energypath1.minY - 1), (double) (energypath1.minZ - 1), (double) (energypath1.maxX + 2), (double) (energypath1.maxY + 2), (double) (energypath1.maxZ + 2)));
						Iterator iterator4 = list.iterator();

						while (iterator4.hasNext()) {
							EntityLiving entityliving = (EntityLiving) iterator4.next();
							int k1 = 0;
							Iterator iterator5 = energypath1.conductors.iterator();

							while (iterator5.hasNext()) {
								IEnergyConductor ienergyconductor2 = (IEnergyConductor) iterator5.next();
								TileEntity tileentity = (TileEntity) ienergyconductor2;
								if (entityliving.boundingBox.a(AxisAlignedBB.a((double) (tileentity.x - 1), (double) (tileentity.y - 1), (double) (tileentity.z - 1), (double) (tileentity.x + 2), (double) (tileentity.y + 2), (double) (tileentity.z + 2)))) {
									int l1 = j1 - ienergyconductor2.getInsulationEnergyAbsorption();
									if (l1 > k1) {
										k1 = l1;
									}

									if (ienergyconductor2.getInsulationEnergyAbsorption() == energypath1.minInsulationEnergyAbsorption) {
										break;
									}
								}
							}

							if (this.entityLivingToShockEnergyMap.containsKey(entityliving)) {
								this.entityLivingToShockEnergyMap.put(entityliving, (Integer) this.entityLivingToShockEnergyMap.get(entityliving) + k1);
							}
							else {
								this.entityLivingToShockEnergyMap.put(entityliving, k1);
							}
						}

						if (j1 >= energypath1.minInsulationBreakdownEnergy) {
							iterator4 = energypath1.conductors.iterator();

							while (iterator4.hasNext()) {
								IEnergyConductor ienergyconductor1 = (IEnergyConductor) iterator4.next();
								if (j1 >= ienergyconductor1.getInsulationBreakdownEnergy()) {
									ienergyconductor1.removeInsulation();
									if (ienergyconductor1.getInsulationEnergyAbsorption() < energypath1.minInsulationEnergyAbsorption) {
										energypath1.minInsulationEnergyAbsorption = ienergyconductor1.getInsulationEnergyAbsorption();
									}
								}
							}
						}
					}
				} while (j1 < energypath1.minConductorBreakdownEnergy);

				Iterator iterator2 = energypath1.conductors.iterator();

				while (iterator2.hasNext()) {
					IEnergyConductor ienergyconductor = (IEnergyConductor) iterator2.next();
					if (j1 >= ienergyconductor.getConductorBreakdownEnergy()) {
						ienergyconductor.removeConductor();
					}
				}
			}
		}
	}

	public long getTotalEnergyConducted(TileEntity tileentity) {
		long l = 0L;
		if (tileentity instanceof IEnergyConductor || tileentity instanceof IEnergySink) {
			List list = this.discover(tileentity, true, Integer.MAX_VALUE);
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
					} while (!this.energySourceToEnergyPathMap.containsKey(ienergysource));
				} while ((double) ienergysource.getMaxEnergyOutput() <= energypath1.loss);

				Iterator iterator2 = ((List) this.energySourceToEnergyPathMap.get(ienergysource)).iterator();

				while (true) {
					EnergyPath energypath2;
					do {
						if (!iterator2.hasNext()) {
							continue label56;
						}

						energypath2 = (EnergyPath) iterator2.next();
					} while ((!(tileentity instanceof IEnergySink) || energypath2.target != tileentity) && (!(tileentity instanceof IEnergyConductor) || !energypath2.conductors.contains(tileentity)));

					l += energypath2.totalEnergyConducted;
				}
			}
		}

		EnergyPath energypath;
		if (tileentity instanceof IEnergySource && this.energySourceToEnergyPathMap.containsKey(tileentity)) {
			for (Iterator iterator = ((List) this.energySourceToEnergyPathMap.get(tileentity)).iterator(); iterator.hasNext(); l += energypath.totalEnergyConducted) {
				energypath = (EnergyPath) iterator.next();
			}
		}

		return l;
	}

	private List discover(TileEntity tileentity, boolean flag, int i) {
		HashMap hashmap = new HashMap();
		LinkedList linkedlist = new LinkedList();
		linkedlist.add(tileentity);

		label144:
		while (true) {
			TileEntity tileentity1;
			do {
				if (linkedlist.isEmpty()) {
					LinkedList linkedlist1 = new LinkedList();
					Iterator iterator = hashmap.entrySet().iterator();

					while (true) {
						label112:
						while (true) {
							Entry entry;
							TileEntity tileentity2;
							do {
								if (!iterator.hasNext()) {
									return linkedlist1;
								}

								entry = (Entry) iterator.next();
								tileentity2 = (TileEntity) entry.getKey();
							} while ((flag || !(tileentity2 instanceof IEnergySink)) && (!flag || !(tileentity2 instanceof IEnergySource)));

							EnergyBlockLink energyblocklink = (EnergyBlockLink) entry.getValue();
							EnergyPath energypath = new EnergyPath();
							if (energyblocklink.loss > 0.1D) {
								energypath.loss = energyblocklink.loss;
							}
							else {
								energypath.loss = 0.1D;
							}

							energypath.target = tileentity2;
							energypath.targetDirection = energyblocklink.direction;
							if (!flag && tileentity instanceof IEnergySource) {
								while (true) {
									tileentity2 = energyblocklink.direction.applyToTileEntity(tileentity2);
									if (tileentity2 == tileentity) {
										break;
									}

									if (!(tileentity2 instanceof IEnergyConductor)) {
										if (tileentity2 != null) {
											System.out.println("EnergyNet: EnergyBlockLink corrupted (" + energypath.target + " [" + energypath.target.x + " " + energypath.target.y + " " + energypath.target.z + "] -> " + tileentity2 + " [" + tileentity2.x + " " + tileentity2.y + " " + tileentity2.z + "] -> " + tileentity + " [" + tileentity.x + " " + tileentity.y + " " + tileentity.z + "])");
										}
										continue label112;
									}

									IEnergyConductor ienergyconductor = (IEnergyConductor) tileentity2;
									if (tileentity2.x < energypath.minX) {
										energypath.minX = tileentity2.x;
									}

									if (tileentity2.y < energypath.minY) {
										energypath.minY = tileentity2.y;
									}

									if (tileentity2.z < energypath.minZ) {
										energypath.minZ = tileentity2.z;
									}

									if (tileentity2.x > energypath.maxX) {
										energypath.maxX = tileentity2.x;
									}

									if (tileentity2.y > energypath.maxY) {
										energypath.maxY = tileentity2.y;
									}

									if (tileentity2.z > energypath.maxZ) {
										energypath.maxZ = tileentity2.z;
									}

									energypath.conductors.add(ienergyconductor);
									if (ienergyconductor.getInsulationEnergyAbsorption() < energypath.minInsulationEnergyAbsorption) {
										energypath.minInsulationEnergyAbsorption = ienergyconductor.getInsulationEnergyAbsorption();
									}

									if (ienergyconductor.getInsulationBreakdownEnergy() < energypath.minInsulationBreakdownEnergy) {
										energypath.minInsulationBreakdownEnergy = ienergyconductor.getInsulationBreakdownEnergy();
									}

									if (ienergyconductor.getConductorBreakdownEnergy() < energypath.minConductorBreakdownEnergy) {
										energypath.minConductorBreakdownEnergy = ienergyconductor.getConductorBreakdownEnergy();
									}

									energyblocklink = (EnergyBlockLink) hashmap.get(tileentity2);
									if (energyblocklink == null) {
										Platform.displayError("An energy network pathfinding entry is corrupted.\nThis could happen due to incorrect Minecraft behavior or a bug.\n\n(Technical information: energyBlockLink, tile entities below)\nE: " + tileentity + " (" + tileentity.x + "," + tileentity.y + "," + tileentity.z + ")\n" + "C: " + tileentity2 + " (" + tileentity2.x + "," + tileentity2.y + "," + tileentity2.z + ")\n" + "R: " + energypath.target + " (" + energypath.target.x + "," + energypath.target.y + "," + energypath.target.z + ")");
									}
								}
							}

							linkedlist1.add(energypath);
						}
					}
				}

				tileentity1 = (TileEntity) linkedlist.remove();
			} while (tileentity1.l());

			double d = 0.0D;
			if (tileentity1 != tileentity) {
				d = ((EnergyBlockLink) hashmap.get(tileentity1)).loss;
			}

			List list = this.getValidReceivers(tileentity1, flag);
			Iterator iterator1 = list.iterator();

			while (true) {
				EnergyTarget energytarget;
				double d1;
				do {
					do {
						do {
							if (!iterator1.hasNext()) {
								continue label144;
							}

							energytarget = (EnergyTarget) iterator1.next();
						} while (energytarget.tileEntity == tileentity);

						d1 = 0.0D;
						if (!(energytarget.tileEntity instanceof IEnergyConductor)) {
							break;
						}

						d1 = ((IEnergyConductor) energytarget.tileEntity).getConductionLoss();
						if (d1 < 1.0E-4D) {
							d1 = 1.0E-4D;
						}
					} while (d + d1 >= (double) i);
				} while (hashmap.containsKey(energytarget.tileEntity) && ((EnergyBlockLink) hashmap.get(energytarget.tileEntity)).loss <= d + d1);

				hashmap.put(energytarget.tileEntity, new EnergyBlockLink(energytarget.direction, d + d1));
				if (energytarget.tileEntity instanceof IEnergyConductor) {
					linkedlist.remove(energytarget.tileEntity);
					linkedlist.add(energytarget.tileEntity);
				}
			}
		}
	}

	private List getValidReceivers(TileEntity tileentity, boolean flag) {
		LinkedList linkedlist = new LinkedList();
		Direction[] adirection = Direction.values();
		int i = adirection.length;

		for (int j = 0; j < i; ++j) {
			Direction direction = adirection[j];
			TileEntity tileentity1 = direction.applyToTileEntity(tileentity);
			if (tileentity1 instanceof IEnergyTile && ((IEnergyTile) tileentity1).isAddedToEnergyNet()) {
				Direction direction1 = direction.getInverse();
				if ((!flag && tileentity instanceof IEnergyEmitter && ((IEnergyEmitter) tileentity).emitsEnergyTo(tileentity1, direction) || flag && tileentity instanceof IEnergyAcceptor && ((IEnergyAcceptor) tileentity).acceptsEnergyFrom(tileentity1, direction)) && (!flag && tileentity1 instanceof IEnergyAcceptor && ((IEnergyAcceptor) tileentity1).acceptsEnergyFrom(tileentity, direction1) || flag && tileentity1 instanceof IEnergyEmitter && ((IEnergyEmitter) tileentity1).emitsEnergyTo(tileentity, direction1))) {
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
			this.direction = direction1;
			this.loss = d;
		}
	}

	static class EnergyPath {
		TileEntity target = null;
		Direction targetDirection;
		Set conductors = new HashSet();
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
	}

	static class EnergyTarget {
		TileEntity tileEntity;
		Direction direction;

		EnergyTarget(TileEntity tileentity, Direction direction1) {
			this.tileEntity = tileentity;
			this.direction = direction1;
		}
	}
}
