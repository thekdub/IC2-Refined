package ic2.common;

import net.minecraft.server.*;

import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class ItemScrapbox extends ItemIC2 {
  public static List dropList = new Vector();

  public ItemScrapbox(int i, int j) {
    super(i, j);
  }

  public static void init() {
    if (mod_IC2.suddenlyHoes) {
      addDrop(Item.WOOD_HOE, 9001.0F);
    }
    else {
      addDrop(Item.WOOD_HOE, 5.01F);
    }

    addDrop(Block.DIRT, 5.0F);
    addDrop(Item.STICK, 4.0F);
    addDrop(Block.GRASS, 3.0F);
    addDrop(Block.GRAVEL, 3.0F);
    addDrop(Block.NETHERRACK, 2.0F);
    addDrop(Item.ROTTEN_FLESH, 2.0F);
    addDrop(Item.APPLE, 1.5F);
    addDrop(Item.BREAD, 1.5F);
    addDrop(Ic2Items.filledTinCan.getItem(), 1.5F);
    addDrop(Item.WOOD_SWORD);
    addDrop(Item.WOOD_SPADE);
    addDrop(Item.WOOD_PICKAXE);
    addDrop(Block.SOUL_SAND);
    addDrop(Item.SIGN);
    addDrop(Item.LEATHER);
    addDrop(Item.FEATHER);
    addDrop(Item.BONE);
    addDrop(Item.GRILLED_PORK, 0.9F);
    addDrop(Item.COOKED_BEEF, 0.9F);
    addDrop(Block.PUMPKIN, 0.9F);
    addDrop(Item.COOKED_CHICKEN, 0.9F);
    addDrop(Item.MINECART, 0.9F);
    addDrop(Item.REDSTONE, 0.9F);
    addDrop(Ic2Items.rubber.getItem(), 0.8F);
    addDrop(Item.GLOWSTONE_DUST, 0.8F);
    addDrop(Ic2Items.coalDust.getItem(), 0.8F);
    addDrop(Ic2Items.copperDust.getItem(), 0.8F);
    addDrop(Ic2Items.tinDust.getItem(), 0.8F);
    addDrop(Ic2Items.plantBall.getItem(), 0.7F);
    addDrop(Ic2Items.suBattery.getItem(), 0.7F);
    addDrop(Ic2Items.ironDust.getItem(), 0.7F);
    addDrop(Ic2Items.goldDust.getItem(), 0.7F);
    addDrop(Item.SLIME_BALL, 0.6F);
    addDrop(Block.IRON_ORE, 0.5F);
    addDrop(Item.GOLD_HELMET, 0.5F);
    addDrop(Block.GOLD_ORE, 0.5F);
    addDrop(Item.CAKE, 0.5F);
    addDrop(Item.DIAMOND, 0.1F);
    if (Ic2Items.copperOre != null) {
      addDrop(Ic2Items.copperOre.getItem(), 0.7F);
    }

    if (Ic2Items.tinOre != null) {
      addDrop(Ic2Items.tinOre.getItem(), 0.7F);
    }

  }

  public static void addDrop(Item item) {
    addDrop(new ItemStack(item), 1.0F);
  }

  public static void addDrop(Item item, float f) {
    addDrop(new ItemStack(item), f);
  }

  public static void addDrop(Block block) {
    addDrop(new ItemStack(block), 1.0F);
  }

  public static void addDrop(Block block, float f) {
    addDrop(new ItemStack(block), f);
  }

  public static void addDrop(ItemStack itemstack) {
    addDrop(itemstack, 1.0F);
  }

  public static void addDrop(ItemStack itemstack, float f) {
    dropList.add(new Drop(itemstack, f));
  }

  public static ItemStack getDrop(World world) {
    if (!dropList.isEmpty()) {
      float f = world.random.nextFloat() * ((Drop) dropList.get(dropList.size() - 1)).upperChanceBound;
      Iterator iterator = dropList.iterator();

      while (iterator.hasNext()) {
        Drop drop = (Drop) iterator.next();
        if (drop.upperChanceBound >= f) {
          return drop.itemStack.cloneItemStack();
        }
      }
    }

    return null;
  }

  public static List getDropList() {
    Vector vector = new Vector();
    Iterator iterator = dropList.iterator();

    while (iterator.hasNext()) {
      Drop drop = (Drop) iterator.next();
      vector.add(new SimpleEntry(drop.itemStack, drop.upperChanceBound));
    }

    return vector;
  }

  public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
    if (!entityhuman.abilities.canInstantlyBuild) {
      --itemstack.count;
    }

    ItemStack itemstack1 = getDrop(world);
    if (itemstack1 != null) {
      entityhuman.drop(itemstack1);
    }

    return itemstack;
  }

  static class Drop {
    ItemStack itemStack;
    float upperChanceBound;

    Drop(ItemStack itemstack, float f) {
      this.itemStack = itemstack;
      if (ItemScrapbox.dropList.isEmpty()) {
        this.upperChanceBound = f;
      }
      else {
        this.upperChanceBound = ((Drop) ItemScrapbox.dropList.get(ItemScrapbox.dropList.size() - 1)).upperChanceBound + f;
      }

    }
  }
}
