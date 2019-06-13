package ic2.common;

import forge.ISpecialResistance;
import ic2.api.ExplosionWhitelist;
import ic2.api.FakePlayer;
import ic2.platform.Platform;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.*;
import java.util.Map.Entry;

public class ExplosionIC2 {
	private final double dropPowerLimit;
	private final int secondaryRayCount;
	public double explosionX;
	public double explosionY;
	public double explosionZ;
	public Entity exploder;
	public float power;
	public float explosionDropRate;
	public float explosionDamage;
	public DamageSource damageSource;
	public String igniter;
	public List entitiesInRange;
	public Map destroyedBlockPositions;
	private Random ExplosionRNG;
	private World worldObj;
	private int mapHeight;
	private Player player;

	public ExplosionIC2(World world, Entity entity, double d, double d1, double d2, float f, float f1, float f2, DamageSource damagesource) {
		this.dropPowerLimit = 8.0D;
		this.secondaryRayCount = 5;
		this.ExplosionRNG = new Random();
		this.destroyedBlockPositions = new HashMap();
		this.worldObj = world;
		this.mapHeight = mod_IC2.getWorldHeight(world);
		this.exploder = entity;
		if (entity != null) {
			if (entity.getBukkitEntity() instanceof Player) {
				this.player = (Player) entity.getBukkitEntity();
			}
			else {
				FakePlayer.getBukkitEntity(world);
			}
		}

		this.power = f;
		this.explosionDropRate = f1;
		this.explosionDamage = f2;
		this.explosionX = d;
		this.explosionY = d1;
		this.explosionZ = d2;
		this.damageSource = damagesource;
	}

	public ExplosionIC2(World world, Entity entity, double d, double d1, double d2, float f, float f1, float f2) {
		this(world, entity, d, d1, d2, f, f1, f2, DamageSource.EXPLOSION);
	}

	public ExplosionIC2(World world, Entity entity, double d, double d1, double d2, float f, float f1, float f2, DamageSource damagesource, String s) {
		this(world, entity, d, d1, d2, f, f1, f2, damagesource);
		this.igniter = s;
	}

