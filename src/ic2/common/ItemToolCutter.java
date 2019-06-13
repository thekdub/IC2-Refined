package ic2.common;

import ic2.platform.AudioManager;
import ic2.platform.Platform;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class ItemToolCutter extends ItemIC2 {
	public ItemToolCutter(int i, int j) {
		super(i, j);
		this.setMaxDurability(512);
		this.e(1);
	}

	public static void cutInsulationFrom(ItemStack itemstack, World world, int i, int j, int k) {
		TileEntity tileentity = world.getTileEntity(i, j, k);
		if (tileentity instanceof TileEntityCable) {
			TileEntityCable tileentitycable = (TileEntityCable) tileentity;
			if (tileentitycable.tryRemoveInsulation()) {
				if (Platform.isSimulating()) {
					double d = (double) world.random.nextFloat() * 0.7D + 0.15D;
					double d1 = (double) world.random.nextFloat() * 0.7D + 0.15D;
					double d2 = (double) world.random.nextFloat() * 0.7D + 0.15D;
					EntityItem entityitem = new EntityItem(world, (double) i + d, (double) j + d1, (double) k + d2, Ic2Items.rubber.cloneItemStack());
					entityitem.pickupDelay = 10;
					world.addEntity(entityitem);
					damageCutter(itemstack, 3);
				}

				if (Platform.isRendering()) {
					AudioManager.playOnce(new AudioPosition(world, (float) i + 0.5F, (float) j + 0.5F, (float) k + 0.5F), PositionSpec.Center, "Tools/InsulationCutters.ogg", true, AudioManager.defaultVolume);
				}
			}
		}

	}

	public static void damageCutter(ItemStack itemstack, int i) {
		if (itemstack.d()) {
			itemstack.setData(itemstack.getData() + i);
			if (itemstack.getData() > itemstack.i()) {
				--itemstack.count;
				if (itemstack.count < 0) {
					itemstack.count = 0;
				}

				itemstack.setData(0);
			}

		}
	}

	public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
		TileEntity tileentity = world.getTileEntity(i, j, k);
		if (tileentity instanceof TileEntityCable) {
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

			TileEntityCable tileentitycable = (TileEntityCable) tileentity;
			if (tileentitycable.tryAddInsulation()) {
				if (entityhuman.inventory.c(Ic2Items.rubber.id)) {
					if (Platform.isSimulating()) {
						damageCutter(itemstack, 1);
					}

					return true;
				}

				tileentitycable.tryRemoveInsulation();
			}
		}

		return false;
	}
}
