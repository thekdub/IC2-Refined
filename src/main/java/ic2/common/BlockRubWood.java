package ic2.common;

import ic2.platform.ItemBlockCommon;
import ic2.platform.NetworkManager;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.Random;

public class BlockRubWood extends BlockTex {
  public BlockRubWood(int i) {
    super(i, 44, Material.WOOD);
    this.a(true);
    this.c(1.0F);
    this.a(e);
    this.a("blockRubWood");
    ModLoader.registerBlock(this, ItemBlockCommon.class);
    Ic2Items.rubberWood = new ItemStack(this);
  }
  
  public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l) {
    int i1 = iblockaccess.getData(i, j, k);
    if (l < 2) {
      return 47;
    }
    else if (l == i1 % 6) {
      return i1 <= 5 ? 45 : 46;
    }
    else {
      return 44;
    }
  }
  
  public int a(int i, int j) {
    return i >= 2 ? 44 : 47;
  }
  
  public void dropNaturally(World world, int i, int j, int k, int l, float f, int i1) {
    if (Platform.isSimulating()) {
      int j1 = this.a(world.random);
  
      for (int k1 = 0; k1 < j1; ++k1) {
        if (world.random.nextFloat() <= f) {
          int l1 = this.getDropType(l, world.random, 0);
          if (l1 > 0) {
            this.a(world, i, j, k, new ItemStack(l1, 1, 0));
          }
  
          if (l != 0 && world.random.nextInt(6) == 0) {
            this.a(world, i, j, k, new ItemStack(Ic2Items.resin.getItem()));
          }
        }
      }
  
    }
  }
  
  public void remove(World world, int i, int j, int k) {
    byte byte0 = 4;
    int l = byte0 + 1;
    if (world.a(i - l, j - l, k - l, i + l, j + l, k + l)) {
      for (int i1 = -byte0; i1 <= byte0; ++i1) {
        for (int j1 = -byte0; j1 <= byte0; ++j1) {
          for (int k1 = -byte0; k1 <= byte0; ++k1) {
            int l1 = world.getTypeId(i + i1, j + j1, k + k1);
            if (l1 == Ic2Items.rubberLeaves.id) {
              int i2 = world.getData(i + i1, j + j1, k + k1);
              if ((i2 & 8) == 0) {
                world.setRawData(i + i1, j + j1, k + k1, i2 | 8);
              }
            }
          }
        }
      }
    }
    
  }
  
  public void a(World world, int i, int j, int k, Random random) {
    int l = world.getData(i, j, k);
    if (l >= 6) {
      if (random.nextInt(200) == 0) {
        world.setRawData(i, j, k, l % 6);
        NetworkManager.announceBlockUpdate(world, i, j, k);
      }
      else {
        world.c(i, j, k, this.id, this.d());
      }
  
    }
  }
  
  public int d() {
    return 100;
  }
  
  public int g() {
    return 2;
  }
}
