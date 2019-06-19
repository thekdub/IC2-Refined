package ic2.common;

import ic2.api.IElectricItem;
import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Slot;

public class SlotCharge extends Slot {
  int tier;

  public SlotCharge(IInventory iinventory, int i, int j, int k, int l) {
    super(iinventory, j, k, l);
    this.tier = Integer.MAX_VALUE;
    this.tier = i;
  }

  public SlotCharge(IInventory iinventory, int i, int j, int k) {
    this(iinventory, Integer.MAX_VALUE, i, j, k);
  }

  public boolean isAllowed(ItemStack itemstack) {
    return itemstack.getItem() instanceof IElectricItem && ((IElectricItem) itemstack.getItem()).getTier() <= this.tier;
  }
}
