package ic2.common;

public class ItemArmorBatpack extends ItemArmorElectric {
  public ItemArmorBatpack(int i, int j, int k) {
    super(i, j, k, 1, 60000, 100, 1);
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
}
