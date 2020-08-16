package ic2.common;

import ic2.api.IMetalArmor;
import ic2.platform.ItemBlockCommon;
import net.minecraft.server.*;

import java.util.ArrayList;

public class BlockPoleFence extends BlockTex {
  public BlockPoleFence(int i, int j) {
    super(i, j, Material.ORE);
    this.c(1.5F);
    this.b(5.0F);
    this.a(i);
    this.a("blockFenceIron");
    ModLoader.registerBlock(this, ItemBlockCommon.class);
    Ic2Items.ironFence = new ItemStack(this);
  }
  
  public boolean a() {
    return false;
  }
  
  public boolean b() {
    return false;
  }
  
  public boolean isBlockNormalCube(World world, int i, int j, int k) {
    return false;
  }
  
  public int c() {
    return mod_IC2.fenceRenderId;
  }
  
  public AxisAlignedBB e(World world, int i, int j, int k) {
    return this.material == Material.ORE && this.isPole(world, i, j, k) ? AxisAlignedBB
        .b((float) i + 0.375F, (float) j, (float) k + 0.375F,
            (float) i + 0.625F, (float) j + 1.0F, (float) k + 0.625F) : AxisAlignedBB
        .b(i, j, k, i + 1, (float) j + 1.5F, k + 1);
  }
  
  public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
    return this.material == Material.ORE && this.isPole(world, i, j, k) ? AxisAlignedBB
        .b((float) i + 0.375F, (float) j, (float) k + 0.375F,
            (float) i + 0.625F, (float) j + 1.0F, (float) k + 0.625F) :
        AxisAlignedBB.b(i, j, k, i + 1, j + 1, k + 1);
  }
  
  public boolean isPole(World world, int i, int j, int k) {
    return world.getTypeId(i - 1, j, k) != this.id && world.getTypeId(i + 1, j, k) != this.id &&
        world.getTypeId(i, j, k - 1) != this.id && world.getTypeId(i, j, k + 1) != this.id;
  }
  
  public void a(World world, int i, int j, int k, Entity entity) {
    if (this.material == Material.ORE && this.isPole(world, i, j, k) && entity instanceof EntityHuman) {
      boolean flag = world.getData(i, j, k) > 0;
      boolean flag1 = false;
      EntityHuman entityhuman = (EntityHuman) entity;
      ItemStack itemstack = entityhuman.inventory.armor[0];
      if (itemstack != null) {
        int l = itemstack.id;
        if (l == Item.IRON_BOOTS.id || l == Item.GOLD_BOOTS.id || l == Item.CHAINMAIL_BOOTS.id ||
            itemstack.getItem() instanceof IMetalArmor &&
                ((IMetalArmor) itemstack.getItem()).isMetalArmor(itemstack, entityhuman)) {
          flag1 = true;
        }
      }
  
      if (flag && flag1) {
        world.setRawData(i, j, k, world.getData(i, j, k) - 1);
        entityhuman.motY += 0.07500000298023224D;
        if (entityhuman.motY > 0.0D) {
          entityhuman.motY *= 1.0299999713897705D;
          mod_IC2.setFallDistanceOfEntity(entityhuman, 0.0F);
        }
    
        if (entityhuman.isSneaking()) {
          if (entityhuman.motY > 0.30000001192092896D) {
            entityhuman.motY = 0.30000001192092896D;
          }
        }
        else if (entityhuman.motY > 1.5D) {
          entityhuman.motY = 1.5D;
        }
      }
      else if (entityhuman.isSneaking()) {
        if (entityhuman.motY < -0.25D) {
          entityhuman.motY *= 0.8999999761581421D;
        }
        else {
          mod_IC2.setFallDistanceOfEntity(entityhuman, 0.0F);
        }
      }
  
    }
  }
  
  public void addCreativeItems(ArrayList arraylist) {
    arraylist.add(new ItemStack(this));
  }
}
