package ic2.common;

import forge.ISidedInventory;
import net.minecraft.server.*;

import java.util.Random;

public class TileEntityWaterGenerator extends TileEntityBaseGenerator implements ISidedInventory {
  public static Random randomizer = new Random();
  public int ticker;
  public int water = 0;
  public int microStorage = 0;
  public int maxWater = 2000;

  public TileEntityWaterGenerator() {
    super(2, 2, 2);
    this.production = 2;
    this.ticker = randomizer.nextInt(this.tickRate());
  }

  public void onCreated() {
    super.onCreated();
    this.updateWaterCount();
  }

  public int gaugeFuelScaled(int i) {
    return this.fuel <= 0 ? 0 : this.fuel * i / this.maxWater;
  }

  public boolean gainFuel() {
    if (this.inventory[1] != null && this.maxWater - this.fuel >= 500) {
      if (this.inventory[1].id == Item.WATER_BUCKET.id) {
        this.production = 2;
        this.fuel += 500;
        this.inventory[1].id = Item.BUCKET.id;
        return true;
      }

      if (this.inventory[1].id == Ic2Items.waterCell.id) {
        this.production = 2;
        this.fuel += 500;
        --this.inventory[1].count;
        if (this.inventory[1].count <= 0) {
          this.inventory[1] = null;
        }

        return true;
      }

      if (this.gainFuelSub(this.inventory[1])) {
        this.production = 2;
        this.fuel += 500;
        if (this.inventory[1].getItem().k()) {
          this.inventory[1] = new ItemStack(this.inventory[1].getItem().j());
        }
        else {
          --this.inventory[1].count;
          if (this.inventory[1].count <= 0) {
            this.inventory[1] = null;
          }
        }

        return true;
      }
    }
    else if (this.fuel <= 0) {
      this.flowPower();
      this.production = this.microStorage / 100;
      this.microStorage -= this.production * 100;
      if (this.production > 0) {
        ++this.fuel;
        return true;
      }

      return false;
    }

    return false;
  }

  public boolean gainFuelSub(ItemStack itemstack) {
    return false;
  }

  public boolean needsFuel() {
    return this.fuel <= this.maxWater;
  }

  public void flowPower() {
    if (this.ticker++ % this.tickRate() == 0) {
      this.updateWaterCount();
    }

    this.water = this.water * mod_IC2.energyGeneratorWater / 100;
    if (this.water > 0) {
      this.microStorage += this.water;
    }

  }

  public void updateWaterCount() {
    int i = 0;

    for (int j = this.x - 1; j < this.x + 2; ++j) {
      for (int k = this.y - 1; k < this.y + 2; ++k) {
        for (int l = this.z - 1; l < this.z + 2; ++l) {
          if (this.world.getTypeId(j, k, l) == Block.WATER.id || this.world.getTypeId(j, k, l) == Block.STATIONARY_WATER.id) {
            ++i;
          }
        }
      }
    }

    this.water = i;
  }

  public String getName() {
    return "Water Mill";
  }

  public String getGuiClassName(EntityHuman entityhuman) {
    return "GuiWaterGenerator";
  }

  public int tickRate() {
    return 128;
  }

  public String getOperationSoundFile() {
    return "Generators/WatermillLoop.ogg";
  }

  public boolean delayActiveUpdate() {
    return true;
  }

  public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
    return new ContainerWaterGenerator(entityhuman, this);
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
