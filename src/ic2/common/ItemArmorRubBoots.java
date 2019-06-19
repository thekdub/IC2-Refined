package ic2.common;

import forge.ArmorProperties;
import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.ItemStack;

public class ItemArmorRubBoots extends ItemArmorUtility {
  public ItemArmorRubBoots(int i, int j, int k) {
    super(i, j, k, 3);
    this.setMaxDurability(64);
  }

  public ArmorProperties getProperties(EntityLiving entityliving, ItemStack itemstack, DamageSource damagesource, double d, int i) {
    return damagesource == DamageSource.FALL ? new ArmorProperties(10, d >= 8.0D ? 0.875D : 1.0D, Integer.MAX_VALUE) : super.getProperties(entityliving, itemstack, damagesource, d, i);
  }

  public void damageArmor(EntityLiving entityliving, ItemStack itemstack, DamageSource damagesource, int i, int j) {
    if (damagesource == DamageSource.FALL) {
      itemstack.damage((i + 1) / 2, entityliving);
    }
    else {
      super.damageArmor(entityliving, itemstack, damagesource, i, j);
    }

  }

  public boolean isRepairable() {
    return true;
  }
}
