package ic2.common;

import ic2.api.Direction;
import ic2.api.IEnergySink;
import ic2.api.IEnergySource;
import ic2.platform.Platform;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;
import net.minecraft.server.mod_IC2;

public abstract class TileEntityTransformer extends TileEntityBlock implements IEnergySink, IEnergySource {
  public int lowOutput;
  public int highOutput;
  public int maxStorage;
  public int energy = 0;
  public boolean redstone = false;
  public boolean addedToEnergyNet = false;

  public TileEntityTransformer(int i, int j, int k) {
    this.lowOutput = i;
    this.highOutput = j;
    this.maxStorage = k;
  }

  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);
    this.energy = nbttagcompound.getInt("energy");
  }

  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.setInt("energy", this.energy);
  }

  public boolean canUpdate() {
    return Platform.isSimulating();
  }

  public void q_() {
    super.q_();
    this.updateRedstone();
    if (this.redstone) {
      if (this.energy >= this.highOutput) {
        this.energy -= this.highOutput - EnergyNet.getForWorld(this.world).emitEnergyFrom(this, this.highOutput);
      }
    }
    else {
      for (int i = 0; i < 4 && this.energy >= this.lowOutput; ++i) {
        this.energy -= this.lowOutput - EnergyNet.getForWorld(this.world).emitEnergyFrom(this, this.lowOutput);
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

  public void j() {
    if (Platform.isSimulating() && this.addedToEnergyNet) {
      EnergyNet.getForWorld(this.world).removeTileEntity(this);
      this.addedToEnergyNet = false;
    }

    super.j();
  }

  public void updateRedstone() {
    boolean flag = this.world.isBlockIndirectlyPowered(this.x, this.y, this.z);
    if (flag != this.redstone) {
      EnergyNet.getForWorld(this.world).removeTileEntity(this);
      this.addedToEnergyNet = false;
      this.redstone = flag;
      EnergyNet.getForWorld(this.world).addTileEntity(this);
      this.addedToEnergyNet = true;
      this.setActive(this.redstone);
    }

  }

  public boolean isAddedToEnergyNet() {
    return this.addedToEnergyNet;
  }

  public boolean acceptsEnergyFrom(TileEntity tileentity, Direction direction) {
    if (this.redstone) {
      return !this.facingMatchesDirection(direction);
    }
    else {
      return this.facingMatchesDirection(direction);
    }
  }

  public boolean emitsEnergyTo(TileEntity tileentity, Direction direction) {
    if (this.redstone) {
      return this.facingMatchesDirection(direction);
    }
    else {
      return !this.facingMatchesDirection(direction);
    }
  }

  public boolean facingMatchesDirection(Direction direction) {
    return direction.toSideValue() == this.getFacing();
  }

  public int getMaxEnergyOutput() {
    return this.redstone ? this.highOutput : this.lowOutput;
  }

  public boolean demandsEnergy() {
    return this.energy < this.maxStorage;
  }

  public int injectEnergy(Direction direction, int i) {
    if ((!this.redstone || i <= this.lowOutput) && (this.redstone || i <= this.highOutput || this.highOutput == 2048)) {
      int j = i;
      if (this.energy + i >= this.maxStorage + this.highOutput) {
        j = this.maxStorage + this.highOutput - this.energy - 1;
      }

      this.energy += j;
      return i - j;
    }
    else {
      mod_IC2.explodeMachineAt(this.world, this.x, this.y, this.z);
      return 0;
    }
  }

  public boolean wrenchCanSetFacing(EntityHuman entityhuman, int i) {
    return this.getFacing() != i;
  }

  public void setFacing(short word0) {
    if (this.addedToEnergyNet) {
      EnergyNet.getForWorld(this.world).removeTileEntity(this);
    }

    this.addedToEnergyNet = false;
    super.setFacing(word0);
    EnergyNet.getForWorld(this.world).addTileEntity(this);
    this.addedToEnergyNet = true;
  }
}
