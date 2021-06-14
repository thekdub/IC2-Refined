package ic2.common;

import forge.ITextureProvider;
import ic2.platform.BlockContainerCommon;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class BlockMultiID extends BlockContainerCommon implements ITextureProvider {
  public static final int[][] sideAndFacingToSpriteOffset =
      new int[][]{{3, 2, 0, 0, 0, 0}, {2, 3, 1, 1, 1, 1}, {1, 1, 3, 2, 5, 4}, {0, 0, 2, 3, 4, 5}, {4, 5, 4, 5, 3, 2},
          {5, 4, 5, 4, 2, 3}};
  
  protected BlockMultiID(int i, Material material) {
    super(i, material);
  }
  
  public static boolean isActive(IBlockAccess iblockaccess, int i, int j, int k) {
    TileEntity tileentity = iblockaccess.getTileEntity(i, j, k);
    return tileentity instanceof TileEntityBlock && ((TileEntityBlock) tileentity).getActive();
  }
  
  public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l) {
    TileEntity tileentity = iblockaccess.getTileEntity(i, j, k);
    short word0 = tileentity instanceof TileEntityBlock ? ((TileEntityBlock) tileentity).getFacing() : 0;
    int i1 = iblockaccess.getData(i, j, k);
    return isActive(iblockaccess, i, j, k) ? i1 + (sideAndFacingToSpriteOffset[l][word0] + 6) * 16 :
        i1 + sideAndFacingToSpriteOffset[l][word0] * 16;
  }
  
  public int a(int i, int j) {
    return j + sideAndFacingToSpriteOffset[i][3] * 16;
  }
  
  public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
    if (entityhuman.isSneaking()) {
      return false;
    }
    else {
      TileEntity tileentity = world.getTileEntity(i, j, k);
      if (tileentity instanceof IHasGui) {
        return !Platform.isSimulating() || Platform.launchGui(entityhuman, (IHasGui) tileentity);
      }
      else {
        return false;
      }
    }
  }
  
  public ArrayList getBlockDropped(World world, int i, int j, int k, int l, int i1) {
    ArrayList arraylist = super.getBlockDropped(world, i, j, k, l, i1);
    TileEntity tileentity = world.getTileEntity(i, j, k);
    if (tileentity instanceof IInventory) {
      IInventory iinventory = (IInventory) tileentity;
  
      for (int j1 = 0; j1 < iinventory.getSize(); ++j1) {
        ItemStack itemstack = iinventory.getItem(j1);
        if (itemstack != null) {
          arraylist.add(itemstack);
          iinventory.setItem(j1, null);
        }
      }
    }
    
    return arraylist;
  }
  
  public TileEntityBlock getBlockEntity() {
    return null;
  }
  
  public abstract TileEntityBlock getBlockEntity(int var1);
  
  public void onPlace(World world, int i, int j, int k) {
  }
  
  public void remove(World world, int i, int j, int k) {
    boolean flag = true;
    Iterator iterator = this.getBlockDropped(world, i, j, k, world.getData(i, j, k), 0).iterator();
    
    while (iterator.hasNext()) {
      ItemStack itemstack = (ItemStack) iterator.next();
      if (flag) {
        flag = false;
      }
      else {
        StackUtil.dropAsEntity(world, i, j, k, itemstack);
      }
    }
    
    super.remove(world, i, j, k);
  }
  
  public void postPlace(World world, int i, int j, int k, EntityLiving entityliving) {
    if (Platform.isSimulating()) {
      TileEntityBlock tileentityblock = (TileEntityBlock) world.getTileEntity(i, j, k);
      if (entityliving == null) {
        tileentityblock.setFacing((short) 2);
      }
      else {
        int l = MathHelper.floor((double) (entityliving.yaw * 4.0F / 360.0F) + 0.5D) & 3;
        switch (l) {
          case 0:
            tileentityblock.setFacing((short) 2);
            break;
          case 1:
            tileentityblock.setFacing((short) 5);
            break;
          case 2:
            tileentityblock.setFacing((short) 3);
            break;
          case 3:
            tileentityblock.setFacing((short) 4);
        }
      }
  
    }
  }
  
  public void addCreativeItems(ArrayList arraylist) {
    for (int i = 0; i < 16; ++i) {
      ItemStack itemstack = new ItemStack(this, 1, i);
      if (Item.byId[this.id].a(itemstack) != null) {
        arraylist.add(itemstack);
      }
    }
    
  }
  
  public TileEntity a_() {
    return this.getBlockEntity();
  }
}
