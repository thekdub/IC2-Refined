package ic2.common;

import forge.IFuelHandler;
import forge.MinecraftForge;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;

public class ItemFuelCanFilled extends ItemFuelCan implements IFuelHandler {
  public ItemFuelCanFilled(int i, int j) {
    super(i, j);
    MinecraftForge.registerFuelHandler(this);
  }
  
  public int getItemBurnTime(ItemStack itemstack) {
    if (itemstack.id != this.id) {
      return 0;
    }
    else {
      NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
      if (itemstack.getData() > 0) {
        nbttagcompound.setInt("value", itemstack.getData());
      }
  
      int i = nbttagcompound.getInt("value") * 2;
      return i <= 32767 ? i : 32767;
    }
  }
}
