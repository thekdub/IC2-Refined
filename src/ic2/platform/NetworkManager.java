package ic2.platform;

import forge.DimensionManager;
import forge.IPacketHandler;
import ic2.api.INetworkClientTileEntityEventListener;
import ic2.api.INetworkDataProvider;
import ic2.api.INetworkItemEventListener;
import ic2.common.DataEncoder;
import ic2.common.IHandHeldInventory;
import ic2.common.IHasGui;
import net.minecraft.server.*;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.zip.GZIPOutputStream;

public class NetworkManager implements IPacketHandler {
  private static final int updatePeriod = 2;
  private static final Map fieldsToUpdateSet = new HashMap();
  private static final Map ticksLeftToUpdate = new HashMap();
  
  public static void onTick(World world) {
    int i;
    if (!ticksLeftToUpdate.containsKey(world)) {
      i = 2;
    }
    else {
      i = (Integer) ticksLeftToUpdate.get(world);
    }
    
    --i;
    if (i == 0) {
      sendUpdatePacket(world);
      i = 2;
    }
    
    ticksLeftToUpdate.put(world, i);
  }
  
  public static void updateTileEntityField(TileEntity tileentity, String s) {
    if (!fieldsToUpdateSet.containsKey(tileentity.world)) {
      fieldsToUpdateSet.put(tileentity.world, new HashSet());
    }
    
    Set set = (Set) fieldsToUpdateSet.get(tileentity.world);
    set.add(new TileEntityField(tileentity, s));
    if (set.size() > 10000) {
      sendUpdatePacket(tileentity.world);
    }
    
  }
  
  public static void initiateTileEntityEvent(TileEntity tileentity, int i, boolean flag) {
    int j = flag ? 400 : ModLoader.getMinecraftServerInstance().serverConfigurationManager.a() + 16;
    World world = tileentity.world;
    Packet250CustomPayload packet250custompayload = null;
    Iterator iterator = world.players.iterator();
    
    while (iterator.hasNext()) {
      Object obj = iterator.next();
      EntityPlayer entityplayer = (EntityPlayer) obj;
      int k = tileentity.x - (int) entityplayer.locX;
      int l = tileentity.z - (int) entityplayer.locZ;
      int i1;
      if (flag) {
        i1 = k * k + l * l;
      }
      else {
        i1 = Math.max(Math.abs(k), Math.abs(l));
      }
      
      if (i1 <= j) {
        if (packet250custompayload == null) {
          try {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
            dataoutputstream.writeByte(1);
            dataoutputstream.writeInt(world.worldProvider.dimension);
            dataoutputstream.writeInt(tileentity.x);
            dataoutputstream.writeInt(tileentity.y);
            dataoutputstream.writeInt(tileentity.z);
            dataoutputstream.writeInt(i);
            dataoutputstream.close();
            packet250custompayload = new Packet250CustomPayload();
            packet250custompayload.tag = "ic2";
            packet250custompayload.lowPriority = false;
            packet250custompayload.data = bytearrayoutputstream.toByteArray();
            packet250custompayload.length = bytearrayoutputstream.size();
          } catch (IOException var14) {
            throw new RuntimeException(var14);
          }
        }
        
        entityplayer.netServerHandler.sendPacket(packet250custompayload);
      }
    }
    
  }
  
  public static void initiateItemEvent(EntityHuman entityhuman, ItemStack itemstack, int i, boolean flag) {
    if (entityhuman.name.length() <= 127) {
      int j = flag ? 400 : ModLoader.getMinecraftServerInstance().serverConfigurationManager.a() + 16;
      Packet250CustomPayload packet250custompayload = null;
      Iterator iterator = entityhuman.world.players.iterator();
  
      while (iterator.hasNext()) {
        Object obj = iterator.next();
        EntityPlayer entityplayer = (EntityPlayer) obj;
        int k = (int) entityhuman.locX - (int) entityplayer.locX;
        int l = (int) entityhuman.locZ - (int) entityplayer.locZ;
        int i1;
        if (flag) {
          i1 = k * k + l * l;
        }
        else {
          i1 = Math.max(Math.abs(k), Math.abs(l));
        }
    
        if (i1 <= j) {
          if (packet250custompayload == null) {
            try {
              ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
              DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
              dataoutputstream.writeByte(2);
              dataoutputstream.writeByte(entityhuman.name.length());
              dataoutputstream.writeChars(entityhuman.name);
              dataoutputstream.writeInt(itemstack.id);
              dataoutputstream.writeInt(itemstack.getData());
              dataoutputstream.writeInt(i);
              dataoutputstream.close();
              packet250custompayload = new Packet250CustomPayload();
              packet250custompayload.tag = "ic2";
              packet250custompayload.lowPriority = false;
              packet250custompayload.data = bytearrayoutputstream.toByteArray();
              packet250custompayload.length = bytearrayoutputstream.size();
            } catch (IOException var14) {
              throw new RuntimeException(var14);
            }
          }
      
          entityplayer.netServerHandler.sendPacket(packet250custompayload);
        }
      }
  
    }
  }
  
