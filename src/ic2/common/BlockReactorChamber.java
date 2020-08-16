package ic2.common;

import forge.ITextureProvider;
import ic2.platform.BlockContainerCommon;
import ic2.platform.ItemBlockCommon;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.ArrayList;
import java.util.Random;

public class BlockReactorChamber extends BlockContainerCommon implements ITextureProvider, IRareBlock {
  public BlockReactorChamber(int i) {
    super(i, 67, Material.ORE);
    this.c(2.0F);
    this.a(i);
    this.a("blockReactorChamber");
    ModLoader.registerBlock(this, ItemBlockCommon.class);
    Ic2Items.reactorChamber = new ItemStack(this);
  }
  
  public int a(int i, int j) {
    if (i == 0) {
      return 16;
    }
    else {
      return i != 1 ? 67 : 17;
    }
  }
  
  public void doPhysics(World world, int i, int j, int k, int l) {
    if (!this.canPlace(world, i, j, k)) {
      this.a(world, i, j, k, new ItemStack(world.getTypeId(i, j, k), 1, 0));
      world.setTypeId(i, j, k, 0);
    }
    
  }
  
  public boolean canPlace(World world, int i, int j, int k) {
    int l = 0;
    if (this.isReactorAt(world, i + 1, j, k)) {
      ++l;
    }
    
    if (this.isReactorAt(world, i - 1, j, k)) {
      ++l;
    }
    
    if (this.isReactorAt(world, i, j + 1, k)) {
      ++l;
    }
    
    if (this.isReactorAt(world, i, j - 1, k)) {
      ++l;
    }
    
    if (this.isReactorAt(world, i, j, k + 1)) {
      ++l;
    }
    
    if (this.isReactorAt(world, i, j, k - 1)) {
      ++l;
    }
    
    return l == 1;
  }
  
  public void randomDisplayTick(World world, int i, int j, int k, Random random) {
    TileEntityNuclearReactor tileentitynuclearreactor = this.getReactorEntity(world, i, j, k);
    if (tileentitynuclearreactor == null) {
      this.doPhysics(world, i, j, k, this.id);
    }
    else {
      int l = tileentitynuclearreactor.heat / 1000;
      if (l > 0) {
        l = world.random.nextInt(l);
  
        int j1;
        for (j1 = 0; j1 < l; ++j1) {
          world.a("smoke", (float) i + random.nextFloat(), (float) j + 0.95F,
              (float) k + random.nextFloat(), 0.0D, 0.0D, 0.0D);
        }
  
        l -= world.random.nextInt(4) + 3;
  
        for (j1 = 0; j1 < l; ++j1) {
          world.a("flame", (float) i + random.nextFloat(), (float) j + 1.0F,
              (float) k + random.nextFloat(), 0.0D, 0.0D, 0.0D);
        }
  
      }
    }
  }
  
  public boolean isReactorAt(World world, int i, int j, int k) {
    return world.getTileEntity(i, j, k) instanceof TileEntityNuclearReactor &&
        world.getTypeId(i, j, k) == Ic2Items.nuclearReactor.id &&
        world.getData(i, j, k) == Ic2Items.nuclearReactor.getData();
  }
  
  public TileEntityNuclearReactor getReactorEntity(World world, int i, int j, int k) {
    if (this.isReactorAt(world, i + 1, j, k)) {
      return (TileEntityNuclearReactor) world.getTileEntity(i + 1, j, k);
    }
    else if (this.isReactorAt(world, i - 1, j, k)) {
      return (TileEntityNuclearReactor) world.getTileEntity(i - 1, j, k);
    }
    else if (this.isReactorAt(world, i, j + 1, k)) {
      return (TileEntityNuclearReactor) world.getTileEntity(i, j + 1, k);
    }
    else if (this.isReactorAt(world, i, j - 1, k)) {
      return (TileEntityNuclearReactor) world.getTileEntity(i, j - 1, k);
    }
    else if (this.isReactorAt(world, i, j, k + 1)) {
      return (TileEntityNuclearReactor) world.getTileEntity(i, j, k + 1);
    }
    else if (this.isReactorAt(world, i, j, k - 1)) {
      return (TileEntityNuclearReactor) world.getTileEntity(i, j, k - 1);
    }
    else {
      this.doPhysics(world, i, j, k, world.getTypeId(i, j, k));
      return null;
    }
  }
  
  public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
    if (entityhuman.isSneaking()) {
      return false;
    }
    else {
      TileEntityNuclearReactor tileentitynuclearreactor = this.getReactorEntity(world, i, j, k);
      if (tileentitynuclearreactor == null) {
        this.doPhysics(world, i, j, k, this.id);
        return false;
      }
      else {
        return !Platform.isSimulating() || Platform.launchGui(entityhuman, tileentitynuclearreactor);
      }
    }
  }
  
  public TileEntity a_() {
    return new TileEntityReactorChamber();
  }
  
  public int getDropType(int i, Random random, int j) {
    return Ic2Items.machine.id;
  }
  
  protected int getDropData(int i) {
    return Ic2Items.machine.getData();
  }
  
  public int rarity(ItemStack itemstack) {
    return 1;
  }
  
  public String getTextureFile() {
    return "/ic2/sprites/block_0.png";
  }
  
  public void addCreativeItems(ArrayList arraylist) {
    arraylist.add(new ItemStack(this));
  }
}
