package ic2.common;

import forge.ISidedInventory;
import forge.MinecraftForge;
import ic2.platform.AudioManager;
import ic2.platform.AudioSource;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.ArrayList;

public class TileEntityPump extends TileEntityElecMachine implements IHasGui, ISidedInventory {
	public int soundTicker;
	public short pumpCharge = 0;
	private AudioSource audioSource;

	public TileEntityPump() {
		super(2, 1, 200, 32);
		this.soundTicker = mod_IC2.random.nextInt(64);
	}

	public String getName() {
		return "Pump";
	}

	public void q_() {
		super.q_();
		boolean flag = false;
		if (this.energy > 0 && !this.isPumpReady()) {
			--this.energy;
			++this.pumpCharge;
		}

		if (this.energy <= this.maxEnergy) {
			flag = this.provideEnergy();
		}

		if (this.isPumpReady()) {
			flag = this.pump();
		}

		if (this.getActive() == this.isPumpReady() && this.energy > 0) {
			this.setActive(!this.getActive());
		}

		if (flag) {
			this.update();
		}

	}

	public void j() {
		if (Platform.isRendering() && this.audioSource != null) {
			AudioManager.removeSources(this);
			this.audioSource = null;
		}

		super.j();
	}

	public boolean pump() {
		if (!this.canHarvest()) {
			return false;
		}
		else if (!this.isWaterBelow() && !this.isLavaBelow()) {
			if (this.inventory[0] != null && this.inventory[0].id == Item.BUCKET.id) {
				ItemStack itemstack = MinecraftForge.fillCustomBucket(this.world, this.x, this.y - 1, this.z);
				if (itemstack != null) {
					ArrayList arraylist = new ArrayList();
					arraylist.add(itemstack);
					StackUtil.distributeDrop(this, arraylist);
					this.inventory[0] = null;
					this.pumpCharge = 0;
					return true;
				}
			}

			return false;
		}
		else {
			int i = this.world.getTypeId(this.x, this.y - 1, this.z);
			this.world.setTypeId(this.x, this.y - 1, this.z, 0);
			if (i == Block.WATER.id) {
				i = Block.STATIONARY_WATER.id;
			}

			if (i == Block.LAVA.id) {
				i = Block.STATIONARY_LAVA.id;
			}

			return this.pumpThis(i);
		}
	}

	public boolean isWaterBelow() {
		return (this.world.getTypeId(this.x, this.y - 1, this.z) == Block.WATER.id || this.world.getTypeId(this.x, this.y - 1, this.z) == Block.STATIONARY_WATER.id) && this.world.getData(this.x, this.y - 1, this.z) == 0;
	}

	public boolean isLavaBelow() {
		return (this.world.getTypeId(this.x, this.y - 1, this.z) == Block.LAVA.id || this.world.getTypeId(this.x, this.y - 1, this.z) == Block.STATIONARY_LAVA.id) && this.world.getData(this.x, this.y - 1, this.z) == 0;
	}

	public boolean pumpThis(int i) {
		if (i == Block.STATIONARY_LAVA.id && this.deliverLavaToGeo()) {
			this.pumpCharge = 0;
			return true;
		}
		else if (this.inventory[0] != null && this.inventory[0].id == Item.BUCKET.id) {
			if (i == Block.STATIONARY_WATER.id) {
				this.inventory[0].id = Item.WATER_BUCKET.id;
			}

			if (i == Block.STATIONARY_LAVA.id) {
				this.inventory[0].id = Item.LAVA_BUCKET.id;
			}

			ArrayList arraylist = new ArrayList();
			arraylist.add(this.inventory[0]);
			StackUtil.distributeDrop(this, arraylist);
			this.inventory[0] = null;
			this.pumpCharge = 0;
			return true;
		}
		else if (this.inventory[0] != null && this.inventory[0].id == Ic2Items.cell.id) {
			ItemStack itemstack = null;
			if (i == Block.STATIONARY_WATER.id) {
				itemstack = Ic2Items.waterCell.cloneItemStack();
			}

			if (i == Block.STATIONARY_LAVA.id) {
				itemstack = Ic2Items.lavaCell.cloneItemStack();
			}

			--this.inventory[0].count;
			if (this.inventory[0].count <= 0) {
				this.inventory[0] = null;
			}

			ArrayList arraylist1 = new ArrayList();
			arraylist1.add(itemstack);
			StackUtil.distributeDrop(this, arraylist1);
			this.pumpCharge = 0;
			return true;
		}
		else {
			this.pumpCharge = 0;
			return this.putInChestBucket(i);
		}
	}

	public boolean putInChestBucket(int i) {
		return this.putInChestBucket(this.x, this.y + 1, this.z, i) || this.putInChestBucket(this.x, this.y - 1, this.z, i) || this.putInChestBucket(this.x + 1, this.y, this.z, i) || this.putInChestBucket(this.x - 1, this.y, this.z, i) || this.putInChestBucket(this.x, this.y, this.z + 1, i) || this.putInChestBucket(this.x, this.y, this.z - 1, i);
	}

