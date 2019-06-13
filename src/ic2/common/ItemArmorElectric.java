package ic2.common;

import forge.ArmorProperties;
import forge.ISpecialArmor;
import forge.ITextureProvider;
import ic2.api.IElectricItem;
import ic2.platform.ItemArmorCommon;
import net.minecraft.server.*;

import java.util.ArrayList;

public abstract class ItemArmorElectric extends ItemArmorCommon implements ITextureProvider, ISpecialArmor, IElectricItem {
	public int maxCharge;
	public int transferLimit;
	public int tier;

	public ItemArmorElectric(int i, int j, int k, int l, int i1, int j1, int k1) {
		super(i, EnumArmorMaterial.DIAMOND, k, l);
		this.textureId = j;
		this.maxCharge = i1;
		this.tier = k1;
		this.transferLimit = j1;
		this.setMaxDurability(27);
		this.e(1);
	}

	public int c() {
		return 0;
	}

	public boolean isRepairable() {
		return false;
	}

	public void addCreativeItems(ArrayList arraylist) {
		ItemStack itemstack = new ItemStack(this, 1);
		ElectricItem.charge(itemstack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
		arraylist.add(itemstack);
		arraylist.add(new ItemStack(this, 1, this.getMaxDurability()));
	}

	public String getTextureFile() {
		return "/ic2/sprites/item_0.png";
	}

	public ArmorProperties getProperties(EntityLiving entityliving, ItemStack itemstack, DamageSource damagesource, double d, int i) {
		double d1 = this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio();
		int j = this.getEnergyPerDamage();
		int k = j <= 0 ? 0 : ElectricItem.discharge(itemstack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) / j;
		return new ArmorProperties(0, d1, k);
	}

	public int getArmorDisplay(EntityHuman entityhuman, ItemStack itemstack, int i) {
		return (int) Math.round(20.0D * this.getBaseAbsorptionRatio() * this.getDamageAbsorptionRatio());
	}

	public void damageArmor(EntityLiving entityliving, ItemStack itemstack, DamageSource damagesource, int i, int j) {
		ElectricItem.discharge(itemstack, i * this.getEnergyPerDamage(), Integer.MAX_VALUE, true, false);
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
		return this.maxCharge;
	}

	public int getTier() {
		return this.tier;
	}

	public int getTransferLimit() {
		return this.transferLimit;
	}

	public abstract double getDamageAbsorptionRatio();

	public abstract int getEnergyPerDamage();

	private double getBaseAbsorptionRatio() {
		switch (this.a) {
			case 0:
				return 0.15D;
			case 1:
				return 0.4D;
			case 2:
				return 0.3D;
			case 3:
				return 0.15D;
			default:
				return 0.0D;
		}
	}
}
