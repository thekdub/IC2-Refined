package ic2.common;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.server.Block;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;

public class CropAurelia extends CropCard {
  public String name() {
    return "Aurelia";
  }
  
  public int tier() {
    return 8;
  }
  
  public int stat(int i) {
    switch (i) {
      case 0:
        return 2;
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
    return new String[]{"Gold", "Leaves", "Metal"};
  }
  
  public int getSpriteIndex(TECrop tecrop) {
    return tecrop.size == 4 ? 36 : 31 + tecrop.size;
  }
  
  public boolean canGrow(TECrop tecrop) {
    if (tecrop.size < 3) {
      return true;
    }
    else {
      return tecrop.size == 3 && tecrop.isBlockBelow(Block.GOLD_ORE);
    }
  }
  
  public boolean canBeHarvested(TECrop tecrop) {
    return tecrop.size == 4;
  }
  
  public ItemStack getGain(TECrop tecrop) {
    return new ItemStack(Item.GOLD_NUGGET);
  }
  
  public int growthDuration(TECrop tecrop) {
    return tecrop.size != 3 ? 1000 : 2200;
  }
  
  public byte getSizeAfterHarvest(TECrop tecrop) {
    return 2;
  }
}
