package ic2.common;

import ic2.api.Direction;
import ic2.api.IEnergySink;
import ic2.platform.NetworkManager;
import ic2.platform.Platform;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.TileEntity;
import net.minecraft.server.mod_IC2;

public class TileEntityMagnetizer extends TileEntityBlock implements IEnergySink {
  public int energy = 0;
  public int ticker = 0;
  public int maxEnergy = 100;
  public boolean addedToEnergyNet = false;

  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);
    this.energy = nbttagcompound.getShort("energy");
  }

  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.setShort("energy", (short) this.energy);
  }

  public boolean canUpdate() {
    return Platform.isSimulating();
  }

  public void q_() {
    super.q_();
    if (this.ticker-- <= 0) {
      boolean flag = false;

      int j;
      int l;
      for (j = this.y - 1; j > 0 && j >= this.y - 20 && this.energy > 0 && this.world.getTypeId(this.x, j, this.z) == Ic2Items.ironFence.id; --j) {
        l = 15 - this.world.getData(this.x, j, this.z);
        if (l > 0) {
          flag = true;
          if (l > this.energy) {
            this.energy = l;
          }

          this.world.setData(this.x, j, this.z, this.world.getData(this.x, j, this.z) + l);
          NetworkManager.announceBlockUpdate(this.world, this.x, j, this.z);
          this.energy -= l;
        }
      }

      for (j = this.y + 1; j < mod_IC2.getWorldHeight(this.world) && j <= this.y + 20 && this.energy > 0 && this.world.getTypeId(this.x, j, this.z) == Ic2Items.ironFence.id; ++j) {
        l = 15 - this.world.getData(this.x, j, this.z);
        if (l > 0) {
          flag = true;
          if (l > this.energy) {
            this.energy = l;
          }

          this.world.setData(this.x, j, this.z, this.world.getData(this.x, j, this.z) + l);
          NetworkManager.announceBlockUpdate(this.world, this.x, j, this.z);
          this.energy -= l;
        }
      }

      if (!flag) {
        this.ticker = 10;
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
    if (i > 32) {
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
