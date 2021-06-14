package ic2.common;

import net.minecraft.server.*;

public class ItemBooze extends ItemIC2 {
  public static float rumStackability = 2.0F;
  public static int rumDuration = 600;
  public String[] solidRatio = new String[]{"Watery ", "Clear ", "Lite ", "", "Strong ", "Thick ", "Stodge ", "X"};
  public String[] hopsRatio = new String[]{"Soup ", "Alcfree ", "White ", "", "Dark ", "Full ", "Black ", "X"};
  public String[] timeRatio = new String[]{"Brew", "Youngster", "Beer", "Ale", "Dragonblood", "X", "X", "X"};
  public int[] baseDuration = new int[]{300, 600, 900, 1200, 1600, 2000, 2400};
  public float[] baseIntensity = new float[]{0.4F, 0.75F, 1.0F, 1.5F, 2.0F};
  
  public ItemBooze(int i, int j) {
    super(i, j);
    this.e(1);
    this.hideFromCreative();
  }
  
  public static int getTypeOfValue(int i) {
    return skipGetOfValue(i, 0, 2);
  }
  
  public static int getAmountOfValue(int i) {
    return getTypeOfValue(i) == 0 ? 0 : skipGetOfValue(i, 2, 5) + 1;
  }
  
  public static int getSolidRatioOfBeerValue(int i) {
    return skipGetOfValue(i, 7, 3);
  }
  
  public static int getHopsRatioOfBeerValue(int i) {
    return skipGetOfValue(i, 10, 3);
  }
  
  public static int getTimeRatioOfBeerValue(int i) {
    return skipGetOfValue(i, 13, 3);
  }
  
  public static int getProgressOfRumValue(int i) {
    return skipGetOfValue(i, 7, 7);
  }
  
  private static int skipGetOfValue(int i, int j, int k) {
    i >>= j;
    k = (int) Math.pow(2.0D, k) - 1;
    return i & k;
  }
  
  public int getIconFromDamage(int i) {
    int j = getTypeOfValue(i);
    if (j == 1) {
      return this.textureId + getTimeRatioOfBeerValue(i);
    }
    else {
      return j == 2 ? this.textureId + 8 : this.textureId;
    }
  }
  
  public String getItemDisplayName(ItemStack itemstack) {
    int i = itemstack.getData();
    int j = getTypeOfValue(i);
    if (j == 1) {
      return getTimeRatioOfBeerValue(i) == 5 ? "Black Stuff" :
          this.solidRatio[getSolidRatioOfBeerValue(i)] + this.hopsRatio[getHopsRatioOfBeerValue(i)] +
              this.timeRatio[getTimeRatioOfBeerValue(i)];
    }
    else {
      return j == 2 ? "Rum" : "Zero";
    }
  }
  
