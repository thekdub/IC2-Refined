package ic2.common;

import ic2.api.Direction;
import ic2.api.IEnergySink;
import ic2.platform.Platform;
import net.minecraft.server.Entity;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;

public class TileEntityLuminator extends TileEntity implements IEnergySink {
	public int energy = 0;
	public int ticker = -1;
	public boolean ignoreBlockStay = false;
	public boolean addedToEnergyNet = false;

	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		this.energy = nbttagcompound.getShort("energy");
	}

	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setShort("energy", (short) this.energy);
	}

	public void j() {
		if (Platform.isSimulating() && this.addedToEnergyNet) {
			EnergyNet.getForWorld(this.world).removeTileEntity(this);
			this.addedToEnergyNet = false;
		}

		super.j();
	}

	public void q_() {
		if (Platform.isSimulating()) {
			if (!this.addedToEnergyNet) {
				EnergyNet.getForWorld(this.world).addTileEntity(this);
				this.addedToEnergyNet = true;
			}
			if (++this.ticker % 20 == 0) { //Changed to once every 4 ticks to once every 20 ticks
				if (this.energy > 0)
					this.energy -= 5; //Energy consumption updated to keep to 0.25EU/t
				if (this.energy <= 0 && this.world.getTypeId(x, y, z) == Ic2Items.activeLuminator.id) {
					this.world.setTypeIdAndData(this.x, this.y, this.z, Ic2Items.luminator.id, this.world.getData(this.x, this.y, this.z));
					this.energy = 0;
				}
			}
		}

	}

	public boolean isAddedToEnergyNet() {
		return this.addedToEnergyNet;
	}

	public boolean acceptsEnergyFrom(TileEntity tileentity, Direction direction) {
		return tileentity instanceof TileEntityCable;
	}

	public boolean demandsEnergy() {
		return this.energy < this.getMaxEnergy();
	}

	public int injectEnergy(Direction direction, int i) {
		if (i > 32) {
			this.poof();
			return 0;
		}
		else if (i <= 0) {
			return 0;
		}
		else if (this.world.getTypeId(this.x, this.y, this.z) == Ic2Items.luminator.id && this.energy > 32) {
			this.world.setTypeIdAndData(this.x, this.y, this.z, Ic2Items.activeLuminator.id, this.world.getData(this.x, this.y, this.z));
			TileEntityLuminator luminator = (TileEntityLuminator) this.world.getTileEntity(x, y, z);
			luminator.addEnergy(this.energy);
		}
		this.energy += i;
		int j = 0;
		if (this.energy >= this.getMaxEnergy()) {
			j = this.energy - this.getMaxEnergy();
			this.energy = this.getMaxEnergy();
		}
		return j;
	}

	public void addEnergy(int i) {
		energy += i;
		if (energy > getMaxEnergy())
			energy = getMaxEnergy();
	}

	public int getMaxEnergy() {
		return 10000;
	}

	public void poof() {
		this.world.setTypeId(this.x, this.y, this.z, 0);
		ExplosionIC2 explosionic2 = new ExplosionIC2(this.world, null, 0.5D + (double) this.x, 0.5D + (double) this.y, 0.5D + (double) this.z, 0.5F, 0.85F, 2.0F);
		explosionic2.doExplosion();
	}

	public boolean canCableConnectFrom(int i, int j, int k) {
		int l = this.world.getData(this.x, this.y, this.z);
		switch (l) {
			case 0:
				return i == this.x && j == this.y + 1 && k == this.z;

			case 1:
				return i == this.x && j == this.y - 1 && k == this.z;

			case 2:
				return i == this.x && j == this.y && k == this.z + 1;

			case 3:
				return i == this.x && j == this.y && k == this.z - 1;

			case 4:
				return i == this.x + 1 && j == this.y && k == this.z;

			case 5:
				return i == this.x - 1 && j == this.y && k == this.z;

			default:
				return false;
		}
	}
}
