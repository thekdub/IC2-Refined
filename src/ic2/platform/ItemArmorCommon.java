package ic2.platform;

import net.minecraft.server.EnumArmorMaterial;
import net.minecraft.server.ItemArmor;
import net.minecraft.server.ItemStack;

public class ItemArmorCommon extends ItemArmor {
	public ItemArmorCommon(int i, EnumArmorMaterial enumarmormaterial, int j, int k) {
		super(i, enumarmormaterial, j, k);
	}

	public int rarity(ItemStack itemstack) {
		return 0;
	}
}
