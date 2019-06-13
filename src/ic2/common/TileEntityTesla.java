package ic2.common;

import ic2.api.Direction;
import ic2.api.IEnergySink;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.List;

public class TileEntityTesla extends TileEntityBlock implements IEnergySink {
	public int energy = 0;
	public int ticker = 0;
	public int maxEnergy = 10000;
	public boolean addedToEnergyNet = false;

	public static int getCost() {
		return 400;
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

	public boolean canUpdate() {
		return Platform.isSimulating();
	}

	public void q_() {
		super.q_();
		if (this.energy >= getCost() && Platform.isSimulating() && this.redstoned()) {
			int i = this.energy / getCost();
			--this.energy;
			if (this.ticker++ % 32 == 0 && this.shock(i)) {
				this.energy = 0;
			}

		}
	}

	public void onCreated() {
		super.onCreated();
		if (Platform.isSimulating()) {
			EnergyNet.getForWorld(this.world).addTileEntity(this);
			this.addedToEnergyNet = true;
		}

	}

	public boolean shock(int i) {
		List list = this.world.a(EntityLiving.class, AxisAlignedBB.a((double) (this.x - 4), (double) (this.y - 4), (double) (this.z - 4), (double) (this.x + 5), (double) (this.y + 5), (double) (this.z + 5)));

		for (int j = 0; j < list.size(); ++j) {
			EntityLiving entityliving = (EntityLiving) list.get(j);
			entityliving.damageEntity(IC2DamageSource.electricity, i);

			for (int k = 0; k < i; ++k) {
				this.world.a("reddust", entityliving.locX + (double) this.world.random.nextFloat(), entityliving.locY + (double) (this.world.random.nextFloat() * 2.0F), entityliving.locZ + (double) this.world.random.nextFloat(), 0.0D, 0.0D, 1.0D);
			}
		}

		return list.size() > 0;
	}

	public boolean redstoned() {
		return this.world.isBlockIndirectlyPowered(this.x, this.y, this.z) || this.world.isBlockIndirectlyPowered(this.x, this.y, this.z);
	}

	public boolean isAddedToEnergyNet() {
		return this.addedToEnergyNet;
	}

	public boolean acceptsEnergyFrom(TileEntity tileentity, Direction direction) {
		return true;
	}

	public boolean demandsEnergy() {
		return this.energy < this.maxEnergy;
	}

	public int injectEnergy(Direction direction, int i) {
		if (i > 128) {
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
}
