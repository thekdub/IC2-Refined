package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EnumToolMaterial;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class ItemTreetapElectric extends ItemElectricTool {
  public ItemTreetapElectric(int i, int j) {
    super(i, j, EnumToolMaterial.IRON, 50);
    this.maxCharge = 10000;
    this.transferLimit = 100;
    this.tier = 1;
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

    if (world.getTypeId(i, j, k) != Ic2Items.rubberWood.id) {
      return false;
    }
    else if (!ElectricItem.use(itemstack, this.operationEnergyCost, entityhuman)) {
      return false;
    }
    else {
      ItemTreetap.attemptExtract(entityhuman, world, i, j, k, l);
      return true;
    }
  }
}
