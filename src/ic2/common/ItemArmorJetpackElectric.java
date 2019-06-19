package ic2.common;

import ic2.api.IElectricItem;
import net.minecraft.server.ItemStack;

public class ItemArmorJetpackElectric extends ItemArmorJetpack implements IElectricItem {
  public ItemArmorJetpackElectric(int i, int j, int k) {
    super(i, j, k);
    this.setMaxDurability(27);
    this.e(1);
  }

  public int getCharge(ItemStack itemstack) {
    return ElectricItem.discharge(itemstack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true);
  }

  public void use(ItemStack itemstack, int i) {
    ElectricItem.discharge(itemstack, i, Integer.MAX_VALUE, true, false);
  }

  public boolean canProvideEnergy() {
    return false;
  }

  public int getChargedItemId() {
    return this.id;
  }

  public int getEmptyItemId() {
    return this.id;
  }

  public int getMaxCharge() {
    return 30000;
  }

  public int getTier() {
    return 1;
  }

  public int getTransferLimit() {
    return 60;
  }
}
