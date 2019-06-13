package ic2.common;

import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class TileEntityBarrel extends TileEntity {
	public int type = 0;
	public int boozeAmount = 0;
	public int age = 0;
	public boolean detailed = true;
	public int treetapSide = 0;
	public int hopsCount = 0;
	public int wheatCount = 0;
	public int solidRatio = 0;
	public int hopsRatio = 0;
	public int timeRatio = 0;

	public void set(int i) {
		this.type = ItemBooze.getTypeOfValue(i);
		if (this.type > 0) {
			this.boozeAmount = ItemBooze.getAmountOfValue(i);
		}

		if (this.type == 1) {
			this.detailed = false;
			this.hopsRatio = ItemBooze.getHopsRatioOfBeerValue(i);
			this.solidRatio = ItemBooze.getSolidRatioOfBeerValue(i);
			this.timeRatio = ItemBooze.getTimeRatioOfBeerValue(i);
		}

		if (this.type == 2) {
			this.detailed = true;
			this.age = this.timeNedForRum(this.boozeAmount) * ItemBooze.getProgressOfRumValue(i) / 100;
		}

	}

	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		this.type = nbttagcompound.getByte("type");
		this.boozeAmount = nbttagcompound.getByte("waterCount");
		this.age = nbttagcompound.getInt("age");
		this.treetapSide = nbttagcompound.getByte("treetapSide");
		this.detailed = nbttagcompound.getBoolean("detailed");
		if (this.type == 1) {
			if (this.detailed) {
				this.hopsCount = nbttagcompound.getByte("hopsCount");
				this.wheatCount = nbttagcompound.getByte("wheatCount");
			}

			this.solidRatio = nbttagcompound.getByte("solidRatio");
			this.hopsRatio = nbttagcompound.getByte("hopsRatio");
			this.timeRatio = nbttagcompound.getByte("timeRatio");
		}

	}

	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setByte("type", (byte) this.type);
		nbttagcompound.setByte("waterCount", (byte) this.boozeAmount);
		nbttagcompound.setInt("age", this.age);
		nbttagcompound.setByte("treetapSide", (byte) this.treetapSide);
		nbttagcompound.setBoolean("detailed", this.detailed);
		if (this.type == 1) {
			if (this.detailed) {
				nbttagcompound.setByte("hopsCount", (byte) this.hopsCount);
				nbttagcompound.setByte("wheatCount", (byte) this.wheatCount);
			}

			nbttagcompound.setByte("solidRatio", (byte) this.solidRatio);
			nbttagcompound.setByte("hopsRatio", (byte) this.hopsRatio);
			nbttagcompound.setByte("timeRatio", (byte) this.timeRatio);
		}

	}

	public void q_() {
		if (!this.isEmpty() && this.treetapSide < 2) {
			++this.age;
			if (this.type == 1 && this.timeRatio < 5) {
				int i = this.timeRatio;
				if (i == 4) {
					i += 2;
				}

				if ((double) this.age >= 24000.0D * Math.pow(3.0D, (double) i)) {
					this.age = 0;
					++this.timeRatio;
				}
			}
		}

	}

	public boolean isEmpty() {
		return this.type == 0 || this.boozeAmount <= 0;
	}

	public boolean rightclick(EntityHuman entityhuman) {
		ItemStack itemstack = entityhuman.U();
		if (itemstack == null) {
			return false;
		}
		else if (itemstack.id == Item.WATER_BUCKET.id) {
			if (this.detailed && this.boozeAmount < 32 && this.type <= 1) {
				this.type = 1;
				itemstack.id = Item.BUCKET.id;
				++this.boozeAmount;
				return true;
			}
			else {
				return false;
			}
		}
		else {
			int l;
			if (itemstack.id == Ic2Items.waterCell.id) {
				if (this.detailed && this.type <= 1) {
					this.type = 1;
					l = itemstack.count;
					if (entityhuman.isSneaking()) {
						l = 1;
					}

					if (l > 32 - this.boozeAmount) {
						l = 32 - this.boozeAmount;
					}

					if (l <= 0) {
						return false;
					}
					else {
						this.boozeAmount += l;
						itemstack.count -= l;
						if (itemstack.count <= 0) {
							entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
						}

						return true;
					}
				}
				else {
					return false;
				}
			}
			else if (itemstack.id == Item.WHEAT.id) {
				if (this.detailed && this.type <= 1) {
					this.type = 1;
					l = itemstack.count;
					if (entityhuman.isSneaking()) {
						l = 1;
					}

					if (l > 64 - this.wheatCount) {
						l = 64 - this.wheatCount;
					}

					if (l <= 0) {
						return false;
					}
					else {
						this.wheatCount += l;
						itemstack.count -= l;
						if (itemstack.count <= 0) {
							entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
						}

						this.alterComposition();
						return true;
					}
				}
				else {
					return false;
				}
			}
			else if (itemstack.id == Ic2Items.hops.id) {
				if (this.detailed && this.type <= 1) {
					this.type = 1;
					l = itemstack.count;
					if (entityhuman.isSneaking()) {
						l = 1;
					}

					if (l > 64 - this.hopsCount) {
						l = 64 - this.hopsCount;
					}

					if (l <= 0) {
						return false;
					}
					else {
						this.hopsCount += l;
						itemstack.count -= l;
						if (itemstack.count <= 0) {
							entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
						}

						this.alterComposition();
						return true;
					}
				}
				else {
					return false;
				}
			}
			else if (itemstack.id != Item.SUGAR_CANE.id) {
				return false;
			}
			else if (this.age <= 600 && (this.type <= 0 || this.type == 2)) {
				this.type = 2;
				l = itemstack.count;
				if (entityhuman.isSneaking()) {
					l = 1;
				}

				if (l > 32 - this.boozeAmount) {
					l = 32 - this.boozeAmount;
				}

				if (l <= 0) {
					return false;
				}
				else {
					this.boozeAmount += l;
					itemstack.count -= l;
					if (itemstack.count <= 0) {
						entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
					}

					return true;
				}
			}
			else {
				return false;
			}
		}
	}

	public void alterComposition() {
		if (this.timeRatio == 0) {
			this.age = 0;
		}

		if (this.timeRatio == 1) {
			if (this.world.random.nextBoolean()) {
				this.timeRatio = 0;
			}
			else if (this.world.random.nextBoolean()) {
				this.timeRatio = 5;
			}
		}

		if (this.timeRatio == 2 && this.world.random.nextBoolean()) {
			this.timeRatio = 5;
		}

		if (this.timeRatio > 2) {
			this.timeRatio = 5;
		}

	}

	public boolean drainLiquid(int i) {
		if (this.isEmpty()) {
			return false;
		}
		else if (i > this.boozeAmount) {
			return false;
		}
		else {
			this.enforceUndetailed();
			if (this.type == 2) {
				int j = this.age * 100 / this.timeNedForRum(this.boozeAmount);
				this.boozeAmount -= i;
				this.age = j / 100 * this.timeNedForRum(this.boozeAmount);
			}
			else {
				this.boozeAmount -= i;
			}

			if (this.boozeAmount <= 0) {
				if (this.type == 1) {
					this.hopsCount = 0;
					this.wheatCount = 0;
					this.hopsRatio = 0;
					this.solidRatio = 0;
					this.timeRatio = 0;
				}

				this.type = 0;
				this.detailed = true;
				this.boozeAmount = 0;
			}

			return true;
		}
	}

	public void enforceUndetailed() {
		if (this.detailed) {
			this.detailed = false;
			if (this.type == 1) {
				float f = this.wheatCount <= 0 ? 10.0F : (float) this.hopsCount / (float) this.wheatCount;
				if (this.hopsCount <= 0 && this.wheatCount <= 0) {
					f = 0.0F;
				}

				float f1 = this.boozeAmount <= 0 ? 10.0F : (float) (this.hopsCount + this.wheatCount) / (float) this.boozeAmount;
				if (f <= 0.25F) {
					this.hopsRatio = 0;
				}

				if (f > 0.25F && f <= 0.3333333F) {
					this.hopsRatio = 1;
				}

				if (f > 0.3333333F && f <= 0.5F) {
					this.hopsRatio = 2;
				}

				if (f > 0.5F && f < 2.0F) {
					this.hopsRatio = 3;
				}

				if (f >= 2.0F && f < 3.0F) {
					this.hopsRatio = 4;
				}

				if (f >= 3.0F && f < 4.0F) {
					this.hopsRatio = 5;
				}

				if (f >= 4.0F && f < 5.0F) {
					this.hopsRatio = 6;
				}

				if (f >= 5.0F) {
					this.timeRatio = 5;
				}

				if (f1 <= 0.4166667F && f1 > 0.4166667F && f1 <= 0.5F) {
					this.solidRatio = 1;
				}

				if (f1 > 0.5F && f1 < 1.0F) {
					this.solidRatio = 2;
				}

				if (f1 == 1.0F) {
					this.solidRatio = 3;
				}

				if (f1 > 1.0F && f1 < 2.0F) {
					this.solidRatio = 4;
				}

				if (f1 >= 2.0F && f1 < 2.4F) {
					this.solidRatio = 5;
				}

				if (f1 >= 2.4F && f1 < 4.0F) {
					this.solidRatio = 6;
				}

				if (f1 >= 4.0F) {
					this.timeRatio = 5;
				}
			}

		}
	}

	public boolean useTreetapOn(EntityHuman entityhuman, int i) {
		ItemStack itemstack = entityhuman.U();
		Entity bukkitentity = entityhuman.getBukkitEntity();
		if (bukkitentity instanceof Player) {
			Player player = (Player) bukkitentity;
			BlockBreakEvent breakev = new BlockBreakEvent(player.getWorld().getBlockAt(this.x, this.y, this.z), player);
			Bukkit.getPluginManager().callEvent(breakev);
			if (breakev.isCancelled()) {
				this.update();
				return false;
			}

			breakev.setCancelled(true);
			this.update();
		}

		if (itemstack != null && itemstack.id == Ic2Items.treetap.id && itemstack.getData() == 0 && i > 1) {
			this.treetapSide = i;
			this.update();
			if (!entityhuman.abilities.canInstantlyBuild) {
				--entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex].count;
				if (entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex].count == 0) {
					entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
				}
			}

			return true;
		}
		else {
			return false;
		}
	}

	public void update() {
		this.world.notify(this.x, this.y, this.z);
	}

	public int calculateMetaValue() {
		if (this.isEmpty()) {
			return 0;
		}
		else {
			int j; //Possibly a byte instead.
			if (this.type == 1) {
				this.enforceUndetailed();
				j = 0;
				j = j | this.timeRatio;
				j <<= 3;
				j |= this.hopsRatio;
				j <<= 3;
				j |= this.solidRatio;
				j <<= 5;
				j |= this.boozeAmount - 1;
				j <<= 2;
				j |= this.type;
				return j;
			}
			else if (this.type == 2) {
				this.enforceUndetailed();
				j = 0;
				int k = this.age * 100 / this.timeNedForRum(this.boozeAmount);
				if (k > 100) {
					k = 100;
				}

				j = j | k;
				j <<= 5;
				j |= this.boozeAmount - 1;
				j <<= 2;
				j |= this.type;
				return j;
			}
			else {
				return 0;
			}
		}
	}

	public int timeNedForRum(int i) {
		return (int) ((double) (1200 * i) * Math.pow(0.95D, (double) (i - 1)));
	}
}
