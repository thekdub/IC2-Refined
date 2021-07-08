package ic2.common;

import ic2.platform.Platform;
import net.minecraft.server.*;

public abstract class ContainerIC2 extends Container {
  EntityHuman player;
  IInventory teinventory;
  
  public ContainerIC2(EntityHuman player, IInventory inv) {
    this.player = player;
    this.teinventory = inv;
  }
  
  public EntityHuman getPlayer() {
    return this.player;
  }
  
  public IInventory getInventory() {
    return this.teinventory;
  }
  
  public abstract int guiInventorySize();
  
  public abstract int getInput();
  
  public int firstEmptyFrom(int i, int j, IInventory iinventory) {
    for (int k = i; k <= j; ++k) {
      if (iinventory.getItem(k) == null) {
        return k;
      }
    }
    
    return -1;
  }
  
  public final ItemStack a(int i) {
    ItemStack itemstack = null;
    Slot slot = (Slot) Platform.getContainerSlots(this).get(i);
    if (slot != null && slot.c()) {
      ItemStack itemstack1 = slot.getItem();
      itemstack = itemstack1.cloneItemStack();
      if (i < this.guiInventorySize()) {
        this.transferToSlots(itemstack1, this.guiInventorySize(), this.e.size(), false);
      }
      else if (i >= this.guiInventorySize() && i < this.e.size() - 9) {
        int j = this.getInput();
        if (j != -1 && j < this.guiInventorySize()) {
          this.transferToSlots(itemstack1, j, j + 1, false);
        }
        else {
          this.transferToSlots(itemstack1, 0, this.guiInventorySize(), false);
        }
      }
      else if (i >= this.guiInventorySize() && i >= this.e.size() - 9 && i < this.e.size()) {
        this.transferToSlots(itemstack1, this.guiInventorySize(), this.e.size() - 9, false);
      }
  
      if (itemstack1.count == 0) {
        slot.set(null);
      }
      else {
        slot.d();
      }
  
      if (itemstack1.count == itemstack.count) {
        return null;
      }
  
      slot.c(itemstack1);
    }
    
    return itemstack;
  }
  
  public void transferToSlots(ItemStack itemstack, int i, int j, boolean flag) {
    this.a(itemstack, i, j, flag);
  }
  
  public abstract void updateProgressBar(int var1, int var2);
  
  public abstract boolean b(EntityHuman var1);
  
  public ItemStack clickItem(int i, int j, boolean flag, EntityHuman entityhuman) {
    return i >= 0 && Platform.getContainerSlots(this).get(i) instanceof SlotDisplay ? null :
        super.clickItem(i, j, flag, entityhuman);
  }
}
