package ic2.common;

import forge.ISidedInventory;
import ic2.platform.AudioManager;
import ic2.platform.AudioSource;
import net.minecraft.server.*;

public class TileEntityCanner extends TileEntityElecMachine implements IHasGui, ISidedInventory {
  public short progress = 0;
  public int energyconsume = 1;
  public int operationLength = 600;
  public AudioSource audioSource;
  private int fuelQuality = 0;
  
  public TileEntityCanner() {
    super(4, 1, 631, 32);
  }
  
  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);
    
    try {
      this.fuelQuality = nbttagcompound.getInt("fuelQuality");
    } catch (Throwable var3) {
      this.fuelQuality = nbttagcompound.getShort("fuelQuality");
    }
    
  }
  
  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.setInt("fuelQuality", this.fuelQuality);
  }
  
  public int gaugeProgressScaled(int i) {
    int j = this.operationLength;
    if (this.getMode() == 1 && this.inventory[0] != null) {
      int k = this.getFoodValue(this.inventory[0]);
      if (k > 0) {
        j = 50 * k;
      }
    }
    
    if (this.getMode() == 3) {
      j = 50;
    }
    
    return this.progress * i / j;
  }
  
  public int gaugeFuelScaled(int i) {
    if (this.energy <= 0) {
      return 0;
    }
    else {
      int j = this.energy * i / (this.operationLength * this.energyconsume);
      return j > i ? i : j;
    }
  }
  
  public void q_() {
    super.q_();
    boolean flag = false;
    boolean flag1 = this.canOperate();
    if (this.energy <= this.energyconsume * this.operationLength && flag1) {
      flag = this.provideEnergy();
    }
    
    boolean flag2 = this.getActive();
    if (flag1 && (this.getMode() == 1 && this.progress >= this.getFoodValue(this.inventory[0]) * 50 ||
        this.getMode() == 2 && this.progress > 0 && this.progress % 100 == 0 ||
        this.getMode() == 3 && this.progress >= 50)) {
      if (this.getMode() != 1 && this.getMode() != 3 && this.progress < 600) {
        this.operate(true);
      }
      else {
        this.operate(false);
        this.fuelQuality = 0;
        this.progress = 0;
        flag2 = false;
      }
      
      flag = true;
    }
    
    if (flag2 && this.progress != 0) {
      if (!flag1 || this.energy < this.energyconsume) {
        if (!flag1 && this.getMode() != 2) {
          this.fuelQuality = 0;
          this.progress = 0;
        }
  
        flag2 = false;
      }
    }
    else if (flag1) {
      if (this.energy >= this.energyconsume) {
        flag2 = true;
      }
    }
    else if (this.getMode() != 2) {
      this.fuelQuality = 0;
      this.progress = 0;
    }
    
    if (flag2) {
      ++this.progress;
      this.energy -= this.energyconsume;
    }
    
    if (flag) {
      this.update();
    }
    
    if (flag2 != this.getActive()) {
      this.setActive(flag2);
    }
    
  }
  
  public void operate(boolean flag) {
    switch (this.getMode()) {
      case 1:
        int i = this.getFoodValue(this.inventory[0]);
        --this.inventory[0].count;
        if (this.inventory[0].getItem() == Item.MUSHROOM_SOUP && this.inventory[0].count <= 0) {
          this.inventory[0] = new ItemStack(Item.BOWL);
        }
  
        if (this.inventory[0].count <= 0) {
          this.inventory[0] = null;
        }
  
        ItemStack var10000 = this.inventory[3];
        var10000.count -= i;
        if (this.inventory[3].count <= 0) {
          this.inventory[3] = null;
        }
  
        if (this.inventory[2] == null) {
          this.inventory[2] = new ItemStack(Ic2Items.filledTinCan.getItem(), i);
        }
        else {
          var10000 = this.inventory[2];
          var10000.count += i;
        }
        break;
      case 2:
        int j = this.getFuelValue(this.inventory[0].id);
        --this.inventory[0].count;
        if (this.inventory[0].count <= 0) {
          this.inventory[0] = null;
        }
  
        this.fuelQuality += j;
        if (!flag) {
          if (this.inventory[3].getItem() instanceof ItemFuelCanEmpty) {
            --this.inventory[3].count;
            if (this.inventory[3].count <= 0) {
              this.inventory[3] = null;
            }
  
            this.inventory[2] = Ic2Items.filledFuelCan.cloneItemStack();
            NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(this.inventory[2]);
            nbttagcompound.setInt("value", this.fuelQuality);
          }
          else {
            int k = this.inventory[3].getData();
            k -= this.fuelQuality;
            if (k < 1) {
              k = 1;
            }
  
            this.inventory[3] = null;
            this.inventory[2] = new ItemStack(Ic2Items.jetpack.id, 1, k);
          }
        }
        break;
      case 3:
        --this.inventory[0].count;
        this.inventory[3].setData(this.inventory[3].getData() - 2);
        if (this.inventory[0].count <= 0) {
          this.inventory[0] = null;
        }
  
        if (this.inventory[0] == null || this.inventory[3].getData() <= 1) {
          this.inventory[2] = this.inventory[3];
          this.inventory[3] = null;
        }
    }
    
  }
  
  public void j() {
    if (this.audioSource != null) {
      AudioManager.removeSources(this);
      this.audioSource = null;
    }
    
    super.j();
  }
  
  public boolean canOperate() {
    if (this.inventory[0] == null) {
      return false;
    }
    else {
      switch (this.getMode()) {
        case 1:
          int i = this.getFoodValue(this.inventory[0]);
          if (i <= 0 || i > this.inventory[3].count || this.inventory[2] != null &&
              (this.inventory[2].count + i > this.inventory[2].getMaxStackSize() ||
                  this.inventory[2].id != Ic2Items.filledTinCan.id)) {
            break;
          }
    
          return true;
        case 2:
          int j = this.getFuelValue(this.inventory[0].id);
          if (j > 0 && this.inventory[2] == null) {
            return true;
          }
          break;
        case 3:
          if (this.inventory[3].getData() > 2 && this.getPelletValue(this.inventory[0]) > 0 &&
              this.inventory[2] == null) {
            return true;
          }
      }
  
      return false;
    }
  }
  
  public int getMode() {
    if (this.inventory[3] == null) {
      return 0;
    }
    else if (this.inventory[3].id == Ic2Items.tinCan.id) {
      return 1;
    }
    else if (!(this.inventory[3].getItem() instanceof ItemFuelCanEmpty) &&
        this.inventory[3].id != Ic2Items.jetpack.id) {
      return this.inventory[3].id != Ic2Items.cfPack.id ? 0 : 3;
    }
    else {
      return 2;
    }
  }
  
  public String getName() {
    return "Canning Machine";
  }
  
  private int getFoodValue(ItemStack itemstack) {
    if (itemstack.getItem() instanceof ItemFood) {
      ItemFood itemfood = (ItemFood) itemstack.getItem();
      return (int) Math.ceil((double) itemfood.getNutrition() / 2.0D);
    }
    else {
      return itemstack.id != Item.CAKE.id && itemstack.id != Block.CAKE_BLOCK.id ? 0 : 6;
    }
  }
  
  public int getFuelValue(int i) {
    if (i == Ic2Items.coalfuelCell.id) {
      return 2548;
    }
    else if (i == Ic2Items.biofuelCell.id) {
      return 868;
    }
    else if (i == Item.REDSTONE.id && this.fuelQuality > 0) {
      return (int) ((double) this.fuelQuality * 0.2D);
    }
    else if (i == Item.GLOWSTONE_DUST.id && this.fuelQuality > 0) {
      return (int) ((double) this.fuelQuality * 0.3D);
    }
    else {
      return i == Item.SULPHUR.id && this.fuelQuality > 0 ? (int) ((double) this.fuelQuality * 0.4D) : 0;
    }
  }
  
  public int getPelletValue(ItemStack itemstack) {
    if (itemstack == null) {
      return 0;
    }
    else {
      return itemstack.id != Ic2Items.constructionFoamPellet.id ? 0 : itemstack.count;
    }
  }
  
  public String getStartSoundFile() {
    return null;
  }
  
  public String getInterruptSoundFile() {
    return null;
  }
  
  public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
    return new ContainerCanner(entityhuman, this);
  }
  
  public String getGuiClassName(EntityHuman entityhuman) {
    return "GuiCanner";
  }
  
  public void onGuiClosed(EntityHuman entityhuman) {
  }
  
  public int getStartInventorySide(int i) {
    byte byte0;
    switch (this.getFacing()) {
      case 2:
        byte0 = 4;
        break;
      case 3:
        byte0 = 5;
        break;
      case 4:
        byte0 = 3;
        break;
      case 5:
        byte0 = 2;
        break;
      default:
        byte0 = 2;
    }
    
    if (i == byte0) {
      return 1;
    }
    else {
      switch (i) {
        case 0:
          return 3;
        case 1:
          return 0;
        default:
          return 2;
      }
    }
  }
  
  public int getSizeInventorySide(int i) {
    return 1;
  }
  
  public float getWrenchDropRate() {
    return 0.85F;
  }
}
