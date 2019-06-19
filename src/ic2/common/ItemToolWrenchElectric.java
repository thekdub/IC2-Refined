package ic2.common;

import ic2.api.IElectricItem;
import ic2.platform.Keyboard;
import ic2.platform.Platform;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.World;

import java.util.ArrayList;

public class ItemToolWrenchElectric extends ItemToolWrench implements IElectricItem {
  public ItemToolWrenchElectric(int i, int j) {
    super(i, j);
    this.setMaxDurability(27);
    this.e(1);
  }

  public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
    if (Platform.isSimulating() && Keyboard.isModeSwitchKeyDown(entityhuman)) {
      NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
      boolean flag = !nbttagcompound.getBoolean("losslessMode");
      nbttagcompound.setBoolean("losslessMode", flag);
      if (flag) {
        Platform.messagePlayer(entityhuman, "Lossless wrench mode enabled");
      }
      else {
        Platform.messagePlayer(entityhuman, "Lossless wrench mode disabled");
      }
    }

    return itemstack;
  }

  public boolean onItemUseFirst(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    return !Keyboard.isModeSwitchKeyDown(entityhuman) && super.onItemUseFirst(itemstack, entityhuman, world, i, j, k, l);
  }

  public boolean canTakeDamage(ItemStack itemstack, int i) {
    i *= 50;
    return ElectricItem.discharge(itemstack, i, Integer.MAX_VALUE, true, true) == i;
  }

  public void damage(ItemStack itemstack, int i, EntityHuman entityhuman) {
    ElectricItem.use(itemstack, 50 * i, entityhuman);
  }

  public boolean canProvideEnergy() {
    return false;
  }

  public int getChargedItemId() {
    return this.id;
  }

  public int getEmptyItemId() {
    return this.id;
  }

  public int getMaxCharge() {
    return 12000;
  }

  public int getTier() {
    return 1;
  }

  public int getTransferLimit() {
    return 250;
  }

  public void addCreativeItems(ArrayList arraylist) {
    ItemStack itemstack = new ItemStack(this, 1);
    ElectricItem.charge(itemstack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
    arraylist.add(itemstack);
    arraylist.add(new ItemStack(this, 1, this.getMaxDurability()));
  }

  public boolean overrideWrenchSuccessRate(ItemStack itemstack) {
    NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
    return nbttagcompound.getBoolean("losslessMode");
  }
}
