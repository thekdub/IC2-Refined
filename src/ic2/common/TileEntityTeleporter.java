package ic2.common;

import ic2.api.Direction;
import ic2.api.INetworkTileEntityEventListener;
import ic2.platform.AudioManager;
import ic2.platform.AudioSource;
import ic2.platform.NetworkManager;
import ic2.platform.Platform;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class TileEntityTeleporter extends TileEntityBlock implements INetworkTileEntityEventListener {
  private static final int EventTeleport = 0;
  public boolean targetSet = false;
  public int targetX;
  public int targetY;
  public int targetZ;
  private AudioSource audioSource = null;

  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);
    this.targetSet = nbttagcompound.getBoolean("targetSet");
    this.targetX = nbttagcompound.getInt("targetX");
    this.targetY = nbttagcompound.getInt("targetY");
    this.targetZ = nbttagcompound.getInt("targetZ");
  }

  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.setBoolean("targetSet", this.targetSet);
    nbttagcompound.setInt("targetX", this.targetX);
    nbttagcompound.setInt("targetY", this.targetY);
    nbttagcompound.setInt("targetZ", this.targetZ);
  }

  public boolean canUpdate() {
    return true;
  }

  public void q_() {
    super.q_();
    if (Platform.isSimulating()) {
      if (this.world.isBlockIndirectlyPowered(this.x, this.y, this.z) && this.targetSet) {
        Chunk chunk = Platform.getOrLoadChunk(this.world, this.targetX >> 4, this.targetZ >> 4);
        if (chunk != null && chunk.getTypeId(this.targetX & 15, this.targetY, this.targetZ & 15) == Ic2Items.teleporter.id && chunk.getData(this.targetX & 15, this.targetY, this.targetZ & 15) == Ic2Items.teleporter.getData()) {
          this.setActive(true);
          List list = this.world.a(Entity.class, AxisAlignedBB.a((double) (this.x - 1), (double) this.y, (double) (this.z - 1), (double) (this.x + 2), (double) (this.y + 3), (double) (this.z + 2)));
          if (!list.isEmpty()) {
            double d = Double.MAX_VALUE;
            Entity entity = null;
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
              Entity entity1 = (Entity) iterator.next();
              if (entity1.vehicle == null) {
                double d1 = ((double) this.x - entity1.locX) * ((double) this.x - entity1.locX) + ((double) (this.y + 1) - entity1.locY) * ((double) (this.y + 1) - entity1.locY) + ((double) this.z - entity1.locZ) * ((double) this.z - entity1.locZ);
                if (d1 < d) {
                  d = d1;
                  entity = entity1;
                }
              }
            }

            this.teleport(entity);
          }
        }
        else {
          this.targetSet = false;
          this.setActive(false);
        }
      }
      else {
        this.setActive(false);
      }
    }

    if (Platform.isRendering() && this.getActive()) {
      this.spawnBlueParticles(2, this.x, this.y, this.z);
    }

  }

  public void j() {
    if (Platform.isRendering() && this.audioSource != null) {
      AudioManager.removeSources(this);
      this.audioSource = null;
    }

    super.j();
  }

  public void teleport(Entity entity) {
    double d = Math.sqrt((double) ((this.x - this.targetX) * (this.x - this.targetX) + (this.y - this.targetY) * (this.y - this.targetY) + (this.z - this.targetZ) * (this.z - this.targetZ)));
    int i = this.getWeightOf(entity);
    if (i != 0) {
      int j = (int) ((double) i * Math.pow(d + 10.0D, 0.7D) * 5.0D);
      if (j <= this.getAvailableEnergy()) {
        this.consumeEnergy(j);
        Platform.teleportTo(entity, (double) this.targetX + 0.5D, (double) this.targetY + 1.5D + entity.W(), (double) this.targetZ + 0.5D, entity.yaw, entity.pitch);
        NetworkManager.initiateTileEntityEvent(this, 0, true);
        if (entity instanceof EntityHuman && d >= 1000.0D) {
          Player player = Bukkit.getServer().getPlayer(((EntityHuman) entity).name);
          player.teleport(new Location(player.getWorld(), (double) this.targetX + 0.5D, (double) this.targetY + 1.5D, (double) this.targetZ + 0.5D, entity.yaw, entity.pitch), TeleportCause.PLUGIN);
          IC2Achievements.issueAchievement((EntityHuman) entity, "teleportFarAway");
        }

      }
    }
  }

  public void spawnBlueParticles(int i, int j, int k, int l) {
    for (int i1 = 0; i1 < i; ++i1) {
      this.world.a("reddust", (double) ((float) j + this.world.random.nextFloat()), (double) ((float) (k + 1) + this.world.random.nextFloat()), (double) ((float) l + this.world.random.nextFloat()), -1.0D, 0.0D, 1.0D);
      this.world.a("reddust", (double) ((float) j + this.world.random.nextFloat()), (double) ((float) (k + 2) + this.world.random.nextFloat()), (double) ((float) l + this.world.random.nextFloat()), -1.0D, 0.0D, 1.0D);
    }

  }

  public void consumeEnergy(int i) {
    LinkedList linkedlist = new LinkedList();
    Direction[] adirection = Direction.values();
    int k = adirection.length;

    int j;
    for (j = 0; j < k; ++j) {
      Direction direction = adirection[j];
      TileEntity tileentity = direction.applyToTileEntity(this);
      if (tileentity instanceof TileEntityElectricBlock) {
        TileEntityElectricBlock tileentityelectricblock1 = (TileEntityElectricBlock) tileentity;
        if (tileentityelectricblock1.energy > 0) {
          linkedlist.add(tileentityelectricblock1);
        }
      }
    }

    while (i > 0) {
      j = (i + linkedlist.size() - 1) / linkedlist.size();
      Iterator iterator = linkedlist.iterator();

      while (iterator.hasNext()) {
        TileEntityElectricBlock tileentityelectricblock = (TileEntityElectricBlock) iterator.next();
        if (j > i) {
          j = i;
        }

        if (tileentityelectricblock.energy <= j) {
          i -= tileentityelectricblock.energy;
          tileentityelectricblock.energy = 0;
          iterator.remove();
        }
        else {
          i -= j;
          tileentityelectricblock.energy -= j;
        }
      }
    }

  }

  public int getAvailableEnergy() {
    int i = 0;
    Direction[] adirection = Direction.values();
    int j = adirection.length;

    for (int k = 0; k < j; ++k) {
      Direction direction = adirection[k];
      TileEntity tileentity = direction.applyToTileEntity(this);
      if (tileentity instanceof TileEntityElectricBlock) {
        i += ((TileEntityElectricBlock) tileentity).energy;
      }
    }

    return i;
  }

  public int getWeightOf(Entity entity) {
    int i = 0;

    for (Entity entity1 = entity; entity1 != null; entity1 = entity1.passenger) {
      if (entity1 instanceof EntityItem) {
        ItemStack itemstack = ((EntityItem) entity1).itemStack;
        i += 100 * itemstack.count / itemstack.getMaxStackSize();
      }
      else if (!(entity1 instanceof EntityAnimal) && !(entity1 instanceof EntityMinecart) && !(entity1 instanceof EntityBoat)) {
        if (!(entity1 instanceof EntityHuman)) {
          if (entity1 instanceof EntityGhast) {
            i += 2500;
          }
          else if (entity1 instanceof EntityEnderDragon) {
            i += 10000;
          }
          else if (entity1 instanceof EntityCreature) {
            i += 500;
          }
        }
        else {
          i += 1000;
          if (mod_IC2.enableTeleporterInventory) {
            PlayerInventory playerinventory = ((EntityHuman) entity1).inventory;

            int k;
            for (k = 0; k < playerinventory.items.length; ++k) {
              if (playerinventory.items[k] != null) {
                i += 100 * playerinventory.items[k].count / playerinventory.items[k].getMaxStackSize();
              }
            }

            for (k = 0; k < playerinventory.armor.length; ++k) {
              if (playerinventory.armor[k] != null) {
                i += 100;
              }
            }
          }
        }
      }
      else {
        i += 100;
      }
    }

    return i;
  }

  public void setTarget(int i, int j, int k) {
    this.targetSet = true;
    this.targetX = i;
    this.targetY = j;
    this.targetZ = k;
    NetworkManager.updateTileEntityField(this, "targetX");
    NetworkManager.updateTileEntityField(this, "targetY");
    NetworkManager.updateTileEntityField(this, "targetZ");
  }

  public List getNetworkedFields() {
    Vector vector = new Vector(3);
    vector.add("targetX");
    vector.add("targetY");
    vector.add("targetZ");
    vector.addAll(super.getNetworkedFields());
    return vector;
  }

  public void onNetworkUpdate(String s) {
    if (s.equals("active") && this.prevActive != this.getActive()) {
      if (this.audioSource == null) {
        this.audioSource = AudioManager.createSource(this, PositionSpec.Center, "Machines/Teleporter/TeleChargedLoop.ogg", true, false, AudioManager.defaultVolume);
      }

      if (this.getActive()) {
        if (this.audioSource != null) {
          this.audioSource.play();
        }
      }
      else if (this.audioSource != null) {
        this.audioSource.stop();
      }
    }

    super.onNetworkUpdate(s);
  }

  public void onNetworkEvent(int i) {
    switch (i) {
      case 0:
        AudioManager.playOnce(this, PositionSpec.Center, "Machines/Teleporter/TeleUse.ogg", true, AudioManager.defaultVolume);
        AudioManager.playOnce(new AudioPosition(this.world, (float) this.targetX + 0.5F, (float) this.targetY + 0.5F, (float) this.targetZ + 0.5F), PositionSpec.Center, "Machines/Teleporter/TeleUse.ogg", true, AudioManager.defaultVolume);
        this.spawnBlueParticles(20, this.x, this.y, this.z);
        this.spawnBlueParticles(20, this.targetX, this.targetY, this.targetZ);
        break;
      default:
        Platform.displayError("An unknown event type was received over multiplayer.\nThis could happen due to corrupted data or a bug.\n\n(Technical information: event ID " + i + ", tile entity below)\n" + "T: " + this + " (" + this.x + "," + this.y + "," + this.z + ")");
    }

  }
}
