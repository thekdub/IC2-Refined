package ic2.common;

import forge.ArmorProperties;
import ic2.api.IMetalArmor;
import ic2.platform.Keyboard;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.HashMap;
import java.util.Map;

public class ItemArmorQuantumSuit extends ItemArmorElectric implements IMetalArmor, IItemTickListener {
  public static float speedCap = 6.0F;
  public static Map speedTickerMap = new HashMap();
  public static Map jumpChargeMap = new HashMap();
  
  public ItemArmorQuantumSuit(int i, int j, int k, int l) {
    super(i, j, k, l, 1000000, 1000, 3);
  }
  
  public ArmorProperties getProperties(EntityLiving entityliving, ItemStack itemstack, DamageSource damagesource,
                                       double d, int i) {
    if (damagesource == DamageSource.FALL && this.a == 3) {
      int j = this.getEnergyPerDamage();
      int k = j <= 0 ? 0 : ElectricItem.discharge(itemstack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true) / j;
      return new ArmorProperties(10, 1.0D, k);
    }
    else {
      return super.getProperties(entityliving, itemstack, damagesource, d, i);
    }
  }
  
  public double getDamageAbsorptionRatio() {
    return this.a != 1 ? 1.0D : 1.1D;
  }
  
  public int getEnergyPerDamage() {
    return 30;
  }
  
  public boolean isMetalArmor(ItemStack itemstack, EntityHuman entityhuman) {
    return true;
  }
  
  public int rarity(ItemStack itemstack) {
    return 2;
  }
  
  public boolean onTick(EntityHuman entityhuman, ItemStack itemstack) {
    boolean flag = false;
    switch (this.a) {
      case 0:
        Platform.profilerStartSection("QuantumHelmet");
        int i = Platform.getEntityAirLeft(entityhuman);
        if (ElectricItem.canUse(itemstack, 1000) && i < 100) {
          Platform.setEntityAirLeft(entityhuman, i + 200);
          ElectricItem.use(itemstack, 1000, null);
          flag = true;
        }
  
        if (ElectricItem.canUse(itemstack, 10000) && Platform.givePlayerOneFood(entityhuman)) {
          ElectricItem.use(itemstack, 10000, null);
          flag = true;
        }
        else if (entityhuman.getFoodData().a() == 0) {
          IC2Achievements.issueAchievement(entityhuman, "starveWithQHelmet");
        }
  
        Platform.removePotionFrom(entityhuman, MobEffectList.POISON.id);
        Platform.profilerEndSection();
        break;
      case 1:
        Platform.profilerStartSection("QuantumBodyarmor");
        Platform.setEntityOnFire(entityhuman, 0);
        Platform.profilerEndSection();
        break;
      case 2:
        Platform.profilerStartSection("QuantumLeggings");
        if (ElectricItem.canUse(itemstack, 1000) &&
            (entityhuman.onGround && Math.abs(entityhuman.motX) + Math.abs(entityhuman.motZ) > 0.10000000149011612D ||
                entityhuman.aU()) && (mod_IC2.enableQuantumSpeedOnSprint && Platform.isPlayerSprinting(entityhuman) ||
            !mod_IC2.enableQuantumSpeedOnSprint && Keyboard.isBoostKeyDown(entityhuman))) {
          int j = speedTickerMap.containsKey(entityhuman) ? (Integer) speedTickerMap.get(entityhuman) : 0;
          ++j;
          if (j >= 10) {
            j = 0;
            ElectricItem.use(itemstack, 1000, null);
            flag = true;
          }
      
          speedTickerMap.put(entityhuman, j);
          float f1 = 0.22F;
          if (entityhuman.aU()) {
            f1 = 0.1F;
            if (mod_IC2.getIsJumpingOfEntityLiving(entityhuman)) {
              entityhuman.motY += 0.10000000149011612D;
            }
          }
      
          if (f1 > 0.0F) {
            entityhuman.a(0.0F, 1.0F, f1);
          }
        }
    
        Platform.profilerEndSection();
        break;
      case 3:
        Platform.profilerStartSection("QuantumBoots");
        float f = jumpChargeMap.containsKey(entityhuman) ? (Float) jumpChargeMap.get(entityhuman) : 1.0F;
        if (ElectricItem.canUse(itemstack, 1000) && entityhuman.onGround && f < 1.0F) {
          f = 1.0F;
          ElectricItem.use(itemstack, 1000, null);
          flag = true;
        }
  
        if (entityhuman.motY >= 0.0D && f > 0.0F && !entityhuman.aU()) {
          if (Keyboard.isJumpKeyDown(entityhuman) && Keyboard.isBoostKeyDown(entityhuman)) {
            if (f == 1.0F) {
              entityhuman.motX *= 3.5D;
              entityhuman.motZ *= 3.5D;
            }
  
            entityhuman.motY += f * 0.3F;
            f = (float) ((double) f * 0.75D);
          }
          else if (f < 1.0F) {
            f = 0.0F;
          }
        }
  
        jumpChargeMap.put(entityhuman, f);
        if (entityhuman.motX > (double) speedCap) {
          entityhuman.motX = speedCap;
        }
  
        if (entityhuman.motZ > (double) speedCap) {
          entityhuman.motZ = speedCap;
        }
  
        Platform.profilerEndSection();
    }
    
    return flag;
  }
}
