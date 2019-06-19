package ic2.common;

import net.minecraft.server.*;

public class ItemCell extends ItemIC2 {
  public ItemCell(int i, int j) {
    super(i, j);
  }

  public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
    float f = 1.0F;
    float f1 = entityhuman.lastPitch + (entityhuman.pitch - entityhuman.lastPitch) * f;
    float f2 = entityhuman.lastYaw + (entityhuman.yaw - entityhuman.lastYaw) * f;
    double d = entityhuman.lastX + (entityhuman.locX - entityhuman.lastX) * (double) f;
    double d1 = entityhuman.lastY + (entityhuman.locY - entityhuman.lastY) * (double) f + 1.62D - (double) entityhuman.height;
    double d2 = entityhuman.lastZ + (entityhuman.locZ - entityhuman.lastZ) * (double) f;
    Vec3D vec3d = Vec3D.create(d, d1, d2);
    float f3 = MathHelper.cos(-f2 * 0.01745329F - 3.1415927F);
    float f4 = MathHelper.sin(-f2 * 0.01745329F - 3.1415927F);
    float f5 = -MathHelper.cos(-f1 * 0.01745329F);
    float f6 = MathHelper.sin(-f1 * 0.01745329F);
    float f7 = f4 * f5;
    float f9 = f3 * f5;
    double d3 = 5.0D;
    Vec3D vec3d1 = vec3d.add((double) f7 * d3, (double) f6 * d3, (double) f9 * d3);
    MovingObjectPosition movingobjectposition = world.rayTrace(vec3d, vec3d1, true);
    if (movingobjectposition == null) {
      return itemstack;
    }
    else {
      if (movingobjectposition.type == EnumMovingObjectType.TILE) {
        int i = movingobjectposition.b;
        int j = movingobjectposition.c;
        int k = movingobjectposition.d;
        if (!world.a(entityhuman, i, j, k)) {
          return itemstack;
        }

        if (world.getTypeId(i, j, k) == Block.STATIONARY_WATER.id && world.getData(i, j, k) == 0 && this.storeCell(Ic2Items.waterCell.cloneItemStack(), entityhuman)) {
          world.setTypeId(i, j, k, 0);
          --itemstack.count;
          return itemstack;
        }

        if (world.getTypeId(i, j, k) == Block.STATIONARY_LAVA.id && world.getData(i, j, k) == 0 && this.storeCell(Ic2Items.lavaCell.cloneItemStack(), entityhuman)) {
          world.setTypeId(i, j, k, 0);
          --itemstack.count;
          return itemstack;
        }
      }

      return itemstack;
    }
  }

  public boolean storeCell(ItemStack itemstack, EntityHuman entityhuman) {
    return entityhuman.inventory.pickup(itemstack);
  }
}
