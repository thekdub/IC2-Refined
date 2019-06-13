package ic2.common;

import forge.ITextureProvider;
import ic2.platform.ItemDoorCommon;
import net.minecraft.server.*;

public class ItemIC2Door extends ItemDoorCommon implements ITextureProvider {
	public Block block;

	public ItemIC2Door(int i, int j, Block block1) {
		super(i, Material.ORE);
		this.d(j);
		this.block = block1;
	}

	public String getTextureFile() {
		return "/ic2/sprites/item_0.png";
	}

	public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
		if (l != 1) {
			return false;
		}
		else {
			++j;
			if (entityhuman.d(i, j, k) && entityhuman.d(i, j + 1, k)) {
				if (!this.block.canPlace(world, i, j, k)) {
					return false;
				}
				else {
					int i1 = MathHelper.floor((double) ((entityhuman.yaw + 180.0F) * 4.0F / 360.0F) - 0.5D) & 3;
					place(world, i, j, k, i1, this.block);
					--itemstack.count;
					return true;
				}
			}
			else {
				return false;
			}
		}
	}
}