  public static void announceBlockUpdate(World world, int i, int j, int k) {
    Packet250CustomPayload packet250custompayload = null;
    Iterator iterator = world.players.iterator();
    
    while (iterator.hasNext()) {
      Object obj = iterator.next();
      EntityPlayer entityplayer = (EntityPlayer) obj;
      int l = Math.min(Math.abs(i - (int) entityplayer.locX), Math.abs(k - (int) entityplayer.locZ));
      if (l <= ModLoader.getMinecraftServerInstance().serverConfigurationManager.a() + 16) {
        if (packet250custompayload == null) {
          try {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
            dataoutputstream.writeByte(3);
            dataoutputstream.writeInt(world.worldProvider.dimension);
            dataoutputstream.writeInt(i);
            dataoutputstream.writeInt(j);
            dataoutputstream.writeInt(k);
            dataoutputstream.close();
            packet250custompayload = new Packet250CustomPayload();
            packet250custompayload.tag = "ic2";
            packet250custompayload.lowPriority = true;
            packet250custompayload.data = bytearrayoutputstream.toByteArray();
            packet250custompayload.length = bytearrayoutputstream.size();
          } catch (IOException var11) {
            throw new RuntimeException(var11);
          }
        }
  
        entityplayer.netServerHandler.sendPacket(packet250custompayload);
      }
    }
    
  }
  
  public static void requestInitialData(INetworkDataProvider inetworkdataprovider) {
  }
  
  public static void initiateClientItemEvent(ItemStack itemstack, int i) {
  }
  
  public static void initiateClientTileEntityEvent(TileEntity tileentity, int i) {
  }
  
  public static void initiateGuiDisplay(EntityPlayer entityplayer, IHasGui ihasgui, int i) {
    try {
      ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
      DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
      dataoutputstream.writeByte(4);
      if (ihasgui instanceof TileEntity) {
        TileEntity tileentity = (TileEntity) ihasgui;
        dataoutputstream.writeByte(0);
        dataoutputstream.writeInt(tileentity.world.worldProvider.dimension);
        dataoutputstream.writeInt(tileentity.x);
        dataoutputstream.writeInt(tileentity.y);
        dataoutputstream.writeInt(tileentity.z);
      }
      else if (entityplayer.inventory.getItemInHand() != null &&
          entityplayer.inventory.getItemInHand().getItem() instanceof IHandHeldInventory) {
        dataoutputstream.writeByte(1);
        dataoutputstream.writeInt(entityplayer.inventory.itemInHandIndex);
      }
      else {
        Platform.displayError(
            "An unknown GUI type was attempted to be displayed.\nThis could happen due to corrupted data from a player or a bug.\n\n(Technical information: " +
                ihasgui + ")");
      }
  
      dataoutputstream.writeInt(i);
      dataoutputstream.close();
      Packet250CustomPayload packet250custompayload = new Packet250CustomPayload();
      packet250custompayload.tag = "ic2";
      packet250custompayload.lowPriority = false;
      packet250custompayload.data = bytearrayoutputstream.toByteArray();
      packet250custompayload.length = bytearrayoutputstream.size();
      entityplayer.netServerHandler.sendPacket(packet250custompayload);
    } catch (IOException var6) {
      throw new RuntimeException(var6);
    }
  }
  
  private static void sendUpdatePacket(World world) {
    if (fieldsToUpdateSet.containsKey(world)) {
      Set set = (Set) fieldsToUpdateSet.get(world);
      if (!set.isEmpty()) {
        label92:
        for (int iter = 0; iter < world.players.size(); ++iter) {
          EntityPlayer entityplayer = (EntityPlayer) world.players.get(iter);
  
          try {
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            bytearrayoutputstream.write(0);
            GZIPOutputStream gzipoutputstream = new GZIPOutputStream(bytearrayoutputstream);
            DataOutputStream dataoutputstream = new DataOutputStream(gzipoutputstream);
            dataoutputstream.writeInt(world.worldProvider.dimension);
            Iterator iterator1 = set.iterator();
    
            while (true) {
              TileEntityField tileentityfield;
              int i;
              do {
                do {
                  do {
                    do {
                      if (!iterator1.hasNext()) {
                        dataoutputstream.close();
                        gzipoutputstream.close();
                        if (bytearrayoutputstream.size() > 1) {
                          Packet250CustomPayload packet250custompayload = new Packet250CustomPayload();
                          packet250custompayload.tag = "ic2";
                          packet250custompayload.lowPriority = true;
                          packet250custompayload.data = bytearrayoutputstream.toByteArray();
                          packet250custompayload.length = bytearrayoutputstream.size();
                          entityplayer.netServerHandler.sendPacket(packet250custompayload);
                        }
                        continue label92;
                      }
  
                      tileentityfield = (TileEntityField) iterator1.next();
                    } while (tileentityfield.te.l());
                  } while (tileentityfield.te.world != world);
                } while (tileentityfield.target != null && tileentityfield.target != entityplayer);
  
                i = Math.min(Math.abs(tileentityfield.te.x - (int) entityplayer.locX),
                    Math.abs(tileentityfield.te.z - (int) entityplayer.locZ));
              } while (i > ModLoader.getMinecraftServerInstance().serverConfigurationManager.a() + 16);
      
              dataoutputstream.writeInt(tileentityfield.te.x);
              dataoutputstream.writeInt(tileentityfield.te.y);
              dataoutputstream.writeInt(tileentityfield.te.z);
              dataoutputstream.writeShort(tileentityfield.field.length());
              dataoutputstream.writeChars(tileentityfield.field);
              Field field = null;
      
              try {
                Class class1 = tileentityfield.te.getClass();
        
                do {
                  try {
                    field = class1.getDeclaredField(tileentityfield.field);
                  } catch (NoSuchFieldException var13) {
                    class1 = class1.getSuperclass();
                  }
                } while (field == null && class1 != null);
        
                if (field == null) {
                  throw new NoSuchFieldException(tileentityfield.field);
                }
        
                field.setAccessible(true);
                DataEncoder.encode(dataoutputstream, field.get(tileentityfield.te));
              } catch (Exception var14) {
                throw new RuntimeException(var14);
              }
            }
          } catch (IOException var15) {
            throw new RuntimeException(var15);
          }
        }
  
        set.clear();
      }
    }
  }
  
