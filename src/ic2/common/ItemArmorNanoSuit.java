package ic2.common;

import forge.ArmorProperties;
import ic2.api.IMetalArmor;
import net.minecraft.server.DamageSource;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.ItemStack;

public class ItemArmorNanoSuit extends ItemArmorElectric implements IMetalArmor {
	public ItemArmorNanoSuit(int i, int j, int k, int l) {
		super(i, j, k, l, 100000, 160, 2);
	}

	public ArmorProperties getProperties(EntityLiving entityliving, ItemStack itemstack, DamageSource damagesource, double d, int i) {
		if (damagesource == DamageSource.FALL && this.a == 3) {
			int j = this.getEnergyPerDamage();
			int k = j <= 0 ? 0 : ElectricItem.discharge(itemstack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) / j;
			return new ArmorProperties(10, d >= 8.0D ? 0.875D : 1.0D, k);
		}
		else {
			return super.getProperties(entityliving, itemstack, damagesource, d, i);
		}
	}

	public double getDamageAbsorptionRatio() {
		return 0.9D;
	}

	public int getEnergyPerDamage() {
		return 40;
	}

	public boolean isMetalArmor(ItemStack itemstack, EntityHuman entityhuman) {
		return true;
	}

	public int rarity(ItemStack itemstack) {
		return 1;
	}
}
