package ic2.platform;

import net.minecraft.server.EnumToolMaterial;
import net.minecraft.server.ItemSpade;
import net.minecraft.server.ItemStack;

public class ItemSpadeCommon extends ItemSpade {
  public ItemSpadeCommon(int i, EnumToolMaterial enumtoolmaterial) {
    super(i, enumtoolmaterial);
  }
  
  public int rarity(ItemStack itemstack) {
    return 0;
  }
}
