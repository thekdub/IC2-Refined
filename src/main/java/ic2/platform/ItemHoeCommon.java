package ic2.platform;

import net.minecraft.server.EnumToolMaterial;
import net.minecraft.server.ItemHoe;
import net.minecraft.server.ItemStack;

public class ItemHoeCommon extends ItemHoe {
  public ItemHoeCommon(int i, EnumToolMaterial enumtoolmaterial) {
    super(i, enumtoolmaterial);
  }
  
  public int rarity(ItemStack itemstack) {
    return 0;
  }
}
