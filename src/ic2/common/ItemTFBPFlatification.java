package ic2.common;

import ic2.api.FakePlayer;
import net.minecraft.server.Block;
import net.minecraft.server.World;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;

public class ItemTFBPFlatification extends ItemTFBP {
  public static ArrayList removeIDs = new ArrayList();

  public ItemTFBPFlatification(int i, int j) {
    super(i, j);
  }

  public static void init() {
    removeIDs.add(Block.SNOW.id);
    removeIDs.add(Block.ICE.id);
    removeIDs.add(Block.GRASS.id);
    removeIDs.add(Block.STONE.id);
    removeIDs.add(Block.GRAVEL.id);
    removeIDs.add(Block.SAND.id);
    removeIDs.add(Block.DIRT.id);
    removeIDs.add(Block.LEAVES.id);
    removeIDs.add(Block.LOG.id);
    removeIDs.add(Block.LONG_GRASS.id);
    removeIDs.add(Block.RED_ROSE.id);
    removeIDs.add(Block.YELLOW_FLOWER.id);
    removeIDs.add(Block.SAPLING.id);
    removeIDs.add(Block.CROPS.id);
    removeIDs.add(Block.RED_MUSHROOM.id);
    removeIDs.add(Block.BROWN_MUSHROOM.id);
    removeIDs.add(Block.PUMPKIN.id);
    if (Ic2Items.rubberLeaves != null) {
      removeIDs.add(Ic2Items.rubberLeaves.id);
    }

    if (Ic2Items.rubberSapling != null) {
      removeIDs.add(Ic2Items.rubberSapling.id);
    }

    if (Ic2Items.rubberWood != null) {
      removeIDs.add(Ic2Items.rubberWood.id);
    }

  }

  public int getConsume() {
    return 4000;
  }

  public int getRange() {
    return 40;
  }

  public boolean terraform(World world, int i, int j, int k) {
    int l = TileEntityTerra.getFirstBlockFrom(world, i, j, k + 20);
    if (l == -1) {
      return false;
    }
    else {
      org.bukkit.block.Block block = world.getWorld().getBlockAt(i, l, j);
      BlockBreakEvent event = new BlockBreakEvent(block, FakePlayer.getBukkitEntity(world));
      world.getServer().getPluginManager().callEvent(event);
      if (event.isCancelled()) {
        return false;
      }
      else {
        event.setCancelled(true);
        if (world.getTypeId(i, l, j) == Block.SNOW.id) {
          --l;
        }

        if (l == k) {
          return false;
        }
        else if (l < k) {
          world.setTypeId(i, l + 1, j, Block.DIRT.id);
          return true;
        }
        else if (this.canRemove(world.getTypeId(i, l, j))) {
          world.setTypeId(i, l, j, 0);
          return true;
        }
        else {
          return false;
        }
      }
    }
  }

  public boolean canRemove(int i) {
    for (int j = 0; j < removeIDs.size(); ++j) {
      if ((Integer) removeIDs.get(j) == i) {
        return true;
      }
    }

    return false;
  }
}
