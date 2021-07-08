package ic2.common;

import ic2.api.Direction;
import ic2.api.IElectricItem;
import ic2.api.IEnergySink;
import ic2.platform.Platform;
import net.minecraft.server.*;

public abstract class TileEntityElecMachine extends TileEntityMachine implements IEnergySink {
  public int energy = 0;
  public int fuelslot;
  public int maxEnergy;
  public int maxInput;
  public int tier = 0;
  public boolean addedToEnergyNet = false;
  
  public TileEntityElecMachine(int i, int j, int k, int l) {
    super(i);
    this.fuelslot = j;
    this.maxEnergy = k;
    this.maxInput = l;
    this.tier = 1;
  }
  
  public TileEntityElecMachine(int i, int j, int k, int l, int i1) {
    super(i);
    this.fuelslot = j;
    this.maxEnergy = k;
    this.maxInput = l;
    this.tier = i1;
  }
  
  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);
    this.energy = nbttagcompound.getInt("energy");
  }
  
  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.setInt("energy", this.energy);
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
  
  public boolean provideEnergy() {
    if (this.inventory[this.fuelslot] == null) {
      return false;
    }
    else {
      int i = this.inventory[this.fuelslot].id;
      if (Item.byId[i] instanceof IElectricItem) {
        if (!((IElectricItem) Item.byId[i]).canProvideEnergy()) {
          return false;
        }
        else {
          int j = ElectricItem
              .discharge(this.inventory[this.fuelslot], this.maxEnergy - this.energy, this.tier, false, false);
          this.energy += j;
          return j > 0;
        }
      }
      else if (i == Item.REDSTONE.id) {
        this.energy += this.maxEnergy;
        --this.inventory[this.fuelslot].count;
        if (this.inventory[this.fuelslot].count <= 0) {
          this.inventory[this.fuelslot] = null;
        }
  
        return true;
      }
      else if (i == Ic2Items.suBattery.id) {
        this.energy += 1000;
        --this.inventory[this.fuelslot].count;
        if (this.inventory[this.fuelslot].count <= 0) {
          this.inventory[this.fuelslot] = null;
        }
  
        return true;
      }
      else {
        return false;
      }
    }
  }
  
  public boolean isAddedToEnergyNet() {
    return this.addedToEnergyNet;
  }
  
  public boolean demandsEnergy() {
    return this.energy <= this.maxEnergy - this.maxInput;
  }
  
  public int injectEnergy(Direction direction, int i) {
    if (i > this.maxInput) {
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
  
  public boolean acceptsEnergyFrom(TileEntity tileentity, Direction direction) {
    return true;
  }
  
  public boolean isRedstonePowered() {
    return this.world.isBlockIndirectlyPowered(this.x, this.y, this.z);
  }
}
