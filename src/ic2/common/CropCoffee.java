package ic2.common;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.server.ItemStack;

public class CropCoffee extends CropCard {
  public String name() {
    return "Coffee";
  }
  
  public String discoveredBy() {
    return "Snoochy";
  }
  
  public int tier() {
    return 7;
  }
  
  public int stat(int i) {
    switch (i) {
      case 0:
        return 1;
      case 1:
        return 4;
      case 2:
        return 1;
      case 3:
        return 2;
      case 4:
        return 0;
      default:
        return 0;
    }
  }
  
  public String[] attributes() {
    return new String[]{"Leaves", "Ingrident", "Beans"};
  }
  
  public int getSpriteIndex(TECrop tecrop) {
    if (tecrop.size == 5) {
      return 43;
    }
    else {
      return tecrop.size == 4 ? 42 : 31 + tecrop.size;
    }
  }
  
  public boolean canGrow(TECrop tecrop) {
    return tecrop.size < 5 && tecrop.getLightLevel() >= 9;
  }
  
  public int weightInfluences(TECrop tecrop, float f, float f1, float f2) {
    return (int) (0.4D * (double) f + 1.4D * (double) f1 + 1.2D * (double) f2);
  }
  
  public int growthDuration(TECrop tecrop) {
    if (tecrop.size == 3) {
      return (int) ((double) super.growthDuration(tecrop) * 0.5D);
    }
    else {
      return tecrop.size == 4 ? (int) ((double) super.growthDuration(tecrop) * 1.5D) : super.growthDuration(tecrop);
    }
  }
  
  public boolean canBeHarvested(TECrop tecrop) {
    return tecrop.size >= 4;
  }
  
  public ItemStack getGain(TECrop tecrop) {
    return tecrop.size == 4 ? null : new ItemStack(Ic2Items.coffeeBeans.getItem());
  }
  
  public byte getSizeAfterHarvest(TECrop tecrop) {
    return 3;
  }
}
