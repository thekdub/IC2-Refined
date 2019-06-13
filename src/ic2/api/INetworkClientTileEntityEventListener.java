package ic2.api;

import net.minecraft.server.EntityHuman;

public interface INetworkClientTileEntityEventListener {
	void onNetworkEvent(EntityHuman var1, int var2);
}
