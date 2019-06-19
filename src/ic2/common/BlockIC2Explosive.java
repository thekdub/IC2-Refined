package ic2.common;

import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.Random;

public abstract class BlockIC2Explosive extends BlockTex {
  public boolean canExplodeByHand = false;

  public BlockIC2Explosive(int i, int j, boolean flag) {
    super(i, j, Material.TNT);
    this.canExplodeByHand = flag;
  }

  public int a(int i) {
    if (i == 0) {
      return this.textureId;
    }
    else {
      return i == 1 ? this.textureId + 1 : this.textureId + 2;
    }
  }

  public void onPlace(World world, int i, int j, int k) {
    super.onPlace(world, i, j, k);
    if (world.isBlockIndirectlyPowered(i, j, k)) {
      this.postBreak(world, i, j, k, 1);
      world.setTypeId(i, j, k, 0);
    }

  }

  public void doPhysics(World world, int i, int j, int k, int l) {
    if (l > 0 && Block.byId[l].isPowerSource() && world.isBlockIndirectlyPowered(i, j, k)) {
      this.postBreak(world, i, j, k, 1);
      world.setTypeId(i, j, k, 0);
    }

  }

  public int a(Random random) {
    return 0;
  }

  public void wasExploded(World world, int i, int j, int k) {
    EntityIC2Explosive entityic2explosive = this.getExplosionEntity(world, (float) i + 0.5F, (float) j + 0.5F, (float) k + 0.5F);
    entityic2explosive.fuse = world.random.nextInt(entityic2explosive.fuse / 4) + entityic2explosive.fuse / 8;
    world.addEntity(entityic2explosive);
  }

  public void postBreak(World world, int i, int j, int k, int l) {
    if (Platform.isSimulating()) {
      if ((l & 1) == 0 && !this.canExplodeByHand) {
        this.a(world, i, j, k, new ItemStack(this.id, 1, 0));
      }
      else {
        EntityIC2Explosive entityic2explosive = this.getExplosionEntity(world, (float) i + 0.5F, (float) j + 0.5F, (float) k + 0.5F);
        world.addEntity(entityic2explosive);
        world.makeSound(entityic2explosive, "random.fuse", 1.0F, 1.0F);
      }

    }
  }

  public void attack(World world, int i, int j, int k, EntityHuman entityhuman) {
    if (entityhuman.U() != null && entityhuman.U().id == Item.FLINT_AND_STEEL.id) {
      world.setRawData(i, j, k, 1);
    }

    super.attack(world, i, j, k, entityhuman);
  }

  public abstract EntityIC2Explosive getExplosionEntity(World var1, float var2, float var3, float var4);
}
