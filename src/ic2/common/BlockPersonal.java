package ic2.common;

import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.ArrayList;
import java.util.Random;

public class BlockPersonal extends BlockMultiID implements IRareBlock {
  public BlockPersonal(int i) {
    super(i, Material.ORE);
    this.l();
    this.b(6000000.0F);
    this.a(i);
    ModLoader.registerBlock(this, ItemPersonalBlock.class);
    Ic2Items.personalSafe = new ItemStack(this, 1, 0);
    Ic2Items.tradeOMat = new ItemStack(this, 1, 1);
  }

  public String getTextureFile() {
    return "/ic2/sprites/block_personal.png";
  }

  public int getDropType(int i, Random random, int j) {
    return this.id;
  }

  protected int getDropData(int i) {
    return i;
  }

  public ArrayList getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
    if (Platform.isSimulating() && Platform.isRendering()) {
      return super.getBlockDropped(world, x, y, z, metadata, fortune);
    }
    else {
      ArrayList ret = new ArrayList();
      ret.add(new ItemStack(this.id, 1, metadata));
      return ret;
    }
  }

  public TileEntityBlock getBlockEntity(int i) {
    switch (i) {
      case 0:
        return new TileEntityPersonalChest();
      case 1:
        return new TileEntityTradeOMat();
      default:
        return new TileEntityBlock();
    }
  }

  public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
    if (entityhuman.isSneaking()) {
      return false;
    }
    else {
      int l = world.getData(i, j, k);
      TileEntity tileentity = world.getTileEntity(i, j, k);
      return (l == 1 || !(tileentity instanceof IPersonalBlock) || ((IPersonalBlock) tileentity).canAccess(entityhuman)) && super.interact(world, i, j, k, entityhuman);
    }
  }

  public int rarity(ItemStack itemstack) {
    return itemstack.getData() != 0 ? 0 : 1;
  }
}
