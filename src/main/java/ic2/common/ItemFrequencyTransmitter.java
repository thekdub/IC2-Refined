package ic2.common;

import ic2.platform.Platform;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class ItemFrequencyTransmitter extends ItemIC2 {
  public ItemFrequencyTransmitter(int i, int j) {
    super(i, j);
    this.maxStackSize = 1;
    this.setMaxDurability(0);
  }
  
  public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
    if (Platform.isSimulating()) {
      NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
      if (nbttagcompound.getBoolean("targetSet")) {
        nbttagcompound.setBoolean("targetSet", false);
        Platform.messagePlayer(entityhuman, "Frequency Transmitter unlinked");
      }
    }
    
    return itemstack;
  }
  
  public boolean onItemUseFirst(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    TileEntity tileentity = world.getTileEntity(i, j, k);
    if (tileentity instanceof TileEntityTeleporter) {
      Entity bukkitentity = entityhuman.getBukkitEntity();
      if (bukkitentity instanceof Player) {
        Player player = (Player) bukkitentity;
        BlockBreakEvent breakev = new BlockBreakEvent(player.getWorld().getBlockAt(i, j, k), player);
        Bukkit.getPluginManager().callEvent(breakev);
        if (breakev.isCancelled()) {
          return false;
        }
  
        breakev.setCancelled(true);
      }
  
      if (Platform.isSimulating()) {
        NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
        boolean flag = nbttagcompound.getBoolean("targetSet");
        int i1 = nbttagcompound.getInt("targetX");
        int j1 = nbttagcompound.getInt("targetY");
        int k1 = nbttagcompound.getInt("targetZ");
        TileEntityTeleporter tileentityteleporter = (TileEntityTeleporter) tileentity;
        if (flag) {
          Chunk chunk = Platform.getOrLoadChunk(world, i1 >> 4, k1 >> 4);
          if (chunk == null || chunk.getTypeId(i1 & 15, j1, k1 & 15) != Ic2Items.teleporter.id ||
              chunk.getData(i1 & 15, j1, k1 & 15) != Ic2Items.teleporter.getData()) {
            flag = false;
          }
        }
    
        if (!flag) {
          flag = true;
          i1 = tileentityteleporter.x;
          j1 = tileentityteleporter.y;
          k1 = tileentityteleporter.z;
          Platform.messagePlayer(entityhuman, "Frequency Transmitter linked to Teleporter.");
        }
        else if (tileentityteleporter.x == i1 && tileentityteleporter.y == j1 && tileentityteleporter.z == k1) {
          Platform.messagePlayer(entityhuman, "Can't link Teleporter to itself.");
        }
        else if (tileentityteleporter.targetSet && tileentityteleporter.targetX == i1 &&
            tileentityteleporter.targetY == j1 && tileentityteleporter.targetZ == k1) {
          Platform.messagePlayer(entityhuman, "Teleportation link unchanged.");
        }
        else {
          tileentityteleporter.setTarget(i1, j1, k1);
          TileEntity tileentity1 = world.getTileEntity(i1, j1, k1);
          if (tileentity1 instanceof TileEntityTeleporter) {
            TileEntityTeleporter tileentityteleporter1 = (TileEntityTeleporter) tileentity1;
            if (!tileentityteleporter1.targetSet) {
              tileentityteleporter1.setTarget(tileentityteleporter.x, tileentityteleporter.y, tileentityteleporter.z);
            }
          }
      
          Platform.messagePlayer(entityhuman, "Teleportation link established.");
        }
    
        nbttagcompound.setBoolean("targetSet", flag);
        nbttagcompound.setInt("targetX", i1);
        nbttagcompound.setInt("targetY", j1);
        nbttagcompound.setInt("targetZ", k1);
      }
  
      return true;
    }
    else {
      return false;
    }
  }
}
