package ic2.common;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.server.Block;
import net.minecraft.server.ItemStack;

public class CropTerraWart extends CropCard {
  public String name() {
    return "Terra Wart";
  }
  
  public int tier() {
    return 5;
  }
  
  public int stat(int i) {
    switch (i) {
      case 0:
        return 2;
      case 1:
        return 4;
      case 2:
        return 0;
      case 3:
        return 3;
      case 4:
        return 0;
      default:
        return 0;
    }
  }
  
  public String[] attributes() {
    return new String[]{"Blue", "Aether", "Consumable", "Snow"};
  }
  
  public int getSpriteIndex(TECrop tecrop) {
    return tecrop.size == 1 ? 37 : tecrop.size + 38;
  }
  
  public boolean canGrow(TECrop tecrop) {
    return tecrop.size < 3;
  }
  
  public boolean canBeHarvested(TECrop tecrop) {
    return tecrop.size == 3;
  }
  
  public float dropGainChance() {
    return 0.8F;
  }
  
  public ItemStack getGain(TECrop tecrop) {
    return new ItemStack(Ic2Items.terraWart.getItem(), 1);
  }
  
  public void tick(TECrop tecrop) {
    TileEntityCrop tileentitycrop = (TileEntityCrop) tecrop;
    if (tileentitycrop.isBlockBelow(Block.SNOW_BLOCK)) {
      if (this.canGrow(tileentitycrop)) {
        tileentitycrop.growthPoints =
            (int) ((double) tileentitycrop.growthPoints + (double) tileentitycrop.calcGrowthRate() * 0.5D);
      }
    }
    else if (tileentitycrop.isBlockBelow(Block.SOUL_SAND) && tecrop.world.random.nextInt(300) == 0) {
      tileentitycrop.id = (short) IC2Crops.cropNetherWart.getId();
    }
    
  }
}
