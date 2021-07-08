package ic2.common;

import forge.ISpecialResistance;
import forge.IThrowableEntity;
import ic2.platform.Platform;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.Iterator;
import java.util.List;

public class EntityMiningLaser extends Entity implements IThrowableEntity {
  public static final int netId = 141;
  public static final double laserSpeed = 1.0D;
  public static Block[] unmineableBlocks;
  
  static {
    unmineableBlocks =
        new Block[]{Block.BRICK, Block.OBSIDIAN, Block.LAVA, Block.STATIONARY_LAVA, Block.WATER, Block.STATIONARY_WATER,
            Block.BEDROCK, Block.GLASS};
  }
  
  public float range;
  public float power;
  public int blockBreaks;
  public boolean explosive;
  public EntityLiving owner;
  public boolean headingSet;
  public boolean smelt;
  private int ticksInAir;
  
  public EntityMiningLaser(World world, double d, double d1, double d2) {
    super(world);
    this.range = 0.0F;
    this.power = 0.0F;
    this.blockBreaks = 0;
    this.explosive = false;
    this.headingSet = false;
    this.smelt = false;
    this.ticksInAir = 0;
    this.b(0.8F, 0.8F);
    this.height = 0.0F;
    this.setPosition(d, d1, d2);
  }
  
  public EntityMiningLaser(World world) {
    this(world, 0.0D, 0.0D, 0.0D);
  }
  
  public EntityMiningLaser(World world, EntityLiving entityliving, float f, float f1, int i, boolean flag) {
    this(world, entityliving, f, f1, i, flag, entityliving.yaw, entityliving.pitch);
  }
  
  public EntityMiningLaser(World world, EntityLiving entityliving, float f, float f1, int i, boolean flag,
                           boolean flag1) {
    this(world, entityliving, f, f1, i, flag, entityliving.yaw, entityliving.pitch);
    this.smelt = flag1;
  }
  
  public EntityMiningLaser(World world, EntityLiving entityliving, float f, float f1, int i, boolean flag, double d,
                           double d1) {
    this(world, entityliving, f, f1, i, flag, d, d1, entityliving.locY + (double) entityliving.getHeadHeight() - 0.1D);
  }
  
  public EntityMiningLaser(World world, EntityLiving entityliving, float f, float f1, int i, boolean flag, double d,
                           double d1, double d2) {
    super(world);
    this.range = 0.0F;
    this.power = 0.0F;
    this.blockBreaks = 0;
    this.explosive = false;
    this.headingSet = false;
    this.smelt = false;
    this.ticksInAir = 0;
    this.owner = entityliving;
    this.b(0.8F, 0.8F);
    this.height = 0.0F;
    double d3 = Math.toRadians(d);
    double d4 = Math.toRadians(d1);
    double d5 = entityliving.locX - Math.cos(d3) * 0.16D;
    double d6 = entityliving.locZ - Math.sin(d3) * 0.16D;
    double d7 = -Math.sin(d3) * Math.cos(d4);
    double d8 = -Math.sin(d4);
    double d9 = Math.cos(d3) * Math.cos(d4);
    this.setPosition(d5, d2, d6);
    this.setLaserHeading(d7, d8, d9, 1.0D);
    this.range = f;
    this.power = f1;
    this.blockBreaks = i;
    this.explosive = flag;
  }
  
  protected void b() {
  }
  
  public void setLaserHeading(double d, double d1, double d2, double d3) {
    double d4 = MathHelper.sqrt(d * d + d1 * d1 + d2 * d2);
    this.motX = d / d4 * d3;
    this.motY = d1 / d4 * d3;
    this.motZ = d2 / d4 * d3;
    this.lastYaw = this.yaw = (float) Math.toDegrees(Math.atan2(d, d2));
    this.lastPitch = this.pitch = (float) Math.toDegrees(Math.atan2(d1, MathHelper.sqrt(d * d + d2 * d2)));
    this.headingSet = true;
  }
  
  public void setVelocity(double d, double d1, double d2) {
    this.setLaserHeading(d, d1, d2, 1.0D);
  }
  
