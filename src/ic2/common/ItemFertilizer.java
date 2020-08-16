package ic2.common;

import ic2.api.IBoxable;
import net.minecraft.server.ItemStack;

public class ItemFertilizer extends ItemIC2 implements IBoxable {
  public ItemFertilizer(int i, int j) {
    super(i, j);
  }
  
  public boolean canBeStoredInToolbox(ItemStack itemstack) {
    return true;
  }
}
