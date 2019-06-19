package ic2.common;

import forge.ISidedInventory;
import ic2.api.Direction;
import ic2.platform.Platform;
import net.minecraft.server.*;

public class TileEntityInduction extends TileEntityElecMachine implements IHasGui, ISidedInventory {
  private static final int inputSlot = 0;
  private static final int fuelSlot = 2;
  private static final int outputSlot = 3;
  public static short maxHeat = 10000;
  public int soundTicker;
  public short heat = 0;
  public short progress = 0;

  public TileEntityInduction() {
    super(5, 2, maxHeat, 128, 2);
    this.soundTicker = mod_IC2.random.nextInt(64);
  }

  public String getName() {
    return Platform.isRendering() ? "Induction Furnace" : "InductionFurnace";
  }

  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);
    this.heat = nbttagcompound.getShort("heat");
    this.progress = nbttagcompound.getShort("progress");
  }

  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.setShort("heat", this.heat);
    nbttagcompound.setShort("progress", this.progress);
  }

  public String getHeat() {
    return "" + this.heat * 100 / maxHeat + "%";
  }

  public int gaugeProgressScaled(int i) {
    return i * this.progress / 4000;
  }

  public int gaugeFuelScaled(int i) {
    return i * this.energy / this.maxEnergy;
  }

  public void q_() {
    super.q_();
    boolean flag = false;
    if (this.energy <= this.maxEnergy) {
      flag = this.provideEnergy();
    }

    boolean flag1 = this.getActive();
    if (this.heat == 0) {
      flag1 = false;
    }

    if (this.progress >= 4000) {
      this.operate();
      flag = true;
      this.progress = 0;
      flag1 = false;
    }

    boolean flag2 = this.canOperate();
    if (this.energy <= 0 || !flag2 && !this.isRedstonePowered()) {
      this.heat = (short) (this.heat - Math.min(this.heat, 4));
    }
    else {
      --this.energy;
      if (this.heat < maxHeat) {
        ++this.heat;
      }

      flag1 = true;
    }

    if (flag1 && this.progress != 0) {
      if (!flag2 || this.energy < 15) {
        if (!flag2) {
          this.progress = 0;
        }

        flag1 = false;
      }
    }
    else if (flag2) {
      if (this.energy >= 15) {
        flag1 = true;
      }
    }
    else {
      this.progress = 0;
    }

    if (flag1 && flag2) {
      this.progress = (short) (this.progress + this.heat / 30);
      this.energy -= 15;
    }

    if (flag) {
      this.update();
    }

    if (flag1 != this.getActive()) {
      this.setActive(flag1);
    }

  }

  public void operate() {
    this.operate(0, 3);
    this.operate(1, 4);
  }

  public void operate(int i, int j) {
    if (this.canOperate(i, j)) {
      ItemStack itemstack = this.getResultFor(this.inventory[i]);
      if (this.inventory[j] == null) {
        this.inventory[j] = itemstack.cloneItemStack();
      }
      else {
        ItemStack var10000 = this.inventory[j];
        var10000.count += itemstack.count;
      }

      if (this.inventory[i].getItem().k()) {
        this.inventory[i] = new ItemStack(this.inventory[i].getItem().j());
      }
      else {
        --this.inventory[i].count;
      }

      if (this.inventory[i].count <= 0) {
        this.inventory[i] = null;
      }

    }
  }

  public boolean canOperate() {
    return this.canOperate(0, 3) || this.canOperate(1, 4);
  }

  public boolean canOperate(int i, int j) {
    if (this.inventory[i] == null) {
      return false;
    }
    else {
      ItemStack itemstack = this.getResultFor(this.inventory[i]);
      if (itemstack == null) {
        return false;
      }
      else {
        return this.inventory[j] == null || this.inventory[j].doMaterialsMatch(itemstack) && this.inventory[j].count + itemstack.count <= itemstack.getMaxStackSize();
      }
    }
  }

  public ItemStack getResultFor(ItemStack itemstack) {
    return FurnaceRecipes.getInstance().getSmeltingResult(itemstack);
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

  public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
    return new ContainerInduction(entityhuman, this);
  }

  public String getGuiClassName(EntityHuman entityhuman) {
    return "GuiInduction";
  }

  public void onGuiClosed(EntityHuman entityhuman) {
  }

  public int getStartInventorySide(int i) {
    switch (i) {
      case 0:
        return 2;
      case 1:
        return 0;
      default:
        return 3;
    }
  }

  public int getSizeInventorySide(int i) {
    switch (i) {
      case 0:
        return 1;
      default:
        return 2;
    }
  }

  public float getWrenchDropRate() {
    return 0.8F;
  }
}
