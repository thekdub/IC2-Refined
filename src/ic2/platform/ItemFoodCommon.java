package ic2.platform;

import net.minecraft.server.ItemFood;
import net.minecraft.server.ItemStack;

public class ItemFoodCommon extends ItemFood {
	public ItemFoodCommon(int i, int j, float f, boolean flag) {
		super(i, j, f, flag);
	}

	public ItemFoodCommon(int i, int j, boolean flag) {
		super(i, j, flag);
	}

	public int rarity(ItemStack itemstack) {
		return 0;
	}
}
