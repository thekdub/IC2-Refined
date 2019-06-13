package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;

public class ItemArmorCFPack extends ItemArmorUtility {
	public ItemArmorCFPack(int i, int j, int k) {
		super(i, j, k, 1);
		this.setMaxDurability(260);
	}

	public boolean getCFPellet(EntityHuman entityhuman, ItemStack itemstack) {
		if (itemstack.getData() < itemstack.i() - 1) {
			itemstack.setData(itemstack.getData() + 1);
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isRepairable() {
		return true;
	}
}
