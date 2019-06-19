package ic2.common;

import forge.ISidedInventory;
import ic2.api.IElectricItem;
import ic2.platform.Platform;
import net.minecraft.server.*;

public class TileEntityGeoGenerator extends TileEntityBaseGenerator implements ISidedInventory {
  public int maxLava = 24000;
  public int energy = 0; //Internal tracker of current energy.
  public boolean operate = false;

  public TileEntityGeoGenerator() {
    super(2, mod_IC2.energyGeneratorGeo, 24000); //Increased maximum EU storage from 20 to 24,000 (1 Minute).
  }

  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);
    this.energy = nbttagcompound.getInt("energy");
  }

  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.setInt("energy", energy);
  }

  public int gaugeFuelScaled(int i) {
    return this.fuel <= 0 ? 0 : this.fuel * i / this.maxLava;
  }

  public boolean gainFuel() {
    if (fuel + 1000 <= maxLava && inventory[1] != null) {
      if (inventory[1].id == Item.LAVA_BUCKET.id) {
        fuel += 1000;
        inventory[1].id = Item.BUCKET.id;
        return true;
      }
      else if (inventory[1].id == Ic2Items.lavaCell.id) {
        fuel += 1000;
        if (--inventory[1].count <= 0)
          inventory[1] = null;
        return true;
      }
      else if (gainFuelSub(inventory[1])) {
        fuel += 1000;
        if (inventory[1].getItem().k())
          inventory[1] = new ItemStack(inventory[1].getItem().j());
        else if (--inventory[1].count <= 0)
          inventory[1] = null;
        return true;
      }
    }
    return false;
  }

  public boolean gainFuelSub(ItemStack itemstack) {
    return false;
  }

  public void q_() {
    boolean update = false;
    update = gainFuel();
    if (energy >= maxStorage) {
      operate = false;
      energy = maxStorage;
    }
    if (energy > 0) {
      if (inventory[0] != null && Item.byId[this.inventory[0].id] instanceof IElectricItem) {
        int charge = ElectricItem.charge(inventory[0], energy, 1, false, false);
        if (energy > charge)
          energy -= charge;
        else
          energy = 0;
      }
    }
    if (energy >= 20) {
      int a = 20 - sendEnergy(20);
      energy -= a;
    }
    if (energy <= 20 && fuel > 0) {
      operate = true;
      update = true;
    }
    else if (fuel == 0)
      operate = false;
    if (update)
      update();
    if (!delayActiveUpdate()) {
      setActive(operate);
    }
    else {
      if (ticksSinceLastActiveUpdate++ % 256 == 0) {
        setActive(operate);
      }
    }
    if (operate)
      gainEnergy();
    storage = (short) (energy / 1200.0);
  }

  public boolean gainEnergy() {
    if (this.isConverting()) {
      energy += production;
      fuel -= 1;
      return true;
    }
    else {
      return false;
    }
  }

  public boolean needsFuel() {
    return this.fuel <= this.maxLava; // && this.storage + this.production <= this.maxStorage
  }

  public int distributeLava(int i) {
    int j = this.maxLava - this.fuel;
    if (j > i) {
      j = i;
    }

    i -= j;
    this.fuel += j / 2;
    return i;
  }

  public String getName() {
    return Platform.isRendering() ? "Geothermal Generator" : "Geoth. Generator";
  }

  public String getOperationSoundFile() {
    return "Generators/GeothermalLoop.ogg";
  }

  public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
    return new ContainerBaseGenerator(entityhuman, this);
  }

  public String getGuiClassName(EntityHuman entityhuman) {
    return "GuiGeoGenerator";
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
