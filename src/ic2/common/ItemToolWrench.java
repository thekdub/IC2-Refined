package ic2.common;

import ic2.api.IWrenchable;
import ic2.platform.AudioManager;
import ic2.platform.Keyboard;
import ic2.platform.Platform;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

public class ItemToolWrench extends ItemIC2 {
  public ItemToolWrench(int i, int j) {
    super(i, j);
    this.setMaxDurability(160);
    this.e(1);
  }
  
  public boolean canTakeDamage(ItemStack itemstack, int i) {
    return true;
  }
  
  public boolean onItemUseFirst(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    if (!this.canTakeDamage(itemstack, 1)) {
      return false;
    }
    else {
      Entity entity = entityhuman.getBukkitEntity();
      if (entity instanceof Player) {
        Player player = (Player) entity;
        BlockBreakEvent breakEv = new BlockBreakEvent(player.getWorld().getBlockAt(i, j, k), player);
        Bukkit.getPluginManager().callEvent(breakEv);
        if (breakEv.isCancelled()) {
          return false;
        }
      }
  
      int i1 = world.getTypeId(i, j, k);
      int j1 = world.getData(i, j, k);
      TileEntity tileentity = world.getTileEntity(i, j, k);
      if (tileentity instanceof TileEntityTerra) {
        TileEntityTerra tileentityterra = (TileEntityTerra) tileentity;
        if (tileentityterra.ejectBlueprint()) {
          if (Platform.isSimulating()) {
            this.damage(itemstack, 1, entityhuman);
          }
  
          if (Platform.isRendering()) {
            AudioManager.playOnce(entityhuman, PositionSpec.Hand, "Tools/wrench.ogg", true, AudioManager.defaultVolume);
          }
  
          return true;
        }
      }
  
      if (tileentity instanceof IWrenchable) {
        IWrenchable iwrenchable = (IWrenchable) tileentity;
        if (Keyboard.isAltKeyDown(entityhuman)) {
          if (entityhuman.isSneaking()) {
            l = (iwrenchable.getFacing() + 5) % 6;
          }
          else {
            l = (iwrenchable.getFacing() + 1) % 6;
          }
        }
        else if (entityhuman.isSneaking()) {
          l += l % 2 * -2 + 1;
        }
    
        if (iwrenchable.wrenchCanSetFacing(entityhuman, l)) {
          if (Platform.isSimulating()) {
            iwrenchable.setFacing((short) l);
            this.damage(itemstack, 1, entityhuman);
          }
      
          if (Platform.isRendering()) {
            AudioManager.playOnce(entityhuman, PositionSpec.Hand, "Tools/wrench.ogg", true, AudioManager.defaultVolume);
          }
      
          return true;
        }
    
        if (this.canTakeDamage(itemstack, 10) && iwrenchable.wrenchCanRemove(entityhuman)) {
          if (Platform.isSimulating()) {
            if (!Platform.isRendering() && mod_IC2.enableLoggingWrench) {
              String s = tileentity.getClass().getName().replace("TileEntity", "");
              Platform.log(Level.INFO,
                  "Player " + entityhuman.name + " used the wrench to remove the " + s + " (" + i1 + "-" + j1 +
                      ") at " + i + "/" + j + "/" + k);
            }
  
            Block block = Block.byId[i1];
            ArrayList arraylist = block.getBlockDropped(world, i, j, k, j1, 0);
            boolean flag = false;
            if (!mod_IC2.portLosslessWrench) {
              if (iwrenchable.getWrenchDropRate() < 1.0F && this.overrideWrenchSuccessRate(itemstack)) {
                if (!this.canTakeDamage(itemstack, 200)) {
                  Platform.messagePlayer(entityhuman, "Not enough energy for lossless wrench operation");
                  return true;
                }
  
                flag = true;
                this.damage(itemstack, 200, entityhuman);
              }
              else {
                flag = world.random.nextFloat() <= iwrenchable.getWrenchDropRate();
                this.damage(itemstack, 10, entityhuman);
              }
            }
            else {
              flag = true;
              this.damage(itemstack, 10, entityhuman);
            }
  
            if (flag) {
              arraylist.set(0, new ItemStack(i1, 1, j1));
            }
  
            Iterator iterator = arraylist.iterator();
  
            while (iterator.hasNext()) {
              ItemStack itemstack1 = (ItemStack) iterator.next();
              StackUtil.dropAsEntity(world, i, j, k, itemstack1);
            }
  
            world.setTypeId(i, j, k, 0);
          }
      
          if (Platform.isRendering()) {
            AudioManager.playOnce(entityhuman, PositionSpec.Hand, "Tools/wrench.ogg", true, AudioManager.defaultVolume);
          }
      
          return true;
        }
      }
  
      return false;
    }
  }
  
  public void damage(ItemStack itemstack, int i, EntityHuman entityhuman) {
    itemstack.damage(i, entityhuman);
  }
  
  public boolean overrideWrenchSuccessRate(ItemStack itemstack) {
    return false;
  }
}
