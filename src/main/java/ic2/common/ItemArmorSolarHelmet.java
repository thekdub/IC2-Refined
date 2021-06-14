package ic2.common;

import ic2.api.IElectricItem;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;

public class ItemArmorSolarHelmet extends ItemArmorUtility implements IItemTickListener {
  public ItemArmorSolarHelmet(int i, int j, int k) {
    super(i, j, k, 0);
    this.setMaxDurability(0);
  }
  
  public boolean onTick(EntityHuman entityhuman, ItemStack itemstack) {
    if (entityhuman.inventory.armor[2] != null && entityhuman.inventory.armor[2].getItem() instanceof IElectricItem &&
        TileEntitySolarGenerator.isSunVisible(entityhuman.world, (int) entityhuman.locX, (int) entityhuman.locY + 1,
            (int) entityhuman.locZ)) {
      return ElectricItem.charge(entityhuman.inventory.armor[2], 1, Integer.MAX_VALUE, true, false) > 0;
    }
    else {
      return false;
    }
  }
  
  public int c() {
    return 0;
  }
}
