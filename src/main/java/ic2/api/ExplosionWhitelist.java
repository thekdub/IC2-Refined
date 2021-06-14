package ic2.api;

import net.minecraft.server.Block;

import java.util.HashSet;
import java.util.Set;

public final class ExplosionWhitelist {
  private static final Set whitelist = new HashSet();
  
  public static void addWhitelistedBlock(Block block) {
    whitelist.add(block);
  }
  
  public static void removeWhitelistedBlock(Block block) {
    whitelist.remove(block);
  }
  
  public static boolean isBlockWhitelisted(Block block) {
    return whitelist.contains(block);
  }
}
