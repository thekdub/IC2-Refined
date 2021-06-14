package ic2.common;

import forge.ITextureProvider;
import ic2.platform.ItemSwordCommon;
import net.minecraft.server.EnumToolMaterial;

public class ItemIC2Sword extends ItemSwordCommon implements ITextureProvider {
  public int weaponDamage;
  
  public ItemIC2Sword(int i, int j, EnumToolMaterial enumtoolmaterial, int k) {
    super(i, enumtoolmaterial);
    this.d(j);
    this.weaponDamage = k;
  }
  
  public String getTextureFile() {
    return "/ic2/sprites/item_0.png";
  }
  
  public int c() {
    return 13;
  }
}
