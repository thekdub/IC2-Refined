package ic2.common;

import forge.ForgeHooks;
import ic2.platform.Platform;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class ItemElectricToolHoe extends ItemElectricTool {
  public ItemElectricToolHoe(int i, int j) {
    super(i, j, EnumToolMaterial.IRON, 50);
    this.maxCharge = 10000;
    this.transferLimit = 100;
    this.tier = 1;
    this.a = 16.0F;
  }

  public void init() {
    this.mineableBlocks.add(Block.DIRT);
    this.mineableBlocks.add(Block.GRASS);
    this.mineableBlocks.add(Block.MYCEL);
  }

  public boolean a(ItemStack itemstack, int i, int j, int k, int l, EntityLiving entityliving) {
    ElectricItem.use(itemstack, this.operationEnergyCost, (EntityHuman) entityliving);
    return true;
  }

  public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    if (!entityhuman.d(i, j, k)) {
      return false;
    }
    else if (!ElectricItem.use(itemstack, this.operationEnergyCost, entityhuman)) {
      return false;
    }
    else if (ForgeHooks.onUseHoe(itemstack, entityhuman, world, i, j, k)) {
      return true;
    }
    else {
      int i1 = world.getTypeId(i, j, k);
      int j1 = world.getTypeId(i, j + 1, k);
      if ((l == 0 || j1 != 0 || i1 != Block.GRASS.id) && i1 != Block.DIRT.id) {
        return false;
      }
      else {
        Block block = Block.SOIL;
        world.makeSound((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), block.stepSound.getName(), (block.stepSound.getVolume1() + 1.0F) / 2.0F, block.stepSound.getVolume2() * 0.8F);
        if (!Platform.isSimulating()) {
          return true;
        }
        else {
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

          world.setTypeId(i, j, k, block.id);
          return true;
        }
      }
    }
  }
}
