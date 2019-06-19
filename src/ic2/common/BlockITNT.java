package ic2.common;

import ic2.platform.ItemBlockCommon;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class BlockITNT extends BlockIC2Explosive implements IRareBlock {
  public HashMap igniterMap = new HashMap();
  public boolean isITNT;

  public BlockITNT(int i, int j, boolean flag) {
    super(i, j, flag);
    this.isITNT = flag;
    ModLoader.registerBlock(this, ItemBlockCommon.class);
  }

  public EntityIC2Explosive getExplosionEntity(World world, float f, float f1, float f2) {
    return this.isITNT ? new EntityItnt(world, (double) f, (double) f1, (double) f2) : (new EntityNuke(world, (double) f, (double) f1, (double) f2)).setIgniter((String) this.igniterMap.get(new ChunkCoordinates((int) (f - 0.5F), (int) (f1 - 0.5F), (int) (f2 - 0.5F))));
  }

  public void postPlace(World world, int i, int j, int k, EntityLiving entityliving) {
    if (!Platform.isRendering() && !this.isITNT && entityliving instanceof EntityHuman) {
      Platform.log(Level.INFO, "Player " + ((EntityHuman) entityliving).name + " placed a nuke at " + i + "/" + j + "/" + k);
    }

    if (entityliving instanceof EntityHuman) {
      this.igniterMap.put(new ChunkCoordinates(i, j, k), ((EntityHuman) entityliving).name);
    }

  }

  public boolean removeBlockByPlayer(World world, EntityHuman entityhuman, int i, int j, int k) {
    this.igniterMap.remove(new ChunkCoordinates(i, j, k));
    return super.removeBlockByPlayer(world, entityhuman, i, j, k);
  }

  public int rarity(ItemStack itemstack) {
    return this.isITNT ? 0 : 1;
  }

  public void addCreativeItems(ArrayList arraylist) {
    if (this.isITNT || mod_IC2.enableCraftingNuke) {
      arraylist.add(new ItemStack(this));
    }
  }
}
