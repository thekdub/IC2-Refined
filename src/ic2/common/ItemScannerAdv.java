package ic2.common;

import net.minecraft.server.ItemStack;

public class ItemScannerAdv extends ItemScanner {
  public ItemScannerAdv(int i, int j, int k) {
    super(i, j, k);
  }
  
  public int startLayerScan(ItemStack itemstack) {
    return ElectricItem.use(itemstack, 250, null) ? 4 : 0;
  }
}
