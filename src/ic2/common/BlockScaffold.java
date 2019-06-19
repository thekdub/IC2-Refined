package ic2.common;

import ic2.platform.ItemBlockCommon;
import ic2.platform.Keyboard;
import ic2.platform.NetworkManager;
import net.minecraft.server.*;

import java.util.ArrayList;
import java.util.Random;

public class BlockScaffold extends BlockTex {
  public static int standardStrength = 2;
  public static int standardIronStrength = 5;
  public static int reinforcedStrength = 5;
  public static int reinforcedIronStrength = 12;
  public static int tickDelay = 1;
  public Material material;

  public BlockScaffold(int i, Material material1) {
    super(i, material1 != Material.ORE ? 116 : 132, material1);
    this.material = material1;
    if (material1 == Material.WOOD) {
      this.c(0.5F);
      this.b(0.2F);
      this.a("blockScaffold");
      this.a(e);
      ModLoader.registerBlock(this, ItemBlockCommon.class);
      Ic2Items.scaffold = new ItemStack(this);
    }

    if (material1 == Material.ORE) {
      this.c(0.8F);
      this.b(10.0F);
      this.a("blockIronScaffold");
      this.a(i);
      ModLoader.registerBlock(this, ItemBlockCommon.class);
      Ic2Items.ironScaffold = new ItemStack(this);
    }

  }

  public int getStandardStrength() {
    return this.material == Material.ORE ? standardIronStrength : standardStrength;
  }

  public int getReinforcedStrength() {
    return this.material == Material.ORE ? reinforcedIronStrength : reinforcedStrength;
  }

