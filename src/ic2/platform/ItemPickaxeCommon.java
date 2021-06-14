package ic2.platform;

import net.minecraft.server.EnumToolMaterial;
import net.minecraft.server.ItemPickaxe;
import net.minecraft.server.ItemStack;

public class ItemPickaxeCommon extends ItemPickaxe {
  public ItemPickaxeCommon(int i, EnumToolMaterial enumtoolmaterial) {
    super(i, enumtoolmaterial);
  }
  
  public int rarity(ItemStack itemstack) {
    return 0;
  }
}
