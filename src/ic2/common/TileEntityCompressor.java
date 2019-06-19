package ic2.common;

import ic2.api.Ic2Recipes;
import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;

import java.util.List;
import java.util.Vector;

public class TileEntityCompressor extends TileEntityElectricMachine {
  public static List recipes = new Vector();
  public TileEntityPump validPump;

  public TileEntityCompressor() {
    super(3, 2, 400, 32);
  }

  public static void init() {
    Ic2Recipes.addCompressorRecipe(Ic2Items.plantBall, Ic2Items.compressedPlantBall);
    Ic2Recipes.addCompressorRecipe(Ic2Items.hydratedCoalDust, Ic2Items.hydratedCoalClump);
    Ic2Recipes.addCompressorRecipe(new ItemStack(Block.NETHERRACK, 3), new ItemStack(Block.NETHER_BRICK));
    Ic2Recipes.addCompressorRecipe(new ItemStack(Block.SAND), new ItemStack(Block.SANDSTONE));
    Ic2Recipes.addCompressorRecipe(new ItemStack(Item.SNOW_BALL), new ItemStack(Block.ICE));
    Ic2Recipes.addCompressorRecipe(Ic2Items.waterCell, new ItemStack(Item.SNOW_BALL));
    Ic2Recipes.addCompressorRecipe(Ic2Items.mixedMetalIngot, Ic2Items.advancedAlloy);
    Ic2Recipes.addCompressorRecipe(Ic2Items.carbonMesh, Ic2Items.carbonPlate);
    Ic2Recipes.addCompressorRecipe(Ic2Items.coalBall, Ic2Items.compressedCoalBall);
    Ic2Recipes.addCompressorRecipe(Ic2Items.coalChunk, new ItemStack(Item.DIAMOND));
    Ic2Recipes.addCompressorRecipe(Ic2Items.constructionFoam, Ic2Items.constructionFoamPellet);
  }

  public ItemStack getResultFor(ItemStack itemstack, boolean flag) {
    return Ic2Recipes.getCompressorOutputFor(itemstack, flag);
  }

  public boolean canOperate() {
    if (this.getValidPump() == null) {
      return super.canOperate();
    }
    else {
      return this.inventory[2] == null || this.inventory[2].doMaterialsMatch(new ItemStack(Item.SNOW_BALL)) && this.inventory[2].count < Item.SNOW_BALL.getMaxStackSize();
    }
  }

  public void operate() {
    if (this.canOperate()) {
      ItemStack itemstack = null;
      if (this.inventory[0] != null) {
        itemstack = this.getResultFor(this.inventory[0], false);
      }

      if (itemstack == null) {
        TileEntityPump tileentitypump = this.getValidPump();
        if (tileentitypump == null) {
          return;
        }

        tileentitypump.pumpCharge = 0;
        this.world.setTypeId(tileentitypump.x, tileentitypump.y - 1, tileentitypump.z, 0);
        if (this.inventory[2] == null) {
          this.inventory[2] = new ItemStack(Item.SNOW_BALL);
        }
        else {
          ++this.inventory[2].count;
        }
      }
      else {
        super.operate();
      }

    }
  }

  public TileEntityPump getValidPump() {
    if (this.validPump != null && this.validPump.isPumpReady() && this.validPump.isWaterBelow()) {
      return this.validPump;
    }
    else {
      TileEntityPump tileentitypump4;
      if (this.world.getTileEntity(this.x, this.y - 1, this.z) instanceof TileEntityPump) {
        tileentitypump4 = (TileEntityPump) this.world.getTileEntity(this.x, this.y - 1, this.z);
        if (tileentitypump4.isPumpReady() && tileentitypump4.isWaterBelow()) {
          return this.validPump = tileentitypump4;
        }
      }

      if (this.world.getTileEntity(this.x + 1, this.y, this.z) instanceof TileEntityPump) {
        tileentitypump4 = (TileEntityPump) this.world.getTileEntity(this.x + 1, this.y, this.z);
        if (tileentitypump4.isPumpReady() && tileentitypump4.isWaterBelow()) {
          return this.validPump = tileentitypump4;
        }
      }

      if (this.world.getTileEntity(this.x - 1, this.y, this.z) instanceof TileEntityPump) {
        tileentitypump4 = (TileEntityPump) this.world.getTileEntity(this.x - 1, this.y, this.z);
        if (tileentitypump4.isPumpReady() && tileentitypump4.isWaterBelow()) {
          return this.validPump = tileentitypump4;
        }
      }

      if (this.world.getTileEntity(this.x, this.y, this.z + 1) instanceof TileEntityPump) {
        tileentitypump4 = (TileEntityPump) this.world.getTileEntity(this.x, this.y, this.z + 1);
        if (tileentitypump4.isPumpReady() && tileentitypump4.isWaterBelow()) {
          return this.validPump = tileentitypump4;
        }
      }

      if (this.world.getTileEntity(this.x, this.y, this.z - 1) instanceof TileEntityPump) {
        tileentitypump4 = (TileEntityPump) this.world.getTileEntity(this.x, this.y, this.z - 1);
        if (tileentitypump4.isPumpReady() && tileentitypump4.isWaterBelow()) {
          return this.validPump = tileentitypump4;
        }
      }

      return null;
    }
  }

  public String getName() {
    return "Compressor";
  }

  public String getGuiClassName(EntityHuman entityhuman) {
    return "GuiCompressor";
  }

  public String getStartSoundFile() {
    return "Machines/CompressorOp.ogg";
  }

  public String getInterruptSoundFile() {
    return "Machines/InterruptOne.ogg";
  }

  public float getWrenchDropRate() {
    return 0.85F;
  }
}