  public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l) {
    int i1 = iblockaccess.getData(i, j, k);
    if (l < 2) {
      return this.textureId + 1;
    }
    else {
      return i1 == this.getReinforcedStrength() ? this.textureId + 2 : this.textureId;
    }
  }

  public int a(int i, int j) {
    return i < 2 ? this.textureId + 1 : this.textureId;
  }

  public boolean a() {
    return false;
  }

  public boolean isBlockNormalCube(World world, int i, int j, int k) {
    return false;
  }

  public AxisAlignedBB e(World world, int i, int j, int k) {
    float f = 0.0625F;
    return AxisAlignedBB.b((double) ((float) i + f), (double) ((float) j), (double) ((float) k + f), (double) ((float) i + 1.0F - f), (double) ((float) j + 1.0F), (double) ((float) k + 1.0F - f));
  }

  public boolean isBlockSolidOnSide(World world, int i, int j, int k, int l) {
    return l < 2;
  }

  public void a(World world, int i, int j, int k, Entity entity) {
    if (entity instanceof EntityHuman) {
      EntityHuman entityhuman = (EntityHuman) entity;
      mod_IC2.setFallDistanceOfEntity(entityhuman, 0.0F);
      if (entityhuman.motY < -0.15D) {
        entityhuman.motY = -0.15D;
      }

      if (Keyboard.isForwardKeyDown(entityhuman) && entityhuman.motY < 0.2D) {
        entityhuman.motY = 0.2D;
      }
    }

  }

  public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
    return AxisAlignedBB.b((double) i, (double) j, (double) k, (double) (i + 1), (double) (j + 1), (double) (k + 1));
  }

  public ArrayList getBlockDropped(World world, int i, int j, int k, int l, int i1) {
    ArrayList arraylist = new ArrayList();
    arraylist.add(new ItemStack(this, 1));
    if (l == this.getReinforcedStrength()) {
      if (this.material == Material.ORE) {
        arraylist.add(new ItemStack(Ic2Items.ironFence.getItem(), 1));
      }

      if (this.material == Material.WOOD) {
        arraylist.add(new ItemStack(Item.STICK, 2));
      }
    }

    return arraylist;
  }

  public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
    if (entityhuman.isSneaking()) {
      return false;
    }
    else {
      ItemStack itemstack = entityhuman.inventory.getItemInHand();
      if (itemstack != null && (this.material != Material.WOOD || itemstack.id == Item.STICK.id && itemstack.count >= 2) && (this.material != Material.ORE || itemstack.id == Ic2Items.ironFence.id)) {
        if (world.getData(i, j, k) != this.getReinforcedStrength() && this.isPillar(world, i, j, k)) {
          if (this.material == Material.WOOD) {
            itemstack.count -= 2;
          }
          else {
            --itemstack.count;
          }

          if (entityhuman.U().count <= 0) {
            entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
          }

          world.setData(i, j, k, this.getReinforcedStrength());
          world.b(i, j, k, i, j, k);
          NetworkManager.announceBlockUpdate(world, i, j, k);
          return true;
        }
        else {
          return false;
        }
      }
      else {
        return false;
      }
    }
  }

  public void attack(World world, int i, int j, int k, EntityHuman entityhuman) {
    if (entityhuman.U() != null && entityhuman.U().id == this.id) {
      while (world.getTypeId(i, j, k) == this.id) {
        ++j;
      }

      if (this.canPlace(world, i, j, k)) {
        world.setTypeId(i, j, k, this.id);
        this.postPlace(world, i, j, k, 0);
        --entityhuman.U().count;
        if (entityhuman.U().count <= 0) {
          entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
        }
      }
    }

  }

  public boolean canPlace(World world, int i, int j, int k) {
    return this.getStrengthFrom(world, i, j, k) > -1 && super.canPlace(world, i, j, k);
  }

  public boolean isPillar(World world, int i, int j, int k) {
    while (world.getTypeId(i, j, k) == this.id) {
      --j;
    }

    return world.e(i, j, k);
  }

  public void doPhysics(World world, int i, int j, int k, int l) {
    this.updateSupportStatus(world, i, j, k);
  }

  public void postPlace(World world, int i, int j, int k, int l) {
    this.a(world, i, j, k, (Random) null);
  }

  public void a(World world, int i, int j, int k, Random random) {
    int l = world.getData(i, j, k);
    if (l >= this.getReinforcedStrength()) {
      if (!this.isPillar(world, i, j, k)) {
        l = this.getStrengthFrom(world, i, j, k);
        ItemStack itemstack = new ItemStack(Item.STICK, 2);
        if (this.material == Material.ORE) {
          itemstack = new ItemStack(Ic2Items.ironFence.getItem());
        }

        this.a(world, i, j, k, itemstack);
      }
    }
    else {
      l = this.getStrengthFrom(world, i, j, k);
    }

    if (l <= -1) {
      world.setTypeId(i, j, k, 0);
      this.a(world, i, j, k, new ItemStack(this));
    }
    else if (l != world.getData(i, j, k)) {
      world.setData(i, j, k, l);
      world.b(i, j, k, i, j, k);
    }

  }

  public int getStrengthFrom(World world, int i, int j, int k) {
    int l = 0;
    if (this.isPillar(world, i, j - 1, k)) {
      l = this.getStandardStrength() + 1;
    }

    l = this.compareStrengthTo(world, i, j - 1, k, l);
    l = this.compareStrengthTo(world, i + 1, j, k, l);
    l = this.compareStrengthTo(world, i - 1, j, k, l);
    l = this.compareStrengthTo(world, i, j, k + 1, l);
    l = this.compareStrengthTo(world, i, j, k - 1, l);
    return l - 1;
  }

  public int compareStrengthTo(World world, int i, int j, int k, int l) {
    int i1 = 0;
    if (world.getTypeId(i, j, k) == this.id) {
      i1 = world.getData(i, j, k);
      if (i1 > this.getReinforcedStrength()) {
        i1 = this.getReinforcedStrength();
      }
    }

    return i1 > l ? i1 : l;
  }

  public void updateSupportStatus(World world, int i, int j, int k) {
    world.c(i, j, k, this.id, tickDelay);
  }
}
