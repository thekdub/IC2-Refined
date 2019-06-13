package ic2.platform;

import net.minecraft.server.EnumToolMaterial;
import net.minecraft.server.ItemStack;
import net.minecraft.server.ItemSword;

public class ItemSwordCommon extends ItemSword {
	public ItemSwordCommon(int i, EnumToolMaterial enumtoolmaterial) {
		super(i, enumtoolmaterial);
	}

	public int rarity(ItemStack itemstack) {
		return 0;
	}
}