  public void F_() {
    super.F_();
    if (!Platform.isSimulating() || this.range >= 1.0F && this.power > 0.0F && this.blockBreaks > 0) {
      ++this.ticksInAir;
      Vec3D vec3d = Vec3D.create(this.locX, this.locY, this.locZ);
      Vec3D vec3d1 = Vec3D.create(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
      MovingObjectPosition movingobjectposition = this.world.rayTrace(vec3d, vec3d1, false, true);
      vec3d = Vec3D.create(this.locX, this.locY, this.locZ);
      if (movingobjectposition != null) {
        vec3d1 = Vec3D.create(movingobjectposition.pos.a, movingobjectposition.pos.b, movingobjectposition.pos.c);
      }
      else {
        vec3d1 = Vec3D.create(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
      }
  
      Entity entity = null;
      List list =
          this.world.getEntities(this, this.boundingBox.a(this.motX, this.motY, this.motZ).grow(1.0D, 1.0D, 1.0D));
      double d = 0.0D;
  
      int k;
      for (k = 0; k < list.size(); ++k) {
        Entity entity1 = (Entity) list.get(k);
        if (entity1.o_() && (entity1 != this.owner || this.ticksInAir >= 5)) {
          float f = 0.3F;
          AxisAlignedBB axisalignedbb = entity1.boundingBox.grow(f, f, f);
          MovingObjectPosition movingobjectposition1 = axisalignedbb.a(vec3d, vec3d1);
          if (movingobjectposition1 != null) {
            double d1 = vec3d.b(movingobjectposition1.pos);
            if (d1 < d || d == 0.0D) {
              entity = entity1;
              d = d1;
            }
          }
        }
      }
  
      if (entity != null) {
        movingobjectposition = new MovingObjectPosition(entity);
      }
  
      if (movingobjectposition != null) {
        if (this.explosive) {
          this.explode();
          this.die();
          return;
        }
    
        if (movingobjectposition.entity != null) {
          if (Platform.isSimulating()) {
            k = (int) this.power;
            if (k > 0) {
              DamageSource damagesource;
              if (this.owner instanceof EntityHuman) {
                damagesource = DamageSource.playerAttack((EntityHuman) this.owner);
              }
              else {
                damagesource = DamageSource.mobAttack(this.owner);
              }
  
              EntityDamageByEntityEvent event =
                  new EntityDamageByEntityEvent(this.owner.getBukkitEntity(), entity.getBukkitEntity(),
                      DamageCause.ENTITY_ATTACK, 1);
              this.world.getServer().getPluginManager().callEvent(event);
              if (event.isCancelled()) {
                this.die();
                return;
              }
  
              event.setCancelled(true);
              Platform.setEntityOnFire(entity, k);
              if (movingobjectposition.entity.damageEntity(damagesource, k) && this.owner instanceof EntityHuman &&
                  (movingobjectposition.entity instanceof EntityEnderDragon &&
                      ((EntityEnderDragon) movingobjectposition.entity).getHealth() <= 0 ||
                      movingobjectposition.entity instanceof EntityComplexPart &&
                          ((EntityComplexPart) movingobjectposition.entity).owner.getHealth() <= 0)) {
                IC2Achievements.issueAchievement((EntityHuman) this.owner, "killDragonMiningLaser");
              }
            }
          }
      
          this.die();
        }
        else {
          k = movingobjectposition.b;
          int l = movingobjectposition.c;
          int i1 = movingobjectposition.d;
          int j1 = this.world.getTypeId(k, l, i1);
          int k1 = this.world.getData(k, l, i1);
          boolean flag = false;
          boolean flag1 = true;
          boolean flag2 = true;
          org.bukkit.entity.Entity ent = this.owner.getBukkitEntity();
          if (ent instanceof Player) {
            Player player = (Player) ent;
            org.bukkit.World bukkitWorld = player.getWorld();
            BlockBreakEvent breakEv = new BlockBreakEvent(bukkitWorld.getBlockAt(k, l, i1), player);
            Bukkit.getPluginManager().callEvent(breakEv);
            if (breakEv.isCancelled()) {
              this.die();
              return;
            }
          }
      
          if (!this.canMine(j1)) {
            this.die();
          }
          else if (Platform.isSimulating()) {
            float f1 = 0.0F;
            if (Block.byId[j1] instanceof ISpecialResistance) {
              ISpecialResistance ispecialresistance = (ISpecialResistance) Block.byId[j1];
              f1 = ispecialresistance
                  .getSpecialExplosionResistance(this.world, k, l, i1, this.locX, this.locY, this.locZ, this) + 0.3F;
            }
            else {
              f1 = Block.byId[j1].a(this) + 0.3F;
            }
        
            this.power -= f1 / 10.0F;
            if (this.power >= 0.0F) {
              if (Block.byId[j1].material == Material.TNT) {
                Block.byId[j1].postBreak(this.world, k, l, i1, 1);
              }
              else if (this.smelt) {
                if (Block.byId[j1].material == Material.WOOD) {
                  flag1 = true;
                  flag2 = false;
                }
                else {
                  Iterator iterator = Block.byId[j1].getBlockDropped(this.world, k, l, i1, k1, 0).iterator();
  
                  label124:
                  while (true) {
                    ItemStack itemstack1;
                    do {
                      if (!iterator.hasNext()) {
                        break label124;
                      }
    
                      ItemStack itemstack = (ItemStack) iterator.next();
                      itemstack1 = FurnaceRecipes.getInstance().getSmeltingResult(itemstack);
                    } while (itemstack1 == null);
  
                    ItemStack itemstack2 = itemstack1.cloneItemStack();
                    if (!flag && itemstack2.id != j1 && itemstack2.id < Block.byId.length &&
                        Block.byId[itemstack2.id] != null) {
                      flag = true;
                      flag1 = false;
                      flag2 = false;
                      this.world.setTypeIdAndData(k, l, i1, itemstack2.id, itemstack2.getData());
                    }
                    else {
                      flag2 = false;
                      float f2 = 0.7F;
                      double d2 = (double) (this.world.random.nextFloat() * f2) + (double) (1.0F - f2) * 0.5D;
                      double d3 = (double) (this.world.random.nextFloat() * f2) + (double) (1.0F - f2) * 0.5D;
                      double d4 = (double) (this.world.random.nextFloat() * f2) + (double) (1.0F - f2) * 0.5D;
                      EntityItem entityitem =
                          new EntityItem(this.world, (double) k + d2, (double) l + d3, (double) i1 + d4, itemstack2);
                      entityitem.pickupDelay = 10;
                      this.world.addEntity(entityitem);
                    }
  
                    this.power = 0.0F;
                  }
                }
              }
  
              if (flag1) {
                if (flag2) {
                  Block.byId[j1].dropNaturally(this.world, k, l, i1, this.world.getData(k, l, i1), 0.9F, 0);
                }
    
                this.world.setTypeId(k, l, i1, 0);
                if (this.world.random.nextInt(10) == 0 && Block.byId[j1].material.isBurnable()) {
                  this.world.setTypeId(k, l, i1, Block.FIRE.id);
                }
              }
  
              --this.blockBreaks;
            }
          }
        }
      }
      else {
        this.power -= 0.5F;
      }
  
      this.setPosition(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
      this.range = (float) ((double) this.range -
          Math.sqrt(this.motX * this.motX + this.motY * this.motY + this.motZ * this.motZ));
      if (this.aU()) {
        this.die();
      }
  
    }
    else {
      if (this.explosive) {
        this.explode();
      }
  
      this.die();
    }
  }
  
  public void b(NBTTagCompound nbttagcompound) {
  }
  
  public void a(NBTTagCompound nbttagcompound) {
  }
  
  public float getShadowSize() {
    return 0.0F;
  }
  
  public void explode() {
    if (Platform.isSimulating()) {
      ExplosionIC2 explosionic2 =
          new ExplosionIC2(this.world, this.owner, this.locX, this.locY, this.locZ, 5.0F, 0.85F, 0.55F);
      explosionic2.doExplosion();
    }
    
  }
  
  public boolean canMine(int i) {
    for (int j = 0; j < unmineableBlocks.length; ++j) {
      if (i == unmineableBlocks[j].id) {
        return false;
      }
    }
    
    return true;
  }
  
  public Entity getThrower() {
    return this.owner;
  }
  
  public void setThrower(Entity entity) {
    if (entity instanceof EntityLiving) {
      this.owner = (EntityLiving) entity;
    }
    
  }
}
