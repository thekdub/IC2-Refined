package ic2.api;

import net.minecraft.server.TileEntity;

public interface IEnergyEmitter extends IEnergyTile {
  boolean emitsEnergyTo(TileEntity var1, Direction var2);
}
