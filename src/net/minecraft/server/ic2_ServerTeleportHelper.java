package net.minecraft.server;

import java.lang.reflect.Method;

public class ic2_ServerTeleportHelper {
  public static void playerInstanceAddPlayer(EntityPlayer entityplayer, int i, int j) {
    getPlayerInstance(entityplayer, i, j, true).a(entityplayer);
  }

  public static void playerInstanceRemovePlayer(EntityPlayer entityplayer, int i, int j) {
    PlayerInstance playerinstance = getPlayerInstance(entityplayer, i, j, false);
    if (playerinstance != null) {
      playerinstance.b(entityplayer);
    }

  }

  private static PlayerInstance getPlayerInstance(EntityPlayer entityplayer, int i, int j, boolean flag) {
    try {
      Method method = null;
      Method[] amethod = ServerConfigurationManager.class.getDeclaredMethods();
      int k = amethod.length;

      for (int l = 0; l < k; ++l) {
        Method method2 = amethod[l];
        if (method2.getReturnType() == PlayerManager.class) {
          method2.setAccessible(true);
          method = method2;
          break;
        }
      }

      PlayerManager playermanager = (PlayerManager) method.invoke(entityplayer.server.serverConfigurationManager, entityplayer.dimension);
      Method method1 = null;
      Method[] amethod1 = PlayerManager.class.getDeclaredMethods();
      int i1 = amethod1.length;

      for (int j1 = 0; j1 < i1; ++j1) {
        Method method3 = amethod1[j1];
        if (method3.getReturnType() == PlayerInstance.class) {
          method3.setAccessible(true);
          method1 = method3;
          break;
        }
      }

      return (PlayerInstance) method1.invoke(playermanager, i, j, flag);
    } catch (Exception var14) {
      throw new RuntimeException(var14);
    }
  }
}
