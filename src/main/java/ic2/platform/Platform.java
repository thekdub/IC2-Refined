package ic2.platform;

import ic2.common.IHasGui;
import ic2.common.ObfuscatedReflectionFields;
import net.minecraft.server.*;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

public class Platform {
  public static int getBlockTexture(Block block, World world, int i, int j, int k, int l) {
    return 0;
  }
  
  public static File getMinecraftDir() {
    return new File(".");
  }
  
  public static Chunk getOrLoadChunk(World world, int i, int j) {
    boolean flag = world.isLoading;
    world.isLoading = true;
    Chunk chunk = world.q().getOrCreateChunk(i, j);
    world.isLoading = flag;
    return chunk;
  }
  
  public static EntityHuman getPlayerInstance() {
    return null;
  }
  
  public static boolean isRendering() {
    return false;
  }
  
  public static boolean isSimulating() {
    return true;
  }
  
  public static boolean launchGui(EntityHuman entityhuman, IHasGui ihasgui) {
    if (entityhuman instanceof EntityPlayer) {
      EntityPlayer entityplayer = (EntityPlayer) entityhuman;
  
      Field field;
      try {
        field = EntityPlayer.class.getDeclaredField(ObfuscatedReflectionFields.EntityPlayerMP_currentWindowId);
      } catch (NoSuchFieldException var8) {
        try {
          field = EntityPlayer.class.getDeclaredField("containerCounter");
        } catch (NoSuchFieldException var7) {
          throw new RuntimeException(var7);
        }
      }
  
      field.setAccessible(true);
  
      int i;
      try {
        i = field.getInt(entityplayer) % 100 + 1;
        field.setInt(entityplayer, i);
      } catch (IllegalAccessException var6) {
        throw new RuntimeException(var6);
      }
  
      entityplayer.H();
      NetworkManager.initiateGuiDisplay(entityplayer, ihasgui, i);
      entityhuman.activeContainer = ihasgui.getGuiContainer(entityhuman);
      entityhuman.activeContainer.windowId = i;
      entityhuman.activeContainer.addSlotListener(entityplayer);
      return true;
    }
    else {
      return false;
    }
  }
  
  public static void log(Level level, String s) {
    NetServerHandler.logger.log(level, s);
  }
  
  public static boolean isBlockOpaqueCube(IBlockAccess iblockaccess, int i, int j, int k) {
    return false;
  }
  
  public static void playSoundSp(String s, float f, float f1) {
  }
  
  public static void resetPlayerInAirTime(EntityHuman entityhuman) {
    if (entityhuman instanceof EntityPlayer) {
      NetServerHandler netserverhandler = ((EntityPlayer) entityhuman).netServerHandler;
  
      try {
        ModLoader.setPrivateValue(NetServerHandler.class, netserverhandler,
            ObfuscatedReflectionFields.NetServerHandler_playerInAirTime, new Integer(0));
      } catch (Exception var3) {
        throw new RuntimeException(var3);
      }
    }
  }
  
  public static void setEntityOnFire(Entity entity, int i) {
    if (i > 0) {
      entity.setOnFire(i);
    }
    else {
      entity.extinguish();
    }
    
  }
  
  public static int getEntityAirLeft(Entity entity) {
    return entity.getAirTicks();
  }
  
  public static void setEntityAirLeft(Entity entity, int i) {
    entity.setAirTicks(i);
  }
  
  public static void messagePlayer(EntityHuman entityhuman, String s) {
    if (entityhuman instanceof EntityPlayer) {
      ((EntityPlayer) entityhuman).netServerHandler.sendPacket(new Packet3Chat(s));
    }
  }
  
  public static String getItemNameIS(ItemStack itemstack) {
    return null;
  }
  
