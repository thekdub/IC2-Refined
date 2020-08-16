package ic2.common;

import ic2.api.Ic2Recipes;
import net.minecraft.server.*;

import java.util.List;
import java.util.Vector;

public class TileEntityMacerator extends TileEntityElectricMachine {
  public static List recipes = new Vector();
  
  public TileEntityMacerator() {
    super(3, 2, 400, 32);
  }
  
  public static void init() {
    Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.IRON_ORE), StackUtil.copyWithSize(Ic2Items.ironDust, 2));
    Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.GOLD_ORE), StackUtil.copyWithSize(Ic2Items.goldDust, 2));
    Ic2Recipes.addMaceratorRecipe(new ItemStack(Item.COAL), Ic2Items.coalDust);
    Ic2Recipes.addMaceratorRecipe(new ItemStack(Item.IRON_INGOT), Ic2Items.ironDust);
    Ic2Recipes.addMaceratorRecipe(new ItemStack(Item.GOLD_INGOT), Ic2Items.goldDust);
    Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.WOOL), new ItemStack(Item.STRING));
    Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.GRAVEL), new ItemStack(Item.FLINT));
    Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.STONE), new ItemStack(Block.COBBLESTONE));
    Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.COBBLESTONE), new ItemStack(Block.SAND));
    Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.SANDSTONE), new ItemStack(Block.SAND));
    Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.ICE), new ItemStack(Item.SNOW_BALL));
    Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.CLAY), StackUtil.copyWithSize(Ic2Items.clayDust, 2));
    Ic2Recipes.addMaceratorRecipe(new ItemStack(Block.GLOWSTONE), new ItemStack(Item.GLOWSTONE_DUST, 4));
    Ic2Recipes.addMaceratorRecipe(new ItemStack(Item.BONE), new ItemStack(Item.INK_SACK, 5, 15));
    Ic2Recipes.addMaceratorRecipe(Ic2Items.plantBall, new ItemStack(Block.DIRT, 8));
    Ic2Recipes.addMaceratorRecipe(Ic2Items.coffeeBeans, new ItemStack(Ic2Items.coffeePowder.getItem(), 3));
    Ic2Recipes.addMaceratorRecipe(new ItemStack(Item.BLAZE_ROD), new ItemStack(Item.BLAZE_POWDER, 5));
    Ic2Recipes.addMaceratorRecipe(new ItemStack(Item.SPIDER_EYE), new ItemStack(Ic2Items.grinPowder.getItem(), 2));
  }
  
  public ItemStack getResultFor(ItemStack itemstack, boolean flag) {
    return Ic2Recipes.getMaceratorOutputFor(itemstack, flag);
  }
  
  public String getName() {
    return "Macerator";
  }
  
  public String getGuiClassName(EntityHuman entityhuman) {
    return "GuiMacerator";
  }
  
  public String getStartSoundFile() {
    return "Machines/MaceratorOp.ogg";
  }
  
  public String getInterruptSoundFile() {
    return "Machines/InterruptOne.ogg";
  }
  
  public float getWrenchDropRate() {
    return 0.85F;
  }
}
