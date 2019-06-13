package ic2.common;

import ic2.api.FakePlayer;
import net.minecraft.server.BiomeBase;
import net.minecraft.server.BlockMushroom;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class ItemTFBPMushroom extends ItemTFBP {
	public ItemTFBPMushroom(int i, int j) {
		super(i, j);
	}

	public int getConsume() {
		return 8000;
	}

	public int getRange() {
		return 25;
	}

	public boolean terraform(World world, int i, int j, int k) {
		int l = TileEntityTerra.getFirstSolidBlockFrom(world, i, j, k + 20);
		if (l == -1) {
			return false;
		}
		else {
			Block block = world.getWorld().getBlockAt(i, l, j);
			BlockBreakEvent event = new BlockBreakEvent(block, FakePlayer.getBukkitEntity(world));
			world.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return false;
			}
			else {
				event.setCancelled(true);
				return this.growBlockWithDependancy(world, i, l, j, net.minecraft.server.Block.BIG_MUSHROOM_1.id, net.minecraft.server.Block.BROWN_MUSHROOM.id);
			}
		}
	}

	public boolean growBlockWithDependancy(World world, int i, int j, int k, int l, int i1) {
		int j1;
		int k2;
		int j3;
		int l3;
		for (j1 = i - 1; i1 != -1 && j1 < i + 1; ++j1) {
			for (k2 = k - 1; k2 < k + 1; ++k2) {
				for (j3 = j + 5; j3 > j - 2; --j3) {
					l3 = world.getTypeId(j1, j3, k2);
					if (i1 == net.minecraft.server.Block.MYCEL.id) {
						if (l3 == i1 || l3 == net.minecraft.server.Block.BIG_MUSHROOM_1.id || l3 == net.minecraft.server.Block.BIG_MUSHROOM_2.id) {
							break;
						}

						if (l3 == 0) {
							continue;
						}

						if (l3 == net.minecraft.server.Block.DIRT.id || l3 == net.minecraft.server.Block.GRASS.id) {
							world.setTypeId(j1, j3, k2, i1);
							TileEntityTerra.setBiomeAt(world, i, k, BiomeBase.MUSHROOM_ISLAND);
							return true;
						}
					}

					if (i1 == net.minecraft.server.Block.BROWN_MUSHROOM.id) {
						if (l3 == net.minecraft.server.Block.BROWN_MUSHROOM.id || l3 == net.minecraft.server.Block.RED_MUSHROOM.id) {
							break;
						}

						if (l3 != 0 && this.growBlockWithDependancy(world, j1, j3, k2, net.minecraft.server.Block.BROWN_MUSHROOM.id, net.minecraft.server.Block.MYCEL.id)) {
							return true;
						}
					}
				}
			}
		}

		if (l == net.minecraft.server.Block.BROWN_MUSHROOM.id) {
			j1 = world.getTypeId(i, j, k);
			if (j1 != net.minecraft.server.Block.MYCEL.id) {
				if (j1 != net.minecraft.server.Block.BIG_MUSHROOM_1.id && j1 != net.minecraft.server.Block.BIG_MUSHROOM_2.id) {
					return false;
				}

				world.setTypeId(i, j, k, net.minecraft.server.Block.MYCEL.id);
			}

			k2 = world.getTypeId(i, j + 1, k);
			if (k2 != 0 && k2 != net.minecraft.server.Block.LONG_GRASS.id) {
				return false;
			}
			else {
				j3 = net.minecraft.server.Block.BROWN_MUSHROOM.id;
				if (world.random.nextBoolean()) {
					j3 = net.minecraft.server.Block.RED_MUSHROOM.id;
				}

				world.setTypeId(i, j + 1, k, j3);
				return true;
			}
		}
		else {
			if (l == net.minecraft.server.Block.BIG_MUSHROOM_1.id) {
				j1 = world.getTypeId(i, j + 1, k);
				if (j1 != net.minecraft.server.Block.BROWN_MUSHROOM.id && j1 != net.minecraft.server.Block.RED_MUSHROOM.id) {
					return false;
				}

				if (((BlockMushroom) net.minecraft.server.Block.byId[j1]).grow(world, i, j + 1, k, world.random, false, null, null)) {
					for (k2 = i - 1; k2 < i + 1; ++k2) {
						for (j3 = k - 1; j3 < k + 1; ++j3) {
							l3 = world.getTypeId(k2, j + 1, j3);
							if (l3 == net.minecraft.server.Block.BROWN_MUSHROOM.id || l3 == net.minecraft.server.Block.RED_MUSHROOM.id) {
								world.setTypeId(k2, j + 1, j3, 0);
							}
						}
					}

					return true;
				}
			}

			return false;
		}
	}
}
