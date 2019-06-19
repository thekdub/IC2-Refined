package ic2.common;

import ic2.api.IBoxable;
import net.minecraft.server.ItemStack;

public class ItemGradual extends ItemIC2 implements IBoxable {
  public ItemGradual(int i, int j) {
    super(i, j);
    this.e(1);
    this.setMaxDurability(10000);
  }

  public boolean canBeStoredInToolbox(ItemStack itemstack) {
    return itemstack.id == Ic2Items.hydratingCell.id;
  }
}
