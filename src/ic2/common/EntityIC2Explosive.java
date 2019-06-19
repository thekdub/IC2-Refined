package ic2.common;

import ic2.platform.Platform;
import net.minecraft.server.*;

public class EntityIC2Explosive extends Entity {
  public DamageSource damageSource;
  public String igniter;
  public int fuse;
  public float explosivePower;
  public float dropRate;
  public float damageVsEntitys;
  public Block renderBlock;

  public EntityIC2Explosive(World world) {
    super(world);
    this.fuse = 80;
    this.explosivePower = 4.0F;
    this.dropRate = 0.3F;
    this.damageVsEntitys = 1.0F;
    this.renderBlock = Block.DIRT;
    this.bf = true;
    this.b(0.98F, 0.98F);
    this.height = this.length / 2.0F;
  }

  public EntityIC2Explosive(World world, double d, double d1, double d2, int i, float f, float f1, float f2, Block block, DamageSource damagesource) {
    this(world);
    this.setPosition(d, d1, d2);
    float f3 = (float) (Math.random() * 3.141592653589793D * 2.0D);
    this.motX = (double) (-MathHelper.sin(f3 * 3.1415927F / 180.0F) * 0.02F);
    this.motY = 0.20000000298023224D;
    this.motZ = (double) (-MathHelper.cos(f3 * 3.1415927F / 180.0F) * 0.02F);
    this.lastX = d;
    this.lastY = d1;
    this.lastZ = d2;
    this.fuse = i;
    this.explosivePower = f;
    this.dropRate = f1;
    this.damageVsEntitys = f2;
    this.renderBlock = block;
    this.damageSource = damagesource;
  }

  public EntityIC2Explosive(World world, double d, double d1, double d2, int i, float f, float f1, float f2, Block block) {
    this(world, d, d1, d2, i, f, f1, f2, block, DamageSource.EXPLOSION);
  }

  protected void b() {
  }

  protected boolean g_() {
    return false;
  }

  public boolean o_() {
    return !this.dead;
  }

  public void F_() {
    this.lastX = this.locX;
    this.lastY = this.locY;
    this.lastZ = this.locZ;
    this.motY -= 0.03999999910593033D;
    this.move(this.motX, this.motY, this.motZ);
    this.motX *= 0.9800000190734863D;
    this.motY *= 0.9800000190734863D;
    this.motZ *= 0.9800000190734863D;
    if (this.onGround) {
      this.motX *= 0.699999988079071D;
      this.motZ *= 0.699999988079071D;
      this.motY *= -0.5D;
    }

    if (this.fuse-- <= 0) {
      if (Platform.isSimulating()) {
        this.die();
        this.explode();
      }
      else {
        this.die();
      }
    }
    else {
      this.world.a("smoke", this.locX, this.locY + 0.5D, this.locZ, 0.0D, 0.0D, 0.0D);
    }

  }

  private void explode() {
    ExplosionIC2 explosionic2 = new ExplosionIC2(this.world, null, this.locX, this.locY, this.locZ, this.explosivePower, this.dropRate, this.damageVsEntitys, this.damageSource, this.igniter);
    explosionic2.doExplosion();
  }

  protected void b(NBTTagCompound nbttagcompound) {
    nbttagcompound.setByte("Fuse", (byte) this.fuse);
  }

  protected void a(NBTTagCompound nbttagcompound) {
    this.fuse = nbttagcompound.getByte("Fuse");
  }

  public float getShadowSize() {
    return 0.0F;
  }

  public EntityIC2Explosive setIgniter(String s) {
    this.igniter = s;
    return this;
  }
}
