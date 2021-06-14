package ic2.common;

import forge.ISidedInventory;
import ic2.api.Direction;
import ic2.api.*;
import ic2.platform.Platform;
import net.minecraft.server.*;

public abstract class TileEntityElectricBlock extends TileEntityMachine
    implements IEnergySink, IEnergySource, IHasGui, ISidedInventory, INetworkClientTileEntityEventListener {
  public static byte redstoneModes = 6;
  public LocaleLanguage translate;
  public int tier;
  public int output;
  public int maxStorage;
  public int energy = 0;
  public byte redstoneMode = 0;
  public boolean addedToEnergyNet = false;
  
  public TileEntityElectricBlock(int i, int j, int k) {
    super(2);
    this.tier = i;
    this.output = j;
    this.maxStorage = k;
    this.translate = LocaleLanguage.a();
  }
  
  public String getNameByTier() {
    switch (this.tier) {
      case 1:
        return this.translate.b("blockBatBox.name");
      case 2:
        return this.translate.b("blockMFE.name");
      case 3:
        return this.translate.b("blockMFSU.name");
      default:
        return null;
    }
  }
  
  public float getChargeLevel() {
    float f = (float) this.energy / (float) this.maxStorage;
    if (f > 1.0F) {
      f = 1.0F;
    }
    
    return f;
  }
  
  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);
    this.setActiveWithoutNotify(nbttagcompound.getBoolean("active"));
    this.energy = nbttagcompound.getInt("energy");
    if (this.maxStorage > Integer.MAX_VALUE) {
      this.energy *= 10;
    }
    
    this.redstoneMode = nbttagcompound.getByte("redstoneMode");
  }
  
  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    int i = this.energy;
    if (this.maxStorage > Integer.MAX_VALUE) {
      i /= 10;
    }
    
    nbttagcompound.setInt("energy", i);
    nbttagcompound.setBoolean("active", this.getActive());
    nbttagcompound.setByte("redstoneMode", this.redstoneMode);
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
    boolean flag = false;
    int j;
    if (this.energy > 0 && this.inventory[0] != null && Item.byId[this.inventory[0].id] instanceof IElectricItem) {
      j = ElectricItem.charge(this.inventory[0], this.energy, this.tier, false, false);
      this.energy -= j;
      flag = j > 0;
    }
    
    if (this.demandsEnergy() && this.inventory[1] != null) {
      if (Item.byId[this.inventory[1].id] instanceof IElectricItem) {
        IElectricItem ielectricitem = (IElectricItem) Item.byId[this.inventory[1].id];
        if (ielectricitem.canProvideEnergy()) {
          int k = ElectricItem.discharge(this.inventory[1], this.maxStorage - this.energy, this.tier, false, false);
          this.energy += k;
          flag = k > 0;
        }
      }
      else {
        j = this.inventory[1].id;
        char c = 0;
        if (j == Item.REDSTONE.id) {
          c = 500;
        }
  
        if (j == Ic2Items.suBattery.id) {
          c = 1000;
        }
  
        if (c > 0 && c <= this.maxStorage - this.energy) {
          --this.inventory[1].count;
          if (this.inventory[1].count <= 0) {
            this.inventory[1] = null;
          }
    
          this.energy += c;
        }
      }
    }
    
    boolean flag1 = false;
    if (this.energy >= this.maxStorage) {
      flag1 = true;
    }
    
    this.setActive(flag1);
    this.world.applyPhysics(this.x, this.y, this.z, this.world.getTypeId(this.x, this.y, this.z));
    if (this.energy >= this.output && (this.redstoneMode != 4 || !this.world.isBlockPowered(this.x, this.y, this.z)) &&
        (this.redstoneMode != 5 || !this.world.isBlockPowered(this.x, this.y, this.z) ||
            this.energy >= this.maxStorage)) {
      this.energy -= this.output - EnergyNet.getForWorld(this.world).emitEnergyFrom(this, this.output);
    }
    
    if (flag) {
      this.update();
    }
    
  }
  
  public void onCreated() {
    super.onCreated();
    if (Platform.isSimulating()) {
      EnergyNet.getForWorld(this.world).addTileEntity(this);
      this.addedToEnergyNet = true;
    }
    
  }
  
  public boolean isAddedToEnergyNet() {
    return this.addedToEnergyNet;
  }
  
  public boolean acceptsEnergyFrom(TileEntity tileentity, Direction direction) {
    return !this.facingMatchesDirection(direction);
  }
  
  public boolean emitsEnergyTo(TileEntity tileentity, Direction direction) {
    return this.facingMatchesDirection(direction);
  }
  
  public boolean facingMatchesDirection(Direction direction) {
    return direction.toSideValue() == this.getFacing();
  }
  
  public int getMaxEnergyOutput() {
    return this.output;
  }
  
  public boolean demandsEnergy() {
    return this.energy < this.maxStorage;
  }
  
  public int injectEnergy(Direction direction, int i) {
    if (i > this.output) {
      mod_IC2.explodeMachineAt(this.world, this.x, this.y, this.z);
      return 0;
    }
    else {
      int j = i;
      if (this.energy + i >= this.maxStorage + this.output) {
        j = this.maxStorage + this.output - this.energy - 1;
      }
  
      this.energy += j;
      return i - j;
    }
  }
  
  public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
    return new ContainerElectricBlock(entityhuman, this);
  }
  
  public String getGuiClassName(EntityHuman entityhuman) {
    return "GuiElectricBlock";
  }
  
  public void onGuiClosed(EntityHuman entityhuman) {
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
  
  public boolean isEmittingRedstone() {
    switch (this.redstoneMode) {
      case 1:
        return this.energy >= this.maxStorage;
  
      case 2:
        return this.energy > this.output && this.energy < this.maxStorage;
  
      case 3:
        return this.energy < this.output;
  
      default:
        return false;
    }
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
  
  public void onNetworkEvent(EntityHuman entityhuman, int i) {
    ++this.redstoneMode;
    if (this.redstoneMode >= redstoneModes) {
      this.redstoneMode = 0;
    }
    
    switch (this.redstoneMode) {
      case 0:
        Platform.messagePlayer(entityhuman, "Redstone Behavior: Nothing");
        break;
      case 1:
        Platform.messagePlayer(entityhuman, "Redstone Behavior: Emit if full");
        break;
      case 2:
        Platform.messagePlayer(entityhuman, "Redstone Behavior: Emit if partially filled");
        break;
      case 3:
        Platform.messagePlayer(entityhuman, "Redstone Behavior: Emit if empty");
        break;
      case 4:
        Platform.messagePlayer(entityhuman, "Redstone Behavior: Do not output energy");
        break;
      case 5:
        Platform.messagePlayer(entityhuman, "Redstone Behavior: Do not output energy unless full");
    }
    
  }
}
