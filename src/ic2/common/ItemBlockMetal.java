package ic2.common;

import ic2.platform.ItemBlockCommon;
import net.minecraft.server.ItemStack;

public class ItemBlockMetal extends ItemBlockCommon {
  public ItemBlockMetal(int i) {
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
        return "blockMetalCopper";
      case 1:
        return "blockMetalTin";
      case 2:
        return "blockMetalBronze";
      case 3:
        return "blockMetalUranium";
      default:
        return null;
    }
  }
}
