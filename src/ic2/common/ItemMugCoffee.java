package ic2.common;

import net.minecraft.server.*;

import java.util.ArrayList;

public class ItemMugCoffee extends ItemIC2 {
	public ItemMugCoffee(int i, int j) {
		super(i, j);
		this.e(1);
	}

	public int getIconFromDamage(int i) {
		return this.textureId + i;
	}

	public String a(ItemStack itemstack) {
		int i = itemstack.getData();
		return "item.itemMugCoffee" + i;
	}

	public ItemStack b(ItemStack itemstack, World world, EntityHuman entityhuman) {
		int i = itemstack.getData();
		int j = 0;
		int k = this.amplifyEffect(entityhuman, MobEffectList.FASTER_MOVEMENT, i);
		if (k > j) {
			j = k;
		}

		k = this.amplifyEffect(entityhuman, MobEffectList.FASTER_DIG, i);
		if (k > j) {
			j = k;
		}

		if (i == 2) {
			j -= 2;
		}

		if (j >= 3) {
			entityhuman.addEffect(new MobEffect(MobEffectList.CONFUSION.id, (j - 2) * 200, 0));
			if (j >= 4) {
				entityhuman.addEffect(new MobEffect(MobEffectList.HARM.id, 1, j - 3));
			}
		}

		return new ItemStack(Ic2Items.mugEmpty.getItem());
	}

	public int amplifyEffect(EntityHuman entityhuman, MobEffectList mobeffectlist, int i) {
		MobEffect mobeffect = entityhuman.getEffect(mobeffectlist);
		if (mobeffect != null) {
			byte byte0 = 1;
			if (i == 1) {
				byte0 = 5;
			}

			if (i == 2) {
				byte0 = 6;
			}

			int j = mobeffect.getAmplifier();
			int k = mobeffect.getDuration();
			if (j < byte0) {
				++j;
			}

			if (i == 0) {
				k += 600;
			}
			else {
				k += 1200;
			}

			mobeffect.a(new MobEffect(mobeffect.getEffectId(), k, j));
			return j;
		}
		else {
			entityhuman.addEffect(new MobEffect(mobeffectlist.id, 300, 0));
			return 1;
		}
	}

	public int c(ItemStack itemstack) {
		return 32;
	}

	public EnumAnimation d(ItemStack itemstack) {
		return EnumAnimation.c;
	}

	public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
		entityhuman.a(itemstack, this.c(itemstack));
		return itemstack;
	}

	public void addCreativeItems(ArrayList arraylist) {
		for (int i = 0; i < 3; ++i) {
			arraylist.add(new ItemStack(this, 1, i));
		}

	}
}
