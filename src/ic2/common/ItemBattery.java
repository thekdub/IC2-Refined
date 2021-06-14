package ic2.common;

import ic2.api.IElectricItem;
import ic2.platform.Platform;
import net.minecraft.server.*;

public class ItemBattery extends ElectricItem {
  public ItemBattery(int i, int j, int k, int l, int i1) {
    super(i, j);
    this.maxCharge = k;
    this.transferLimit = l;
    this.tier = i1;
  }
  
  public boolean canProvideEnergy() {
    return true;
  }
  
  public int getEmptyItemId() {
    return this.id == Ic2Items.chargedReBattery.id ? Ic2Items.reBattery.id : super.getEmptyItemId();
  }
  
  public int getIconFromDamage(int i) {
    if (i <= 1) {
      return this.textureId + 4;
    }
    else if (i <= 8) {
      return this.textureId + 3;
    }
    else if (i <= 14) {
      return this.textureId + 2;
    }
    else {
      return i <= 20 ? this.textureId + 1 : this.textureId;
    }
  }
  
  public int getIconFromChargeLevel(float f) {
    return this.getIconFromDamage(1 + (int) Math.round((1.0D - (double) f) * (double) (this.getMaxDurability() - 2)));
  }
  
  public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
    if (Platform.isSimulating() && itemstack.id == Ic2Items.chargedReBattery.id) {
      boolean flag = false;
  
      for (int i = 0; i < 9; ++i) {
        ItemStack itemstack1 = entityhuman.inventory.items[i];
        if (itemstack1 != null && Item.byId[itemstack1.id] instanceof IElectricItem && itemstack1 != itemstack) {
          IElectricItem ielectricitem = (IElectricItem) itemstack1.getItem();
          int j = discharge(itemstack, 2 * this.transferLimit, ielectricitem.getTier(), true, true);
          j = charge(itemstack1, j, this.tier, true, false);
          discharge(itemstack, j, ielectricitem.getTier(), true, false);
          if (j == 0) {
            break;
          }
  
          flag = true;
        }
      }
  
      if (flag && !Platform.isRendering()) {
        entityhuman.activeContainer.a();
      }
    }
    
    return itemstack;
  }
}