  public void onPacketData(net.minecraft.server.NetworkManager networkmanager, String s, byte[] abyte0) {
    EntityPlayer entityplayer = ((NetServerHandler) networkmanager.getNetHandler()).getPlayerEntity();
    DataInputStream datainputstream = new DataInputStream(new ByteArrayInputStream(abyte0));
    
    try {
      byte byte0 = datainputstream.readByte();
      switch (byte0) {
        case 0:
          int i = datainputstream.readInt();
          int i1 = datainputstream.readInt();
          int l1 = datainputstream.readInt();
          int k2 = datainputstream.readInt();
          World[] aworld = DimensionManager.getWorlds();
          int j3 = aworld.length;
          int k3 = 0;
  
          while (k3 < j3) {
            World world = aworld[k3];
            if (i == world.worldProvider.dimension) {
              TileEntity tileentity = world.getTileEntity(i1, l1, k2);
              if (tileentity instanceof INetworkDataProvider) {
                if (!fieldsToUpdateSet.containsKey(world)) {
                  fieldsToUpdateSet.put(world, new HashSet());
                }
  
                Set set = (Set) fieldsToUpdateSet.get(world);
                Iterator iterator = ((INetworkDataProvider) tileentity).getNetworkedFields().iterator();
  
                while (iterator.hasNext()) {
                  String s1 = (String) iterator.next();
                  set.add(new TileEntityField(tileentity, s1, entityplayer));
                  if (set.size() > 10000) {
                    sendUpdatePacket(world);
                  }
                }
  
                return;
              }
  
              ++k3;
            }
            else {
              ++k3;
            }
          }
  
          return;
        case 1:
          int j = datainputstream.readInt();
          int j1 = datainputstream.readInt();
          int i2 = datainputstream.readInt();
          if (j < Item.byId.length) {
            Item item = Item.byId[j];
            if (item instanceof INetworkItemEventListener) {
              ((INetworkItemEventListener) item).onNetworkEvent(j1, entityplayer, i2);
            }
          }
          break;
        case 2:
          int k = datainputstream.readInt();
          Keyboard.processKeyUpdate(entityplayer, k);
          break;
        case 3:
          int l = datainputstream.readInt();
          int k1 = datainputstream.readInt();
          int j2 = datainputstream.readInt();
          int l2 = datainputstream.readInt();
          int i3 = datainputstream.readInt();
          World[] aworld1 = DimensionManager.getWorlds();
          int l3 = aworld1.length;
  
          for (int i4 = 0; i4 < l3; ++i4) {
            World world1 = aworld1[i4];
            if (l == world1.worldProvider.dimension) {
              TileEntity tileentity1 = world1.getTileEntity(k1, j2, l2);
              if (tileentity1 instanceof INetworkClientTileEntityEventListener) {
                ((INetworkClientTileEntityEventListener) tileentity1).onNetworkEvent(entityplayer, i3);
              }
              break;
            }
          }
      }
    } catch (IOException var29) {
      var29.printStackTrace();
    }
    
  }
  
  static class TileEntityField {
    TileEntity te;
    String field;
    EntityPlayer target = null;
    
    TileEntityField(TileEntity tileentity, String s) {
      this.te = tileentity;
      this.field = s;
    }
    
    TileEntityField(TileEntity tileentity, String s, EntityPlayer entityplayer) {
      this.te = tileentity;
      this.field = s;
      this.target = entityplayer;
    }
    
    public boolean equals(Object obj) {
      if (obj instanceof TileEntityField) {
        TileEntityField tileentityfield = (TileEntityField) obj;
        return tileentityfield.te == this.te && tileentityfield.field.equals(this.field);
      }
      else {
        return false;
      }
    }
    
    public int hashCode() {
      return this.te.hashCode() * 31 ^ this.field.hashCode();
    }
  }
}
