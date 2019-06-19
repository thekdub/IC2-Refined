package ic2.common;

import ic2.api.IBoxable;
import ic2.platform.Platform;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.World;

public class ItemToolbox extends ItemIC2 implements IBoxable {
  public ItemToolbox(int i, int j) {
    super(i, j);
    this.e(1);
  }

  public static ItemStack[] getInventoryFromNBT(ItemStack itemstack) {
    ItemStack[] aitemstack = new ItemStack[8];
    if (itemstack.getTag() == null) {
      return aitemstack;
    }
    else {
      NBTTagCompound nbttagcompound = itemstack.getTag();

      for (int i = 0; i < 8; ++i) {
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("box" + i);
        if (nbttagcompound1 != null) {
          aitemstack[i] = ItemStack.a(nbttagcompound1);
        }
      }

      return aitemstack;
    }
  }

  public int getIconFromDamage(int i) {
    return i == 0 ? this.textureId + 1 : this.textureId;
  }

  public String a(ItemStack itemstack) {
    if (itemstack == null) {
      return "DAMN TMI CAUSING NPE's!";
    }
    else if (itemstack.getData() == 0) {
      return "item.itemToolbox";
    }
    else {
      ItemStack[] aitemstack = getInventoryFromNBT(itemstack);
      return aitemstack[0] == null ? "item.itemToolbox" : aitemstack[0].getItem().a(aitemstack[0]);
    }
  }

  public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
    if (!Platform.isSimulating()) {
      return itemstack;
    }
    else {
      if (itemstack.getData() == 0) {
        this.pack(itemstack, entityhuman);
      }
      else {
        this.unpack(itemstack, entityhuman);
      }

      if (!Platform.isRendering()) {
        entityhuman.activeContainer.a();
      }

      return itemstack;
    }
  }

  public boolean canBeStoredInToolbox(ItemStack itemstack) {
    return false;
  }

  public void pack(ItemStack itemstack, EntityHuman entityhuman) {
    ItemStack[] aitemstack = entityhuman.inventory.items;
    NBTTagCompound nbttagcompound = new NBTTagCompound();
    int i = 0;

    for (int j = 0; j < 9; ++j) {
      if (aitemstack[j] != null && aitemstack[j] != itemstack) {
        if (aitemstack[j].getItem() instanceof IBoxable) {
          if (!((IBoxable) aitemstack[j].getItem()).canBeStoredInToolbox(aitemstack[j])) {
            continue;
          }
        }
        else if (aitemstack[j].getMaxStackSize() > 1 && aitemstack[j].id != Ic2Items.scaffold.id && aitemstack[j].id != Ic2Items.miningPipe.id) {
          continue;
        }

        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        aitemstack[j].save(nbttagcompound1);
        aitemstack[j] = null;
        nbttagcompound.setCompound("box" + i, nbttagcompound1);
        ++i;
      }
    }

    if (i != 0) {
      itemstack.setTag(nbttagcompound);
      itemstack.setData(1);
    }
  }

  public void unpack(ItemStack itemstack, EntityHuman entityhuman) {
    NBTTagCompound nbttagcompound = itemstack.getTag();
    if (nbttagcompound != null) {
      ItemStack[] aitemstack = getInventoryFromNBT(itemstack);
      ItemStack[] aitemstack1 = entityhuman.inventory.items;
      int i = 0;

      for (int j = 0; j < 8 && aitemstack[i] != null; ++j) {
        if (aitemstack1[j] == null) {
          aitemstack1[j] = aitemstack[i];
          ++i;
        }
      }

      while (i < 8 && aitemstack[i] != null) {
        StackUtil.dropAsEntity(entityhuman.world, (int) entityhuman.locX, (int) entityhuman.locY, (int) entityhuman.locZ, aitemstack[i]);
        ++i;
      }

      itemstack.setTag(null);
      itemstack.setData(0);
    }
  }
}
