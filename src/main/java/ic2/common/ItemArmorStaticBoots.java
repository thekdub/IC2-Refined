package ic2.common;

import ic2.api.IElectricItem;
import ic2.api.IMetalArmor;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;

public class ItemArmorStaticBoots extends ItemArmorUtility implements IMetalArmor, IItemTickListener {
  public ItemArmorStaticBoots(int i, int j, int k) {
    super(i, j, k, 3);
  }
  
  public boolean isMetalArmor(ItemStack itemstack, EntityHuman entityhuman) {
    return true;
  }
  
  public boolean onTick(EntityHuman entityhuman, ItemStack itemstack) {
    if (entityhuman.inventory.armor[2] != null && entityhuman.inventory.armor[2].getItem() instanceof IElectricItem) {
      NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
      boolean flag = entityhuman.vehicle != null || entityhuman.aU();
      if (!nbttagcompound.hasKey("x") || flag) {
        nbttagcompound.setInt("x", (int) entityhuman.locX);
      }
  
      if (!nbttagcompound.hasKey("z") || flag) {
        nbttagcompound.setInt("z", (int) entityhuman.locZ);
      }
  
      double d = Math.sqrt((nbttagcompound.getInt("x") - (int) entityhuman.locX) *
          (nbttagcompound.getInt("x") - (int) entityhuman.locX) +
          (nbttagcompound.getInt("z") - (int) entityhuman.locZ) *
              (nbttagcompound.getInt("z") - (int) entityhuman.locZ));
      if (d >= 5.0D) {
        nbttagcompound.setInt("x", (int) entityhuman.locX);
        nbttagcompound.setInt("z", (int) entityhuman.locZ);
        return ElectricItem
            .charge(entityhuman.inventory.armor[2], Math.min(3, (int) d / 5), Integer.MAX_VALUE, true, false) > 0;
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
