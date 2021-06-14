package ic2.common;

import net.minecraft.server.*;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ItemSprayer extends ItemIC2 {
  public ItemSprayer(int i, int j) {
    super(i, j);
    this.e(1);
    this.setMaxDurability(1602);
  }
  
  public static boolean[] calculateDirectionsFromPlayer(EntityHuman entityhuman) {
    float f = entityhuman.yaw % 360.0F;
    float f1 = entityhuman.pitch;
    boolean[] aflag = new boolean[]{true, true, true, true, true, true};
    if (f1 >= -65.0F && f1 <= 65.0F) {
      if (f >= 300.0F && f <= 360.0F || f >= 0.0F && f <= 60.0F) {
        aflag[2] = false;
      }
  
      if (f >= 30.0F && f <= 150.0F) {
        aflag[5] = false;
      }
  
      if (f >= 120.0F && f <= 240.0F) {
        aflag[3] = false;
      }
  
      if (f >= 210.0F && f <= 330.0F) {
        aflag[4] = false;
      }
    }
    
    if (f1 <= -40.0F) {
      aflag[0] = false;
    }
    
    if (f1 >= 40.0F) {
      aflag[1] = false;
    }
    
    return aflag;
  }
  
  public static int getSprayMass() {
    return 13;
  }
  
  public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    ItemStack itemstack1 = entityhuman.inventory.armor[2];
    boolean flag = itemstack1 != null && itemstack1.id == Ic2Items.cfPack.id &&
        ((ItemArmorCFPack) itemstack1.getItem()).getCFPellet(entityhuman, itemstack1);
    if (!flag && itemstack.getData() > 1501) {
      return false;
    }
    else if (world.getTypeId(i, j, k) == Ic2Items.scaffold.id) {
      this.sprayFoam(world, i, j, k, calculateDirectionsFromPlayer(entityhuman), true, entityhuman);
      if (!flag) {
        itemstack.damage(100, null);
      }
      
      return true;
    }
    else {
      if (l == 0) {
        --j;
      }
      
      if (l == 1) {
        ++j;
      }
      
      if (l == 2) {
        --k;
      }
      
      if (l == 3) {
        ++k;
      }
      
      if (l == 4) {
        --i;
      }
      
      if (l == 5) {
        ++i;
      }
      
      world.getTypeId(i, j, k);
      if (this.sprayFoam(world, i, j, k, calculateDirectionsFromPlayer(entityhuman), false, entityhuman)) {
        if (!flag) {
          itemstack.damage(100, null);
        }
  
        return true;
      }
      else {
        return false;
      }
    }
  }
  
  public boolean sprayFoam(World world, int i, int j, int k, boolean[] aflag, boolean flag, EntityHuman player) {
    int l = world.getTypeId(i, j, k);
    if (!flag && !Block.byId[Ic2Items.constructionFoam.id].canPlace(world, i, j, k) &&
        (l != Ic2Items.copperCableBlock.id || world.getData(i, j, k) == 13) || flag && l != Ic2Items.scaffold.id) {
      return false;
    }
    else {
      ArrayList arraylist = new ArrayList();
      ArrayList arraylist1 = new ArrayList();
      int i1 = getSprayMass();
      arraylist.add(new ChunkPosition(i, j, k));
      
      ChunkPosition chunkposition1;
      for (int j1 = 0; j1 < arraylist.size() && i1 > 0; ++j1) {
        chunkposition1 = (ChunkPosition) arraylist.get(j1);
        int k1 = world.getTypeId(chunkposition1.x, chunkposition1.y, chunkposition1.z);
        if (!flag && (Block.byId[Ic2Items.constructionFoam.id]
            .canPlace(world, chunkposition1.x, chunkposition1.y, chunkposition1.z) ||
            k1 == Ic2Items.copperCableBlock.id &&
                world.getData(chunkposition1.x, chunkposition1.y, chunkposition1.z) != 13) ||
            flag && k1 == Ic2Items.scaffold.id) {
          this.considerAddingCoord(chunkposition1, arraylist1);
          this.addAdjacentSpacesOnList(chunkposition1.x, chunkposition1.y, chunkposition1.z, arraylist, aflag, flag);
          --i1;
        }
      }
      
      Iterator iterator = arraylist1.iterator();
      
      while (iterator.hasNext()) {
        chunkposition1 = (ChunkPosition) iterator.next();
        org.bukkit.block.Block block =
            world.getWorld().getBlockAt(chunkposition1.x, chunkposition1.y, chunkposition1.z);
        BlockBreakEvent event =
            new BlockBreakEvent(block, (CraftPlayer) (player == null ? null : player.getBukkitEntity()));
        world.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
          int l1 = world.getTypeId(chunkposition1.x, chunkposition1.y, chunkposition1.z);
          if (l1 == Ic2Items.scaffold.id) {
            Block.byId[Ic2Items.scaffold.id].b(world, chunkposition1.x, chunkposition1.y, chunkposition1.z,
                world.getData(chunkposition1.x, chunkposition1.y, chunkposition1.z), 0);
            world.setTypeId(chunkposition1.x, chunkposition1.y, chunkposition1.z, Ic2Items.constructionFoam.id);
          }
          else if (l1 == Ic2Items.copperCableBlock.id) {
            TileEntity tileentity = world.getTileEntity(chunkposition1.x, chunkposition1.y, chunkposition1.z);
            if (tileentity instanceof TileEntityCable) {
              ((TileEntityCable) tileentity).changeFoam((byte) 1);
            }
          }
          else {
            world.setTypeId(chunkposition1.x, chunkposition1.y, chunkposition1.z, Ic2Items.constructionFoam.id);
          }
        }
      }
      
      return true;
    }
  }
  
  public void addAdjacentSpacesOnList(int i, int j, int k, ArrayList arraylist, boolean[] aflag, boolean flag) {
    int[] ai = this.generateRngSpread(mod_IC2.random);
    
    for (int l = 0; l < ai.length; ++l) {
      if (flag || aflag[ai[l]]) {
        switch (ai[l]) {
          case 0:
            this.considerAddingCoord(new ChunkPosition(i, j - 1, k), arraylist);
            break;
          case 1:
            this.considerAddingCoord(new ChunkPosition(i, j + 1, k), arraylist);
            break;
          case 2:
            this.considerAddingCoord(new ChunkPosition(i, j, k - 1), arraylist);
            break;
          case 3:
            this.considerAddingCoord(new ChunkPosition(i, j, k + 1), arraylist);
            break;
          case 4:
            this.considerAddingCoord(new ChunkPosition(i - 1, j, k), arraylist);
            break;
          case 5:
            this.considerAddingCoord(new ChunkPosition(i + 1, j, k), arraylist);
        }
      }
    }
    
  }
  
  public void considerAddingCoord(ChunkPosition chunkposition, ArrayList arraylist) {
    for (int i = 0; i < arraylist.size(); ++i) {
      if (((ChunkPosition) arraylist.get(i)).x == chunkposition.x &&
          ((ChunkPosition) arraylist.get(i)).y == chunkposition.y &&
          ((ChunkPosition) arraylist.get(i)).z == chunkposition.z) {
        return;
      }
    }
    
    arraylist.add(chunkposition);
  }
  
  public int[] generateRngSpread(Random random) {
    int[] ai = new int[]{0, 1, 2, 3, 4, 5};
    
    for (int i = 0; i < 16; ++i) {
      int j = random.nextInt(6);
      int k = random.nextInt(6);
      int l = ai[j];
      ai[j] = ai[k];
      ai[k] = l;
    }
    
    return ai;
  }
}