	public void doExplosion() {
		if (this.power > 0.0F) {
			double d = (double) this.power / 0.4D;
			this.entitiesInRange = this.worldObj.a(EntityLiving.class, AxisAlignedBB.a(this.explosionX - d, this.explosionY - d, this.explosionZ - d, this.explosionX + d, this.explosionY + d, this.explosionZ + d));
			int i = (int) Math.ceil(3.141592653589793D / Math.atan(1.0D / d));

			for (int j = 0; j < 2 * i; ++j) {
				for (int k = 0; k < i; ++k) {
					double d1 = 6.283185307179586D / (double) i * (double) j;
					double d2 = 3.141592653589793D / (double) i * (double) k;
					this.shootRay(this.explosionX, this.explosionY, this.explosionZ, d1, d2, (double) this.power, j % 8 == 0 && k % 8 == 0);
				}
			}

			org.bukkit.entity.Entity explode = this.exploder == null ? (new EntityTNTPrimed(this.worldObj)).getBukkitEntity() : this.exploder.getBukkitEntity();
			Location location = new Location(this.worldObj.getWorld(), this.explosionX, this.explosionY, this.explosionZ);
			List blockList = new ArrayList(this.destroyedBlockPositions.size());
			Iterator var42 = this.destroyedBlockPositions.entrySet().iterator();

			while (var42.hasNext()) {
				Object entry = var42.next();
				ChunkPosition cpos = (ChunkPosition) ((Entry) entry).getKey();
				Block block = this.worldObj.getWorld().getBlockAt(cpos.x, cpos.y, cpos.z);
				if (block.getTypeId() != 0) {
					blockList.add(block);
				}
			}

			EntityExplodeEvent event = new EntityExplodeEvent(explode, location, blockList, this.power);
			this.worldObj.getServer().getPluginManager().callEvent(event);
			if (!event.isCancelled()) {
				Iterator iterator1;
				if (this.player != null) {
					iterator1 = blockList.iterator();

					while (iterator1.hasNext()) {
						Block block = (Block) iterator1.next();
						BlockBreakEvent breakEv = new BlockBreakEvent(block, this.player);
						Bukkit.getPluginManager().callEvent(breakEv);
						if (breakEv.isCancelled()) {
							return;
						}
					}
				}

				this.destroyedBlockPositions.clear();
				Iterator iterator = event.blockList().iterator();

				while (iterator.hasNext()) {
					Block block = (Block) iterator.next();
					ChunkPosition cpos = new ChunkPosition(block.getX(), block.getY(), block.getZ());
					this.destroyedBlockPositions.put(cpos, (double) this.power <= 8.0D);
				}

				Platform.playExplodeSound(this.worldObj, (int) this.explosionX, (int) this.explosionY, (int) this.explosionZ, this.power);
				if (this.damageSource != IC2DamageSource.nuke && Platform.isRendering()) {
					this.worldObj.a("hugeexplosion", this.explosionX, this.explosionY, this.explosionZ, 0.0D, 0.0D, 0.0D);
				}

				HashMap hashmap = new HashMap();
				iterator = this.destroyedBlockPositions.entrySet().iterator();

				while (true) {
					int l;
					int i1;
					int j1;
					int k1;
					Entry entry;
					do {
						if (!iterator.hasNext()) {
							iterator1 = hashmap.entrySet().iterator();

							while (iterator1.hasNext()) {
								Entry entry1 = (Entry) iterator1.next();
								XZposition xzposition = (XZposition) entry1.getKey();
								Iterator iterator2 = ((Map) entry1.getValue()).entrySet().iterator();

								while (iterator2.hasNext()) {
									Entry entry2 = (Entry) iterator2.next();
									ItemWithMeta itemwithmeta = (ItemWithMeta) entry2.getKey();

									int i2;
									for (int l1 = ((DropData) entry2.getValue()).n; l1 > 0; l1 -= i2) {
										i2 = Math.min(l1, 64);
										EntityItem entityitem = new EntityItem(this.worldObj, (double) ((float) xzposition.x + this.worldObj.random.nextFloat()) * 2.0D, (double) ((DropData) entry2.getValue()).maxY + 0.5D, (double) ((float) xzposition.z + this.worldObj.random.nextFloat()) * 2.0D, new ItemStack(itemwithmeta.itemId, i2, itemwithmeta.metaData));
										entityitem.pickupDelay = 10;
										this.worldObj.addEntity(entityitem);
									}
								}
							}

							return;
						}

						entry = (Entry) iterator.next();
						l = ((ChunkPosition) entry.getKey()).x;
						i1 = ((ChunkPosition) entry.getKey()).y;
						j1 = ((ChunkPosition) entry.getKey()).z;
						k1 = this.worldObj.getTypeId(l, i1, j1);
					} while (k1 == 0);

					if ((Boolean) entry.getValue()) {
						double d3 = (double) ((float) l + this.worldObj.random.nextFloat());
						double d4 = (double) ((float) i1 + this.worldObj.random.nextFloat());
						double d5 = (double) ((float) j1 + this.worldObj.random.nextFloat());
						double d6 = d3 - this.explosionX;
						double d7 = d4 - this.explosionY;
						double d8 = d5 - this.explosionZ;
						double d9 = (double) MathHelper.sqrt(d6 * d6 + d7 * d7 + d8 * d8);
						d6 /= d9;
						d7 /= d9;
						d8 /= d9;
						double d10 = 0.5D / (d9 / (double) this.power + 0.1D);
						d10 *= (double) (this.worldObj.random.nextFloat() * this.worldObj.random.nextFloat() + 0.3F);
						d6 *= d10;
						d7 *= d10;
						d8 *= d10;
						this.worldObj.a("explode", (d3 + this.explosionX) / 2.0D, (d4 + this.explosionY) / 2.0D, (d5 + this.explosionZ) / 2.0D, d6, d7, d8);
						this.worldObj.a("smoke", d3, d4, d5, d6, d7, d8);
						net.minecraft.server.Block block = net.minecraft.server.Block.byId[k1];
						if (this.worldObj.random.nextFloat() <= this.explosionDropRate) {
							int j2 = this.worldObj.getData(l, i1, j1);
							Iterator iterator3 = block.getBlockDropped(this.worldObj, l, i1, j1, j2, 0).iterator();

							while (iterator3.hasNext()) {
								ItemStack itemstack = (ItemStack) iterator3.next();
								XZposition xzposition1 = new XZposition(l / 2, j1 / 2);
								if (!hashmap.containsKey(xzposition1)) {
									hashmap.put(xzposition1, new HashMap());
								}

								Map map = (Map) hashmap.get(xzposition1);
								ItemWithMeta itemwithmeta1 = new ItemWithMeta(itemstack.id, itemstack.getData());
								if (!map.containsKey(itemwithmeta1)) {
									map.put(itemwithmeta1, new DropData(itemstack.count, i1));
								}
								else {
									map.put(itemwithmeta1, ((DropData) map.get(itemwithmeta1)).add(itemstack.count, i1));
								}
							}
						}
					}

					this.worldObj.setTypeId(l, i1, j1, 0);
					net.minecraft.server.Block.byId[k1].wasExploded(this.worldObj, l, i1, j1);
				}
			}
		}
	}

