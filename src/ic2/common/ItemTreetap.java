package ic2.common;

import ic2.platform.AudioManager;
import ic2.platform.NetworkManager;
import ic2.platform.Platform;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class ItemTreetap extends ItemIC2 {
	public ItemTreetap(int i, int j) {
		super(i, j);
		this.e(1);
		this.setMaxDurability(16);
	}

	public static void ejectHarz(World world, int i, int j, int k, int l, int i1) {
		double d = (double) i + 0.5D;
		double d1 = (double) j + 0.5D;
		double d2 = (double) k + 0.5D;
		if (l == 2) {
			d2 -= 0.3D;
		}
		else if (l == 5) {
			d += 0.3D;
		}
		else if (l == 3) {
			d2 += 0.3D;
		}
		else if (l == 4) {
			d -= 0.3D;
		}

		for (int j1 = 0; j1 < i1; ++j1) {
			EntityItem entityitem = new EntityItem(world, d, d1, d2, Ic2Items.resin.cloneItemStack());
			entityitem.pickupDelay = 10;
			world.addEntity(entityitem);
		}

	}

	public static boolean attemptExtract(EntityHuman entityhuman, World world, int i, int j, int k, int l) {
		int i1 = world.getData(i, j, k);
		if (i1 >= 2 && i1 % 6 == l) {
			if (i1 < 6) {
				if (Platform.isSimulating()) {
					world.setData(i, j, k, i1 + 6);
					ejectHarz(world, i, j, k, l, world.random.nextInt(3) + 1);
					IC2Achievements.issueAchievement(entityhuman, "acquireResin");
					world.c(i, j, k, Ic2Items.rubberWood.id, Block.byId[Ic2Items.rubberWood.id].d());
					NetworkManager.announceBlockUpdate(world, i, j, k);
				}

				if (Platform.isRendering()) {
					AudioManager.playOnce(entityhuman, PositionSpec.Hand, "Tools/Treetap.ogg", true, AudioManager.defaultVolume);
				}

				return true;
			}
			else {
				if (world.random.nextInt(5) == 0 && Platform.isSimulating()) {
					world.setData(i, j, k, 1);
					NetworkManager.announceBlockUpdate(world, i, j, k);
				}

				if (world.random.nextInt(5) == 0) {
					if (Platform.isSimulating()) {
						ejectHarz(world, i, j, k, l, 1);
					}

					if (Platform.isRendering()) {
						AudioManager.playOnce(entityhuman, PositionSpec.Hand, "Tools/Treetap.ogg", true, AudioManager.defaultVolume);
					}

					return true;
				}
				else {
					return false;
				}
			}
		}
		else {
			return false;
		}
	}

	public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
		Entity bukkitentity = entityhuman.getBukkitEntity();
		if (bukkitentity instanceof Player) {
			Player player = (Player) bukkitentity;
			BlockBreakEvent breakev = new BlockBreakEvent(player.getWorld().getBlockAt(i, j, k), player);
			Bukkit.getPluginManager().callEvent(breakev);
			if (breakev.isCancelled()) {
				return false;
			}

			breakev.setCancelled(true);
		}

		if (world.getTypeId(i, j, k) == Ic2Items.blockBarrel.id) {
			return ((TileEntityBarrel) world.getTileEntity(i, j, k)).useTreetapOn(entityhuman, l);
		}
		else if (world.getTypeId(i, j, k) == Ic2Items.rubberWood.id) {
			attemptExtract(entityhuman, world, i, j, k, l);
			if (Platform.isSimulating()) {
				itemstack.damage(1, entityhuman);
			}

			return true;
		}
		else {
			return false;
		}
	}
}
