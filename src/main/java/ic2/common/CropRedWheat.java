package ic2.common;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;

public class CropRedWheat extends CropCard {
  public String name() {
    return "Redwheat";
  }
  
  public String discoveredBy() {
    return "raa1337";
  }
  
  public int tier() {
    return 6;
  }
  
  public int stat(int i) {
    switch (i) {
      case 0:
        return 3;
      case 1:
        return 0;
      case 2:
        return 0;
      case 3:
        return 2;
      case 4:
        return 0;
      default:
        return 0;
    }
  }
  
  public String[] attributes() {
    return new String[]{"Red", "Redstone", "Wheat"};
  }
  
  public int getSpriteIndex(TECrop tecrop) {
    return tecrop.size == 7 ? 27 : tecrop.size + 1;
  }
  
  public boolean canGrow(TECrop tecrop) {
    return tecrop.size < 7 && tecrop.getLightLevel() <= 10 && tecrop.getLightLevel() >= 5;
  }
  
  public boolean canBeHarvested(TECrop tecrop) {
    return tecrop.size == 7;
  }
  
  public float dropGainChance() {
    return 0.5F;
  }
  
  public ItemStack getGain(TECrop tecrop) {
    return !tecrop.world.isBlockPowered(tecrop.x, tecrop.y, tecrop.z) && !tecrop.world.random.nextBoolean() ?
        new ItemStack(Item.WHEAT, 1) : new ItemStack(Item.REDSTONE, 1);
  }
  
  public boolean emitRedstone(TECrop tecrop) {
    return tecrop.size == 7;
  }
  
  public int getEmittedLight(TECrop tecrop) {
    return tecrop.size != 7 ? 0 : 7;
  }
  
  public int growthDuration(TECrop tecrop) {
    return 600;
  }
  
  public byte getSizeAfterHarvest(TECrop tecrop) {
    return 2;
  }
}
