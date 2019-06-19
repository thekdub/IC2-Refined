package ic2.common;

import forge.ITextureProvider;
import ic2.platform.BlockCommon;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;

import java.util.ArrayList;
import java.util.Random;

public class BlockTex extends BlockCommon implements ITextureProvider {
  protected boolean addToCreative = true;

  public BlockTex(int i, int j, Material material) {
    super(i, j, material);
  }

  public String getTextureFile() {
    return "/ic2/sprites/block_0.png";
  }

  public int getDropType(int i, Random random, int j) {
    return Ic2Items.uraniumOre != null && this.id == Ic2Items.uraniumOre.id ? Ic2Items.uraniumDrop.id : this.id;
  }

  public void addCreativeItems(ArrayList arraylist) {
    if (this.addToCreative) {
      arraylist.add(new ItemStack(this));
    }

  }
}
