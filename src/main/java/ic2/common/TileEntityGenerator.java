package ic2.common;

import forge.ISidedInventory;
import net.minecraft.server.*;

public class TileEntityGenerator extends TileEntityBaseGenerator implements ISidedInventory {
  public int itemFuelTime = 0;
  
  public TileEntityGenerator() {
    super(2, mod_IC2.energyGeneratorBase, 4000);
  }
  
  public int gaugeFuelScaled(int i) {
    if (this.fuel <= 0) {
      return 0;
    }
    else {
      if (this.itemFuelTime <= 0) {
        this.itemFuelTime = this.fuel;
      }
  
      int j = this.fuel * i / this.itemFuelTime;
      if (j > i) {
        j = i;
      }
  
      return j;
    }
  }
  
  public boolean gainFuel() {
    if (this.inventory[1] == null) {
      return false;
    }
    else if (this.inventory[1].id == Item.LAVA_BUCKET.id) {
      return false;
    }
    else {
      int i = TileEntityIronFurnace.getFuelValueFor(this.inventory[1]) / 4;
      if (this.inventory[1].doMaterialsMatch(Ic2Items.scrap) && !mod_IC2.enableBurningScrap) {
        i = 0;
      }
  
      if (i <= 0) {
        return false;
      }
      else {
        this.fuel += i;
        this.itemFuelTime = i;
        if (this.inventory[1].getItem().k()) {
          this.inventory[1] = new ItemStack(this.inventory[1].getItem().j(), 1, 0);
        }
        else {
          --this.inventory[1].count;
        }
    
        if (this.inventory[1].count == 0) {
          this.inventory[1] = null;
        }
    
        return true;
      }
    }
  }
  
  public String getName() {
    return "Generator";
  }
  
  public String getGuiClassName(EntityHuman entityhuman) {
    return "GuiGenerator";
  }
  
  public boolean isConverting() {
    return this.fuel > 0;
  }
  
  public String getOperationSoundFile() {
    return "Generators/GeneratorLoop.ogg";
  }
  
  public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
    return new ContainerBaseGenerator(entityhuman, this);
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
