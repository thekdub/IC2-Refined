package ic2.common;

import ic2.api.IBoxable;
import ic2.platform.Platform;
import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockPlaceEvent;

public class ItemDynamite extends ItemIC2 implements IBoxable {
	public boolean sticky;

	public ItemDynamite(int i, int j, boolean flag) {
		super(i, j);
		this.sticky = flag;
		this.e(16);
	}

	public int filterData(int i) {
		return i;
	}

	public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
		int clickedX = i;
		int clickedY = j;
		int clickedZ = k;
		if (this.sticky) {
			return false;
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

			int i1 = world.getTypeId(i, j, k);
			CraftBlockState replacedBlockState = CraftBlockState.getBlockState(world, i, j, k);
			BlockPlaceEvent event = CraftEventFactory.callBlockPlaceEvent(world, entityhuman, replacedBlockState, clickedX, clickedY, clickedZ);
			if (!event.isCancelled() && event.canBuild()) {
				world.notify(i, j, k);
				world.applyPhysics(i, j, k, i1);
				if (i1 == 0 && Block.byId[Ic2Items.dynamiteStick.id].canPlace(world, i, j, k)) {
					world.setTypeId(i, j, k, Ic2Items.dynamiteStick.id);
					--itemstack.count;
					return true;
				}
				else {
					return true;
				}
			}
			else {
				world.setTypeIdAndData(i, j, k, replacedBlockState.getTypeId(), replacedBlockState.getRawData());
				return true;
			}
		}
	}

	public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
		if (!entityhuman.abilities.canInstantlyBuild) {
			--itemstack.count;
		}

		world.makeSound(entityhuman, "random.bow", 0.5F, 0.4F / (c.nextFloat() * 0.4F + 0.8F));
		if (Platform.isSimulating()) {
			if (this.sticky) {
				world.addEntity(new EntityStickyDynamite(world, entityhuman));
			}
			else {
				world.addEntity(new EntityDynamite(world, entityhuman));
			}
		}

		return itemstack;
	}

	public boolean canBeStoredInToolbox(ItemStack itemstack) {
		return true;
	}
}
