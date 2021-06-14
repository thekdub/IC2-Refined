package ic2.common;

import forge.ISpecialResistance;
import ic2.api.FakePlayer;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.*;

public class PointExplosion {
  public int explosionX;
  public int explosionY;
  public int explosionZ;
  public Entity exploder;
  public float explosionSize;
  public float explosionDropRate;
  public float explosionDamage;
  public Set destroyedBlockPositions = new HashSet();
  private final Random ExplosionRNG = new Random();
  private final World worldObj;
  private Player player;
  
  public PointExplosion(World world, Entity entity, int i, int j, int k, float f, float f1, float f2) {
    this.worldObj = world;
    this.exploder = entity;
    this.player = null;
    if (entity != null) {
      if (entity.getBukkitEntity() instanceof Player) {
        this.player = (Player) entity.getBukkitEntity();
      }
      else {
        FakePlayer.getBukkitEntity(world);
      }
    }
    
    this.explosionSize = f;
    this.explosionDropRate = f1;
    this.explosionDamage = f2;
    this.explosionX = i;
    this.explosionY = j;
    this.explosionZ = k;
    if (this.explosionX < 0) {
      --this.explosionX;
    }
    
    if (this.explosionZ < 0) {
      --this.explosionZ;
    }
    
  }
  
  public void doExplosionA(int i, int j, int k, int l, int i1, int j1) {
    int k1;
    int i2;
    int k2;
    int i3;
    for (k1 = this.explosionX - i; k1 <= this.explosionX + l; ++k1) {
      for (i2 = this.explosionY - j; i2 <= this.explosionY + i1; ++i2) {
        for (k2 = this.explosionZ - k; k2 <= this.explosionZ + j1; ++k2) {
          i3 = this.worldObj.getTypeId(k1, i2, k2);
          float f = 0.0F;
          if (i3 > 0) {
            if (Block.byId[i3] instanceof ISpecialResistance) {
              ISpecialResistance ispecialresistance = (ISpecialResistance) Block.byId[i3];
              f = ispecialresistance.getSpecialExplosionResistance(this.worldObj, k1, i2, k2, this.explosionX,
                  this.explosionY, this.explosionZ, this.exploder);
            }
            else {
              f = Block.byId[i3].a(this.exploder);
            }
          }
  
          if (this.explosionSize >= f / 10.0F) {
            this.destroyedBlockPositions.add(new ChunkPosition(k1, i2, k2));
          }
        }
      }
    }
    
    this.explosionSize *= 2.0F;
    k1 = MathHelper.floor((double) this.explosionX - (double) this.explosionSize - 1.0D);
    i2 = MathHelper.floor((double) this.explosionX + (double) this.explosionSize + 1.0D);
    k2 = MathHelper.floor((double) this.explosionY - (double) this.explosionSize - 1.0D);
    i3 = MathHelper.floor((double) this.explosionY + (double) this.explosionSize + 1.0D);
    int k3 = MathHelper.floor((double) this.explosionZ - (double) this.explosionSize - 1.0D);
    int l3 = MathHelper.floor((double) this.explosionZ + (double) this.explosionSize + 1.0D);
    List list = this.worldObj.getEntities(this.exploder,
        AxisAlignedBB.b(k1, k2, k3, i2, i3, l3));
    Vec3D vec3d = Vec3D.create(this.explosionX, this.explosionY, this.explosionZ);
    
    for (int i4 = 0; i4 < list.size(); ++i4) {
      Entity entity = (Entity) list.get(i4);
      double d = entity.f(this.explosionX, this.explosionY, this.explosionZ) /
          (double) this.explosionSize;
      if (d <= 1.0D) {
        double d1 = entity.locX - (double) this.explosionX;
        double d2 = entity.locY - (double) this.explosionY;
        double d3 = entity.locZ - (double) this.explosionZ;
        double d4 = MathHelper.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
        d1 /= d4;
        d2 /= d4;
        d3 /= d4;
        double d5 = this.worldObj.a(vec3d, entity.boundingBox);
        double d6 = (1.0D - d) * d5;
        entity.damageEntity(DamageSource.EXPLOSION,
            (int) (((d6 * d6 + d6) / 2.0D * 8.0D * (double) this.explosionSize + 1.0D) *
                (double) this.explosionDamage));
        entity.motX += d1 * d6;
        entity.motY += d2 * d6;
        entity.motZ += d3 * d6;
      }
    }
    
  }
  
