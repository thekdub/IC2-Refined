package ic2.common;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.server.*;

public class CropStickReed extends CropCard {
  public String name() {
    return "Stickreed";
  }
  
  public String discoveredBy() {
    return "raa1337";
  }
  
  public int tier() {
    return 4;
  }
  
  public int stat(int i) {
    switch (i) {
      case 0:
        return 2;
      case 1:
        return 0;
      case 2:
        return 1;
      case 3:
        return 0;
      case 4:
        return 1;
      default:
        return 0;
    }
  }
  
  public String[] attributes() {
    return new String[]{"Reed", "Resin"};
  }
  
  public int getSpriteIndex(TECrop tecrop) {
    return tecrop.size + 27;
  }
  
  public boolean canGrow(TECrop tecrop) {
    return tecrop.size < 4;
  }
  
  public int weightInfluences(TECrop tecrop, float f, float f1, float f2) {
    return (int) ((double) f * 1.2D + (double) f1 + (double) f2 * 0.8D);
  }
  
  public boolean canBeHarvested(TECrop tecrop) {
    return tecrop.size > 1;
  }
  
  public ItemStack getGain(TECrop tecrop) {
    return tecrop.size <= 3 ? new ItemStack(Item.SUGAR_CANE, tecrop.size - 1) : new ItemStack(Ic2Items.resin.getItem());
  }
  
  public byte getSizeAfterHarvest(TECrop tecrop) {
    return tecrop.size == 4 ? (byte) (3 - mod_IC2.random.nextInt(3)) : 1;
  }
  
  public boolean onEntityCollision(TECrop tecrop, Entity entity) {
    return false;
  }
  
  public int growthDuration(TECrop tecrop) {
    return tecrop.size != 4 ? 100 : 400;
  }
}
