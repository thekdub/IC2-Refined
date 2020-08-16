package ic2.common;

import ic2.platform.Platform;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public class ItemCropnalyzer extends ItemIC2 implements IHandHeldInventory {
  HandHeldCropnalyzer cropnalyzer = null;
  
  public ItemCropnalyzer(int i, int j) {
    super(i, j);
    this.e(1);
    this.setRarity(1);
  }
  
  public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
    if (Platform.isSimulating()) {
      Platform.launchGui(entityhuman, this.getInventory(entityhuman, itemstack));
    }
    
    return itemstack;
  }
  
  public IHasGui getInventory(EntityHuman entityhuman, ItemStack itemstack) {
    this.cropnalyzer = new HandHeldCropnalyzer(entityhuman, itemstack);
    return this.cropnalyzer;
  }
  
  public boolean onDroppedByPlayer(ItemStack item, EntityHuman entityHuman) {
    this.cropnalyzer.onGuiClosed(entityHuman);
    return super.onDroppedByPlayer(item, entityHuman);
  }
}
