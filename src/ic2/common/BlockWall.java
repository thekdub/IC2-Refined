package ic2.common;

import ic2.api.IPaintableBlock;
import ic2.platform.ItemBlockCommon;
import ic2.platform.NetworkManager;
import net.minecraft.server.*;

import java.util.Random;

public class BlockWall extends BlockTex implements IPaintableBlock {
  public BlockWall(int i, int j) {
    super(i, j, Material.STONE);
    this.c(3.0F);
    this.b(30.0F);
    this.a("blockWall");
    this.a(h);
    this.addToCreative = false;
    ModLoader.registerBlock(this, ItemBlockCommon.class);
    Ic2Items.constructionFoamWall = new ItemStack(this);
  }

  public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l) {
    int i1 = iblockaccess.getData(i, j, k);
    return this.textureId + i1;
  }

  public int a(int i, int j) {
    return this.textureId + j;
  }

  public int a(Random random) {
    return 0;
  }

  public boolean colorBlock(World world, int i, int j, int k, int l) {
    if (l != world.getData(i, j, k)) {
      world.setRawData(i, j, k, l);
      NetworkManager.announceBlockUpdate(world, i, j, k);
      return true;
    }
    else {
      return false;
    }
  }

  protected ItemStack a_(int i) {
    return null;
  }
}
