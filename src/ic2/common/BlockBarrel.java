package ic2.common;

import forge.ITextureProvider;
import ic2.platform.BlockContainerCommon;
import ic2.platform.ItemBlockCommon;
import ic2.platform.Platform;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;

public class BlockBarrel extends BlockContainerCommon implements ITextureProvider {
  public BlockBarrel(int i) {
    super(i, 44, Material.WOOD);
    this.c(1.0F);
    this.a(e);
    ModLoader.registerBlock(this, ItemBlockCommon.class);
    Ic2Items.blockBarrel = new ItemStack(this);
  }
  
  public String getTextureFile() {
    return "/ic2/sprites/block_0.png";
  }
  
  public void addCreativeItems(ArrayList arraylist) {
  }
  
  public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l) {
    int i1 = ((TileEntityBarrel) iblockaccess.getTileEntity(i, j, k)).treetapSide;
    return i1 > 1 && l == i1 ? 29 : this.a(l);
  }
  
  public int a(int i) {
    return i >= 2 ? 44 : 28;
  }
  
  public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
    return ((TileEntityBarrel) world.getTileEntity(i, j, k)).rightclick(entityhuman);
  }
  
  public void attack(World world, int i, int j, int k, EntityHuman entityhuman) {
    TileEntityBarrel tileentitybarrel = (TileEntityBarrel) world.getTileEntity(i, j, k);
    Entity bukkitentity = entityhuman.getBukkitEntity();
    if (bukkitentity instanceof Player) {
      Player player = (Player) bukkitentity;
      BlockBreakEvent breakev = new BlockBreakEvent(player.getWorld().getBlockAt(i, j, k), player);
      Bukkit.getPluginManager().callEvent(breakev);
      if (breakev.isCancelled()) {
        tileentitybarrel.update();
        return;
      }
  
      breakev.setCancelled(true);
    }
    
    if (tileentitybarrel.treetapSide > 1) {
      if (Platform.isSimulating()) {
        StackUtil.dropAsEntity(world, i, j, k, new ItemStack(Ic2Items.treetap.getItem()));
      }
      
      tileentitybarrel.treetapSide = 0;
      tileentitybarrel.update();
      tileentitybarrel.drainLiquid(1);
    }
    else {
      if (Platform.isSimulating()) {
        StackUtil.dropAsEntity(world, i, j, k,
            new ItemStack(Ic2Items.barrel.getItem(), 1, tileentitybarrel.calculateMetaValue()));
      }
      
      world.setTypeId(i, j, k, Ic2Items.scaffold.id);
    }
  }
  
  public TileEntity a_() {
    return new TileEntityBarrel();
  }
  
  public ArrayList getBlockDropped(World world, int i, int j, int k, int l, int i1) {
    ArrayList arraylist = new ArrayList();
    arraylist.add(new ItemStack(Ic2Items.scaffold.getItem()));
    arraylist.add(new ItemStack(Ic2Items.barrel.getItem(), 1, 0));
    return arraylist;
  }
}
