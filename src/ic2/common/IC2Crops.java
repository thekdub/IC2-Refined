package ic2.common;

import ic2.api.CropCard;
import ic2.platform.Platform;
import net.minecraft.server.Block;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;

public class IC2Crops {
  public static CropCard weed = new CropWeed();
  public static CropCard cropWheat = new CropWheat();
  public static CropCard cropPumpkin = new CropPumpkin();
  public static CropCard cropMelon = new CropMelon();
  public static CropCard cropYellowFlower = new CropColorFlower("Dandelion", new String[]{"Yellow", "Flower"}, 15, 11);
  public static CropCard cropRedFlower = new CropColorFlower("Rose", new String[]{"Red", "Flower", "Rose"}, 21, 1);
  public static CropCard cropBlackFlower =
      new CropColorFlower("Blackthorn", new String[]{"Black", "Flower", "Rose"}, 22, 0);
  public static CropCard cropPurpleFlower =
      new CropColorFlower("Tulip", new String[]{"Purple", "Flower", "Tulip"}, 23, 5);
  public static CropCard cropBlueFlower = new CropColorFlower("Cyazint", new String[]{"Blue", "Flower"}, 24, 6);
  public static CropCard cropVenomilia = new CropVenomilia();
  public static CropCard cropReed = new CropReed();
  public static CropCard cropStickReed = new CropStickReed();
  public static CropCard cropCocoa = new CropCocoa();
  public static CropCard cropFerru = new CropFerru();
  public static CropCard cropAurelia = new CropAurelia();
  public static CropCard cropRedwheat = new CropRedWheat();
  public static CropCard cropNetherWart = new CropNetherWart();
  public static CropCard cropTerraWart = new CropTerraWart();
  public static CropCard cropCoffee = new CropCoffee();
  public static CropCard cropHops = new CropHops();
  
  public static void init() {
    registerCrops();
    registerBaseSeeds();
  }
  
  public static void registerCrops() {
    CropCard.nameReference = new TileEntityCrop();
    if (!CropCard.registerCrop(weed, 0) || !CropCard.registerCrop(cropWheat, 1) ||
        !CropCard.registerCrop(cropPumpkin, 2) || !CropCard.registerCrop(cropMelon, 3) ||
        !CropCard.registerCrop(cropYellowFlower, 4) || !CropCard.registerCrop(cropRedFlower, 5) ||
        !CropCard.registerCrop(cropBlackFlower, 6) || !CropCard.registerCrop(cropPurpleFlower, 7) ||
        !CropCard.registerCrop(cropBlueFlower, 8) || !CropCard.registerCrop(cropVenomilia, 9) ||
        !CropCard.registerCrop(cropReed, 10) || !CropCard.registerCrop(cropStickReed, 11) ||
        !CropCard.registerCrop(cropCocoa, 12) || !CropCard.registerCrop(cropFerru, 13) ||
        !CropCard.registerCrop(cropAurelia, 14) || !CropCard.registerCrop(cropRedwheat, 15) ||
        !CropCard.registerCrop(cropNetherWart, 16) || !CropCard.registerCrop(cropTerraWart, 17) ||
        !CropCard.registerCrop(cropCoffee, 18) || !CropCard.registerCrop(cropHops, 19)) {
      Platform.displayError(
          "One or more crops have failed to initialize.\nThis could happen due to a crop addon using a crop ID already taken\nby a crop from IndustrialCraft 2.");
    }
    
  }
  
  public static void registerBaseSeeds() {
    CropCard.registerBaseSeed(new ItemStack(Item.SEEDS.id, 1, -1), cropWheat.getId(), 1, 1, 1, 1);
    CropCard.registerBaseSeed(new ItemStack(Item.PUMPKIN_SEEDS.id, 1, -1), cropPumpkin.getId(), 1, 1, 1, 1);
    CropCard.registerBaseSeed(new ItemStack(Item.MELON_SEEDS.id, 1, -1), cropMelon.getId(), 1, 1, 1, 1);
    CropCard.registerBaseSeed(new ItemStack(Item.NETHER_STALK.id, 1, -1), cropNetherWart.getId(), 1, 1, 1, 1);
    CropCard.registerBaseSeed(new ItemStack(Ic2Items.terraWart.id, 1, -1), cropTerraWart.getId(), 1, 1, 1, 1);
    CropCard.registerBaseSeed(new ItemStack(Ic2Items.coffeeBeans.id, 1, -1), cropCoffee.getId(), 1, 1, 1, 1);
    CropCard.registerBaseSeed(new ItemStack(Item.SUGAR_CANE.id, 1, -1), cropReed.getId(), 1, 3, 0, 2);
    CropCard.registerBaseSeed(new ItemStack(Item.INK_SACK.id, 1, 3), cropCocoa.getId(), 1, 0, 0, 0);
    CropCard.registerBaseSeed(new ItemStack(Block.RED_ROSE.id, 4, -1), cropRedFlower.getId(), 4, 1, 1, 1);
    CropCard.registerBaseSeed(new ItemStack(Block.YELLOW_FLOWER.id, 4, -1), cropYellowFlower.getId(), 4, 1, 1, 1);
  }
}
