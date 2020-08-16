package ic2.bcIntegration22x.common;

import buildcraft.api.ILiquidContainer;
import buildcraft.api.Orientations;
import ic2.common.TileEntityGeoGenerator;
import net.minecraft.server.Block;

public class TileEntityGeoGeneratorBc22x extends TileEntityGeoGenerator implements ILiquidContainer {
  public int fill(Orientations orientations, int i, int j, boolean flag) {
    if (j != this.getLiquidId()) {
      return 0;
    }
    else {
      int k = Math.min(i, this.maxLava - this.fuel);
      if (flag) {
        this.fuel += k;
      }
  
      return k;
    }
  }
  
  public int empty(int i, boolean flag) {
    return 0;
  }
  
  public int getLiquidQuantity() {
    return this.fuel;
  }
  
  public int getCapacity() {
    return this.maxLava;
  }
  
  public int getLiquidId() {
    return Block.STATIONARY_LAVA.id;
  }
}
