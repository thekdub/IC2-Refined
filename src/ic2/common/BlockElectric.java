package ic2.common;

import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.Random;

public class BlockElectric extends BlockMultiID implements IRareBlock {
  public BlockElectric(int i) {
    super(i, Material.ORE);
    this.c(1.5F);
    this.a(i);
    ModLoader.registerBlock(this, ItemElectricBlock.class);
    Ic2Items.batBox = new ItemStack(this, 1, 0);
    Ic2Items.mfeUnit = new ItemStack(this, 1, 1);
    Ic2Items.mfsUnit = new ItemStack(this, 1, 2);
    Ic2Items.lvTransformer = new ItemStack(this, 1, 3);
    Ic2Items.mvTransformer = new ItemStack(this, 1, 4);
    Ic2Items.hvTransformer = new ItemStack(this, 1, 5);
  }
  
  public String getTextureFile() {
    return "/ic2/sprites/block_electric.png";
  }
  
  public int getDropType(int i, Random random, int j) {
    switch (i) {
      case 0:
        return this.id;
      case 1:
      case 2:
      default:
        return Ic2Items.machine.id;
      case 3:
        return this.id;
    }
  }
  
  protected int getDropData(int i) {
    switch (i) {
      case 0:
        return i;
      case 1:
      case 2:
      default:
        return Ic2Items.machine.getData();
      case 3:
        return i;
    }
  }
  
  public int a(Random random) {
    return 1;
  }
  
  public boolean a(IBlockAccess iblockaccess, int i, int j, int k, int l) {
    TileEntity tileentity = iblockaccess.getTileEntity(i, j, k);
    if (tileentity instanceof TileEntityElectricBlock) {
      TileEntityElectricBlock tileentityelectricblock = (TileEntityElectricBlock) tileentity;
      return tileentityelectricblock.isEmittingRedstone();
    }
    else {
      return false;
    }
  }
  
  public boolean isBlockNormalCube(World world, int i, int j, int k) {
    return false;
  }
  
  public boolean isBlockSolidOnSide(World world, int i, int j, int k, int l) {
    return true;
  }
  
  public TileEntityBlock getBlockEntity(int i) {
    switch (i) {
      case 0:
        return new TileEntityElectricBatBox();
      case 1:
        return new TileEntityElectricMFE();
      case 2:
        return new TileEntityElectricMFSU();
      case 3:
        return new TileEntityTransformerLV();
      case 4:
        return new TileEntityTransformerMV();
      case 5:
        return new TileEntityTransformerHV();
      default:
        return null;
    }
  }
  
  public void postPlace(World world, int i, int j, int k, EntityLiving entityliving) {
    if (Platform.isSimulating()) {
      TileEntityBlock tileentityblock = (TileEntityBlock) world.getTileEntity(i, j, k);
      if (entityliving == null) {
        tileentityblock.setFacing((short) 1);
      }
      else {
        int l = MathHelper.floor((double) (entityliving.yaw * 4.0F / 360.0F) + 0.5D) & 3;
        int i1 = Math.round(entityliving.pitch);
        if (i1 >= 65) {
          tileentityblock.setFacing((short) 1);
        }
        else if (i1 <= -65) {
          tileentityblock.setFacing((short) 0);
        }
        else {
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
  }
  
  public int rarity(ItemStack itemstack) {
    return itemstack.getData() != 2 && itemstack.getData() != 5 ? 0 : 1;
  }
}
