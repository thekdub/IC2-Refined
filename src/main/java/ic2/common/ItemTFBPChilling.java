package ic2.common;

import ic2.api.FakePlayer;
import net.minecraft.server.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

public class ItemTFBPChilling extends ItemTFBP {
  public ItemTFBPChilling(int i, int j) {
    super(i, j);
  }
  
  public int getConsume() {
    return 2000;
  }
  
  public int getRange() {
    return 50;
  }
  
  public boolean terraform(World world, int i, int j, int k) {
    int l = TileEntityTerra.getFirstBlockFrom(world, i, j, k + 10);
    if (l == -1) {
      return false;
    }
    else {
      Block block = world.getWorld().getBlockAt(i, l, j);
      BlockBreakEvent event = new BlockBreakEvent(block, FakePlayer.getBukkitEntity(world));
      world.getServer().getPluginManager().callEvent(event);
      if (event.isCancelled()) {
        return false;
      }
      else {
        event.setCancelled(true);
        int i1 = world.getTypeId(i, l, j);
        if (i1 != net.minecraft.server.Block.WATER.id && i1 != net.minecraft.server.Block.STATIONARY_WATER.id) {
          if (i1 == net.minecraft.server.Block.ICE.id) {
            int j1 = world.getTypeId(i, l - 1, j);
            if (j1 == net.minecraft.server.Block.WATER.id || j1 == net.minecraft.server.Block.STATIONARY_WATER.id) {
              world.setTypeId(i, l - 1, j, net.minecraft.server.Block.ICE.id);
              return true;
            }
          }
  
          if (i1 == net.minecraft.server.Block.SNOW.id && this.isSurroundedBySnow(world, i, l, j)) {
            world.setTypeId(i, l, j, net.minecraft.server.Block.SNOW_BLOCK.id);
            return true;
          }
          else {
            if (net.minecraft.server.Block.SNOW.canPlace(world, i, l + 1, j) ||
                i1 == net.minecraft.server.Block.ICE.id) {
              world.setTypeId(i, l + 1, j, net.minecraft.server.Block.SNOW.id);
            }
    
            return false;
          }
        }
        else {
          world.setTypeId(i, l, j, net.minecraft.server.Block.ICE.id);
          return true;
        }
      }
    }
  }
  
  public boolean isSurroundedBySnow(World world, int i, int j, int k) {
    return this.isSnowHere(world, i + 1, j, k) && this.isSnowHere(world, i - 1, j, k) &&
        this.isSnowHere(world, i, j, k + 1) && this.isSnowHere(world, i, j, k - 1);
  }
  
  public boolean isSnowHere(World world, int i, int j, int k) {
    int l = j;
    j = TileEntityTerra.getFirstBlockFrom(world, i, k, j + 16);
    if (l > j) {
      return false;
    }
    else {
      int i1 = world.getTypeId(i, j, k);
      if (i1 != net.minecraft.server.Block.SNOW.id && i1 != net.minecraft.server.Block.SNOW_BLOCK.id) {
        if (net.minecraft.server.Block.SNOW.canPlace(world, i, j + 1, k) || i1 == net.minecraft.server.Block.ICE.id) {
          world.setTypeId(i, j + 1, k, net.minecraft.server.Block.SNOW.id);
        }
  
        return false;
      }
      else {
        return true;
      }
    }
  }
}
