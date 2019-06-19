package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockPlaceEvent;

public class ItemBarrel extends ItemIC2 {
  public ItemBarrel(int i, int j) {
    super(i, j);
    this.e(1);
  }

  public String getItemDisplayName(ItemStack itemstack) {
    int i = ItemBooze.getAmountOfValue(itemstack.getData());
    return i > 0 ? "" + i + "L Booze Barrel" : "Empty Booze Barrel";
  }

  public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    CraftBlockState replacedBlockState = CraftBlockState.getBlockState(world, i, j, k);
    BlockPlaceEvent event = CraftEventFactory.callBlockPlaceEvent(world, entityhuman, replacedBlockState, i, j, k);
    if (!event.isCancelled() && event.canBuild()) {
      if (world.getTypeId(i, j, k) == Ic2Items.scaffold.id && world.getData(i, j, k) < BlockScaffold.reinforcedStrength) {
        world.setTypeId(i, j, k, Ic2Items.blockBarrel.id);
        ((TileEntityBarrel) world.getTileEntity(i, j, k)).set(itemstack.getData());
        if (!entityhuman.abilities.canInstantlyBuild) {
          --entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex].count;
          if (entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex].count == 0) {
            entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
          }
        }

        return true;
      }
      else {
        return false;
      }
    }
    else {
      world.setTypeIdAndData(i, j, k, replacedBlockState.getTypeId(), replacedBlockState.getRawData());
      return true;
    }
  }
}
