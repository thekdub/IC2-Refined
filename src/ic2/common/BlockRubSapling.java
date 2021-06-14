package ic2.common;

import forge.ITextureProvider;
import ic2.platform.ItemBlockCommon;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.ArrayList;
import java.util.Random;

public class BlockRubSapling extends BlockSapling implements ITextureProvider {
  public BlockRubSapling(int i, int j) {
    super(i, j);
    this.c(0.0F);
    this.a(g);
    this.a("blockRubSapling");
    ModLoader.registerBlock(this, ItemBlockCommon.class);
    Ic2Items.rubberSapling = new ItemStack(this);
  }
  
  public String getTextureFile() {
    return "/ic2/sprites/block_0.png";
  }
  
  public int a(int i, int j) {
    return this.textureId;
  }
  
  public void a(World world, int i, int j, int k, Random random) {
    if (Platform.isSimulating()) {
      if (!this.f(world, i, j, k)) {
        this.b(world, i, j, k, world.getData(i, j, k), 0);
        world.setTypeId(i, j, k, 0);
      }
      else {
        if (world.getLightLevel(i, j + 1, k) >= 9 && random.nextInt(30) == 0) {
          this.grow(world, i, j, k, random);
        }
  
      }
    }
  }
  
  public void grow(World world, int i, int j, int k, Random random) {
    (new WorldGenRubTree()).grow(world, i, j, k, random);
  }
  
  protected int getDropData(int i) {
    return 0;
  }
  
  public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
    if (!Platform.isSimulating()) {
      return false;
    }
    else {
      ItemStack itemstack = entityhuman.U();
      if (itemstack == null) {
        return false;
      }
      else {
        if (itemstack.getItem() == Item.INK_SACK && itemstack.getData() == 15) {
          this.grow(world, i, j, k, world.random);
          --itemstack.count;
        }
  
        return false;
      }
    }
  }
  
  public void addCreativeItems(ArrayList arraylist) {
    arraylist.add(new ItemStack(this));
  }
}
