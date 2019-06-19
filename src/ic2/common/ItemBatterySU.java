package ic2.common;

import ic2.api.IBoxable;
import ic2.api.IElectricItem;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public class ItemBatterySU extends ItemIC2 implements IBoxable {
  public int ratio;
  public int tier;
  public int soundTicker = 0;

  public ItemBatterySU(int i, int j, int k, int l) {
    super(i, j);
    this.ratio = k;
    this.tier = l;
    this.setMaxDurability(1002);
  }

  public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
    if (itemstack.id != Ic2Items.suBattery.id) {
      return itemstack;
    }
    else {
      ++this.soundTicker;
      if (this.soundTicker % 32 == 0) {
        world.makeSound(entityhuman, "battery", 1.0F, 1.0F);
      }

      int i = this.ratio * 1000;

      for (int j = 0; j < 9; ++j) {
        ItemStack itemstack1 = entityhuman.inventory.items[j];
        if (itemstack1 != null && Item.byId[itemstack1.id] instanceof IElectricItem && itemstack1 != itemstack) {
          int k;
          do {
            k = i;
            i -= ElectricItem.charge(itemstack1, i, 1, true, false);
            System.err.println(itemstack1.getData());
          } while (i > 0 && i != k);

          if (i <= 0) {
            break;
          }
        }
      }

      if (i < this.ratio * 1000) {
        --itemstack.count;
      }

      return itemstack;
    }
  }

  public boolean canBeStoredInToolbox(ItemStack itemstack) {
    return true;
  }
}
