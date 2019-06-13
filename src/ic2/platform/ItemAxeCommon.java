package ic2.platform;

import net.minecraft.server.EnumToolMaterial;
import net.minecraft.server.ItemAxe;
import net.minecraft.server.ItemStack;

public class ItemAxeCommon extends ItemAxe {
	public ItemAxeCommon(int i, EnumToolMaterial enumtoolmaterial) {
		super(i, enumtoolmaterial);
	}

	public int rarity(ItemStack itemstack) {
		return 0;
	}
}