	public boolean putInChestBucket(int i, int j, int k, int l) {
		if (!(this.world.getTileEntity(i, j, k) instanceof TileEntityChest)) {
			return false;
		}
		else {
			TileEntityChest tileentitychest = (TileEntityChest) this.world.getTileEntity(i, j, k);

			for (int i1 = 0; i1 < tileentitychest.getSize(); ++i1) {
				if (tileentitychest.getItem(i1) != null && tileentitychest.getItem(i1).id == Item.BUCKET.id) {
					if (l == Block.STATIONARY_WATER.id) {
						tileentitychest.getItem(i1).id = Item.WATER_BUCKET.id;
					}

					if (l == Block.STATIONARY_LAVA.id) {
						tileentitychest.getItem(i1).id = Item.LAVA_BUCKET.id;
					}

					return true;
				}
			}

			return false;
		}
	}

	public void fountain() {
		if (this.world.getTime() % 10L == 0L) {
			--this.pumpCharge;
		}

		int i = 0;

		for (int j = 1; j < 4; ++j) {
			if (this.world.getTypeId(this.x, this.y + j, this.z) == 0 || this.world.getTypeId(this.x, this.y + j, this.z) == Block.WATER.id) {
				i = j;
			}
		}

		if (i != 0) {
			this.world.setTypeIdAndData(this.x, this.y + i, this.z, Block.WATER.id, 1);
		}

	}

	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		this.pumpCharge = nbttagcompound.getShort("pumpCharge");
	}

	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setShort("pumpCharge", this.pumpCharge);
	}

	public boolean isPumpReady() {
		return this.pumpCharge >= 200;
	}

	public boolean canHarvest() {
		if (!this.isPumpReady()) {
			return false;
		}
		else {
			return this.inventory[0] != null && (this.inventory[0].id == Ic2Items.cell.id || this.inventory[0].id == Item.BUCKET.id) || this.isBucketInChestAvaible();
		}
	}

	public boolean isBucketInChestAvaible() {
		return this.isBucketInChestAvaible(this.x, this.y + 1, this.z) || this.isBucketInChestAvaible(this.x, this.y - 1, this.z) || this.isBucketInChestAvaible(this.x + 1, this.y, this.z) || this.isBucketInChestAvaible(this.x - 1, this.y, this.z) || this.isBucketInChestAvaible(this.x, this.y, this.z + 1) || this.isBucketInChestAvaible(this.x, this.y, this.z - 1);
	}

	public boolean isBucketInChestAvaible(int i, int j, int k) {
		if (!(this.world.getTileEntity(i, j, k) instanceof TileEntityChest)) {
			return false;
		}
		else {
			TileEntityChest tileentitychest = (TileEntityChest) this.world.getTileEntity(i, j, k);

			for (int l = 0; l < tileentitychest.getSize(); ++l) {
				if (tileentitychest.getItem(l) != null && tileentitychest.getItem(l).id == Item.BUCKET.id) {
					return true;
				}
			}

			return false;
		}
	}

	public boolean deliverLavaToGeo() {
		int i = 3000;
		if (i > 0 && this.world.getTileEntity(this.x, this.y + 1, this.z) instanceof TileEntityGeoGenerator) {
			i = ((TileEntityGeoGenerator) this.world.getTileEntity(this.x, this.y + 1, this.z)).distributeLava(i);
		}

		if (i > 0 && this.world.getTileEntity(this.x, this.y - 1, this.z) instanceof TileEntityGeoGenerator) {
			i = ((TileEntityGeoGenerator) this.world.getTileEntity(this.x, this.y - 1, this.z)).distributeLava(i);
		}

		if (i > 0 && this.world.getTileEntity(this.x + 1, this.y, this.z) instanceof TileEntityGeoGenerator) {
			i = ((TileEntityGeoGenerator) this.world.getTileEntity(this.x + 1, this.y, this.z)).distributeLava(i);
		}

		if (i > 0 && this.world.getTileEntity(this.x - 1, this.y, this.z) instanceof TileEntityGeoGenerator) {
			i = ((TileEntityGeoGenerator) this.world.getTileEntity(this.x - 1, this.y, this.z)).distributeLava(i);
		}

		if (i > 0 && this.world.getTileEntity(this.x, this.y, this.z + 1) instanceof TileEntityGeoGenerator) {
			i = ((TileEntityGeoGenerator) this.world.getTileEntity(this.x, this.y, this.z + 1)).distributeLava(i);
		}

		if (i > 0 && this.world.getTileEntity(this.x, this.y, this.z - 1) instanceof TileEntityGeoGenerator) {
			i = ((TileEntityGeoGenerator) this.world.getTileEntity(this.x, this.y, this.z - 1)).distributeLava(i);
		}

		return i < 2980;
	}

	public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
		return new ContainerPump(entityhuman, this);
	}

	public String getGuiClassName(EntityHuman entityhuman) {
		return "GuiPump";
	}

	public void onGuiClosed(EntityHuman entityhuman) {
	}

	public void onNetworkUpdate(String s) {
		if (s.equals("active") && this.prevActive != this.getActive()) {
			if (this.audioSource == null) {
				this.audioSource = AudioManager.createSource(this, PositionSpec.Center, "Machines/PumpOp.ogg", true, false, AudioManager.defaultVolume);
			}

			if (this.getActive()) {
				if (this.audioSource != null) {
					this.audioSource.play();
				}
			}
			else if (this.audioSource != null) {
				this.audioSource.stop();
			}
		}

		super.onNetworkUpdate(s);
	}

	public int getStartInventorySide(int i) {
		switch (i) {
			case 0:
				return 1;
			case 1:
			default:
				return 0;
		}
	}

	public int getSizeInventorySide(int i) {
		return 1;
	}
}
