package ic2.api;

import net.minecraft.server.*;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class FakePlayer {
  public static String name;
  public static boolean doLogin;
  private static Method method;
  private static EntityPlayer fakePlayer;
  // $FF: synthetic field
  private static int[] $SWITCH_TABLE$ic2$api$FakePlayer$Method;
  
  static {
    method = Method.FAKEPLAYER;
    fakePlayer = null;
    name = "[IndustrialCraft]";
    doLogin = false;
  }
  
  public static void setMethod(String value) {
    if (value.equalsIgnoreCase("null")) {
      method = Method.NULL;
    }
    else if (value.equalsIgnoreCase("fakeplayer")) {
      method = Method.FAKEPLAYER;
    }
    else {
      System.err.println("Unknown blocks.placedby type '" + value + "'");
    }
    
  }
  
  public static EntityPlayer get(World world) {
    switch ($SWITCH_TABLE$ic2$api$FakePlayer$Method()[method.ordinal()]) {
      case 1:
        return null;
      case 2:
        if (fakePlayer == null) {
          fakePlayer =
              new EntityPlayer(ModLoader.getMinecraftServerInstance(), world, name, new ItemInWorldManager(world));
          if (doLogin) {
            PlayerLoginEvent ple = new PlayerLoginEvent(fakePlayer.getBukkitEntity());
            world.getServer().getPluginManager().callEvent(ple);
            if (ple.getResult() != Result.ALLOWED) {
              System.err.println(
                  "[IndustrialCraft] FakePlayer login event was disallowed. Ignoring, but this may cause confused plugins.");
            }
    
            PlayerJoinEvent pje = new PlayerJoinEvent(fakePlayer.getBukkitEntity(), "");
            world.getServer().getPluginManager().callEvent(pje);
          }
        }
  
        return fakePlayer;
      default:
        return null;
    }
  }
  
  public static CraftPlayer getBukkitEntity(World world) {
    EntityPlayer player = get(world);
    return player != null ? player.getBukkitEntity() : null;
  }
  
  // $FF: synthetic method
  static int[] $SWITCH_TABLE$ic2$api$FakePlayer$Method() {
    int[] var10000 = $SWITCH_TABLE$ic2$api$FakePlayer$Method;
    if (var10000 != null) {
      return var10000;
    }
    else {
      int[] var0 = new int[Method.values().length];
  
      try {
        var0[Method.FAKEPLAYER.ordinal()] = 2;
      } catch (NoSuchFieldError var2) {
      }
  
      try {
        var0[Method.NULL.ordinal()] = 1;
      } catch (NoSuchFieldError var1) {
      }
  
      $SWITCH_TABLE$ic2$api$FakePlayer$Method = var0;
      return var0;
    }
  }
  
  enum Method {
    NULL,
    FAKEPLAYER
  }
}
