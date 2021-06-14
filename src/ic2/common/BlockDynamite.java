package ic2.common;

import ic2.platform.ItemBlockCommon;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.Random;

public class BlockDynamite extends BlockTex {
  public BlockDynamite(int i, int j) {
    super(i, j, Material.TNT);
    this.a(true);
    this.addToCreative = false;
    ModLoader.registerBlock(this, ItemBlockCommon.class);
  }
  
  public AxisAlignedBB e(World world, int i, int j, int k) {
    return null;
  }
  
  public boolean a() {
    return false;
  }
  
  public boolean b() {
    return false;
  }
  
  public int c() {
    return 2;
  }
  
  public boolean canPlace(World world, int i, int j, int k) {
    if (world.e(i - 1, j, k)) {
      return true;
    }
    else if (world.e(i + 1, j, k)) {
      return true;
    }
    else if (world.e(i, j, k - 1)) {
      return true;
    }
    else {
      return world.e(i, j, k + 1) || world.e(i, j - 1, k);
    }
  }
  
  public void postPlace(World world, int i, int j, int k, int l) {
    int i1 = world.getData(i, j, k);
    if (l == 1 && world.e(i, j - 1, k)) {
      i1 = 5;
    }
    
    if (l == 2 && world.e(i, j, k + 1)) {
      i1 = 4;
    }
    
    if (l == 3 && world.e(i, j, k - 1)) {
      i1 = 3;
    }
    
    if (l == 4 && world.e(i + 1, j, k)) {
      i1 = 2;
    }
    
    if (l == 5 && world.e(i - 1, j, k)) {
      i1 = 1;
    }
    
    world.setData(i, j, k, i1);
  }
  
  public void a(World world, int i, int j, int k, Random random) {
    super.a(world, i, j, k, random);
    if (world.getData(i, j, k) == 0) {
      this.onPlace(world, i, j, k);
    }
    
  }
  
  public void onPlace(World world, int i, int j, int k) {
    if (world.isBlockIndirectlyPowered(i, j, k)) {
      this.postBreak(world, i, j, k, 1);
      world.setTypeId(i, j, k, 0);
    }
    else {
      if (world.e(i, j - 1, k)) {
        world.setData(i, j, k, 5);
      }
      else if (world.e(i - 1, j, k)) {
        world.setData(i, j, k, 1);
      }
      else if (world.e(i + 1, j, k)) {
        world.setData(i, j, k, 2);
      }
      else if (world.e(i, j, k - 1)) {
        world.setData(i, j, k, 3);
      }
      else if (world.e(i, j, k + 1)) {
        world.setData(i, j, k, 4);
      }
  
      this.dropBlockIfCantStay(world, i, j, k);
    }
  }
  
  public int a(Random random) {
    return 0;
  }
  
  public int getDropType(int i, Random random, int j) {
    return Ic2Items.dynamite.id;
  }
  
  public void wasExploded(World world, int i, int j, int k) {
    EntityDynamite entitydynamite = new EntityDynamite(world, (float) i + 0.5F, (float) j + 0.5F,
        (float) k + 0.5F);
    entitydynamite.fuse = 5;
    world.addEntity(entitydynamite);
  }
  
  public void postBreak(World world, int i, int j, int k, int l) {
    if (Platform.isSimulating()) {
      EntityDynamite entitydynamite =
          new EntityDynamite(world, (float) i + 0.5F, (float) j + 0.5F,
              (float) k + 0.5F);
      entitydynamite.fuse = 40;
      world.addEntity(entitydynamite);
      world.makeSound(entitydynamite, "random.fuse", 1.0F, 1.0F);
    }
  }
  
  public void doPhysics(World world, int i, int j, int k, int l) {
    if (l > 0 && Block.byId[l].isPowerSource() && world.isBlockIndirectlyPowered(i, j, k)) {
      this.postBreak(world, i, j, k, 1);
      world.setTypeId(i, j, k, 0);
    }
    else {
      if (this.dropBlockIfCantStay(world, i, j, k)) {
        int i1 = world.getData(i, j, k);
        boolean flag = false;
        if (!world.e(i - 1, j, k) && i1 == 1) {
          flag = true;
        }
  
        if (!world.e(i + 1, j, k) && i1 == 2) {
          flag = true;
        }
  
        if (!world.e(i, j, k - 1) && i1 == 3) {
          flag = true;
        }
  
        if (!world.e(i, j, k + 1) && i1 == 4) {
          flag = true;
        }
  
        if (!world.e(i, j - 1, k) && i1 == 5) {
          flag = true;
        }
  
        if (flag) {
          this.b(world, i, j, k, world.getData(i, j, k), 0);
          world.setTypeId(i, j, k, 0);
        }
      }
  
    }
  }
  
  public boolean dropBlockIfCantStay(World world, int i, int j, int k) {
    if (!this.canPlace(world, i, j, k)) {
      this.wasExploded(world, i, j, k);
      world.setTypeId(i, j, k, 0);
      return false;
    }
    else {
      return true;
    }
  }
  
  public MovingObjectPosition a(World world, int i, int j, int k, Vec3D vec3d, Vec3D vec3d1) {
    int l = world.getData(i, j, k) & 7;
    float f = 0.15F;
    if (l == 1) {
      this.a(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
    }
    else if (l == 2) {
      this.a(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
    }
    else if (l == 3) {
      this.a(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
    }
    else if (l == 4) {
      this.a(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
    }
    else {
      float f1 = 0.1F;
      this.a(0.5F - f1, 0.0F, 0.5F - f1, 0.5F + f1, 0.6F, 0.5F + f1);
    }
    
    return super.a(world, i, j, k, vec3d, vec3d1);
  }
}