	private void shootRay(double d, double d1, double d2, double d3, double d4, double d5, boolean flag) {
		double d6 = Math.sin(d4) * Math.cos(d3);
		double d7 = Math.cos(d4);
		double d8 = Math.sin(d4) * Math.sin(d3);

		do {
			int i = this.worldObj.getTypeId((int) d, (int) d1, (int) d2);
			double d9 = 0.5D;
			if (i > 0) {
				if (net.minecraft.server.Block.byId[i] instanceof ISpecialResistance) {
					ISpecialResistance ispecialresistance = (ISpecialResistance) net.minecraft.server.Block.byId[i];
					d9 += ((double) ispecialresistance.getSpecialExplosionResistance(this.worldObj, (int) d, (int) d1, (int) d2, this.explosionX, this.explosionY, this.explosionZ, this.exploder) + 4.0D) * 0.3D;
				}
				else {
					d9 += ((double) net.minecraft.server.Block.byId[i].a(this.exploder) + 4.0D) * 0.3D;
				}
			}

			if (d9 > 1000.0D && !ExplosionWhitelist.isBlockWhitelisted(net.minecraft.server.Block.byId[i])) {
				d9 = 0.5D;
			}
			else {
				if (d9 > d5) {
					break;
				}

				if (i > 0) {
					ChunkPosition chunkposition = new ChunkPosition((int) d, (int) d1, (int) d2);
					if (!this.destroyedBlockPositions.containsKey(chunkposition) || d5 > 8.0D && (Boolean) this.destroyedBlockPositions.get(chunkposition)) {
						this.destroyedBlockPositions.put(chunkposition, d5 <= 8.0D);
					}
				}
			}

			if (flag) {
				Iterator iterator = this.entitiesInRange.iterator();

				while (iterator.hasNext()) {
					EntityLiving entityliving = (EntityLiving) iterator.next();
					if ((entityliving.locX - d) * (entityliving.locX - d) + (entityliving.locY - d1) * (entityliving.locY - d1) + (entityliving.locZ - d2) * (entityliving.locZ - d2) <= 25.0D) {
						double d10 = entityliving.locX - this.explosionX;
						double d11 = entityliving.locY - this.explosionY;
						double d12 = entityliving.locZ - this.explosionZ;
						double d13 = Math.sqrt(d10 * d10 + d11 * d11 + d12 * d12);
						double d14 = d5 / 2.0D / (Math.pow(d13, 0.8D) + 1.0D);
						entityliving.damageEntity(this.damageSource, (int) Math.pow(d14 * 3.0D, 2.0D));
						if (this.damageSource == IC2DamageSource.nuke && entityliving instanceof EntityHuman && ((EntityHuman) entityliving).name.equals(this.igniter) && entityliving.getHealth() <= 0) {
							IC2Achievements.issueAchievement((EntityHuman) entityliving, "dieFromOwnNuke");
						}

						d10 /= d13;
						d11 /= d13;
						d12 /= d13;
						entityliving.motX += d10 * d14;
						entityliving.motY += d11 * d14;
						entityliving.motZ += d12 * d14;
						iterator.remove();
					}
				}
			}

			if (d9 > 10.0D) {
				for (int j = 0; j < 5; ++j) {
					this.shootRay(d, d1, d2, this.ExplosionRNG.nextDouble() * 2.0D * 3.141592653589793D, this.ExplosionRNG.nextDouble() * 3.141592653589793D, d9 * 0.4D, false);
				}
			}

			d5 -= d9;
			d += d6;
			d1 += d7;
			d2 += d8;
		} while (d1 > 0.0D && d1 < (double) this.mapHeight);

	}

	static class DropData {
		int n;
		int maxY;

		DropData(int i, int j) {
			this.n = i;
			this.maxY = j;
		}

		public DropData add(int i, int j) {
			this.n += i;
			if (j > this.maxY) {
				this.maxY = j;
			}

			return this;
		}
	}

	static class ItemWithMeta {
		int itemId;
		int metaData;

		ItemWithMeta(int i, int j) {
			this.itemId = i;
			this.metaData = j;
		}

		public boolean equals(Object obj) {
			if (obj instanceof ItemWithMeta) {
				ItemWithMeta itemwithmeta = (ItemWithMeta) obj;
				return itemwithmeta.itemId == this.itemId && itemwithmeta.metaData == this.metaData;
			}
			else {
				return false;
			}
		}

		public int hashCode() {
			return this.itemId * 31 ^ this.metaData;
		}
	}

	static class XZposition {
		int x;
		int z;

		XZposition(int i, int j) {
			this.x = i;
			this.z = j;
		}

		public boolean equals(Object obj) {
			if (obj instanceof XZposition) {
				XZposition xzposition = (XZposition) obj;
				return xzposition.x == this.x && xzposition.z == this.z;
			}
			else {
				return false;
			}
		}

		public int hashCode() {
			return this.x * 31 ^ this.z;
		}
	}
}
