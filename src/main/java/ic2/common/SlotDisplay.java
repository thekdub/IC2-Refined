package ic2.common;

import net.minecraft.server.IInventory;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Slot;

public class SlotDisplay extends Slot {
  public SlotDisplay(IInventory iinventory, int i, int j, int k) {
    super(iinventory, i, j, k);
  }
  
  public boolean isAllowed(ItemStack itemstack) {
    return false;
  }
  
  public void c(ItemStack itemstack) {
  }
  
  public ItemStack a(int i) {
    return this.getItem();
  }
}