  public void doExplosionB(boolean flag) {
    this.worldObj
        .makeSound(this.explosionX, this.explosionY, this.explosionZ, "random.explode", 4.0F,
            (1.0F + (this.worldObj.random.nextFloat() - this.worldObj.random.nextFloat()) * 0.2F) * 0.7F);
    org.bukkit.entity.Entity explode = this.exploder == null ? null : this.exploder.getBukkitEntity();
    Location location = new Location(this.worldObj.getWorld(), this.explosionX, this.explosionY,
        this.explosionZ);
    List blockList = new ArrayList(this.destroyedBlockPositions.size());
    Iterator var6 = this.destroyedBlockPositions.iterator();
    
    while (var6.hasNext()) {
      Object entry = var6.next();
      ChunkPosition cpos = (ChunkPosition) entry;
      org.bukkit.block.Block block = this.worldObj.getWorld().getBlockAt(cpos.x, cpos.y, cpos.z);
      if (block.getTypeId() != 0) {
        blockList.add(block);
      }
    }
    
    EntityExplodeEvent event = new EntityExplodeEvent(explode, location, blockList, this.explosionDamage);
    this.worldObj.getServer().getPluginManager().callEvent(event);
    if (!event.isCancelled()) {
      if (this.player != null) {
        Iterator var36 = blockList.iterator();
  
        while (var36.hasNext()) {
          org.bukkit.block.Block block = (org.bukkit.block.Block) var36.next();
          BlockBreakEvent breakEv = new BlockBreakEvent(block, this.player);
          Bukkit.getPluginManager().callEvent(breakEv);
          if (breakEv.isCancelled()) {
            return;
          }
        }
      }
  
      this.destroyedBlockPositions.clear();
      Iterator var34 = event.blockList().iterator();
  
      ChunkPosition chunkposition;
      while (var34.hasNext()) {
        org.bukkit.block.Block block = (org.bukkit.block.Block) var34.next();
        chunkposition = new ChunkPosition(block.getX(), block.getY(), block.getZ());
        this.destroyedBlockPositions.add(chunkposition);
      }
  
      ArrayList arraylist = new ArrayList();
      arraylist.addAll(this.destroyedBlockPositions);
  
      for (int i = arraylist.size() - 1; i >= 0; --i) {
        chunkposition = (ChunkPosition) arraylist.get(i);
        int j = chunkposition.x;
        int k = chunkposition.y;
        int l = chunkposition.z;
        int i1 = this.worldObj.getTypeId(j, k, l);
        if (flag) {
          double d = (float) j + this.worldObj.random.nextFloat();
          double d1 = (float) k + this.worldObj.random.nextFloat();
          double d2 = (float) l + this.worldObj.random.nextFloat();
          double d3 = d - (double) this.explosionX;
          double d4 = d1 - (double) this.explosionY;
          double d5 = d2 - (double) this.explosionZ;
          double d6 = MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
          d3 /= d6;
          d4 /= d6;
          d5 /= d6;
          double d7 = 0.5D / (d6 / (double) this.explosionSize + 0.1D);
          d7 *= this.worldObj.random.nextFloat() * this.worldObj.random.nextFloat() + 0.3F;
          d3 *= d7;
          d4 *= d7;
          d5 *= d7;
          this.worldObj
              .a("explode", (d + (double) this.explosionX * 1.0D) / 2.0D, (d1 + (double) this.explosionY * 1.0D) / 2.0D,
                  (d2 + (double) this.explosionZ * 1.0D) / 2.0D, d3, d4, d5);
          this.worldObj.a("smoke", d, d1, d2, d3, d4, d5);
        }
    
        if (i1 > 0) {
          Block.byId[i1]
              .dropNaturally(this.worldObj, j, k, l, this.worldObj.getData(j, k, l), this.explosionDropRate, 0);
          this.worldObj.setTypeId(j, k, l, 0);
          Block.byId[i1].wasExploded(this.worldObj, j, k, l);
        }
      }
  
    }
  }
}
