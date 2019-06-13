package ic2.platform;

import net.minecraft.server.ItemBlock;
import net.minecraft.server.ItemStack;

public class ItemBlockCommon extends ItemBlock {
	public ItemBlockCommon(int i) {
		super(i);
	}

	public int filterData(int i) {
		return this.getPlacedBlockMetadata(i);
	}

	public int getPlacedBlockMetadata(int i) {
		return 0;
	}

	public String a(ItemStack itemstack) {
		return null;
	}

	public int rarity(ItemStack itemstack) {
		return 0;
	}
}
