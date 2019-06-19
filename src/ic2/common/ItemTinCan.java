package ic2.common;

import forge.ITextureProvider;
import ic2.platform.ItemFoodCommon;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public class ItemTinCan extends ItemFoodCommon implements ITextureProvider {
  public ItemTinCan(int i, int j) {
    super(i, 2, 0.95F, false);
    this.textureId = j;
  }

  public ItemStack b(ItemStack itemstack, World world, EntityHuman entityhuman) {
    super.b(itemstack, world, entityhuman);
    entityhuman.heal(2);
    ItemStack itemstack1 = Ic2Items.tinCan.cloneItemStack();
    if (!entityhuman.inventory.pickup(itemstack1)) {
      entityhuman.drop(itemstack1);
    }

    return itemstack;
  }

  public int c(ItemStack itemstack) {
    return 20;
  }

  public String getTextureFile() {
    return "/ic2/sprites/item_0.png";
  }
}
