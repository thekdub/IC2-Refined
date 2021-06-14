package ic2.common;

import ic2.api.FakePlayer;
import net.minecraft.server.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

public class ItemTFBPDesertification extends ItemTFBP {
  public ItemTFBPDesertification(int i, int j) {
    super(i, j);
  }
  
  public int getConsume() {
    return 2500;
  }
  
  public int getRange() {
    return 40;
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
        if (!TileEntityTerra
            .switchGround(world, net.minecraft.server.Block.DIRT, net.minecraft.server.Block.SAND, i, l, j, false) &&
            !TileEntityTerra
                .switchGround(world, net.minecraft.server.Block.GRASS, net.minecraft.server.Block.SAND, i, l, j,
                    false) && !TileEntityTerra
            .switchGround(world, net.minecraft.server.Block.SOIL, net.minecraft.server.Block.SAND, i, l, j, false)) {
          int i1 = world.getTypeId(i, l, j);
          if (i1 != net.minecraft.server.Block.WATER.id && i1 != net.minecraft.server.Block.STATIONARY_WATER.id &&
              i1 != net.minecraft.server.Block.SNOW.id && i1 != net.minecraft.server.Block.LEAVES.id &&
              i1 != Ic2Items.rubberLeaves.id && !this.isPlant(i1)) {
            if (i1 != net.minecraft.server.Block.ICE.id && i1 != net.minecraft.server.Block.SNOW_BLOCK.id) {
              if ((i1 == net.minecraft.server.Block.WOOD.id || i1 == net.minecraft.server.Block.LOG.id ||
                  i1 == Ic2Items.rubberWood.id) && world.random.nextInt(15) == 0) {
                world.setTypeId(i, l, j, net.minecraft.server.Block.FIRE.id);
                return true;
              }
              else {
                return false;
              }
            }
            else {
              world.setTypeId(i, l, j, net.minecraft.server.Block.WATER.id);
              return true;
            }
          }
          else {
            world.setTypeId(i, l, j, 0);
            return true;
          }
        }
        else {
          TileEntityTerra
              .switchGround(world, net.minecraft.server.Block.DIRT, net.minecraft.server.Block.SAND, i, l, j, false);
          return true;
        }
      }
    }
  }
  
  public boolean isPlant(int i) {
    for (int j = 0; j < ItemTFBPCultivation.plantIDs.size(); ++j) {
      if ((Integer) ItemTFBPCultivation.plantIDs.get(j) == i) {
        return true;
      }
    }
    
    return false;
  }
}
