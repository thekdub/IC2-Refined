package ic2.common;

import forge.ITextureProvider;
import ic2.platform.ItemAxeCommon;
import net.minecraft.server.EnumToolMaterial;

public class ItemIC2Axe extends ItemAxeCommon implements ITextureProvider {
  public float a;

  public ItemIC2Axe(int i, int j, EnumToolMaterial enumtoolmaterial, float f) {
    super(i, enumtoolmaterial);
    this.a = f;
    this.d(j);
  }

  public String getTextureFile() {
    return "/ic2/sprites/item_0.png";
  }

  public int c() {
    return 13;
  }
}
