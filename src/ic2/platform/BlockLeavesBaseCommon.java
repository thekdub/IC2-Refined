package ic2.platform;

import net.minecraft.server.BlockTransparant;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.Material;

public class BlockLeavesBaseCommon extends BlockTransparant {
  public BlockLeavesBaseCommon(int i, int j, Material material, boolean flag) {
    super(i, j, material, flag);
  }

  public int getRenderColor(int i) {
    return 0;
  }

  public int colorMultiplier(IBlockAccess iblockaccess, int i, int j, int k) {
    return 0;
  }
}
