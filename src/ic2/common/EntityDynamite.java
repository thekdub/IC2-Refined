package ic2.common;

import ic2.platform.Platform;
import net.minecraft.server.*;

public class EntityDynamite extends Entity {
	public static final int netId = 142;
	public boolean sticky;
	public int stickX;
	public int stickY;
	public int stickZ;
	public int fuse;
	public EntityLiving owner;
	private boolean inGround;
	private int ticksInGround;
	private int ticksInAir;

	public EntityDynamite(World world, double d, double d1, double d2) {
		super(world);
		this.sticky = false;
		this.fuse = 100;
		this.inGround = false;
		this.ticksInAir = 0;
		this.b(0.5F, 0.5F);
		this.setPosition(d, d1, d2);
		this.height = 0.0F;
	}

	public EntityDynamite(World world) {
		this(world, 0.0D, 0.0D, 0.0D);
	}

	public EntityDynamite(World world, EntityLiving entityliving) {
		super(world);
		this.sticky = false;
		this.fuse = 100;
		this.inGround = false;
		this.ticksInAir = 0;
		this.owner = entityliving;
		this.b(0.5F, 0.5F);
		this.setPositionRotation(entityliving.locX, entityliving.locY + (double) entityliving.getHeadHeight(), entityliving.locZ, entityliving.yaw, entityliving.pitch);
		this.locX -= (double) (MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * 0.16F);
		this.locY -= 0.10000000149011612D;
		this.locZ -= (double) (MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * 0.16F);
		this.setPosition(this.locX, this.locY, this.locZ);
		this.height = 0.0F;
		this.motX = (double) (-MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F));
		this.motZ = (double) (MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F));
		this.motY = (double) (-MathHelper.sin(this.pitch / 180.0F * 3.1415927F));
		this.setDynamiteHeading(this.motX, this.motY, this.motZ, 1.0F, 1.0F);
	}

	protected void b() {
	}

	public void setDynamiteHeading(double d, double d1, double d2, float f, float f1) {
		float f2 = MathHelper.sqrt(d * d + d1 * d1 + d2 * d2);
		d /= (double) f2;
		d1 /= (double) f2;
		d2 /= (double) f2;
		d += this.random.nextGaussian() * 0.007499999832361937D * (double) f1;
		d1 += this.random.nextGaussian() * 0.007499999832361937D * (double) f1;
		d2 += this.random.nextGaussian() * 0.007499999832361937D * (double) f1;
		d *= (double) f;
		d1 *= (double) f;
		d2 *= (double) f;
		this.motX = d;
		this.motY = d1;
		this.motZ = d2;
		float f3 = MathHelper.sqrt(d * d + d2 * d2);
		this.lastYaw = this.yaw = (float) (Math.atan2(d, d2) * 180.0D / 3.141592653589793D);
		this.lastPitch = this.pitch = (float) (Math.atan2(d1, (double) f3) * 180.0D / 3.141592653589793D);
		this.ticksInGround = 0;
	}

	public void setVelocity(double d, double d1, double d2) {
		this.motX = d;
		this.motY = d1;
		this.motZ = d2;
		if (this.lastPitch == 0.0F && this.lastYaw == 0.0F) {
			float f = MathHelper.sqrt(d * d + d2 * d2);
			this.lastYaw = this.yaw = (float) (Math.atan2(d, d2) * 180.0D / 3.141592653589793D);
			this.lastPitch = this.pitch = (float) (Math.atan2(d1, (double) f) * 180.0D / 3.141592653589793D);
			this.lastPitch = this.pitch;
			this.lastYaw = this.yaw;
			this.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
			this.ticksInGround = 0;
		}

	}

	public void F_() {
		super.F_();
		if (this.lastPitch == 0.0F && this.lastYaw == 0.0F) {
			float f = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
			this.lastYaw = this.yaw = (float) (Math.atan2(this.motX, this.motZ) * 180.0D / 3.141592653589793D);
			this.lastPitch = this.pitch = (float) (Math.atan2(this.motY, (double) f) * 180.0D / 3.141592653589793D);
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
		else if (this.fuse < 100 && this.fuse % 2 == 0) {
			this.world.a("smoke", this.locX, this.locY + 0.5D, this.locZ, 0.0D, 0.0D, 0.0D);
		}

		if (this.inGround) {
			++this.ticksInGround;
			if (this.ticksInGround >= 200) {
				this.die();
			}

			if (this.sticky) {
				this.fuse -= 3;
				this.motX = 0.0D;
				this.motY = 0.0D;
				this.motZ = 0.0D;
				if (this.world.getTypeId(this.stickX, this.stickY, this.stickZ) != 0) {
					return;
				}
			}
		}

		++this.ticksInAir;
		Vec3D vec3d = Vec3D.create(this.locX, this.locY, this.locZ);
		Vec3D vec3d1 = Vec3D.create(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
		MovingObjectPosition movingobjectposition = this.world.rayTrace(vec3d, vec3d1, false, true);
		vec3d = Vec3D.create(this.locX, this.locY, this.locZ);
		vec3d1 = Vec3D.create(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
		float f4;
		float f6;
		float f8;
		if (movingobjectposition != null) {
			Vec3D vec3d2 = Vec3D.create(movingobjectposition.pos.a, movingobjectposition.pos.b, movingobjectposition.pos.c);
			f4 = (float) (movingobjectposition.pos.a - this.locX);
			f6 = (float) (movingobjectposition.pos.b - this.locY);
			float f5 = (float) (movingobjectposition.pos.c - this.locZ);
			f8 = MathHelper.sqrt((double) (f4 * f4 + f6 * f6 + f5 * f5));
			this.stickX = movingobjectposition.b;
			this.stickY = movingobjectposition.c;
			this.stickZ = movingobjectposition.d;
			this.locX -= (double) f4 / (double) f8 * 0.05000000074505806D;
			this.locY -= (double) f6 / (double) f8 * 0.05000000074505806D;
			this.locZ -= (double) f5 / (double) f8 * 0.05000000074505806D;
			this.locX += (double) f4;
			this.locY += (double) f6;
			this.locZ += (double) f5;
			this.motX *= (double) (0.75F - this.random.nextFloat());
			this.motY *= -0.30000001192092896D;
			this.motZ *= (double) (0.75F - this.random.nextFloat());
			this.inGround = true;
		}
		else {
			this.locX += this.motX;
			this.locY += this.motY;
			this.locZ += this.motZ;
			this.inGround = false;
		}

		float f2 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
		this.yaw = (float) (Math.atan2(this.motX, this.motZ) * 180.0D / 3.141592653589793D);

		for (this.pitch = (float) (Math.atan2(this.motY, (double) f2) * 180.0D / 3.141592653589793D); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
		}

		while (this.pitch - this.lastPitch >= 180.0F) {
			this.lastPitch += 360.0F;
		}

		while (this.yaw - this.lastYaw < -180.0F) {
			this.lastYaw -= 360.0F;
		}

		while (this.yaw - this.lastYaw >= 180.0F) {
			this.lastYaw += 360.0F;
		}

		this.pitch = this.lastPitch + (this.pitch - this.lastPitch) * 0.2F;
		this.yaw = this.lastYaw + (this.yaw - this.lastYaw) * 0.2F;
		f4 = 0.98F;
		f6 = 0.04F;
		if (this.aU()) {
			this.fuse += 2000;

			for (int i = 0; i < 4; ++i) {
				f8 = 0.25F;
				this.world.a("bubble", this.locX - this.motX * (double) f8, this.locY - this.motY * (double) f8, this.locZ - this.motZ * (double) f8, this.motX, this.motY, this.motZ);
			}

			f4 = 0.75F;
		}

		this.motX *= (double) f4;
		this.motY *= (double) f4;
		this.motZ *= (double) f4;
		this.motY -= (double) f6;
		this.setPosition(this.locX, this.locY, this.locZ);
	}

	public void b(NBTTagCompound nbttagcompound) {
		nbttagcompound.setByte("inGround", (byte) (this.inGround ? 1 : 0));
	}

	public void a(NBTTagCompound nbttagcompound) {
		this.inGround = nbttagcompound.getByte("inGround") == 1;
	}

	public float getShadowSize() {
		return 0.0F;
	}

	public void explode() {
		PointExplosion pointexplosion = new PointExplosion(this.world, this.owner, (int) this.locX, (int) this.locY, (int) this.locZ, 1.0F, 1.0F, 0.8F);
		pointexplosion.doExplosionA(1, 1, 1, 1, 1, 1);
		pointexplosion.doExplosionB(true);
	}
}
