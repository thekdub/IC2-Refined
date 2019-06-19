package ic2.common;

import ic2.api.IBoxable;
import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;

public class ItemCable extends ItemIC2 implements IBoxable {
  public ItemCable(int i, int j) {
    super(i, j);
    this.a(true);
    Ic2Items.copperCableItem = new ItemStack(this, 1, 1);
    Ic2Items.insulatedCopperCableItem = new ItemStack(this, 1, 0);
    Ic2Items.goldCableItem = new ItemStack(this, 1, 2);
    Ic2Items.insulatedGoldCableItem = new ItemStack(this, 1, 3);
    Ic2Items.doubleInsulatedGoldCableItem = new ItemStack(this, 1, 4);
    Ic2Items.ironCableItem = new ItemStack(this, 1, 5);
    Ic2Items.insulatedIronCableItem = new ItemStack(this, 1, 6);
    Ic2Items.doubleInsulatedIronCableItem = new ItemStack(this, 1, 7);
    Ic2Items.trippleInsulatedIronCableItem = new ItemStack(this, 1, 8);
    Ic2Items.glassFiberCableItem = new ItemStack(this, 1, 9);
    Ic2Items.tinCableItem = new ItemStack(this, 1, 10);
    Ic2Items.detectorCableItem = new ItemStack(this, 1, 11);
    Ic2Items.splitterCableItem = new ItemStack(this, 1, 12);
  }

  public int getIconFromDamage(int i) {
    return this.textureId + i;
  }

  public String a(ItemStack itemstack) {
    int i = itemstack.getData();
    switch (i) {
      case 0:
        return "itemCable";
      case 1:
        return "itemCableO";
      case 2:
        return "itemGoldCable";
      case 3:
        return "itemGoldCableI";
      case 4:
        return "itemGoldCableII";
      case 5:
        return "itemIronCable";
      case 6:
        return "itemIronCableI";
      case 7:
        return "itemIronCableII";
      case 8:
        return "itemIronCableIIII";
      case 9:
        return "itemGlassCable";
      case 10:
        return "itemTinCable";
      case 11:
        return "itemDetectorCable";
      case 12:
        return "itemSplitterCable";
      default:
        return null;
    }
  }

  public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    int clickedX = i;
    int clickedY = j;
    int clickedZ = k;
    int i1 = world.getTypeId(i, j, k);
    if (i1 > 0) {
      if (i1 == Block.SNOW.id) {
        l = 0;
      }
      else if (i1 != Block.VINE.id) {
        switch (l) {
          case 0:
            --j;
            break;
          case 1:
            ++j;
            break;
          case 2:
            --k;
            break;
          case 3:
            ++k;
            break;
          case 4:
            --i;
            break;
          case 5:
            ++i;
        }
      }
    }

    BlockCable blockcable = (BlockCable) Block.byId[Ic2Items.insulatedCopperCableBlock.id];
    if ((i1 == 0 || world.mayPlace(i1, i, j, k, true, l)) && world.containsEntity(blockcable.getCollisionBoundingBoxFromPool(world, i, j, k, itemstack.getData()))) {
      CraftBlockState replacedBlockState = CraftBlockState.getBlockState(world, i, j, k);
      if (world.setRawTypeIdAndData(i, j, k, blockcable.id, itemstack.getData())) {
        BlockPlaceEvent event = CraftEventFactory.callBlockPlaceEvent(world, entityhuman, replacedBlockState, clickedX, clickedY, clickedZ);
        if (event.isCancelled() || !event.canBuild()) {
          world.setTypeIdAndData(i, j, k, replacedBlockState.getTypeId(), replacedBlockState.getRawData());
          return true;
        }

        world.notify(i, j, k);
        world.applyPhysics(i, j, k, i1);
        blockcable.postPlace(world, i, j, k, l);
        blockcable.postPlace(world, i, j, k, entityhuman);
        --itemstack.count;
      }

      return true;
    }
    else {
      return false;
    }
  }

  public void addCreativeItems(ArrayList arraylist) {
    for (int i = 0; i < 32767; ++i) {
      ItemStack itemstack = new ItemStack(this, 1, i);
      if (this.a(itemstack) == null) {
        break;
      }

      arraylist.add(itemstack);
    }

  }

  public boolean canBeStoredInToolbox(ItemStack itemstack) {
    return true;
  }
}
