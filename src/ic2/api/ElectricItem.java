package ic2.api;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;

public final class ElectricItem {
  public static int charge(ItemStack itemstack, int i, int j, boolean flag, boolean flag1) {
    try {
      return (Integer) Class.forName(getPackage() + ".common.ElectricItem")
          .getMethod("charge", ItemStack.class, Integer.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE)
          .invoke(null, itemstack, i, j, flag, flag1);
    } catch (Exception var6) {
      throw new RuntimeException(var6);
    }
  }
  
  public static int discharge(ItemStack itemstack, int i, int j, boolean flag, boolean flag1) {
    try {
      return (Integer) Class.forName(getPackage() + ".common.ElectricItem")
          .getMethod("discharge", ItemStack.class, Integer.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE)
          .invoke(null, itemstack, i, j, flag, flag1);
    } catch (Exception var6) {
      throw new RuntimeException(var6);
    }
  }
  
  public static boolean canUse(ItemStack itemstack, int i) {
    try {
      return (Boolean) Class.forName(getPackage() + ".common.ElectricItem")
          .getMethod("canUse", ItemStack.class, Integer.TYPE).invoke(null, itemstack, i);
    } catch (Exception var3) {
      throw new RuntimeException(var3);
    }
  }
  
  public static boolean use(ItemStack itemstack, int i, EntityHuman entityhuman) {
    try {
      return (Boolean) Class.forName(getPackage() + ".common.ElectricItem")
          .getMethod("use", ItemStack.class, Integer.TYPE, EntityHuman.class).invoke(null, itemstack, i, entityhuman);
    } catch (Exception var4) {
      throw new RuntimeException(var4);
    }
  }
  
  public static void chargeFromArmor(ItemStack itemstack, EntityHuman entityhuman) {
    try {
      Class.forName(getPackage() + ".common.ElectricItem")
          .getMethod("chargeFromArmor", ItemStack.class, EntityHuman.class).invoke(null, itemstack, entityhuman);
    } catch (Exception var3) {
      throw new RuntimeException(var3);
    }
  }
  
  private static String getPackage() {
    Package package1 = ElectricItem.class.getPackage();
    return package1 != null ? package1.getName().substring(0, package1.getName().lastIndexOf(46)) : "ic2";
  }
}
