package ic2.common;

import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Slot;

public class SlotReactor extends Slot {
	public SlotReactor(IInventory iinventory, int i, int j, int k) {
		super(iinventory, i, j, k);
	}

	public boolean isAllowed(ItemStack itemstack) {
		return TileEntityNuclearReactor.isUsefulItem(itemstack);
	}
}
