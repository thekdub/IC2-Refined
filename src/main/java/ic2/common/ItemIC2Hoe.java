package ic2.common;

import forge.ITextureProvider;
import ic2.platform.ItemHoeCommon;
import net.minecraft.server.EnumToolMaterial;

public class ItemIC2Hoe extends ItemHoeCommon implements ITextureProvider {
  public ItemIC2Hoe(int i, int j, EnumToolMaterial enumtoolmaterial) {
    super(i, enumtoolmaterial);
    this.d(j);
  }
  
  public String getTextureFile() {
    return "/ic2/sprites/item_0.png";
  }
}
