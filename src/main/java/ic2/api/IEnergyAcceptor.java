package ic2.api;

import net.minecraft.server.TileEntity;

public interface IEnergyAcceptor extends IEnergyTile {
  boolean acceptsEnergyFrom(TileEntity var1, Direction var2);
}
