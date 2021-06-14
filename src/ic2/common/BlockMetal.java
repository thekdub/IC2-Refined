package ic2.common;

import forge.ITextureProvider;
import net.minecraft.server.*;

import java.util.ArrayList;

public class BlockMetal extends Block implements ITextureProvider {
  public BlockMetal(int i) {
    super(i, Material.ORE);
    this.c(4.0F);
    this.a(Block.i);
    ModLoader.registerBlock(this, ItemBlockMetal.class);
    Ic2Items.bronzeBlock = new ItemStack(this, 1, 2);
    Ic2Items.copperBlock = new ItemStack(this, 1, 0);
    Ic2Items.tinBlock = new ItemStack(this, 1, 1);
    Ic2Items.uraniumBlock = new ItemStack(this, 1, 3);
  }
  
  protected int getDropData(int i) {
    return i;
  }
  
  public int a(int i, int j) {
    switch (j) {
      case 0:
        return 93;
      case 1:
        return 94;
      case 2:
        return 78;
      case 3:
        return i >= 2 ? 95 : 79;
      default:
        return 0;
    }
  }
  
  public String getTextureFile() {
    return "/ic2/sprites/block_0.png";
  }
  
  public void addCreativeItems(ArrayList arraylist) {
    for (int i = 0; i < 16; ++i) {
      ItemStack itemstack = new ItemStack(this, 1, i);
      if (Item.byId[this.id].a(itemstack) != null) {
        arraylist.add(itemstack);
      }
    }
    
  }
}
