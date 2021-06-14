package ic2.common;

import ic2.platform.*;
import net.minecraft.server.*;

import java.util.ArrayList;

public class ItemArmorJetpack extends ItemArmorUtility implements IItemTickListener {
  public static AudioSource audioSource;
  private static boolean lastJetpackUsed = false;
  
  public ItemArmorJetpack(int i, int j, int k) {
    super(i, j, k, 1);
    this.setMaxDurability(18002);
  }
  
  public int getCharge(ItemStack itemstack) {
    int i = this.getMaxCharge(itemstack) - itemstack.getData() - 1;
    return i <= 0 ? 0 : i;
  }
  
  public int getMaxCharge(ItemStack itemstack) {
    return itemstack.i() - 2;
  }
  
  public void use(ItemStack itemstack, int i) {
    int j = this.getCharge(itemstack) - i;
    if (j < 0) {
      j = 0;
    }
    
    itemstack.setData(1 + itemstack.i() - j);
  }
  
  public boolean useJetpack(EntityHuman entityhuman, boolean flag) {
    ItemStack itemstack = entityhuman.inventory.armor[2];
    if (this.getCharge(itemstack) == 0) {
      return false;
    }
    else {
      boolean flag1 = itemstack.id != Ic2Items.jetpack.id;
      float f = 1.0F;
      float f1 = 0.2F;
      if (flag1) {
        f = 0.7F;
        f1 = 0.05F;
      }
  
      if ((float) this.getCharge(itemstack) / (float) this.getMaxCharge(itemstack) <= f1) {
        f *= (float) this.getCharge(itemstack) / ((float) this.getMaxCharge(itemstack) * f1);
      }
  
      if (Keyboard.isForwardKeyDown(entityhuman)) {
        float f2 = 0.15F;
        if (flag) {
          f2 = 0.5F;
        }
    
        if (flag1) {
          f2 += 0.15F;
        }
    
        float f3 = f * f2 * 2.0F;
        if (f3 > 0.0F) {
          entityhuman.a(0.0F, 0.4F * f3, 0.02F);
        }
      }
  
      int i = mod_IC2.getWorldHeight(entityhuman.world);
      int j = flag1 ? (int) ((float) i / 1.28F) : i;
      double d = entityhuman.locY;
      if (d > (double) (j - 25)) {
        if (d > (double) j) {
          d = j;
        }
  
        f = (float) ((double) f * (((double) j - d) / 25.0D));
      }
  
      double d1 = entityhuman.motY;
      entityhuman.motY = Math.min(entityhuman.motY + (double) (f * 0.2F), 0.6000000238418579D);
      if (flag) {
        float f4 = -0.1F;
        if (flag1 && Keyboard.isJumpKeyDown(entityhuman)) {
          f4 = 0.1F;
        }
  
        if (entityhuman.motY > (double) f4) {
          entityhuman.motY = f4;
          if (d1 > entityhuman.motY) {
            entityhuman.motY = d1;
          }
        }
      }
  
      int k = 9;
      if (flag) {
        k = 6;
      }
  
      if (flag1) {
        k -= 2;
      }
  
      this.use(itemstack, k);
      mod_IC2.setFallDistanceOfEntity(entityhuman, 0.0F);
      entityhuman.bJ = 0.0F;
      Platform.resetPlayerInAirTime(entityhuman);
      return true;
    }
  }
  
  public boolean onTick(EntityHuman entityhuman, ItemStack itemstack) {
    NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
    boolean flag = nbttagcompound.getBoolean("hoverMode");
    byte byte0 = nbttagcompound.getByte("toggleTimer");
    boolean flag1 = false;
    if (Keyboard.isJumpKeyDown(entityhuman) && Keyboard.isModeSwitchKeyDown(entityhuman) && byte0 == 0) {
      byte0 = 10;
      flag = !flag;
      if (Platform.isSimulating()) {
        nbttagcompound.setBoolean("hoverMode", flag);
        if (flag) {
          Platform.messagePlayer(entityhuman, "Hover Mode enabled.");
        }
        else {
          Platform.messagePlayer(entityhuman, "Hover Mode disabled.");
        }
      }
    }
    
    if (Keyboard.isJumpKeyDown(entityhuman) || flag && entityhuman.motY < -0.3499999940395355D) {
      flag1 = this.useJetpack(entityhuman, flag);
    }
    
    if (Platform.isSimulating() && byte0 > 0) {
      --byte0;
      nbttagcompound.setByte("toggleTimer", byte0);
    }
    
    if (Platform.isRendering() && entityhuman == Platform.getPlayerInstance()) {
      if (lastJetpackUsed != flag1) {
        if (flag1) {
          if (audioSource == null) {
            audioSource = AudioManager
                .createSource(entityhuman, PositionSpec.Backpack, "Tools/Jetpack/JetpackLoop.ogg", true, false,
                    AudioManager.defaultVolume);
          }
  
          if (audioSource != null) {
            audioSource.play();
          }
        }
        else if (audioSource != null) {
          audioSource.remove();
          audioSource = null;
        }
  
        lastJetpackUsed = flag1;
      }
      
      if (audioSource != null) {
        audioSource.updatePosition();
      }
    }
    
    return flag1;
  }
  
  public void addCreativeItems(ArrayList arraylist) {
    arraylist.add(new ItemStack(this, 1, 1));
  }
}
