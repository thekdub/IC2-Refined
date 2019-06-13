package ic2.common;

import ic2.api.Direction;
import ic2.api.IEnergySink;
import ic2.platform.Platform;
import net.minecraft.server.*;

public class TileEntityCropmatron extends TileEntityMachine implements IEnergySink, IHasGui {
	public static int maxInput = 32;
	public int energy = 0;
	public int ticker = 0;
	public int maxEnergy = 1000;
	public int scanX = -4;
	public int scanY = -1;
	public int scanZ = -4;
	public boolean addedToEnergyNet = false;

	public TileEntityCropmatron() {
		super(9);
	}

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
		super.q_();
		if (this.energy >= 31) {
			this.scan();
		}

	}

	public void onCreated() {
		super.onCreated();
		if (Platform.isSimulating()) {
			EnergyNet.getForWorld(this.world).addTileEntity(this);
			this.addedToEnergyNet = true;
		}

	}

	public void scan() {
		++this.scanX;
		if (this.scanX > 4) {
			this.scanX = -4;
			++this.scanZ;
			if (this.scanZ > 4) {
				this.scanZ = -4;
				++this.scanY;
				if (this.scanY > 1) {
					this.scanY = -1;
				}
			}
		}

		--this.energy;
		TileEntity tileentity = this.world.getTileEntity(this.x + this.scanX, this.y + this.scanY, this.z + this.scanZ);
		if (tileentity instanceof TileEntityCrop) {
			TileEntityCrop tileentitycrop = (TileEntityCrop) tileentity;
			this.updateSlots();
			if (this.inventory[0] != null && this.inventory[0].id == Ic2Items.fertilizer.id && tileentitycrop.applyFertilizer(false)) {
				this.energy -= 10;
				--this.inventory[0].count;
				this.checkStackSizeZero(0);
			}

			if (this.inventory[3] != null && this.inventory[3].id == Ic2Items.hydratingCell.id && tileentitycrop.applyHydration(false, this.inventory[3])) {
				this.energy -= 10;
				this.checkStackSizeZero(3);
			}

			if (this.inventory[6] != null && this.inventory[6].id == Ic2Items.weedEx.id && tileentitycrop.applyWeedEx(false)) {
				this.energy -= 10;
				this.inventory[6].damage(1, null);
				if (this.inventory[6].getData() >= this.inventory[6].i()) {
					--this.inventory[6].count;
					this.checkStackSizeZero(6);
				}
			}
		}

	}

	public void checkStackSizeZero(int i) {
		if (this.inventory[i] != null && this.inventory[i].count <= 0) {
			this.inventory[i] = null;
		}

	}

	public void updateSlots() {
		this.moveFrom(1, 0);
		this.moveFrom(2, 1);
		this.moveFrom(4, 3);
		this.moveFrom(5, 4);
		this.moveFrom(7, 6);
		this.moveFrom(8, 7);
	}

	public void moveFrom(int i, int j) {
		if (this.inventory[i] != null && this.inventory[j] == null) {
			this.inventory[j] = this.inventory[i];
			this.inventory[i] = null;
		}

	}

	public boolean acceptsEnergyFrom(TileEntity tileentity, Direction direction) {
		return true;
	}

	public boolean isAddedToEnergyNet() {
		return this.addedToEnergyNet;
	}

	public boolean demandsEnergy() {
		return this.energy <= this.maxEnergy - maxInput;
	}

	public int gaugeEnergyScaled(int i) {
		if (this.energy <= 0) {
			return 0;
		}
		else {
			int j = this.energy * i / this.maxEnergy;
			if (j > i) {
				j = i;
			}

			return j;
		}
	}

	public int injectEnergy(Direction direction, int i) {
		if (i > maxInput) {
			mod_IC2.explodeMachineAt(this.world, this.x, this.y, this.z);
			return 0;
		}
		else {
			this.energy += i;
			int j = 0;
			if (this.energy > this.maxEnergy) {
				j = this.energy - this.maxEnergy;
				this.energy = this.maxEnergy;
			}

			return j;
		}
	}

	public String getName() {
		return "Crop-Matron";
	}

	public String getGuiClassName(EntityHuman entityhuman) {
		return "GuiCropmatron";
	}

	public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
		return new ContainerCropmatron(entityhuman, this);
	}

	public void onGuiClosed(EntityHuman entityhuman) {
	}
}
