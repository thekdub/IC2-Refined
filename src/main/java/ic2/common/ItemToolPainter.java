package ic2.common;

import ic2.api.IPaintableBlock;
import ic2.platform.NetworkManager;
import ic2.platform.*;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

public class ItemToolPainter extends ItemIC2 {
  public int color;
  
  public ItemToolPainter(int i, int j) {
    super(i, 128);
    this.setMaxDurability(32);
    this.e(1);
    this.color = j;
  }
  
  public int getIconFromDamage(int i) {
    return this.textureId + this.color;
  }
  
  public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    int i1 = world.getTypeId(i, j, k);
    Entity entity = entityhuman.getBukkitEntity();
    if (entity instanceof Player) {
      Player player = (Player) entity;
      BlockBreakEvent event = new BlockBreakEvent(player.getWorld().getBlockAt(i, j, k), player);
      Bukkit.getPluginManager().callEvent(event);
      if (event.isCancelled()) {
        return false;
      }
      
      event.setCancelled(true);
    }
    
    if (i1 > 0 && Block.byId[i1] instanceof IPaintableBlock &&
        ((IPaintableBlock) Block.byId[i1]).colorBlock(world, i, j, k, this.color)) {
      if (Platform.isSimulating()) {
        if (itemstack.getData() >= itemstack.i() - 1) {
          this.refillPainter(itemstack, entityhuman.inventory);
        }
        else {
          itemstack.damage(1, null);
        }
      }
      
      if (Platform.isRendering()) {
        AudioManager.playOnce(entityhuman, PositionSpec.Hand, "Tools/Painter.ogg", true, AudioManager.defaultVolume);
      }
      
      return true;
    }
    else if (i1 == Block.WOOL.id && world.getData(i, j, k) != 15 - this.color) {
      world.setRawData(i, j, k, 15 - this.color);
      NetworkManager.announceBlockUpdate(world, i, j, k);
      if (itemstack.getData() >= itemstack.i() - 1) {
        this.refillPainter(itemstack, entityhuman.inventory);
      }
      else {
        itemstack.damage(1, null);
      }
      
      if (Platform.isRendering()) {
        AudioManager.playOnce(entityhuman, PositionSpec.Hand, "Tools/Painter.ogg", true, AudioManager.defaultVolume);
      }
      
      return true;
    }
    else {
      return false;
    }
  }
  
  public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
    if (Platform.isSimulating() && Keyboard.isModeSwitchKeyDown(entityhuman)) {
      NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
      boolean flag = !nbttagcompound.getBoolean("autoRefill");
      nbttagcompound.setBoolean("autoRefill", flag);
      if (flag) {
        Platform.messagePlayer(entityhuman, "Painter automatic refill mode enabled");
      }
      else {
        Platform.messagePlayer(entityhuman, "Painter automatic refill mode disabled");
      }
    }
    
    return itemstack;
  }
  
  public void addInformation(ItemStack itemstack, List list) {
    list.add(LocaleI18n.get(Item.INK_SACK.a(new ItemStack(Item.INK_SACK, 1, this.color)) + ".name"));
  }
  
  public void refillPainter(ItemStack itemstack, PlayerInventory playerinventory) {
    int i = -1;
    NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
    if (nbttagcompound.getBoolean("autoRefill")) {
      for (int j = 0; j < playerinventory.items.length; ++j) {
        if (playerinventory.items[j] != null && playerinventory.items[j].id == Item.INK_SACK.id &&
            playerinventory.items[j].getData() == this.color) {
          i = j;
          break;
        }
      }
    }
    
    if (i == -1) {
      playerinventory.items[playerinventory.itemInHandIndex] = Ic2Items.painter.cloneItemStack();
    }
    else {
      --playerinventory.items[i].count;
      if (playerinventory.items[i].count == 0) {
        playerinventory.items[i] = null;
      }
      
      itemstack.setData(0);
    }
    
    playerinventory.update();
  }
}