  public static void teleportTo(Entity entity, double d, double d1, double d2, float f, float f1) {
    if (entity instanceof EntityPlayer) {
      EntityPlayer entityplayer = (EntityPlayer) entity;
      entityplayer.netServerHandler.a(d, d1, d2, f, f1);
      int i = (entityplayer.server.serverConfigurationManager.a() + 16) / 16;
      int j = (int) entityplayer.locX >> 4;
      int k = (int) entityplayer.locZ >> 4;
      int l = (int) entityplayer.d >> 4;
      int i1 = (int) entityplayer.e >> 4;
      if (!isChunkPairWithinDistance(j, k, l, i1, i)) {
        ic2_ServerTeleportHelper.playerInstanceAddPlayer(entityplayer, j, k);
      }
  
      int j1 = j;
      int k1 = k;
      int l1 = 1;
      int i2 = 1;
  
      while (true) {
        int j2;
        for (j2 = 0; j2 < i2; ++j2) {
          k1 += l1;
          if (!isChunkPairWithinDistance(j1, k1, l, i1, i)) {
            ic2_ServerTeleportHelper.playerInstanceAddPlayer(entityplayer, j1, k1);
          }
  
          if (k1 - k == i) {
            for (j2 = l - i; j2 <= l + i; ++j2) {
              for (int i3 = i1 - i; i3 <= i1 + i; ++i3) {
                if (!isChunkPairWithinDistance(j2, i3, j, k, i)) {
                  ic2_ServerTeleportHelper.playerInstanceRemovePlayer(entityplayer, j2, i3);
                }
              }
            }
    
            entityplayer.d = entityplayer.locX;
            entityplayer.e = entityplayer.locZ;
            return;
          }
        }
    
        for (j2 = 0; j2 < i2; ++j2) {
          j1 += l1;
          if (!isChunkPairWithinDistance(j1, k1, l, i1, i)) {
            ic2_ServerTeleportHelper.playerInstanceAddPlayer(entityplayer, j1, k1);
          }
        }
    
        l1 = -l1;
        ++i2;
      }
    }
    else {
      entity.setLocation(d, d1, d2, f, f1);
    }
  }
  
  public static String translateBlockName(Block block) {
    return null;
  }
  
  public static List getContainerSlots(Container container) {
    return container.e;
  }
  
  public static boolean isPlayerOp(EntityHuman entityhuman) {
    return ModLoader.getMinecraftServerInstance().serverConfigurationManager.isOp(entityhuman.name);
  }
  
  public static boolean isPlayerSprinting(EntityHuman entityhuman) {
    return entityhuman.isSprinting();
  }
  
  public static boolean givePlayerOneFood(EntityHuman entityhuman) {
    if (entityhuman.getFoodData().a() < 18) {
      entityhuman.getFoodData().eat(1, 0.9F);
      return true;
    }
    else {
      return false;
    }
  }
  
  public static void removePotionFrom(EntityHuman entityhuman, int i) {
  }
  
  private static boolean isChunkPairWithinDistance(int i, int j, int k, int l, int i1) {
    return Math.abs(i - k) <= i1 && Math.abs(j - l) <= i1;
  }
  
  public static void AddLocalization(String s, String s1) {
    Properties properties = null;
    
    try {
      properties = ModLoader.getPrivateValue(LocaleLanguage.class, LocaleLanguage.a(), 1);
    } catch (Throwable var4) {
    }
    
    if (properties != null) {
      properties.put(s, s1);
    }
    
  }
  
  public static void profilerStartSection(String s) {
  }
  
  public static void profilerEndStartSection(String s) {
  }
  
  public static void profilerEndSection() {
  }
  
  public static BiomeBase getBiomeAt(Chunk chunk, int i, int j, WorldChunkManager worldchunkmanager) {
    return chunk.a(i, j, worldchunkmanager);
  }
  
  public static boolean unknown1(Item item) {
    return item.i();
  }
  
  public static void displayError(String s) {
    ModLoader.getLogger().severe("IndustrialCraft 2 Error\n\n=== IndustrialCraft 2 Error ===\n\n" + s +
        "\n\n===============================\n".replace("\n", System.getProperty("line.separator")));
    System.exit(1);
  }
  
  public static void displayError(Throwable throwable) {
    StringWriter stringwriter = new StringWriter();
    PrintWriter printwriter = new PrintWriter(stringwriter);
    throwable.printStackTrace(printwriter);
    displayError("An unrecoverable exception occurred:\n\n" + stringwriter.toString());
  }
  
  public static void playExplodeSound(World world, int i, int j, int k, float f) {
    ModLoader.getMinecraftServerInstance().serverConfigurationManager
        .sendPacketNearby(i, j, k, 64.0D, world.worldProvider.dimension,
            new Packet60Explosion(i, j, k, f, new HashSet()));
  }
}
