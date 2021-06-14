package ic2.common;

import ic2.platform.ItemBlockCommon;
import net.minecraft.server.*;

public class BlockMiningPipe extends BlockTex {
  public BlockMiningPipe(int i, int j) {
    super(i, j, Material.ORE);
    this.c(6.0F);
    this.b(10.0F);
    this.a("blockMiningPipe");
    ModLoader.registerBlock(this, ItemBlockCommon.class);
    Ic2Items.miningPipe = new ItemStack(this);
  }
  
  public boolean canPlace(World world, int i, int j, int k) {
    return false;
  }
  
  public boolean a() {
    return false;
  }
  
  public boolean isBlockNormalCube(World world, int i, int j, int k) {
    return false;
  }
  
  public boolean b() {
    return false;
  }
  
  public int c() {
    return mod_IC2.miningPipeRenderId;
  }
  
  public AxisAlignedBB e(World world, int i, int j, int k) {
    return AxisAlignedBB.b((float) i + 0.375F, (float) j, (float) k + 0.375F,
        (float) i + 0.625F, (float) j + 1.0F, (float) k + 0.625F);
  }
  
  public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
    return AxisAlignedBB.b((float) i + 0.375F, (float) j, (float) k + 0.375F,
        (float) i + 0.625F, (float) j + 1.0F, (float) k + 0.625F);
  }
}
