package ic2.common;

import ic2.api.FakePlayer;
import net.minecraft.server.*;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.Random;

public class ItemTFBPCultivation extends ItemTFBP {
  public static ArrayList plantIDs = new ArrayList();
  
  public ItemTFBPCultivation(int i, int j) {
    super(i, j);
  }
  
  public static void init() {
    plantIDs.add(Block.LONG_GRASS.id);
    plantIDs.add(Block.RED_ROSE.id);
    plantIDs.add(Block.YELLOW_FLOWER.id);
    plantIDs.add(Block.SAPLING.id);
    plantIDs.add(Block.CROPS.id);
    plantIDs.add(Block.RED_MUSHROOM.id);
    plantIDs.add(Block.BROWN_MUSHROOM.id);
    plantIDs.add(Block.PUMPKIN.id);
    if (Ic2Items.rubberSapling != null) {
      plantIDs.add(Ic2Items.rubberSapling.id);
    }
    
  }
  
  public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    if (super.interactWith(itemstack, entityhuman, world, i, j, k, l)) {
      if (entityhuman.dimension == 1) {
        IC2Achievements.issueAchievement(entityhuman, "terraformEndCultivation");
      }
  
      return true;
    }
    else {
      return false;
    }
  }
  
  public int getConsume() {
    return 4000;
  }
  
  public int getRange() {
    return 40;
  }
  
  public boolean terraform(World world, int i, int j, int k) {
    int l = TileEntityTerra.getFirstSolidBlockFrom(world, i, j, k + 10);
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
        if (TileEntityTerra.switchGround(world, Block.SAND, Block.DIRT, i, l, j, true)) {
          return true;
        }
        else {
          int i1 = world.getTypeId(i, l, j);
          if (i1 == Block.DIRT.id) {
            world.setTypeId(i, l, j, Block.GRASS.id);
            return true;
          }
          else {
            return i1 == Block.GRASS.id && this.growPlantsOn(world, i, l + 1, j);
          }
        }
      }
    }
  }
  
  public boolean growPlantsOn(World world, int i, int j, int k) {
    int l = world.getTypeId(i, j, k);
    if (l != 0 && (l != Block.LONG_GRASS.id || world.random.nextInt(4) != 0)) {
      return false;
    }
    else {
      int i1 = this.pickRandomPlantId(world.random);
      if (i1 == Block.CROPS.id) {
        world.setTypeId(i, j - 1, k, Block.SOIL.id);
      }
  
      if (i1 == Block.LONG_GRASS.id) {
        world.setTypeIdAndData(i, j, k, i1, 1);
        return true;
      }
      else {
        world.setTypeId(i, j, k, i1);
        return true;
      }
    }
  }
  
  public int pickRandomPlantId(Random random) {
    for (int i = 0; i < plantIDs.size(); ++i) {
      if (random.nextInt(5) <= 1) {
        return (Integer) plantIDs.get(i);
      }
    }
    
    return Block.LONG_GRASS.id;
  }
}
