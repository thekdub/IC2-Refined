package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.FurnaceRecipes;
import net.minecraft.server.ItemStack;

public class TileEntityElecFurnace extends TileEntityElectricMachine {
  public TileEntityElecFurnace() {
    super(3, 3, 130, 32);
  }
  
  public ItemStack getResultFor(ItemStack itemstack, boolean flag) {
    ItemStack itemstack1 = FurnaceRecipes.getInstance().getSmeltingResult(itemstack);
    if (flag && itemstack1 != null) {
      --itemstack.count;
    }
    
    return itemstack1;
  }
  
  public String getName() {
    return "Electric Furnace";
  }
  
  public String getGuiClassName(EntityHuman entityhuman) {
    return "GuiElecFurnace";
  }
  
  public String getStartSoundFile() {
    return "Machines/Electro Furnace/ElectroFurnaceLoop.ogg";
  }
  
  public String getInterruptSoundFile() {
    return null;
  }
}
