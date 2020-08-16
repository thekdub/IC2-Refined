package ic2.common;

import net.minecraft.server.*;

import java.util.Random;

public class BlockMachine2 extends BlockMultiID implements IRareBlock {
  public BlockMachine2(int i) {
    super(i, Material.ORE);
    this.c(2.0F);
    this.a(i);
    ModLoader.registerBlock(this, ItemMachine2.class);
    Ic2Items.teleporter = new ItemStack(this, 1, 0);
    Ic2Items.teslaCoil = new ItemStack(this, 1, 1);
    Ic2Items.cropmatron = new ItemStack(this, 1, 2);
  }
  
  public String getTextureFile() {
    return "/ic2/sprites/block_machine2.png";
  }
  
  public int getDropType(int i, Random random, int j) {
    switch (i) {
      case 0:
        return Ic2Items.advancedMachine.id;
      default:
        return Ic2Items.machine.id;
    }
  }
  
  protected int getDropData(int i) {
    switch (i) {
      case 0:
        return Ic2Items.advancedMachine.getData();
      default:
        return Ic2Items.machine.getData();
    }
  }
  
  public TileEntityBlock getBlockEntity(int i) {
    switch (i) {
      case 0:
        return new TileEntityTeleporter();
      case 1:
        return new TileEntityTesla();
      case 2:
        return new TileEntityCropmatron();
      default:
        return new TileEntityBlock();
    }
  }
  
  public void randomDisplayTick(World world, int i, int j, int k, Random random) {
    world.getData(i, j, k);
  }
  
  public int rarity(ItemStack itemstack) {
    return itemstack.getData() != 0 ? 0 : 2;
  }
}
