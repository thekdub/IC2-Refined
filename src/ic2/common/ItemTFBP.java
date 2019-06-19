package ic2.common;

import ic2.api.ITerraformingBP;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public abstract class ItemTFBP extends ItemIC2 implements ITerraformingBP {
  public ItemTFBP(int i, int j) {
    super(i, j);
    this.e(1);
  }

  public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    if (world.getTileEntity(i, j, k) instanceof TileEntityTerra) {
      ((TileEntityTerra) world.getTileEntity(i, j, k)).insertBlueprint(itemstack);
      entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
      return true;
    }
    else {
      return false;
    }
  }
}
