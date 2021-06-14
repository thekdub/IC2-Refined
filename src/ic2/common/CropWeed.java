package ic2.common;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;

public class CropWeed extends CropCard {
  public String name() {
    return "Weed";
  }
  
  public int tier() {
    return 0;
  }
  
  public int stat(int i) {
    switch (i) {
      case 0:
        return 0;
      case 1:
        return 0;
      case 2:
        return 1;
      case 3:
        return 0;
      case 4:
        return 5;
      default:
        return 0;
    }
  }
  
  public String[] attributes() {
    return new String[]{"Weed", "Bad"};
  }
  
  public int getSpriteIndex(TECrop tecrop) {
    return tecrop.size + 8;
  }
  
  public boolean canGrow(TECrop tecrop) {
    return tecrop.size < 3;
  }
  
  public boolean leftclick(TECrop tecrop, EntityHuman entityhuman) {
    tecrop.reset();
    return true;
  }
  
  public boolean canBeHarvested(TECrop tecrop) {
    return false;
  }
  
  public ItemStack getGain(TECrop tecrop) {
    return null;
  }
  
  public int growthDuration(TECrop tecrop) {
    return 300;
  }
  
  public boolean onEntityCollision(TECrop tecrop, Entity entity) {
    return false;
  }
}
