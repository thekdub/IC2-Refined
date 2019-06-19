package ic2.common;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;

public class CropWheat extends CropCard {
  public String name() {
    return "Wheat";
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
        return 4;
      case 2:
        return 0;
      case 3:
        return 0;
      case 4:
        return 2;
      default:
        return 0;
    }
  }

  public String[] attributes() {
    return new String[]{"Yellow", "Food", "Wheat"};
  }

  public int getSpriteIndex(TECrop tecrop) {
    return tecrop.size + 1;
  }

  public boolean canGrow(TECrop tecrop) {
    return tecrop.size < 7 && tecrop.getLightLevel() >= 9;
  }

  public boolean canBeHarvested(TECrop tecrop) {
    return tecrop.size == 7;
  }

  public ItemStack getGain(TECrop tecrop) {
    return new ItemStack(Item.WHEAT, 1);
  }

  public ItemStack getSeeds(TECrop tecrop) {
    return tecrop.statGain <= 1 && tecrop.statGrowth <= 1 && tecrop.statResistance <= 1 ? new ItemStack(Item.SEEDS) : super.getSeeds(tecrop);
  }

  public byte getSizeAfterHarvest(TECrop tecrop) {
    return 2;
  }
}
