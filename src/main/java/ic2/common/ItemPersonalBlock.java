package ic2.common;

import ic2.platform.ItemBlockCommon;
import net.minecraft.server.ItemStack;

public class ItemPersonalBlock extends ItemBlockCommon {
  public ItemPersonalBlock(int i) {
    super(i);
    this.setMaxDurability(0);
    this.a(true);
  }
  
  public int filterData(int i) {
    return i;
  }
  
  public String a(ItemStack itemstack) {
    int i = itemstack.getData();
    switch (i) {
      case 0:
        return "blockPersonalChest";
      case 1:
        return "blockPersonalTrader";
      default:
        return null;
    }
  }
}
