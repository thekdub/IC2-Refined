package ic2.common;

import forge.ITextureProvider;
import ic2.platform.ItemFoodCommon;
import ic2.platform.Platform;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.MobEffectList;
import net.minecraft.server.World;

public class ItemTerraWart extends ItemFoodCommon implements ITextureProvider {
  public ItemTerraWart(int i, int j) {
    super(i, 0, 1.0F, false);
    this.textureId = j;
    this.r();
  }

  public ItemStack b(ItemStack itemstack, World world, EntityHuman entityhuman) {
    --itemstack.count;
    world.makeSound(entityhuman, "random.burp", 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
    Platform.removePotionFrom(entityhuman, MobEffectList.CONFUSION.id);
    Platform.removePotionFrom(entityhuman, MobEffectList.SLOWER_DIG.id);
    Platform.removePotionFrom(entityhuman, MobEffectList.HUNGER.id);
    Platform.removePotionFrom(entityhuman, MobEffectList.SLOWER_MOVEMENT.id);
    Platform.removePotionFrom(entityhuman, MobEffectList.WEAKNESS.id);
    return itemstack;
  }

  public int rarity(ItemStack itemstack) {
    return 2;
  }

  public String getTextureFile() {
    return "/ic2/sprites/item_0.png";
  }
}
