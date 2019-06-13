package ic2.common;

import ic2.platform.BlockLeavesBaseCommon;
import ic2.platform.ColorizerFoliageCommon;
import ic2.platform.ItemBlockCommon;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.Random;

public class BlockRubLeaves extends BlockLeavesBaseCommon {
	int[] adjacentTreeBlocks;

	public BlockRubLeaves(int i) {
		super(i, 52, Material.LEAVES, false);
		this.a(true);
		this.c(0.2F);
		this.f(1);
		this.a(g);
		this.a("leaves");
		this.s();
		ModLoader.registerBlock(this, ItemBlockCommon.class);
		Ic2Items.rubberLeaves = new ItemStack(this);
	}

	public void remove(World world, int i, int j, int k) {
		int l = 1;
		int i1 = l + 1;
		if (world.a(i - i1, j - i1, k - i1, i + i1, j + i1, k + i1)) {
			for (int j1 = -l; j1 <= l; ++j1) {
				for (int k1 = -l; k1 <= l; ++k1) {
					for (int l1 = -l; l1 <= l; ++l1) {
						int i2 = world.getTypeId(i + j1, j + k1, k + l1);
						if (i2 == this.id) {
							int j2 = world.getData(i + j1, j + k1, k + l1);
							world.setRawData(i + j1, j + k1, k + l1, j2 | 8);
						}
					}
				}
			}
		}

	}

	public void a(World world, int i, int j, int k, Random random) {
		if (Platform.isSimulating()) {
			int l = world.getData(i, j, k);
			if ((l & 8) != 0) {
				byte byte0 = 4;
				int i1 = byte0 + 1;
				byte byte1 = 32;
				int j1 = byte1 * byte1;
				int k1 = byte1 / 2;
				if (this.adjacentTreeBlocks == null) {
					this.adjacentTreeBlocks = new int[byte1 * byte1 * byte1];
				}

				int i2;
				if (world.a(i - i1, j - i1, k - i1, i + i1, j + i1, k + i1)) {
					i2 = -byte0;

					label112:
					while (true) {
						int l2;
						int j3;
						int l3;
						if (i2 > byte0) {
							i2 = 1;

							while (true) {
								if (i2 > 4) {
									break label112;
								}

								for (l2 = -byte0; l2 <= byte0; ++l2) {
									for (j3 = -byte0; j3 <= byte0; ++j3) {
										for (l3 = -byte0; l3 <= byte0; ++l3) {
											if (this.adjacentTreeBlocks[(l2 + k1) * j1 + (j3 + k1) * byte1 + l3 + k1] == i2 - 1) {
												if (this.adjacentTreeBlocks[(l2 + k1 - 1) * j1 + (j3 + k1) * byte1 + l3 + k1] == -2) {
													this.adjacentTreeBlocks[(l2 + k1 - 1) * j1 + (j3 + k1) * byte1 + l3 + k1] = i2;
												}

												if (this.adjacentTreeBlocks[(l2 + k1 + 1) * j1 + (j3 + k1) * byte1 + l3 + k1] == -2) {
													this.adjacentTreeBlocks[(l2 + k1 + 1) * j1 + (j3 + k1) * byte1 + l3 + k1] = i2;
												}

												if (this.adjacentTreeBlocks[(l2 + k1) * j1 + (j3 + k1 - 1) * byte1 + l3 + k1] == -2) {
													this.adjacentTreeBlocks[(l2 + k1) * j1 + (j3 + k1 - 1) * byte1 + l3 + k1] = i2;
												}

												if (this.adjacentTreeBlocks[(l2 + k1) * j1 + (j3 + k1 + 1) * byte1 + l3 + k1] == -2) {
													this.adjacentTreeBlocks[(l2 + k1) * j1 + (j3 + k1 + 1) * byte1 + l3 + k1] = i2;
												}

												if (this.adjacentTreeBlocks[(l2 + k1) * j1 + (j3 + k1) * byte1 + (l3 + k1 - 1)] == -2) {
													this.adjacentTreeBlocks[(l2 + k1) * j1 + (j3 + k1) * byte1 + (l3 + k1 - 1)] = i2;
												}

												if (this.adjacentTreeBlocks[(l2 + k1) * j1 + (j3 + k1) * byte1 + l3 + k1 + 1] == -2) {
													this.adjacentTreeBlocks[(l2 + k1) * j1 + (j3 + k1) * byte1 + l3 + k1 + 1] = i2;
												}
											}
										}
									}
								}

								++i2;
							}
						}

						for (l2 = -byte0; l2 <= byte0; ++l2) {
							for (j3 = -byte0; j3 <= byte0; ++j3) {
								l3 = world.getTypeId(i + i2, j + l2, k + j3);
								if (l3 == Ic2Items.rubberWood.id) {
									this.adjacentTreeBlocks[(i2 + k1) * j1 + (l2 + k1) * byte1 + j3 + k1] = 0;
								}
								else if (l3 == this.id) {
									this.adjacentTreeBlocks[(i2 + k1) * j1 + (l2 + k1) * byte1 + j3 + k1] = -2;
								}
								else {
									this.adjacentTreeBlocks[(i2 + k1) * j1 + (l2 + k1) * byte1 + j3 + k1] = -1;
								}
							}
						}

						++i2;
					}
				}

				i2 = this.adjacentTreeBlocks[k1 * j1 + k1 * byte1 + k1];
				if (i2 >= 0) {
					world.setRawData(i, j, k, l & -9);
				}
				else {
					this.removeLeaves(world, i, j, k);
				}
			}

		}
	}

	private void removeLeaves(World world, int i, int j, int k) {
		this.b(world, i, j, k, world.getData(i, j, k), 0);
		world.setTypeId(i, j, k, 0);
	}

	public void a(World world, EntityHuman entityhuman, int i, int j, int k, int l) {
		if (Platform.isSimulating() && entityhuman.U() != null && entityhuman.U().id == Item.SHEARS.id) {
			entityhuman.a(StatisticList.C[this.id], 1);
			this.a(world, i, j, k, new ItemStack(this.id, 1, l & 3));
		}
		else {
			super.a(world, entityhuman, i, j, k, l);
		}

	}

	protected int getDropData(int i) {
		return 0;
	}

	public boolean a() {
		return false;
	}

	public int a(int i, int j) {
		return this.textureId;
	}

	public int getRenderColor(int i) {
		return ColorizerFoliageCommon.getFoliageColorBirch();
	}

	public int colorMultiplier(IBlockAccess iblockaccess, int i, int j, int k) {
		return ColorizerFoliageCommon.getFoliageColorBirch();
	}

	public int a(Random random) {
		return random.nextInt(35) == 0 ? 1 : 0;
	}

	public int getDropType(int i, Random random, int j) {
		return Ic2Items.rubberSapling.id;
	}

	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		if (l == 0 && this.minY > 0.0D) {
			return true;
		}
		else if (l == 1 && this.maxY < 1.0D) {
			return true;
		}
		else if (l == 2 && this.minZ > 0.0D) {
			return true;
		}
		else if (l == 3 && this.maxZ < 1.0D) {
			return true;
		}
		else if (l == 4 && this.minX > 0.0D) {
			return true;
		}
		else if (l == 5 && this.maxX < 1.0D) {
			return true;
		}
		else {
			return !Platform.isBlockOpaqueCube(iblockaccess, i, j, k);
		}
	}
}
