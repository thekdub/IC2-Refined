package ic2.platform;

import net.minecraft.server.*;

public class BlockCommon extends Block {
  public BlockCommon(int i, Material material) {
    super(i, material);
  }
  
  public BlockCommon(int i, int j, Material material) {
    super(i, j, material);
  }
  
  public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
    return null;
  }
  
  public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l) {
    return 0;
  }
}
