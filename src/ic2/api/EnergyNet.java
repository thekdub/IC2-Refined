package ic2.api;

import net.minecraft.server.TileEntity;
import net.minecraft.server.World;

public final class EnergyNet {
  Object energyNetInstance;

  private EnergyNet(Object obj) {
    this.energyNetInstance = obj;
  }

  public static EnergyNet getForWorld(World world) {
    try {
      return new EnergyNet(Class.forName(getPackage() + ".common.EnergyNet").getMethod("getForWorld", World.class).invoke(null, world));
    } catch (Exception var2) {
      throw new RuntimeException(var2);
    }
  }

  private static String getPackage() {
    Package package1 = EnergyNet.class.getPackage();
    return package1 != null ? package1.getName().substring(0, package1.getName().lastIndexOf(46)) : "ic2";
  }

  public void addTileEntity(TileEntity tileentity) {
    try {
      Class.forName(getPackage() + ".common.EnergyNet").getMethod("addTileEntity", TileEntity.class).invoke(this.energyNetInstance, tileentity);
    } catch (Exception var3) {
      throw new RuntimeException(var3);
    }
  }

  public void removeTileEntity(TileEntity tileentity) {
    try {
      Class.forName(getPackage() + ".common.EnergyNet").getMethod("removeTileEntity", TileEntity.class).invoke(this.energyNetInstance, tileentity);
    } catch (Exception var3) {
      throw new RuntimeException(var3);
    }
  }

  public int emitEnergyFrom(IEnergySource ienergysource, int i) {
    try {
      return (Integer) Class.forName(getPackage() + ".common.EnergyNet").getMethod("emitEnergyFrom", IEnergySource.class, Integer.TYPE).invoke(this.energyNetInstance, ienergysource, i);
    } catch (Exception var4) {
      throw new RuntimeException(var4);
    }
  }

  public long getTotalEnergyConducted(TileEntity tileentity) {
    try {
      return (Long) Class.forName(getPackage() + ".common.EnergyNet").getMethod("getTotalEnergyConducted", TileEntity.class).invoke(this.energyNetInstance, tileentity);
    } catch (Exception var3) {
      throw new RuntimeException(var3);
    }
  }
}
