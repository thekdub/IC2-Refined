package ic2.common;

import forge.ISidedInventory;
import ic2.api.Direction;
import ic2.api.INetworkTileEntityEventListener;
import ic2.platform.NetworkManager;
import ic2.platform.*;
import net.minecraft.server.*;

public abstract class TileEntityElectricMachine extends TileEntityElecMachine
    implements IHasGui, INetworkTileEntityEventListener, ISidedInventory {
  static final boolean $assertionsDisabled = !TileEntityElectricMachine.class.desiredAssertionStatus();
  private static final int EventStart = 0;
  private static final int EventInterrupt = 1;
  private static final int EventStop = 2;
  public short progress = 0;
  public int defaultEnergyConsume;
  public int defaultOperationLength;
  public int defaultMaxInput;
  public int defaultEnergyStorage;
  public int energyConsume;
  public int operationLength;
  public float serverChargeLevel;
  public float serverProgress;
  public AudioSource audioSource;
  
  public TileEntityElectricMachine(int i, int j, int k, int l) {
    super(i + 4, 1, j * k + l - 1, l);
    this.defaultEnergyConsume = this.energyConsume = j;
    this.defaultOperationLength = this.operationLength = k;
    this.defaultMaxInput = this.maxInput;
    this.defaultEnergyStorage = j * k;
  }
  
  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);
    this.progress = nbttagcompound.getShort("progress");
  }
  
  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.setShort("progress", this.progress);
  }
  
  public float getChargeLevel() {
    float f;
    if (Platform.isSimulating()) {
      f = (float) this.energy / (float) (this.maxEnergy - this.maxInput + 1);
      if (f > 1.0F) {
        f = 1.0F;
      }
    }
    else {
      f = this.serverChargeLevel;
    }
    
    return f;
  }
  
  public void setChargeLevel(float f) {
    if (!$assertionsDisabled && Platform.isSimulating()) {
      throw new AssertionError();
    }
    else {
      this.serverChargeLevel = f;
    }
  }
  
  public float getProgress() {
    float f;
    if (Platform.isSimulating()) {
      f = (float) this.progress / (float) this.operationLength;
      if (f > 1.0F) {
        f = 1.0F;
      }
    }
    else {
      f = this.serverProgress;
    }
    
    return f;
  }
  
  public void setProgress(float f) {
    if (!$assertionsDisabled && Platform.isSimulating()) {
      throw new AssertionError();
    }
    else {
      this.serverProgress = f;
    }
  }
  
  public void q_() {
    super.q_();
    boolean flag = this.canOperate();
    if (flag) {
      this.setOverclockRates();
    }
    
    boolean flag1 = false;
    if (this.energy <= this.energyConsume * this.operationLength && flag) {
      flag1 = this.provideEnergy();
    }
    
    boolean flag2 = this.getActive();
    if (this.progress >= this.operationLength) {
      this.operate();
      flag1 = true;
      this.progress = 0;
      flag2 = false;
      NetworkManager.initiateTileEntityEvent(this, 2, true);
    }
    
    flag = this.canOperate();
    if (flag2 && this.progress != 0) {
      if (!flag || this.energy < this.energyConsume) {
        if (!flag) {
          this.progress = 0;
        }
  
        flag2 = false;
        NetworkManager.initiateTileEntityEvent(this, 1, true);
      }
    }
    else if (flag) {
      if (this.energy >= this.energyConsume) {
        flag2 = true;
        NetworkManager.initiateTileEntityEvent(this, 0, true);
      }
    }
    else {
      this.progress = 0;
    }
    
    if (flag2) {
      ++this.progress;
      this.energy -= this.energyConsume;
    }
    
    if (flag1) {
      this.update();
    }
    
    if (flag2 != this.getActive()) {
      this.setActive(flag2);
    }
    
  }
  
  public void j() {
    super.j();
    if (Platform.isRendering() && this.audioSource != null) {
      AudioManager.removeSources(this);
      this.audioSource = null;
    }
    
  }
  
  public int injectEnergy(Direction direction, int i) {
    this.setOverclockRates();
    return super.injectEnergy(direction, i);
  }
  
  public void setOverclockRates() {
    int i = 0;
    int j = 0;
    int k = 0;
    
    for (int l = 0; l < 4; ++l) {
      ItemStack itemstack = this.inventory[l + this.inventory.length - 4];
      if (itemstack != null) {
        if (itemstack.doMaterialsMatch(Ic2Items.overclockerUpgrade)) {
          i += itemstack.count;
        }
        else if (itemstack.doMaterialsMatch(Ic2Items.transformerUpgrade)) {
          j += itemstack.count;
        }
        else if (itemstack.doMaterialsMatch(Ic2Items.energyStorageUpgrade)) {
          k += itemstack.count;
        }
      }
    }
    
    if (i > 32) {
      i = 32;
    }
    
    if (j > 10) {
      j = 10;
    }
    
    this.energyConsume = (int) ((double) this.defaultEnergyConsume * Math.pow(1.6D, i));
    this.operationLength = (int) Math.round((double) this.defaultOperationLength * Math.pow(0.7D, i));
    this.maxInput = this.defaultMaxInput * (int) Math.pow(4.0D, j);
    this.maxEnergy = this.defaultEnergyStorage + k * 10000 + this.maxInput - 1;
    this.tier = j + 1;
    if (this.operationLength < 1) {
      this.operationLength = 1;
    }
    
  }
  
  public boolean provideEnergy() {
    if (this.inventory[this.fuelslot] != null && this.inventory[this.fuelslot].getItem() == Item.REDSTONE) {
      this.energy += this.defaultEnergyConsume * this.defaultOperationLength;
      --this.inventory[this.fuelslot].count;
      if (this.inventory[this.fuelslot].count <= 0) {
        this.inventory[this.fuelslot] = null;
      }
  
      return true;
    }
    else {
      return super.provideEnergy();
    }
  }
  
  public void operate() {
    if (this.canOperate()) {
      ItemStack itemstack;
      if (this.inventory[0].getItem().k()) {
        itemstack = this.getResultFor(this.inventory[0], false).cloneItemStack();
        this.inventory[0] = new ItemStack(this.inventory[0].getItem().j());
      }
      else {
        itemstack = this.getResultFor(this.inventory[0], true).cloneItemStack();
      }
  
      if (this.inventory[0].count <= 0) {
        this.inventory[0] = null;
      }
  
      if (this.inventory[2] == null) {
        this.inventory[2] = itemstack;
      }
      else {
        ItemStack var10000 = this.inventory[2];
        var10000.count += itemstack.count;
      }
  
    }
  }
  
  public boolean canOperate() {
    if (this.inventory[0] == null) {
      return false;
    }
    else {
      ItemStack itemstack = this.getResultFor(this.inventory[0], false);
      if (itemstack == null) {
        return false;
      }
      else if (this.inventory[2] == null) {
        return true;
      }
      else if (!this.inventory[2].doMaterialsMatch(itemstack)) {
        return false;
      }
      else {
        return this.inventory[2].count + itemstack.count <= this.inventory[2].getMaxStackSize();
      }
    }
  }
  
  public abstract ItemStack getResultFor(ItemStack var1, boolean var2);
  
  public abstract String getName();
  
  public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
    return new ContainerElectricMachine(entityhuman, this);
  }
  
  public void onGuiClosed(EntityHuman entityhuman) {
  }
  
  public String getStartSoundFile() {
    return null;
  }
  
  public String getInterruptSoundFile() {
    return null;
  }
  
  public void onNetworkEvent(int i) {
    if (this.audioSource == null && this.getStartSoundFile() != null) {
      this.audioSource = AudioManager.createSource(this, this.getStartSoundFile());
    }
    
    switch (i) {
      case 0:
        if (this.audioSource != null) {
          this.audioSource.play();
        }
        break;
      case 1:
        if (this.audioSource != null) {
          this.audioSource.stop();
          if (this.getInterruptSoundFile() != null) {
            AudioManager.playOnce(this, this.getInterruptSoundFile());
          }
        }
        break;
      case 2:
        if (this.audioSource != null) {
          this.audioSource.stop();
        }
    }
    
  }
  
  public int getStartInventorySide(int i) {
    switch (i) {
      case 0:
        return 1;
      case 1:
        return 0;
      default:
        return 2;
    }
  }
  
  public int getSizeInventorySide(int i) {
    return 1;
  }
}
