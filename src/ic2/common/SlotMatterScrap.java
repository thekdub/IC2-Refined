package ic2.common;

import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Slot;

import java.util.Iterator;
import java.util.Map.Entry;

public class SlotMatterScrap extends Slot {
	public SlotMatterScrap(IInventory iinventory, int i, int j, int k) {
		super(iinventory, i, j, k);
	}

	public boolean isAllowed(ItemStack itemstack) {
		if (itemstack.doMaterialsMatch(Ic2Items.scrap)) {
			return true;
		}
		else {
			Iterator iterator = TileEntityMatter.amplifiers.iterator();

			while (iterator.hasNext()) {
				Entry entry = (Entry) iterator.next();
				if (itemstack.doMaterialsMatch((ItemStack) entry.getKey())) {
					return true;
				}
			}

			return false;
		}
	}
}
