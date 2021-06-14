package ic2.platform;

import net.minecraft.server.EntityHuman;

import java.util.HashMap;
import java.util.Map;

public class Keyboard {
  private static final Map altKeyState = new HashMap();
  private static final Map boostKeyState = new HashMap();
  private static final Map forwardKeyState = new HashMap();
  private static final Map modeSwitchKeyState = new HashMap();
  private static final Map jumpKeyState = new HashMap();
  
  public static boolean isAltKeyDown(EntityHuman entityhuman) {
    return altKeyState.containsKey(entityhuman) ? (Boolean) altKeyState.get(entityhuman) : false;
  }
  
  public static boolean isBoostKeyDown(EntityHuman entityhuman) {
    return boostKeyState.containsKey(entityhuman) ? (Boolean) boostKeyState.get(entityhuman) : false;
  }
  
  public static boolean isForwardKeyDown(EntityHuman entityhuman) {
    return forwardKeyState.containsKey(entityhuman) ? (Boolean) forwardKeyState.get(entityhuman) : false;
  }
  
  public static boolean isJumpKeyDown(EntityHuman entityhuman) {
    return jumpKeyState.containsKey(entityhuman) ? (Boolean) jumpKeyState.get(entityhuman) : false;
  }
  
  public static boolean isModeSwitchKeyDown(EntityHuman entityhuman) {
    return modeSwitchKeyState.containsKey(entityhuman) ? (Boolean) modeSwitchKeyState.get(entityhuman) : false;
  }
  
  public static boolean isSneakKeyDown(EntityHuman entityhuman) {
    return entityhuman.isSneaking();
  }
  
  public static void sendKeyUpdate() {
  }
  
  public static void processKeyUpdate(EntityHuman entityhuman, int i) {
    altKeyState.put(entityhuman, (i & 1) != 0);
    boostKeyState.put(entityhuman, (i & 2) != 0);
    forwardKeyState.put(entityhuman, (i & 4) != 0);
    modeSwitchKeyState.put(entityhuman, (i & 8) != 0);
    jumpKeyState.put(entityhuman, (i & 16) != 0);
  }
}
