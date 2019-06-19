package ic2.common;

import ic2.api.Direction;
import ic2.api.IElectricItem;
import ic2.api.IEnergySource;
import ic2.platform.AudioManager;
import ic2.platform.AudioSource;
import ic2.platform.Platform;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.Item;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;

import java.util.Random;

public abstract class TileEntityBaseGenerator extends TileEntityMachine implements IEnergySource, IHasGui {
	public static Random random = new Random();
	public final short maxStorage;
	public int fuel = 0;
	public short storage = 0;
	public int production;
	public int tier = 1;
	public int ticksSinceLastActiveUpdate;
	public int activityMeter = 0;
	public boolean addedToEnergyNet = false;
	public AudioSource audioSource;

	public TileEntityBaseGenerator(int i, int j, int k) {
		super(i);
		this.production = j;
		this.maxStorage = (short) k;
		this.ticksSinceLastActiveUpdate = random.nextInt(256);
	}

	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		try {
			this.fuel = nbttagcompound.getInt("fuel");
		} catch (Throwable var3) {
			this.fuel = nbttagcompound.getShort("fuel");
		}

		this.storage = nbttagcompound.getShort("storage");
	}

	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setInt("fuel", this.fuel);
		nbttagcompound.setShort("storage", this.storage);
	}

	public int gaugeStorageScaled(int i) {
		return this.storage * i / this.maxStorage;
	}

	public abstract int gaugeFuelScaled(int var1);

	public void q_() {
		super.q_();
		boolean flag = false;
		if (this.needsFuel()) {
			flag = this.gainFuel();
		}
		boolean flag1 = this.gainEnergy();
		if (this.storage > this.maxStorage) {
			this.storage = this.maxStorage;
		}
		if (this.storage > 0) {
			int j;
			if (this.inventory[0] != null && Item.byId[this.inventory[0].id] instanceof IElectricItem) {
				j = ElectricItem.charge(this.inventory[0], this.storage, 1, false, false);
				this.storage = (short) (this.storage - j);
				if (j > 0) {
					flag = true;
				}
			}
			j = Math.min(this.production, this.storage);
			if (j > 0) {
				this.storage = (short) (this.storage + (this.sendEnergy(j) - j));
			}
		}
		if (flag) {
			this.update();
		}
		if (!this.delayActiveUpdate()) {
			this.setActive(flag1);
		}
		else {
			if (this.ticksSinceLastActiveUpdate % 256 == 0) {
				this.setActive(this.activityMeter > 0);
				this.activityMeter = 0;
			}
			if (flag1) {
				++this.activityMeter;
			}
			else {
				--this.activityMeter;
			}
			++this.ticksSinceLastActiveUpdate;
		}
	}

	public void onCreated() {
		super.onCreated();
		if (Platform.isSimulating()) {
			EnergyNet.getForWorld(this.world).addTileEntity(this);
			this.addedToEnergyNet = true;
		}

	}

	public void j() {
		if (Platform.isSimulating() && this.addedToEnergyNet) {
			EnergyNet.getForWorld(this.world).removeTileEntity(this);
			this.addedToEnergyNet = false;
		}
		if (Platform.isRendering() && this.audioSource != null) {
			AudioManager.removeSources(this);
			this.audioSource = null;
		}
		super.j();
	}

	public boolean gainEnergy() {
		if (this.isConverting()) {
			this.storage = (short) (this.storage + this.production);
			--this.fuel;
			return true;
		}
		else {
			return false;
		}
	}

	public boolean isConverting() {
		return this.fuel > 0 && this.storage + this.production <= this.maxStorage;
	}

	public boolean needsFuel() {
		return this.fuel <= 0 && this.storage + this.production <= this.maxStorage;
	}

	public abstract boolean gainFuel();

	public int sendEnergy(int i) {
		return EnergyNet.getForWorld(this.world).emitEnergyFrom(this, i);
	}

	public boolean isAddedToEnergyNet() {
		return this.addedToEnergyNet;
	}

	public boolean emitsEnergyTo(TileEntity tileentity, Direction direction) {
		return true;
	}

	public int getMaxEnergyOutput() {
		return this.production;
	}

	public abstract String getName();

	public void onGuiClosed(EntityHuman entityhuman) {
	}

	public String getOperationSoundFile() {
		return null;
	}

	public boolean delayActiveUpdate() {
		return false;
	}

	public void onNetworkUpdate(String s) {
		if (s.equals("active") && this.prevActive != this.getActive()) {
			if (this.audioSource == null && this.getOperationSoundFile() != null) {
				this.audioSource = AudioManager.createSource(this, PositionSpec.Center, this.getOperationSoundFile(), true, false, AudioManager.defaultVolume);
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

	public float getWrenchDropRate() {
		return 0.9F;
	}
}
