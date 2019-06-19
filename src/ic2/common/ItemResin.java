package ic2.common;

import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockPlaceEvent;

public class ItemResin extends ItemIC2 {
  public ItemResin(int i, int j) {
    super(i, j);
  }

  public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    if (l != 1) {
      return false;
    }
    else {
      ++j;
      if (world.getTypeId(i, j, k) == 0 && Block.byId[Ic2Items.resinSheet.id].canPlace(world, i, j, k)) {
        CraftBlockState replacedBlockState = CraftBlockState.getBlockState(world, i, j, k);
        BlockPlaceEvent event = CraftEventFactory.callBlockPlaceEvent(world, entityhuman, replacedBlockState, i, j - 1, k);
        if (!event.isCancelled() && event.canBuild()) {
          world.notify(i, j, k);
          world.applyPhysics(i, j, k, l);
          world.setTypeId(i, j, k, Ic2Items.resinSheet.id);
          --itemstack.count;
          return true;
        }
        else {
          world.setTypeIdAndData(i, j, k, replacedBlockState.getTypeId(), replacedBlockState.getRawData());
          return true;
        }
      }
      else {
        return false;
      }
    }
  }
}
