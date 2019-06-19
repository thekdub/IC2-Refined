package ic2.common;

import forge.ISidedInventory;
import net.minecraft.server.BiomeDesert;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.World;
import net.minecraft.server.mod_IC2;

import java.util.Random;

public class TileEntitySolarGenerator extends TileEntityBaseGenerator implements ISidedInventory {
  public static Random randomizer = new Random();
  public int ticker;
  public boolean sunIsVisible = false;

  public TileEntitySolarGenerator() {
    super(1, 1, 1);
    this.ticker = randomizer.nextInt(this.tickRate());
  }

  public static boolean isSunVisible(World world, int i, int j, int k) {
    return world.e() && !world.worldProvider.e && world.isChunkLoaded(i, j, k) && (world.getWorldChunkManager().getBiome(i, k) instanceof BiomeDesert || !world.x() && !world.w());
  }

  public void onCreated() {
    super.onCreated();
    this.updateSunVisibility();
  }

  public int gaugeFuelScaled(int i) {
    return i;
  }

  public boolean gainEnergy() {
    if (this.ticker++ % this.tickRate() == 0) {
      this.updateSunVisibility();
    }
    if (this.sunIsVisible) {
      if (randomizer.nextInt(100) < mod_IC2.energyGeneratorSolar) {
        ++this.storage;
      }
      return true;
    }
    else {
      return false;
    }
  }

  public boolean gainFuel() {
    return false;
  }

  public void updateSunVisibility() {
    this.sunIsVisible = isSunVisible(this.world, this.x, this.y + 1, this.z);
  }

  public boolean needsFuel() {
    return true;
  }

  public String getName() {
    return "Solar Panel";
  }

  public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
    return new ContainerSolarGenerator(entityhuman, this);
  }

  public String getGuiClassName(EntityHuman entityhuman) {
    return "GuiSolarGenerator";
  }

  public int tickRate() {
    return 128;
  }

  public boolean delayActiveUpdate() {
    return true;
  }

  public int getStartInventorySide(int i) {
    return 0;
  }

  public int getSizeInventorySide(int i) {
    return 1;
  }
}
