package ic2.common;

import ic2.api.FakePlayer;
import net.minecraft.server.BlockSapling;
import net.minecraft.server.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

public class ItemTFBPIrrigation extends ItemTFBP {
  public ItemTFBPIrrigation(int i, int j) {
    super(i, j);
  }

  public int getConsume() {
    return 3000;
  }

  public int getRange() {
    return 60;
  }

  public boolean terraform(World world, int i, int j, int k) {
    if (world.random.nextInt(48000) == 0) {
      world.getWorldData().setStorm(true);
      return true;
    }
    else {
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
          if (TileEntityTerra.switchGround(world, net.minecraft.server.Block.SAND, net.minecraft.server.Block.DIRT, i, l, j, true)) {
            TileEntityTerra.switchGround(world, net.minecraft.server.Block.SAND, net.minecraft.server.Block.DIRT, i, l, j, true);
            return true;
          }
          else {
            int i1 = world.getTypeId(i, l, j);
            if (i1 == net.minecraft.server.Block.LONG_GRASS.id) {
              return this.spreadGrass(world, i + 1, l, j) || this.spreadGrass(world, i - 1, l, j) || this.spreadGrass(world, i, l, j + 1) || this.spreadGrass(world, i, l, j - 1);
            }
            else if (i1 == net.minecraft.server.Block.SAPLING.id) {
              ((BlockSapling) net.minecraft.server.Block.SAPLING).grow(world, i, l, j, world.random, false, null, null);
              return true;
            }
            else if (i1 == Ic2Items.rubberSapling.id) {
              ((BlockRubSapling) net.minecraft.server.Block.byId[Ic2Items.rubberSapling.id]).grow(world, i, l, j, world.random);
              return true;
            }
            else if (i1 == net.minecraft.server.Block.LOG.id) {
              int j1 = world.getData(i, l, j);
              world.setTypeIdAndData(i, l + 1, j, net.minecraft.server.Block.LOG.id, j1);
              this.createLeaves(world, i, l + 2, j, j1);
              this.createLeaves(world, i + 1, l + 1, j, j1);
              this.createLeaves(world, i - 1, l + 1, j, j1);
              this.createLeaves(world, i, l + 1, j + 1, j1);
              this.createLeaves(world, i, l + 1, j - 1, j1);
              return true;
            }
            else if (i1 == net.minecraft.server.Block.CROPS.id) {
              world.setData(i, l, j, 7);
              return true;
            }
            else if (i1 == net.minecraft.server.Block.FIRE.id) {
              world.setTypeId(i, l, j, 0);
              return true;
            }
            else {
              return false;
            }
          }
        }
      }
    }
  }

  public void createLeaves(World world, int i, int j, int k, int l) {
    if (world.getTypeId(i, j, k) == 0) {
      world.setTypeIdAndData(i, j, k, net.minecraft.server.Block.LEAVES.id, l);
    }

  }

  public boolean spreadGrass(World world, int i, int j, int k) {
    if (world.random.nextBoolean()) {
      return false;
    }
    else {
      j = TileEntityTerra.getFirstBlockFrom(world, i, k, j);
      int l = world.getTypeId(i, j, k);
      if (l == net.minecraft.server.Block.DIRT.id) {
        world.setTypeId(i, j, k, net.minecraft.server.Block.GRASS.id);
        return true;
      }
      else if (l == net.minecraft.server.Block.GRASS.id) {
        world.setTypeIdAndData(i, j + 1, k, net.minecraft.server.Block.LONG_GRASS.id, 1);
        return true;
      }
      else {
        return false;
      }
    }
  }
}
