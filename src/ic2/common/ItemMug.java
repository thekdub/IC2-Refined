package ic2.common;

import ic2.platform.Platform;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class ItemMug extends ItemIC2 {
  public ItemMug(int i, int j) {
    super(i, j);
    this.e(1);
  }
  
  public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    if (world.getTypeId(i, j, k) == Ic2Items.blockBarrel.id) {
      TileEntityBarrel tileentitybarrel = (TileEntityBarrel) world.getTileEntity(i, j, k);
      if (tileentitybarrel.treetapSide >= 2 && tileentitybarrel.treetapSide == l) {
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
  
        int i1 = tileentitybarrel.calculateMetaValue();
        if (tileentitybarrel.drainLiquid(1) && Platform.isSimulating()) {
          ItemStack itemstack1 = new ItemStack(Ic2Items.mugBooze.id, 1, i1);
          if (entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex].count > 1) {
            --entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex].count;
            if (!entityhuman.inventory.pickup(itemstack1)) {
              entityhuman.drop(itemstack1);
            }
          }
          else {
            entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = itemstack1;
          }
  
          return true;
        }
        else {
          return false;
        }
      }
      else {
        return false;
      }
    }
    else {
      return false;
    }
  }
}
