package ic2.common;

import ic2.api.IElectricItem;
import net.minecraft.server.IInventory;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Slot;

public class SlotDischarge extends Slot {
	public int tier = Integer.MAX_VALUE;

	public SlotDischarge(IInventory iinventory, int i, int j, int k, int l) {
		super(iinventory, j, k, l);
		this.tier = i;
	}

	public SlotDischarge(IInventory iinventory, int i, int j, int k) {
		super(iinventory, i, j, k);
	}

	public boolean isAllowed(ItemStack itemstack) {
		if (itemstack.id != Item.REDSTONE.id && !(itemstack.getItem() instanceof ItemBatterySU)) {
			if (itemstack.getItem() instanceof IElectricItem) {
				IElectricItem ielectricitem = (IElectricItem) itemstack.getItem();
				return ielectricitem.canProvideEnergy() && ielectricitem.getTier() <= this.tier;
			}

			return false;
		}
		else {
			return true;
		}
	}
}