  public ItemStack b(ItemStack itemstack, World world, EntityHuman entityhuman) {
    int i = itemstack.getData();
    int j = getTypeOfValue(i);
    if (j == 0) {
      return new ItemStack(Ic2Items.mugEmpty.getItem());
    }
    else {
      int i1;
      if (j == 1) {
        if (getTimeRatioOfBeerValue(i) == 5) {
          return this.drinkBlackStuff(entityhuman);
        }
  
        int k = getSolidRatioOfBeerValue(i);
        i1 = getHopsRatioOfBeerValue(i);
        int j1 = this.baseDuration[k];
        float f = this.baseIntensity[getTimeRatioOfBeerValue(i)];
        entityhuman.getFoodData().eat(6 - i1, (float) k * 0.15F);
        int k1 = (int) (f * (float) i1 * 0.5F);
        MobEffect mobeffect1 = entityhuman.getEffect(MobEffectList.SLOWER_DIG);
        int l1 = -1;
        if (mobeffect1 != null) {
          l1 = mobeffect1.getAmplifier();
        }
  
        this.amplifyEffect(entityhuman, MobEffectList.SLOWER_DIG, k1, f, j1);
        if (l1 > -1) {
          this.amplifyEffect(entityhuman, MobEffectList.INCREASE_DAMAGE, k1, f, j1);
          if (l1 > 0) {
            this.amplifyEffect(entityhuman, MobEffectList.SLOWER_MOVEMENT, k1 / 2, f, j1);
            if (l1 > 1) {
              this.amplifyEffect(entityhuman, MobEffectList.RESISTANCE, k1 - 1, f, j1);
              if (l1 > 2) {
                this.amplifyEffect(entityhuman, MobEffectList.CONFUSION, 0, f, j1);
                if (l1 > 3) {
                  entityhuman.addEffect(new MobEffect(MobEffectList.HARM.id, 1, entityhuman.world.random.nextInt(3)));
                }
              }
            }
          }
        }
      }
  
      if (j == 2) {
        if (getProgressOfRumValue(i) < 100) {
          this.drinkBlackStuff(entityhuman);
        }
        else {
          this.amplifyEffect(entityhuman, MobEffectList.FIRE_RESISTANCE, 0, rumStackability, rumDuration);
          MobEffect mobeffect = entityhuman.getEffect(MobEffectList.RESISTANCE);
          i1 = -1;
          if (mobeffect != null) {
            i1 = mobeffect.getAmplifier();
          }
  
          this.amplifyEffect(entityhuman, MobEffectList.RESISTANCE, 2, rumStackability, rumDuration);
          if (i1 >= 0) {
            this.amplifyEffect(entityhuman, MobEffectList.BLINDNESS, 0, rumStackability, rumDuration);
          }
  
          if (i1 >= 1) {
            this.amplifyEffect(entityhuman, MobEffectList.CONFUSION, 0, rumStackability, rumDuration);
          }
        }
      }
  
      return new ItemStack(Ic2Items.mugEmpty.getItem());
    }
  }
  
  public void amplifyEffect(EntityHuman entityhuman, MobEffectList mobeffectlist, int i, float f, int j) {
    MobEffect mobeffect = entityhuman.getEffect(mobeffectlist);
    if (mobeffect == null) {
      entityhuman.addEffect(new MobEffect(mobeffectlist.id, j, 0));
    }
    else {
      int k = mobeffect.getDuration();
      int l = (int) ((float) j * (1.0F + f * 2.0F) - (float) k) / 2;
      if (l < 0) {
        l = 0;
      }
  
      if (l < j) {
        j = l;
      }
  
      k += j;
      int i1 = mobeffect.getAmplifier();
      if (i1 < i) {
        ++i1;
      }
  
      entityhuman.addEffect(new MobEffect(mobeffectlist.id, k, i1));
    }
    
  }
  
  public ItemStack drinkBlackStuff(EntityHuman entityhuman) {
    switch (entityhuman.world.random.nextInt(6)) {
      case 1:
        entityhuman.addEffect(new MobEffect(MobEffectList.CONFUSION.id, 1200, 0));
        break;
      case 2:
        entityhuman.addEffect(new MobEffect(MobEffectList.BLINDNESS.id, 2400, 0));
        break;
      case 3:
        entityhuman.addEffect(new MobEffect(MobEffectList.POISON.id, 2400, 0));
        break;
      case 4:
        entityhuman.addEffect(new MobEffect(MobEffectList.POISON.id, 200, 2));
        break;
      case 5:
        entityhuman.addEffect(new MobEffect(MobEffectList.HARM.id, 1, entityhuman.world.random.nextInt(4)));
    }
    
    return new ItemStack(Ic2Items.mugEmpty.getItem());
  }
  
  public int c(ItemStack itemstack) {
    return 32;
  }
  
  public EnumAnimation d(ItemStack itemstack) {
    return EnumAnimation.c;
  }
  
  public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
    entityhuman.a(itemstack, this.c(itemstack));
    return itemstack;
  }
}
