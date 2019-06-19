package ic2.common;

import forge.ITextureProvider;
import ic2.platform.BlockContainerCommon;
import net.minecraft.server.*;

import java.util.Random;

public class BlockLuminatorOLD extends BlockContainerCommon implements ITextureProvider {
  public BlockLuminatorOLD(int i, int j) {
    super(i, j, Material.SHATTERABLE);
  }

  public int a(Random random) {
    return 0;
  }

  public int getRenderBlockPass() {
    return 0;
  }

  public boolean a() {
    return false;
  }

  public float getBlockBrightness(IBlockAccess iblockaccess, int i, int j, int k) {
    TileEntityLuminatorOLD tileentityluminatorold = (TileEntityLuminatorOLD) iblockaccess.getTileEntity(i, j, k);
    return tileentityluminatorold == null ? super.getBlockBrightness(iblockaccess, i, j, k) : tileentityluminatorold.getLightLevel();
  }

  public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
    TileEntityLuminatorOLD tileentityluminatorold = (TileEntityLuminatorOLD) world.getTileEntity(i, j, k);
    tileentityluminatorold.switchStrength();
    return true;
  }

  public TileEntity a_() {
    return new TileEntityLuminator();
  }

  public String getTextureFile() {
    return "/ic2/sprites/block_0.png";
  }
}
