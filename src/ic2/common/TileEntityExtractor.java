package ic2.common;

import ic2.api.Ic2Recipes;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;

import java.util.List;
import java.util.Vector;

public class TileEntityExtractor extends TileEntityElectricMachine {
  public static List recipes = new Vector();
  
  public TileEntityExtractor() {
    super(3, 2, 400, 32);
  }
  
  public static void init() {
    if (Ic2Items.rubberSapling != null) {
      Ic2Recipes.addExtractorRecipe(Ic2Items.rubberSapling, Ic2Items.rubber);
    }
    
    Ic2Recipes.addExtractorRecipe(Ic2Items.resin, StackUtil.copyWithSize(Ic2Items.rubber, 3));
    Ic2Recipes.addExtractorRecipe(Ic2Items.bioCell, Ic2Items.biofuelCell);
    Ic2Recipes.addExtractorRecipe(Ic2Items.hydratedCoalCell, Ic2Items.coalfuelCell);
    Ic2Recipes.addExtractorRecipe(Ic2Items.waterCell, Ic2Items.coolingCell);
    Ic2Recipes.addExtractorRecipe(Ic2Items.coolingCell, Ic2Items.hydratingCell);
  }
  
  public ItemStack getResultFor(ItemStack itemstack, boolean flag) {
    return Ic2Recipes.getExtractorOutputFor(itemstack, flag);
  }
  
  public String getName() {
    return "Extractor";
  }
  
  public String getGuiClassName(EntityHuman entityhuman) {
    return "GuiExtractor";
  }
  
  public String getStartSoundFile() {
    return "Machines/ExtractorOp.ogg";
  }
  
  public String getInterruptSoundFile() {
    return "Machines/InterruptOne.ogg";
  }
  
  public float getWrenchDropRate() {
    return 0.85F;
  }
}
