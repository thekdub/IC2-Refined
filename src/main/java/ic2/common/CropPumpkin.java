package ic2.common;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.server.*;

public class CropPumpkin extends CropCard {
  public String name() {
    return "Pumpkin";
  }
  
  public String discoveredBy() {
    return "Notch";
  }
  
  public int tier() {
    return 1;
  }
  
  public int stat(int i) {
    switch (i) {
      case 0:
        return 0;
      case 1:
        return 1;
      case 2:
        return 0;
      case 3:
        return 3;
      case 4:
        return 1;
      default:
        return 0;
    }
  }
  
  public String[] attributes() {
    return new String[]{"Orange", "Decoration", "Stem"};
  }
  
  public int getSpriteIndex(TECrop tecrop) {
    return tecrop.size + 15;
  }
  
  public boolean canGrow(TECrop tecrop) {
    return tecrop.size <= 3;
  }
  
  public int weightInfluences(TECrop tecrop, float f, float f1, float f2) {
    return (int) ((double) f * 1.1D + (double) f1 * 0.9D + (double) f2);
  }
  
  public boolean canBeHarvested(TECrop tecrop) {
    return tecrop.size == 4;
  }
  
  public ItemStack getGain(TECrop tecrop) {
    return new ItemStack(Block.PUMPKIN);
  }
  
  public ItemStack getSeeds(TECrop tecrop) {
    return tecrop.statGain <= 1 && tecrop.statGrowth <= 1 && tecrop.statResistance <= 1 ?
        new ItemStack(Item.PUMPKIN_SEEDS, mod_IC2.random.nextInt(3) + 1) : super.getSeeds(tecrop);
  }
  
  public int growthDuration(TECrop tecrop) {
    return tecrop.size != 3 ? 200 : 600;
  }
  
  public byte getSizeAfterHarvest(TECrop tecrop) {
    return 3;
  }
}
