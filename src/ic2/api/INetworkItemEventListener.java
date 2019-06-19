package ic2.api;

import net.minecraft.server.EntityHuman;

public interface INetworkItemEventListener {
  void onNetworkEvent(int var1, EntityHuman var2, int var3);
}
