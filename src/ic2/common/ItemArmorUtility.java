package ic2.common;

import forge.ArmorProperties;
import forge.ISpecialArmor;
import forge.ITextureProvider;
import ic2.platform.ItemArmorCommon;
import net.minecraft.server.*;

public class ItemArmorUtility extends ItemArmorCommon implements ITextureProvider, ISpecialArmor {
  public ItemArmorUtility(int i, int j, int k, int l) {
    super(i, EnumArmorMaterial.DIAMOND, k, l);
    this.textureId = j;
  }
  
  public int c() {
    return 0;
  }
  
  public boolean isRepairable() {
    return false;
  }
  
  public String getTextureFile() {
    return "/ic2/sprites/item_0.png";
  }
  
  public ArmorProperties getProperties(EntityLiving entityliving, ItemStack itemstack, DamageSource damagesource,
                                       double d, int i) {
    return new ArmorProperties(0, 0.0D, 0);
  }
  
  public int getArmorDisplay(EntityHuman entityhuman, ItemStack itemstack, int i) {
    return 0;
  }
  
  public void damageArmor(EntityLiving entityliving, ItemStack itemstack, DamageSource damagesource, int i, int j) {
  }
}
