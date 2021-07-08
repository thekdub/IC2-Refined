package ic2.common;

import net.minecraft.server.*;

import java.util.List;

public class ItemCropSeed extends ItemIC2 {
  public ItemCropSeed(int i, int j) {
    super(i, j);
    this.e(1);
    this.hideFromCreative();
  }
  
  public static ItemStack generateItemStackFromValues(short word0, byte byte0, byte byte1, byte byte2, byte byte3) {
    ItemStack itemstack = new ItemStack(Ic2Items.cropSeed.getItem());
    NBTTagCompound nbttagcompound = new NBTTagCompound();
    nbttagcompound.setShort("id", word0);
    nbttagcompound.setByte("growth", byte0);
    nbttagcompound.setByte("gain", byte1);
    nbttagcompound.setByte("resistance", byte2);
    nbttagcompound.setByte("scan", byte3);
    itemstack.setTag(nbttagcompound);
    return itemstack;
  }
  
  public static short getIdFromStack(ItemStack itemstack) {
    return itemstack.getTag() == null ? -1 : itemstack.getTag().getShort("id");
  }
  
  public static byte getGrowthFromStack(ItemStack itemstack) {
    return itemstack.getTag() == null ? -1 : itemstack.getTag().getByte("growth");
  }
  
  public static byte getGainFromStack(ItemStack itemstack) {
    return itemstack.getTag() == null ? -1 : itemstack.getTag().getByte("gain");
  }
  
  public static byte getResistanceFromStack(ItemStack itemstack) {
    return itemstack.getTag() == null ? -1 : itemstack.getTag().getByte("resistance");
  }
  
  public static byte getScannedFromStack(ItemStack itemstack) {
    return itemstack.getTag() == null ? -1 : itemstack.getTag().getByte("scan");
  }
  
  public static void incrementScannedOfStack(ItemStack itemstack) {
    if (itemstack.getTag() != null) {
      itemstack.getTag().setByte("scan", (byte) (getScannedFromStack(itemstack) + 1));
    }
  }
  
  public String a(ItemStack itemstack) {
    if (itemstack == null) {
      return "item.cropSeedUn";
    }
    else {
      byte byte0 = getScannedFromStack(itemstack);
      if (byte0 == 0) {
        return "item.cropSeedUn";
      }
      else {
        return byte0 < 0 ? "item.cropSeedInvalid" : "item.cropSeed" + getIdFromStack(itemstack);
      }
    }
  }
  
  public boolean g() {
    return true;
  }
  
  public boolean isRepairable() {
    return false;
  }
  
  public void addInformation(ItemStack itemstack, List list) {
    if (getScannedFromStack(itemstack) >= 4) {
      list.add("§2Gr§7 " + getGrowthFromStack(itemstack));
      list.add("§6Ga§7 " + getGainFromStack(itemstack));
      list.add("§3Re§7 " + getResistanceFromStack(itemstack));
    }
    
  }
  
  public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    if (world.getTileEntity(i, j, k) instanceof TileEntityCrop) {
      TileEntityCrop tileentitycrop = (TileEntityCrop) world.getTileEntity(i, j, k);
      if (tileentitycrop
          .tryPlantIn(getIdFromStack(itemstack), 1, getGrowthFromStack(itemstack), getGainFromStack(itemstack),
              getResistanceFromStack(itemstack), getScannedFromStack(itemstack))) {
        entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
        return true;
      }
      else {
        return false;
      }
    }
    else {
      return false;
    }
  }
}
