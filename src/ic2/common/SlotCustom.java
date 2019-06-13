package ic2.common;

import net.minecraft.server.*;

public class SlotCustom extends Slot {
	private Object[] items;

	public SlotCustom(IInventory iinventory, Object[] aobj, int i, int j, int k) {
		super(iinventory, i, j, k);
		this.items = aobj;
	}

	public boolean isAllowed(ItemStack itemstack) {
		Object[] aobj = this.items;
		int i = aobj.length;

		for (int j = 0; j < i; ++j) {
			Object obj = aobj[j];
			if (obj != null) {
				if (obj instanceof Class) {
					if (itemstack.id < Block.byId.length && Block.byId[itemstack.id] != null && ((Class) obj).isAssignableFrom(Block.byId[itemstack.id].getClass())) {
						return true;
					}

					if ((itemstack.id >= Block.byId.length || Block.byId[itemstack.id] == null) && Item.byId[itemstack.id] != null && ((Class) obj).isAssignableFrom(Item.byId[itemstack.id].getClass())) {
						return true;
					}
				}
				else if (obj instanceof ItemStack) {
					if (itemstack.getData() == -1 && itemstack.id == ((ItemStack) obj).id) {
						return true;
					}

					if (itemstack.doMaterialsMatch((ItemStack) obj)) {
						return true;
					}
				}
				else {
					if (obj instanceof Block && itemstack.id == ((Block) obj).id) {
						return true;
					}

					if (obj instanceof Item && itemstack.id == ((Item) obj).id) {
						return true;
					}

					if (obj instanceof Integer && itemstack.id == (Integer) obj) {
						return true;
					}
				}
			}
		}

		return false;
	}
}
