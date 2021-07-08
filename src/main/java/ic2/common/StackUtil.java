package ic2.common;

import ic2.api.Direction;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.Iterator;
import java.util.List;

public final class StackUtil {
  public static void distributeDrop(TileEntity tileentity, List list) {
    Direction[] adirection = Direction.values();
    int i = adirection.length;
  
    for (int j = 0; j < i; ++j) {
      Direction direction = adirection[j];
      if (list.isEmpty()) {
        break;
      }
    
      TileEntity tileentity1 = direction.applyToTileEntity(tileentity);
      if (tileentity1 instanceof IInventory) {
        Object obj = tileentity1;
        if (((IInventory) obj).getSize() >= 18) {
          if (tileentity1 instanceof TileEntityChest) {
            Direction[] adirection1 = Direction.values();
            int k = adirection1.length;
  
            for (int l = 0; l < k; ++l) {
              Direction direction1 = adirection1[l];
              if (direction1 != Direction.YN && direction1 != Direction.YP) {
                TileEntity tileentity2 = direction1.applyToTileEntity(tileentity1);
                if (tileentity2 instanceof TileEntityChest) {
                  obj = new InventoryLargeChest("", (IInventory) obj, (IInventory) tileentity2);
                  break;
                }
              }
            }
          }
  
          Iterator iterator1 = list.iterator();
  
          while (iterator1.hasNext()) {
            ItemStack itemstack1 = (ItemStack) iterator1.next();
            if (itemstack1 != null) {
              putInInventory((IInventory) obj, itemstack1);
              if (itemstack1.count == 0) {
                iterator1.remove();
              }
            }
          }
        }
      }
    }
  
    Iterator iterator = list.iterator();
  
    while (iterator.hasNext()) {
      ItemStack itemstack = (ItemStack) iterator.next();
      dropAsEntity(tileentity.world, tileentity.x, tileentity.y, tileentity.z, itemstack);
    }
  
    list.clear();
  }
  
  public static ItemStack getFromInventory(IInventory iinventory, ItemStack itemstack) {
    ItemStack itemstack1 = null;
    int i = itemstack.count;
    itemstack.count = 0;
    
    for (int j = 0; j < iinventory.getSize(); ++j) {
      ItemStack itemstack2 = iinventory.getItem(j);
      if (itemstack2 != null && isStackEqual(itemstack2, itemstack)) {
        if (itemstack1 == null) {
          itemstack1 = itemstack2.cloneItemStack();
          itemstack1.count = 0;
        }
  
        int k = Math.min(i, itemstack2.count);
        i -= k;
        itemstack2.count -= k;
        itemstack.count += k;
        itemstack1.count += k;
        if (itemstack2.count == 0) {
          iinventory.setItem(j, null);
        }
  
        if (i == 0) {
          return itemstack1;
        }
      }
    }
    
    return null;
  }
  
  public static void putInInventory(IInventory iinventory, ItemStack itemstack) {
    int j;
    ItemStack itemstack2;
    int l;
    for (j = 0; j < iinventory.getSize(); ++j) {
      itemstack2 = iinventory.getItem(j);
      if (itemstack2 != null && itemstack2.doMaterialsMatch(itemstack)) {
        l = Math.min(itemstack.count, itemstack2.getMaxStackSize() - itemstack2.count);
        itemstack2.count += l;
        itemstack.count -= l;
        if (itemstack.count == 0) {
          return;
        }
      }
    }
    
    for (j = 0; j < iinventory.getSize(); ++j) {
      itemstack2 = iinventory.getItem(j);
      if (itemstack2 == null) {
        l = Math.min(itemstack.count, itemstack.getMaxStackSize());
        iinventory.setItem(j, new ItemStack(itemstack.id, l, itemstack.getData()));
        itemstack.count -= l;
        if (itemstack.count == 0) {
          return;
        }
      }
    }
    
  }
  
  public static void dropAsEntity(World world, int i, int j, int k, ItemStack itemstack) {
    if (itemstack != null) {
      double d = 0.7D;
      double d1 = (double) world.random.nextFloat() * d + (1.0D - d) * 0.5D;
      double d2 = (double) world.random.nextFloat() * d + (1.0D - d) * 0.5D;
      double d3 = (double) world.random.nextFloat() * d + (1.0D - d) * 0.5D;
      EntityItem entityitem = new EntityItem(world, (double) i + d1, (double) j + d2, (double) k + d3, itemstack);
      entityitem.pickupDelay = 10;
      world.addEntity(entityitem);
    }
  }
  
  public static ItemStack copyWithSize(ItemStack itemstack, int i) {
    ItemStack itemstack1 = itemstack.cloneItemStack();
    itemstack1.count = i;
    return itemstack1;
  }
  
  public static NBTTagCompound getOrCreateNbtData(ItemStack itemstack) {
    NBTTagCompound nbttagcompound = itemstack.getTag();
    if (nbttagcompound == null) {
      nbttagcompound = new NBTTagCompound();
      itemstack.setTag(nbttagcompound);
    }
    
    return nbttagcompound;
  }
  
  public static boolean isStackEqual(ItemStack itemstack, ItemStack itemstack1) {
    return itemstack != null && itemstack1 != null && itemstack.id == itemstack1.id &&
        (Platform.unknown1(itemstack.getItem()) || itemstack.getItem().g() ||
            itemstack.getData() == itemstack1.getData());
  }
}
