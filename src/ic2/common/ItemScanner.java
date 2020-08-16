package ic2.common;

import ic2.platform.AudioManager;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.Map;

public class ItemScanner extends ElectricItem {
  public ItemScanner(int i, int j, int k) {
    super(i, j);
    this.maxCharge = 10000;
    this.transferLimit = 50;
    this.tier = k;
  }
  
  public static int valueOfArea(World world, int i, int j, int k, boolean flag) {
    int l = 0;
    int i1 = 0;
    byte byte0 = (byte) (flag ? 4 : 2);
    
    for (int j1 = j; j1 > 0; --j1) {
      for (int k1 = i - byte0; k1 <= i + byte0; ++k1) {
        for (int l1 = k - byte0; l1 <= k + byte0; ++l1) {
          int i2 = world.getTypeId(k1, j1, l1);
          int j2 = world.getData(i, j, k);
          if (flag) {
            l += valueOf(i2, j2);
          }
          else if (isValuable(i2, j2)) {
            ++l;
          }
  
          ++i1;
        }
      }
    }
    
    return i1 <= 0 ? null : (int) (1000.0D * (double) l / (double) i1);
  }
  
  public static boolean isValuable(int i, int j) {
    return valueOf(i, j) > 0;
  }
  
  public static int valueOf(int i, int j) {
    if (mod_IC2.valuableOres.containsKey(i)) {
      Map map = (Map) mod_IC2.valuableOres.get(i);
      if (map.containsKey(-1)) {
        return (Integer) map.get(-1);
      }
  
      if (map.containsKey(j)) {
        return (Integer) map.get(j);
      }
    }
    
    return 0;
  }
  
  public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
    if ((this.tier != 1 || ElectricItem.use(itemstack, 50, entityhuman)) &&
        (this.tier != 2 || ElectricItem.use(itemstack, 250, entityhuman))) {
      AudioManager.playOnce(entityhuman, PositionSpec.Hand, "Tools/ODScanner.ogg", true, AudioManager.defaultVolume);
      int i;
      if (this.tier == 2) {
        i = valueOfArea(world, Util.roundToNegInf(entityhuman.locX), Util.roundToNegInf(entityhuman.locY),
            Util.roundToNegInf(entityhuman.locZ), true);
        Platform.messagePlayer(entityhuman, "SCAN RESULT: Ore value in this area is " + i);
      }
      else {
        i = valueOfArea(world, Util.roundToNegInf(entityhuman.locX), Util.roundToNegInf(entityhuman.locY),
            Util.roundToNegInf(entityhuman.locZ), false);
        Platform.messagePlayer(entityhuman, "SCAN RESULT: Ore density in this area is " + i);
      }
      
      return itemstack;
    }
    else {
      return itemstack;
    }
  }
  
  public int startLayerScan(ItemStack itemstack) {
    return ElectricItem.use(itemstack, 50, null) ? 2 : 0;
  }
}
