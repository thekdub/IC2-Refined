package ic2.common;

import forge.ISidedInventory;
import net.minecraft.server.*;

public class TileEntityIronFurnace extends TileEntityMachine implements IHasGui, ISidedInventory {
  public final short operationLength = 160;
  public int fuel = 0;
  public int maxFuel = 0;
  public short progress = 0;

  public TileEntityIronFurnace() {
    super(3);
  }

  public static int getFuelValueFor(ItemStack itemstack) {
    if (itemstack == null) {
      return 0;
    }
    else {
      int i = itemstack.getItem().id;
      if (i == Item.LAVA_BUCKET.id) {
        return 2000;
      }
      else if (itemstack.getItem() instanceof ItemFuelCanFilled) {
        NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
        if (itemstack.getData() > 0) {
          nbttagcompound.setInt("value", itemstack.getData());
        }

        return nbttagcompound.getInt("value") * 2;
      }
      else {
        return TileEntityFurnace.fuelTime(itemstack);
      }
    }
  }

  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);

    try {
      this.fuel = nbttagcompound.getInt("fuel");
    } catch (Throwable var4) {
      this.fuel = nbttagcompound.getShort("fuel");
    }

    try {
      this.maxFuel = nbttagcompound.getInt("maxFuel");
    } catch (Throwable var3) {
      this.maxFuel = nbttagcompound.getShort("maxFuel");
    }

    this.progress = nbttagcompound.getShort("progress");
  }

  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.setInt("fuel", this.fuel);
    nbttagcompound.setInt("maxFuel", this.maxFuel);
    nbttagcompound.setShort("progress", this.progress);
  }

  public int gaugeProgressScaled(int i) {
    return this.progress * i / 160;
  }

  public int gaugeFuelScaled(int i) {
    if (this.maxFuel == 0) {
      this.maxFuel = this.fuel;
      if (this.maxFuel == 0) {
        this.maxFuel = 160;
      }
    }

    return this.fuel * i / this.maxFuel;
  }

  public void q_() {
    super.q_();
    boolean flag = this.isBurning();
    boolean flag1 = false;
    if (this.fuel <= 0 && this.canOperate()) {
      this.fuel = this.maxFuel = getFuelValueFor(this.inventory[1]);
      if (this.fuel > 0) {
        if (this.inventory[1].getItem().k()) {
          this.inventory[1] = new ItemStack(this.inventory[1].getItem().j());
        }
        else {
          --this.inventory[1].count;
        }

        if (this.inventory[1].count <= 0) {
          this.inventory[1] = null;
        }

        flag1 = true;
      }
    }

    if (this.isBurning() && this.canOperate()) {
      ++this.progress;
      if (this.progress >= 160) {
        this.progress = 0;
        this.operate();
        flag1 = true;
      }
    }
    else {
      this.progress = 0;
    }

    if (this.fuel > 0) {
      --this.fuel;
    }

    if (flag != this.isBurning()) {
      this.setActive(this.isBurning());
      flag1 = true;
    }

    if (flag1) {
      this.update();
    }

  }

  public void operate() {
    if (this.canOperate()) {
      ItemStack itemstack = this.getResultFor(this.inventory[0]);
      if (this.inventory[2] == null) {
        this.inventory[2] = itemstack.cloneItemStack();
      }
      else {
        ItemStack var10000 = this.inventory[2];
        var10000.count += itemstack.count;
      }

      if (this.inventory[0].getItem().k()) {
        this.inventory[0] = new ItemStack(this.inventory[0].getItem().j());
      }
      else {
        --this.inventory[0].count;
      }

      if (this.inventory[0].count <= 0) {
        this.inventory[0] = null;
      }

    }
  }

  public boolean isBurning() {
    return this.fuel > 0;
  }

  public boolean canOperate() {
    if (this.inventory[0] == null) {
      return false;
    }
    else {
      ItemStack itemstack = this.getResultFor(this.inventory[0]);
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

  public ItemStack getResultFor(ItemStack itemstack) {
    return FurnaceRecipes.getInstance().getSmeltingResult(itemstack);
  }

  public String getName() {
    return "Iron Furnace";
  }

  public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
    return new ContainerIronFurnace(entityhuman, this);
  }

  public String getGuiClassName(EntityHuman entityhuman) {
    return "GuiIronFurnace";
  }

  public void onGuiClosed(EntityHuman entityhuman) {
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
