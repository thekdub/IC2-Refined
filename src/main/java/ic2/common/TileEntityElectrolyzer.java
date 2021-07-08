package ic2.common;

import forge.ISidedInventory;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.NBTTagCompound;

import java.util.Random;

public class TileEntityElectrolyzer extends TileEntityMachine implements IHasGui, ISidedInventory {
  public static Random randomizer = new Random();
  public short energy = 0;
  public TileEntityElectricBlock mfe = null;
  public int ticker;
  
  public TileEntityElectrolyzer() {
    super(2);
    this.ticker = randomizer.nextInt(16);
  }
  
  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);
    this.energy = nbttagcompound.getShort("energy");
  }
  
  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.setShort("energy", this.energy);
  }
  
  public String getName() {
    return "Electrolyzer";
  }
  
  public void q_() {
    super.q_();
    boolean flag = false;
    boolean flag1 = false;
    if (this.ticker++ % 16 == 0) {
      this.mfe = this.lookForMFE();
    }
    
    if (this.mfe != null) {
      if (this.shouldDrain() && this.canDrain()) {
        flag = this.drain();
        flag1 = true;
      }
      
      if (this.shouldPower() && (this.canPower() || this.energy > 0)) {
        flag = this.power();
        flag1 = true;
      }
      
      if (this.getActive() != flag1) {
        this.setActive(flag1);
        flag = true;
      }
      
      if (flag) {
        this.update();
      }
      
    }
  }
  
  public boolean shouldDrain() {
    return this.mfe != null && (double) this.mfe.energy / (double) this.mfe.maxStorage >= 0.7D;
  }
  
  public boolean shouldPower() {
    return this.mfe != null && (double) this.mfe.energy / (double) this.mfe.maxStorage <= 0.3D;
  }
  
  public boolean canDrain() {
    return this.inventory[0] != null && this.inventory[0].doMaterialsMatch(Ic2Items.waterCell) &&
        (this.inventory[1] == null || this.inventory[1].doMaterialsMatch(Ic2Items.electrolyzedWaterCell) &&
            this.inventory[1].count < this.inventory[1].getMaxStackSize());
  }
  
  public boolean canPower() {
    return (this.inventory[0] == null || this.inventory[0].doMaterialsMatch(Ic2Items.waterCell) &&
        this.inventory[0].count < this.inventory[0].getMaxStackSize()) && this.inventory[1] != null &&
        this.inventory[1].doMaterialsMatch(Ic2Items.electrolyzedWaterCell);
  }
  
  public boolean drain() {
    TileEntityElectricBlock var10000 = this.mfe;
    var10000.energy -= this.processRate();
    this.energy = (short) (this.energy + this.processRate());
    if (this.energy >= 20000) {
      this.energy = (short) (this.energy - 20000);
      --this.inventory[0].count;
      if (this.inventory[0].count <= 0) {
        this.inventory[0] = null;
      }
  
      if (this.inventory[1] == null) {
        this.inventory[1] = Ic2Items.electrolyzedWaterCell.cloneItemStack();
      }
      else {
        ++this.inventory[1].count;
      }
  
      return true;
    }
    else {
      return false;
    }
  }
  
  public boolean power() {
    if (this.energy > 0) {
      int i = this.processRate();
      if (i > this.energy) {
        i = this.energy;
      }
  
      this.energy = (short) (this.energy - i);
      TileEntityElectricBlock var2 = this.mfe;
      var2.energy += i;
      return false;
    }
    else {
      this.energy = (short) (this.energy + 12000 + 2000 * this.mfe.tier);
      --this.inventory[1].count;
      if (this.inventory[1].count <= 0) {
        this.inventory[1] = null;
      }
  
      if (this.inventory[0] == null) {
        this.inventory[0] = Ic2Items.waterCell.cloneItemStack();
      }
      else {
        ++this.inventory[0].count;
      }
  
      return true;
    }
  }
  
  public int processRate() {
    switch (this.mfe.tier) {
      case 2:
        return 8;
      case 3:
        return 32;
      default:
        return 2;
    }
  }
  
  public TileEntityElectricBlock lookForMFE() {
    if (this.world.getTileEntity(this.x, this.y - 1, this.z) instanceof TileEntityElectricBlock) {
      return (TileEntityElectricBlock) this.world.getTileEntity(this.x, this.y - 1, this.z);
    }
    else if (this.world.getTileEntity(this.x, this.y + 1, this.z) instanceof TileEntityElectricBlock) {
      return (TileEntityElectricBlock) this.world.getTileEntity(this.x, this.y + 1, this.z);
    }
    else if (this.world.getTileEntity(this.x - 1, this.y, this.z) instanceof TileEntityElectricBlock) {
      return (TileEntityElectricBlock) this.world.getTileEntity(this.x - 1, this.y, this.z);
    }
    else if (this.world.getTileEntity(this.x + 1, this.y, this.z) instanceof TileEntityElectricBlock) {
      return (TileEntityElectricBlock) this.world.getTileEntity(this.x + 1, this.y, this.z);
    }
    else if (this.world.getTileEntity(this.x, this.y, this.z - 1) instanceof TileEntityElectricBlock) {
      return (TileEntityElectricBlock) this.world.getTileEntity(this.x, this.y, this.z - 1);
    }
    else {
      return this.world.getTileEntity(this.x, this.y, this.z + 1) instanceof TileEntityElectricBlock ?
          (TileEntityElectricBlock) this.world.getTileEntity(this.x, this.y, this.z + 1) : null;
    }
  }
  
  public int gaugeEnergyScaled(int i) {
    if (this.energy <= 0) {
      return 0;
    }
    else {
      int j = this.energy * i / 20000;
      if (j > i) {
        j = i;
      }
  
      return j;
    }
  }
  
  public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
    return new ContainerElectrolyzer(entityhuman, this);
  }
  
  public String getGuiClassName(EntityHuman entityhuman) {
    return "GuiElectrolyzer";
  }
  
  public void onGuiClosed(EntityHuman entityhuman) {
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
