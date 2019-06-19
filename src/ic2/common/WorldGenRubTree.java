package ic2.common;

import ic2.platform.NetworkManager;
import net.minecraft.server.Block;
import net.minecraft.server.World;
import net.minecraft.server.WorldGenerator;
import net.minecraft.server.mod_IC2;

import java.util.Random;

public class WorldGenRubTree extends WorldGenerator {
  public static final int maxHeight = 8;

  public boolean a(World world, Random random, int i, int j, int k) {
    while (j > 0) {
      int l;
      for (l = mod_IC2.getWorldHeight(world) - 1; world.getTypeId(i, l - 1, k) == 0 && l > 0; --l) {
      }

      if (!this.grow(world, i, l, k, random)) {
        j -= 3;
      }

      i += random.nextInt(15) - 7;
      k += random.nextInt(15) - 7;
      --j;
    }

    return true;
  }

  public boolean grow(World world, int i, int j, int k, Random random) {
    if (world != null && Ic2Items.rubberWood != null) {
      int l = 25;
      int i1 = this.getGrowHeight(world, i, j, k);
      if (i1 < 2) {
        return false;
      }
      else {
        int j1 = i1 / 2;
        i1 -= i1 / 2;
        j1 += random.nextInt(i1 + 1);

        int k1;
        for (k1 = 0; k1 < j1; ++k1) {
          world.setTypeId(i, j + k1, k, Ic2Items.rubberWood.id);
          if (random.nextInt(100) <= l) {
            l -= 10;
            world.setRawData(i, j + k1, k, random.nextInt(4) + 2);
          }
          else {
            world.setRawData(i, j + k1, k, 1);
          }

          NetworkManager.announceBlockUpdate(world, i, j + k1, k);
          if (j1 < 4 || j1 < 7 && k1 > 1 || k1 > 2) {
            for (int i2 = i - 2; i2 <= i + 2; ++i2) {
              for (int j2 = k - 2; j2 <= k + 2; ++j2) {
                int k2 = k1 + 4 - j1;
                if (k2 < 1) {
                  k2 = 1;
                }

                boolean flag = i2 > i - 2 && i2 < i + 2 && j2 > k - 2 && j2 < k + 2 || i2 > i - 2 && i2 < i + 2 && random.nextInt(k2) == 0 || j2 > k - 2 && j2 < k + 2 && random.nextInt(k2) == 0;
                if (flag && world.getTypeId(i2, j + k1, j2) == 0) {
                  world.setTypeId(i2, j + k1, j2, Ic2Items.rubberLeaves.id);
                }
              }
            }
          }
        }

        for (k1 = 0; k1 <= j1 / 4 + random.nextInt(2); ++k1) {
          if (world.getTypeId(i, j + j1 + k1, k) == 0) {
            world.setTypeId(i, j + j1 + k1, k, Ic2Items.rubberLeaves.id);
          }
        }

        return true;
      }
    }
    else {
      System.out.println("[ERROR] Had a null that shouldn't have been. RubberTree did not spawn! w=" + world + " r=" + Ic2Items.rubberWood);
      return false;
    }
  }

  public int getGrowHeight(World world, int i, int j, int k) {
    if ((world.getTypeId(i, j - 1, k) == Block.GRASS.id || world.getTypeId(i, j - 1, k) == Block.DIRT.id) && (world.getTypeId(i, j, k) == 0 || world.getTypeId(i, j, k) == Ic2Items.rubberSapling.id)) {
      int l;
      for (l = 1; world.getTypeId(i, j + 1, k) == 0 && l < 8; ++j) {
        ++l;
      }

      return l;
    }
    else {
      return 0;
    }
  }
}
