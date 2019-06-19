package ic2.platform;

import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;

import java.util.List;

public class ItemCommon extends Item {
  public ItemCommon(int i) {
    super(i);
  }

  public int getIconFromDamage(int i) {
    return 0;
  }

  public String a(ItemStack itemstack) {
    return null;
  }

  public void addInformation(ItemStack itemstack, List list) {
  }

  public int rarity(ItemStack itemstack) {
    return 0;
  }
}
