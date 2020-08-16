package ic2.common;

import net.minecraft.server.ItemStack;

public class ItemArmorLappack extends ItemArmorElectric {
  public ItemArmorLappack(int i, int j, int k) {
    super(i, j, k, 1, 300000, 250, 2);
  }
  
  public boolean canProvideEnergy() {
    return true;
  }
  
  public double getDamageAbsorptionRatio() {
    return 0.0D;
  }
  
  public int getEnergyPerDamage() {
    return 0;
  }
  
  public int rarity(ItemStack itemstack) {
    return 1;
  }
}
