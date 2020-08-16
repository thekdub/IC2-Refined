package ic2.common;

import ic2.api.INetworkItemEventListener;
import ic2.platform.NetworkManager;
import ic2.platform.*;
import net.minecraft.server.*;

public class ItemToolMiningLaser extends ItemElectricTool implements INetworkItemEventListener {
  private static final int EventShotMining = 0;
  private static final int EventShotLowFocus = 1;
  private static final int EventShotLongRange = 2;
  private static final int EventShotHorizontal = 3;
  private static final int EventShotSuperHeat = 4;
  private static final int EventShotScatter = 5;
  private static final int EventShotExplosive = 6;
  
  public ItemToolMiningLaser(int i, int j) {
    super(i, j, EnumToolMaterial.WOOD, 100);
    this.maxCharge = 200000;
    this.transferLimit = 120;
    this.tier = 2;
  }
  
  public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
    if (!mod_IC2.portEnableCraftingLaser) {
      return itemstack;
    }
    else if (!Platform.isSimulating()) {
      return itemstack;
    }
    else {
      NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
      int i = nbttagcompound.getInt("laserSetting");
      if (Keyboard.isModeSwitchKeyDown(entityhuman)) {
        i = (i + 1) % 7;
        nbttagcompound.setInt("laserSetting", i);
        String s =
            (new String[]{"Mining", "Low-Focus", "Long-Range", "Horizontal", "Super-Heat", "Scatter", "Explosive"})[i];
        Platform.messagePlayer(entityhuman, "Laser Mode: " + s);
      }
      else {
        int j = (new int[]{1250, 100, 5000, 0, 2500, 10000, 5000})[i];
        if (!ElectricItem.use(itemstack, j, entityhuman)) {
          return itemstack;
        }
  
        switch (i) {
          case 0:
            world.addEntity(
                new EntityMiningLaser(world, entityhuman, Float.POSITIVE_INFINITY, 5.0F, Integer.MAX_VALUE, false));
            NetworkManager.initiateItemEvent(entityhuman, itemstack, 0, true);
            break;
          case 1:
            world.addEntity(new EntityMiningLaser(world, entityhuman, 4.0F, 5.0F, 1, false));
            NetworkManager.initiateItemEvent(entityhuman, itemstack, 1, true);
            break;
          case 2:
            world.addEntity(
                new EntityMiningLaser(world, entityhuman, Float.POSITIVE_INFINITY, 20.0F, Integer.MAX_VALUE, false));
            NetworkManager.initiateItemEvent(entityhuman, itemstack, 2, true);
          case 3:
          default:
            break;
          case 4:
            world.addEntity(
                new EntityMiningLaser(world, entityhuman, Float.POSITIVE_INFINITY, 8.0F, Integer.MAX_VALUE, false,
                    true));
            NetworkManager.initiateItemEvent(entityhuman, itemstack, 4, true);
            break;
          case 5:
            for (int k = -2; k <= 2; ++k) {
              for (int l = -2; l <= 2; ++l) {
                world.addEntity(
                    new EntityMiningLaser(world, entityhuman, Float.POSITIVE_INFINITY, 12.0F, Integer.MAX_VALUE, false,
                        entityhuman.yaw + 20.0F * (float) k,
                        entityhuman.pitch + 20.0F * (float) l));
              }
            }
      
            NetworkManager.initiateItemEvent(entityhuman, itemstack, 5, true);
            break;
          case 6:
            world.addEntity(
                new EntityMiningLaser(world, entityhuman, Float.POSITIVE_INFINITY, 12.0F, Integer.MAX_VALUE, true));
            NetworkManager.initiateItemEvent(entityhuman, itemstack, 6, true);
        }
      }
  
      return itemstack;
    }
  }
  
  public boolean onItemUseFirst(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    if (!Platform.isSimulating()) {
      return false;
    }
    else {
      NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
      if (!Keyboard.isModeSwitchKeyDown(entityhuman) && nbttagcompound.getInt("laserSetting") == 3) {
        if (Math.abs(entityhuman.locY + (double) entityhuman.getHeadHeight() - 0.1D - ((double) j + 0.5D)) < 1.5D) {
          if (ElectricItem.use(itemstack, 3000, entityhuman)) {
            world.addEntity(
                new EntityMiningLaser(world, entityhuman, Float.POSITIVE_INFINITY, 5.0F, Integer.MAX_VALUE, false,
                    entityhuman.yaw, 0.0D, (double) j + 0.5D));
            NetworkManager.initiateItemEvent(entityhuman, itemstack, 3, true);
          }
        }
        else {
          Platform.messagePlayer(entityhuman, "Mining laser aiming angle too steep");
        }
      }
  
      return false;
    }
  }
  
  public int rarity(ItemStack itemstack) {
    return 1;
  }
  
  public void onNetworkEvent(int i, EntityHuman entityhuman, int j) {
    switch (j) {
      case 0:
        AudioManager.playOnce(entityhuman, PositionSpec.Hand, "Tools/MiningLaser/MiningLaser.ogg", true,
            AudioManager.defaultVolume);
        break;
      case 1:
        AudioManager.playOnce(entityhuman, PositionSpec.Hand, "Tools/MiningLaser/MiningLaserLowFocus.ogg", true,
            AudioManager.defaultVolume);
        break;
      case 2:
        AudioManager.playOnce(entityhuman, PositionSpec.Hand, "Tools/MiningLaser/MiningLaserLongRange.ogg", true,
            AudioManager.defaultVolume);
        break;
      case 3:
        AudioManager.playOnce(entityhuman, PositionSpec.Hand, "Tools/MiningLaser/MiningLaser.ogg", true,
            AudioManager.defaultVolume);
        break;
      case 4:
        AudioManager.playOnce(entityhuman, PositionSpec.Hand, "Tools/MiningLaser/MiningLaser.ogg", true,
            AudioManager.defaultVolume);
        break;
      case 5:
        AudioManager.playOnce(entityhuman, PositionSpec.Hand, "Tools/MiningLaser/MiningLaserScatter.ogg", true,
            AudioManager.defaultVolume);
        break;
      case 6:
        AudioManager.playOnce(entityhuman, PositionSpec.Hand, "Tools/MiningLaser/MiningLaserExplosive.ogg", true,
            AudioManager.defaultVolume);
    }
    
  }
}
