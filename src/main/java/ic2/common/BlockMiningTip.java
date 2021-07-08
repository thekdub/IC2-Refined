package ic2.common;

import ic2.platform.ItemBlockCommon;
import net.minecraft.server.*;

import java.util.Random;

public class BlockMiningTip extends BlockTex {
  public BlockMiningTip(int i, int j) {
    super(i, j, Material.ORE);
    this.c(6.0F);
    this.b(10.0F);
    this.a("blockMiningTip");
    this.addToCreative = false;
    ModLoader.registerBlock(this, ItemBlockCommon.class);
    Ic2Items.miningPipeTip = new ItemStack(this);
  }
  
  public boolean canPlace(World world, int i, int j, int k) {
    return false;
  }
  
  public int getDropType(int i, Random random, int j) {
    return Ic2Items.miningPipe.id;
  }
}
