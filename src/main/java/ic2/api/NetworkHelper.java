package ic2.api;

import net.minecraft.server.*;

public final class NetworkHelper {
  public static void updateTileEntityField(TileEntity tileentity, String s) {
    try {
      Class.forName(getPackage() + ".platform.NetworkManager")
          .getMethod("updateTileEntityField", TileEntity.class, String.class).invoke(null, tileentity, s);
    } catch (Exception var3) {
      throw new RuntimeException(var3);
    }
  }
  
  public static void initiateTileEntityEvent(TileEntity tileentity, int i, boolean flag) {
    try {
      Class.forName(getPackage() + ".platform.NetworkManager")
          .getMethod("initiateTileEntityEvent", TileEntity.class, Integer.TYPE, Boolean.TYPE)
          .invoke(null, tileentity, i, flag);
    } catch (Exception var4) {
      throw new RuntimeException(var4);
    }
  }
  
  public static void initiateItemEvent(EntityHuman entityhuman, ItemStack itemstack, int i, boolean flag) {
    try {
      Class.forName(getPackage() + ".platform.NetworkManager")
          .getMethod("initiateItemEvent", EntityHuman.class, ItemStack.class, Integer.TYPE, Boolean.TYPE)
          .invoke(null, entityhuman, itemstack, i, flag);
    } catch (Exception var5) {
      throw new RuntimeException(var5);
    }
  }
  
  public static void announceBlockUpdate(World world, int i, int j, int k) {
    try {
      Class.forName(getPackage() + ".platform.NetworkManager")
          .getMethod("announceBlockUpdate", World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE)
          .invoke(null, world, i, j, k);
    } catch (Exception var5) {
      throw new RuntimeException(var5);
    }
  }
  
  public static void requestInitialData(INetworkDataProvider inetworkdataprovider) {
    try {
      Class.forName(getPackage() + ".platform.NetworkManager")
          .getMethod("requestInitialData", INetworkDataProvider.class).invoke(null, inetworkdataprovider);
    } catch (Exception var2) {
      throw new RuntimeException(var2);
    }
  }
  
  public static void initiateClientTileEntityEvent(TileEntity tileentity, int i) {
    try {
      Class.forName(getPackage() + ".platform.NetworkManager")
          .getMethod("initiateClientTileEntityEvent", TileEntity.class, Integer.TYPE).invoke(null, tileentity, i);
    } catch (Exception var3) {
      throw new RuntimeException(var3);
    }
  }
  
  public static void initiateClientItemEvent(ItemStack itemstack, int i) {
    try {
      Class.forName(getPackage() + ".platform.NetworkManager")
          .getMethod("initiateClientItemEvent", ItemStack.class, Integer.TYPE).invoke(null, itemstack, i);
    } catch (Exception var3) {
      throw new RuntimeException(var3);
    }
  }
  
  private static String getPackage() {
    Package package1 = NetworkHelper.class.getPackage();
    return package1 != null ? package1.getName().substring(0, package1.getName().lastIndexOf(46)) : "ic2";
  }
}
