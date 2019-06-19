package ic2.common;

import ic2.api.IBoxable;
import net.minecraft.server.ItemStack;

public class ItemFuelCan extends ItemIC2 implements IBoxable {
  public ItemFuelCan(int i, int j) {
    super(i, j);
  }

  public boolean canBeStoredInToolbox(ItemStack itemstack) {
    return false;
  }
}
