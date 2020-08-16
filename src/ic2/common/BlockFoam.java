package ic2.common;

import ic2.platform.ItemBlockCommon;
import ic2.platform.NetworkManager;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.Random;

public class BlockFoam extends BlockTex {
  public BlockFoam(int i, int j) {
    super(i, j, Material.CLOTH);
    this.a(true);
    this.c(0.01F);
    this.b(10.0F);
    this.a("blockFoam");
    this.a(k);
    ModLoader.registerBlock(this, ItemBlockCommon.class);
    Ic2Items.constructionFoam = new ItemStack(this);
  }
  
  public int d() {
    return 500;
  }
  
  public int a(Random random) {
    return 0;
  }
  
  public boolean a() {
    return false;
  }
  
  public boolean isBlockNormalCube(World world, int i, int j, int k) {
    return true;
  }
  
  public AxisAlignedBB e(World world, int i, int j, int k) {
    return null;
  }
  
  public boolean isBlockSolidOnSide(World world, int i, int j, int k, int l) {
    return false;
  }
  
  public void a(World world, int i, int j, int k, Random random) {
    if (Platform.isSimulating()) {
      if (world.getLightLevel(i, j, k) * 6 >= world.random.nextInt(1000)) {
        world.setTypeIdAndData(i, j, k, Ic2Items.constructionFoamWall.id, 7);
        NetworkManager.announceBlockUpdate(world, i, j, k);
      }
      else {
        world.c(i, j, k, this.id, this.d());
      }
  
    }
  }
  
  public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
    ItemStack itemstack = entityhuman.U();
    if (itemstack != null && itemstack.id == Block.SAND.id) {
      world.setTypeIdAndData(i, j, k, Ic2Items.constructionFoamWall.id, 7);
      NetworkManager.announceBlockUpdate(world, i, j, k);
      --itemstack.count;
      if (itemstack.count <= 0) {
        entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
      }
  
      return true;
    }
    else {
      return false;
    }
  }
  
  public boolean canPlace(World world, int i, int j, int k) {
    int l = world.getTypeId(i, j, k);
    return l == 0 || l == Block.FIRE.id || world.getMaterial(i, j, k).isLiquid();
  }
}
