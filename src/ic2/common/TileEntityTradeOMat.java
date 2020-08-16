package ic2.common;

import forge.ISidedInventory;
import ic2.api.Direction;
import ic2.api.INetworkTileEntityEventListener;
import ic2.platform.AudioManager;
import ic2.platform.NetworkManager;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.List;
import java.util.Random;
import java.util.Vector;

public class TileEntityTradeOMat extends TileEntityMachine
    implements IPersonalBlock, IHasGui, ISidedInventory, INetworkTileEntityEventListener {
  private static final int EventTrade = 0;
  public static Random randomizer = new Random();
  public String owner = "null";
  public int totalTradeCount = 0;
  public int stock = 0;
  
  public TileEntityTradeOMat() {
    super(4);
  }
  
  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);
    this.owner = nbttagcompound.getString("owner");
    this.totalTradeCount = nbttagcompound.getInt("totalTradeCount");
  }
  
  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.setString("owner", this.owner);
    nbttagcompound.setInt("totalTradeCount", this.totalTradeCount);
  }
  
  public List getNetworkedFields() {
    Vector vector = new Vector(1);
    vector.add("owner");
    vector.addAll(super.getNetworkedFields());
    return vector;
  }
  
  public void q_() {
    super.q_();
    if (this.inventory[0] != null && this.inventory[1] != null && this.inventory[2] != null &&
        StackUtil.isStackEqual(this.inventory[0], this.inventory[2]) &&
        this.inventory[2].count >= this.inventory[0].count && (this.inventory[3] == null ||
        StackUtil.isStackEqual(this.inventory[1], this.inventory[3]) &&
            this.inventory[3].count + this.inventory[1].count <= this.inventory[3].getMaxStackSize())) {
      boolean flag = false;
      Direction[] adirection = Direction.values();
      int i = adirection.length;
      
      for (int j = 0; j < i; ++j) {
        Direction direction = adirection[j];
        TileEntity tileentity = direction.applyToTileEntity(this);
        if (tileentity instanceof IInventory && (!(tileentity instanceof TileEntityPersonalChest) ||
            ((TileEntityPersonalChest) tileentity).owner.equals(this.owner))) {
          IInventory iinventory = (IInventory) tileentity;
          if (iinventory.getSize() >= 18) {
            int k = 0;
            int l = 0;
            
            int j1;
            for (j1 = 0; j1 < iinventory.getSize(); ++j1) {
              ItemStack itemstack = iinventory.getItem(j1);
              if (itemstack == null) {
                k += this.inventory[0].getMaxStackSize();
              }
              else {
                if (StackUtil.isStackEqual(itemstack, this.inventory[0])) {
                  k += itemstack.getMaxStackSize() - itemstack.count;
                }
                
                if (StackUtil.isStackEqual(itemstack, this.inventory[1])) {
                  l += itemstack.count;
                }
              }
            }
            
            j1 = this.inventory[3] != null ? this.inventory[3].getMaxStackSize() - this.inventory[3].count :
                this.inventory[1].getMaxStackSize();
            int k1 = Math.min(
                Math.min(Math.min(this.inventory[2].count / this.inventory[0].count, k / this.inventory[0].count),
                    j1 / this.inventory[1].count), l / this.inventory[1].count);
            if (k1 > 0) {
              int l1 = this.inventory[0].count * k1;
              int i2 = this.inventory[1].count * k1;
              ItemStack var10000 = this.inventory[2];
              var10000.count -= l1;
              if (this.inventory[2].count == 0) {
                this.inventory[2] = null;
              }
              
              ItemStack itemstack1 = StackUtil
                  .getFromInventory(iinventory, new ItemStack(this.inventory[1].id, i2, this.inventory[1].getData()));
              if (itemstack1 != null) {
                if (this.inventory[3] == null) {
                  this.inventory[3] = itemstack1;
                }
                else {
                  var10000 = this.inventory[3];
                  var10000.count += itemstack1.count;
                }
              }
              
              StackUtil
                  .putInInventory(iinventory, new ItemStack(this.inventory[0].id, l1, this.inventory[0].getData()));
              this.totalTradeCount += k1;
              flag = true;
              NetworkManager.initiateTileEntityEvent(this, 0, true);
              this.update();
              break;
            }
          }
        }
      }
      
      if (flag) {
        this.updateStock();
      }
    }
    
  }
  
  public void onCreated() {
    super.onCreated();
    if (Platform.isSimulating()) {
      this.updateStock();
    }
    
  }
  
  public void updateStock() {
    this.stock = 0;
    Direction[] adirection = Direction.values();
    int i = adirection.length;
    
    for (int j = 0; j < i; ++j) {
      Direction direction = adirection[j];
      TileEntity tileentity = direction.applyToTileEntity(this);
      if (tileentity instanceof IInventory && (!(tileentity instanceof TileEntityPersonalChest) ||
          ((TileEntityPersonalChest) tileentity).owner.equals(this.owner))) {
        IInventory iinventory = (IInventory) tileentity;
        if (iinventory.getSize() >= 18) {
          for (int k = 0; k < iinventory.getSize(); ++k) {
            ItemStack itemstack = iinventory.getItem(k);
            if (StackUtil.isStackEqual(this.inventory[1], itemstack)) {
              this.stock += itemstack.count;
            }
          }
        }
      }
    }
    
  }
  
  public boolean wrenchCanRemove(EntityHuman entityhuman) {
    return this.canAccess(entityhuman);
  }
  
  public boolean canAccess(EntityHuman entityhuman) {
    if (this.owner.equals("null")) {
      this.owner = entityhuman.name;
      NetworkManager.updateTileEntityField(this, "owner");
      return true;
    }
    else {
      return this.owner.equalsIgnoreCase(entityhuman.name);
    }
  }
  
  public String getName() {
    return "Trade-O-Mat";
  }
  
  public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
    return this.canAccess(entityhuman) ? new ContainerTradeOMatOpen(entityhuman, this) :
        new ContainerTradeOMatClosed(entityhuman, this);
  }
  
  public String getGuiClassName(EntityHuman entityhuman) {
    return this.canAccess(entityhuman) ? "GuiTradeOMatOpen" : "GuiTradeOMatClosed";
  }
  
  public void onGuiClosed(EntityHuman entityhuman) {
  }
  
  public int getStartInventorySide(int i) {
    switch (i) {
      case 0:
        return 3;
      case 1:
      default:
        return 2;
    }
  }
  
  public int getSizeInventorySide(int i) {
    return 1;
  }
  
  public void onNetworkEvent(int i) {
    switch (i) {
      case 0:
        AudioManager.playOnce(this, PositionSpec.Center, "Machines/o-mat.ogg", true, AudioManager.defaultVolume);
        break;
      default:
        Platform.displayError(
            "An unknown event type was received over multiplayer.\nThis could happen due to corrupted data or a bug.\n\n(Technical information: event ID " +
                i + ", tile entity below)\n" + "T: " + this + " (" + this.x + "," + this.y + "," + this.z + ")");
    }
    
  }
}
