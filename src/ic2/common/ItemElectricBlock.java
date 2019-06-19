package ic2.common;

import ic2.platform.ItemBlockCommon;
import net.minecraft.server.ItemStack;

public class ItemElectricBlock extends ItemBlockCommon {
  public ItemElectricBlock(int i) {
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
        return "blockBatBox";
      case 1:
        return "blockMFE";
      case 2:
        return "blockMFSU";
      case 3:
        return "blockTransformerLV";
      case 4:
        return "blockTransformerMV";
      case 5:
        return "blockTransformerHV";
      default:
        return null;
    }
  }
}
