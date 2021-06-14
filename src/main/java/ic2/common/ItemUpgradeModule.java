package ic2.common;

import net.minecraft.server.ItemStack;

import java.util.ArrayList;

public class ItemUpgradeModule extends ItemIC2 {
  public ItemUpgradeModule(int i) {
    super(i, 176);
    this.a(true);
    Ic2Items.overclockerUpgrade = new ItemStack(this, 1, 0);
    Ic2Items.transformerUpgrade = new ItemStack(this, 1, 1);
    Ic2Items.energyStorageUpgrade = new ItemStack(this, 1, 2);
  }
  
  public int getIconFromDamage(int i) {
    return this.textureId + i;
  }
  
  public String a(ItemStack itemstack) {
    switch (itemstack.getData()) {
      case 0:
        return "overclockerUpgrade";
      case 1:
        return "transformerUpgrade";
      case 2:
        return "energyStorageUpgrade";
      default:
        return null;
    }
  }
  
  public void addCreativeItems(ArrayList arraylist) {
    for (int i = 0; i <= 32767; ++i) {
      ItemStack itemstack = new ItemStack(this, 1, i);
      if (this.a(itemstack) == null) {
        break;
      }
  
      arraylist.add(itemstack);
    }
    
  }
}
