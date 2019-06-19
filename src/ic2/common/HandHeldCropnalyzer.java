package ic2.common;

import ic2.api.CropCard;
import ic2.api.IElectricItem;
import ic2.platform.Platform;
import net.minecraft.server.*;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HandHeldCropnalyzer implements IHasGui, ITickCallback {
  private ItemStack itemStack;
  private ItemStack[] inventory = new ItemStack[3];
  private List transaction = new ArrayList(2);

  HandHeldCropnalyzer(EntityHuman entityhuman, ItemStack itemstack) {
    this.itemStack = itemstack;
    if (Platform.isSimulating()) {
      NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
      nbttagcompound.setInt("uid", (new Random()).nextInt());
      NBTTagList nbttaglist = nbttagcompound.getList("Items");

      for (int i = 0; i < nbttaglist.size(); ++i) {
        NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.get(i);
        byte byte0 = nbttagcompound1.getByte("Slot");
        if (byte0 >= 0 && byte0 < this.inventory.length) {
          this.inventory[byte0] = ItemStack.a(nbttagcompound1);
        }
      }

      mod_IC2.addContinuousTickCallback(entityhuman.world, this);
    }

  }

  public int getSize() {
    return this.inventory.length;
  }

  public ItemStack getItem(int i) {
    return this.inventory[i];
  }

  public ItemStack splitStack(int i, int j) {
    if (this.inventory[i] != null) {
      ItemStack itemstack1;
      if (this.inventory[i].count <= j) {
        itemstack1 = this.inventory[i];
        this.inventory[i] = null;
        return itemstack1;
      }
      else {
        itemstack1 = this.inventory[i].a(j);
        if (this.inventory[i].count == 0) {
          this.inventory[i] = null;
        }

        return itemstack1;
      }
    }
    else {
      return null;
    }
  }

  public void setItem(int i, ItemStack itemstack) {
    this.inventory[i] = itemstack;
    if (itemstack != null && itemstack.count > this.getMaxStackSize()) {
      itemstack.count = this.getMaxStackSize();
    }

  }

  public String getName() {
    return "Cropnalyzer";
  }

  public int getMaxStackSize() {
    return 64;
  }

  public void setMaxStackSize(int arg0) {
  }

  public void update() {
  }

  public boolean a(EntityHuman entityhuman) {
    return true;
  }

  public void f() {
  }

  public void g() {
  }

  public ItemStack splitWithoutUpdate(int i) {
    return null;
  }

  public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
    return new ContainerCropnalyzer(entityhuman, this);
  }

  public String getGuiClassName(EntityHuman entityhuman) {
    return "GuiCropnalyzer";
  }

  public void onGuiClosed(EntityHuman entityhuman) {
    if (Platform.isSimulating()) {
      mod_IC2.removeContinuousTickCallback(entityhuman.world, this);
      NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(this.itemStack);
      boolean flag = false;

      for (int i = 0; i < this.getSize(); ++i) {
        if (this.inventory[i] != null) {
          NBTTagCompound nbttagcompound1 = StackUtil.getOrCreateNbtData(this.inventory[i]);
          if (nbttagcompound.getInt("uid") == nbttagcompound1.getInt("uid")) {
            this.itemStack.count = 1;
            this.inventory[i] = null;
            flag = true;
            break;
          }
        }
      }

      NBTTagList nbttaglist = new NBTTagList();

      int k;
      for (k = 0; k < this.inventory.length; ++k) {
        if (this.inventory[k] != null) {
          NBTTagCompound nbttagcompound2 = new NBTTagCompound();
          nbttagcompound2.setByte("Slot", (byte) k);
          this.inventory[k].save(nbttagcompound2);
          nbttaglist.add(nbttagcompound2);
        }
      }

      nbttagcompound.set("Items", nbttaglist);
      if (flag) {
        StackUtil.dropAsEntity(entityhuman.world, (int) entityhuman.locX, (int) entityhuman.locY, (int) entityhuman.locZ, this.itemStack);
      }
      else {
        for (k = -1; k < entityhuman.inventory.getSize(); ++k) {
          ItemStack itemstack;
          if (k == -1) {
            itemstack = entityhuman.inventory.getCarried();
          }
          else {
            itemstack = entityhuman.inventory.getItem(k);
          }

          if (itemstack != null) {
            NBTTagCompound nbttagcompound3 = StackUtil.getOrCreateNbtData(itemstack);
            if (nbttagcompound.getInt("uid") == nbttagcompound3.getInt("uid")) {
              this.itemStack.count = 1;
              if (k == -1) {
                entityhuman.inventory.setCarried(this.itemStack);
              }
              else {
                entityhuman.inventory.setItem(k, this.itemStack);
              }
              break;
            }
          }
        }
      }
    }

  }

  public void portOnClick(EntityHuman entityhuman) {
    if (Platform.isSimulating()) {
      NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(this.itemStack);
      boolean flag = false;

      for (int i = 0; i < this.getSize(); ++i) {
        if (this.inventory[i] != null) {
          NBTTagCompound nbttagcompound1 = StackUtil.getOrCreateNbtData(this.inventory[i]);
          if (nbttagcompound.getInt("uid") == nbttagcompound1.getInt("uid")) {
            this.itemStack.count = 1;
            this.inventory[i] = null;
            flag = true;
            break;
          }
        }
      }

      NBTTagList nbttaglist = new NBTTagList();

      int k;
      for (k = 0; k < this.inventory.length; ++k) {
        if (this.inventory[k] != null) {
          NBTTagCompound nbttagcompound2 = new NBTTagCompound();
          nbttagcompound2.setByte("Slot", (byte) k);
          this.inventory[k].save(nbttagcompound2);
          nbttaglist.add(nbttagcompound2);
        }
      }

      nbttagcompound.set("Items", nbttaglist);
      if (flag) {
        StackUtil.dropAsEntity(entityhuman.world, (int) entityhuman.locX, (int) entityhuman.locY, (int) entityhuman.locZ, this.itemStack);
      }
      else {
        for (k = -1; k < entityhuman.inventory.getSize(); ++k) {
          ItemStack itemstack;
          if (k == -1) {
            itemstack = entityhuman.inventory.getCarried();
          }
          else {
            itemstack = entityhuman.inventory.getItem(k);
          }

          if (itemstack != null) {
            NBTTagCompound nbttagcompound3 = StackUtil.getOrCreateNbtData(itemstack);
            if (nbttagcompound.getInt("uid") == nbttagcompound3.getInt("uid")) {
              this.itemStack.count = 1;
              if (k == -1) {
                entityhuman.inventory.setCarried(this.itemStack);
              }
              else {
                entityhuman.inventory.setItem(k, this.itemStack);
              }
              break;
            }
          }
        }
      }
    }

  }

  public void tickCallback(World world) {
    if (this.inventory[1] == null && this.inventory[0] != null && this.inventory[0].id == Ic2Items.cropSeed.id) {
      byte byte0 = ItemCropSeed.getScannedFromStack(this.inventory[0]);
      if (byte0 == 4) {
        this.inventory[1] = this.inventory[0];
        this.inventory[0] = null;
        return;
      }

      if (this.inventory[2] == null || !(this.inventory[2].getItem() instanceof IElectricItem)) {
        return;
      }

      int i = this.energyForLevel(byte0);
      int j = ElectricItem.discharge(this.inventory[2], i, 2, true, false);
      if (j < i) {
        return;
      }

      ItemCropSeed.incrementScannedOfStack(this.inventory[0]);
      this.inventory[1] = this.inventory[0];
      this.inventory[0] = null;
    }

  }

  public int energyForLevel(int i) {
    switch (i) {
      case 1:
        return 90;
      case 2:
        return 900;
      case 3:
        return 9000;
      default:
        return 10;
    }
  }

  public CropCard crop() {
    return CropCard.getCrop(ItemCropSeed.getIdFromStack(this.inventory[1]));
  }

  public int getScannedLevel() {
    return this.inventory[1] != null && this.inventory[1].getItem() == Ic2Items.cropSeed.getItem() ? ItemCropSeed.getScannedFromStack(this.inventory[1]) : -1;
  }

  public String getSeedName() {
    return this.crop().name();
  }

  public String getSeedTier() {
    switch (this.crop().tier()) {
      case 1:
        return "I";
      case 2:
        return "II";
      case 3:
        return "III";
      case 4:
        return "IV";
      case 5:
        return "V";
      case 6:
        return "VI";
      case 7:
        return "VII";
      case 8:
        return "VIII";
      case 9:
        return "IX";
      case 10:
        return "X";
      case 11:
        return "XI";
      case 12:
        return "XII";
      case 13:
        return "XIII";
      case 14:
        return "XIV";
      case 15:
        return "XV";
      case 16:
        return "XVI";
      default:
        return "0";
    }
  }

  public String getSeedDiscovered() {
    return this.crop().discoveredBy();
  }

  public String getSeedDesc(int i) {
    return this.crop().desc(i);
  }

  public int getSeedGrowth() {
    return ItemCropSeed.getGrowthFromStack(this.inventory[1]);
  }

  public int getSeedGain() {
    return ItemCropSeed.getGainFromStack(this.inventory[1]);
  }

  public int getSeedResistence() {
    return ItemCropSeed.getResistanceFromStack(this.inventory[1]);
  }

  public boolean matchesUid(int i) {
    NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(this.itemStack);
    return nbttagcompound.getInt("uid") == i;
  }

  public ItemStack[] getContents() {
    return this.inventory;
  }

  public InventoryHolder getOwner() {
    return null;
  }

  public List getViewers() {
    return this.transaction;
  }

  public void onClose(CraftHumanEntity arg0) {
    this.transaction.remove(arg0);
  }

  public void onOpen(CraftHumanEntity arg0) {
    this.transaction.add(arg0);
  }
}
