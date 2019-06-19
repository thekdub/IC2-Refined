package ic2.common;

import ic2.api.IBoxable;
import net.minecraft.server.ItemStack;

import java.util.ArrayList;

public class ItemBatteryDischarged extends ItemBattery implements IBoxable {
  public ItemBatteryDischarged(int i, int j, int k, int l, int i1) {
    super(i, j, k, l, i1);
    this.setMaxDurability(0);
    this.e(16);
  }

  public int getChargedItemId() {
    return Ic2Items.chargedReBattery.id;
  }

  public int getIconFromDamage(int i) {
    return this.textureId;
  }

  public void addCreativeItems(ArrayList arraylist) {
    arraylist.add(new ItemStack(this));
  }

  public boolean canBeStoredInToolbox(ItemStack itemstack) {
    return true;
  }
}
