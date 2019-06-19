package ic2.common;

import forge.ITextureProvider;
import ic2.api.IMetalArmor;
import ic2.platform.ItemArmorCommon;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EnumArmorMaterial;
import net.minecraft.server.ItemStack;

public class ItemArmorIC2 extends ItemArmorCommon implements ITextureProvider, IMetalArmor {
  private static final int[] damageReduceAmountArray = new int[]{3, 8, 6, 3};
  private static final int[] maxDamageArray = new int[]{11, 16, 15, 13};
  private int enchantability;

  public ItemArmorIC2(int i, int j, EnumArmorMaterial enumarmormaterial, int k, int l, int i1) {
    super(i, enumarmormaterial, k, l);
    this.d(j);
    this.setMaxDurability(maxDamageArray[l] * i1);
    this.enchantability = EnumArmorMaterial.IRON.a();
  }

  public int c() {
    return this.enchantability;
  }

  public ItemArmorIC2 setEnchantability(int i) {
    this.enchantability = i;
    return this;
  }

  public boolean isMetalArmor(ItemStack itemstack, EntityHuman entityhuman) {
    return true;
  }

  public String getTextureFile() {
    return "/ic2/sprites/item_0.png";
  }
}
