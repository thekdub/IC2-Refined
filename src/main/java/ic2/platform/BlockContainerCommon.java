package ic2.platform;

import net.minecraft.server.*;

import java.util.Random;

public abstract class BlockContainerCommon extends BlockContainer {
  public BlockContainerCommon(int i, Material material) {
    super(i, material);
  }
  
  public BlockContainerCommon(int i, int j, Material material) {
    super(i, j, material);
  }
  
  public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l) {
    return 0;
  }
  
  public void randomDisplayTick(World world, int i, int j, int k, Random random) {
  }
  
  public int getRenderBlockPass() {
    return 0;
  }
  
  public float getBlockBrightness(IBlockAccess iblockaccess, int i, int j, int k) {
    return 0.0F;
  }
}
