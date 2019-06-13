package ic2.common;

import ic2.platform.ItemBlockCommon;
import net.minecraft.server.ItemStack;

public class ItemGenerator extends ItemBlockCommon {
	public ItemGenerator(int i) {
		super(i);
		this.setMaxDurability(0);
		this.a(true);
	}

	public int filterData(int i) {
		return i;
	}

	public String a(ItemStack itemstack) {
		int i = itemstack.getData();
		switch (i) {
			case 0:
				return "blockGenerator";
			case 1:
				return "blockGeoGenerator";
			case 2:
				return "blockWaterGenerator";
			case 3:
				return "blockSolarGenerator";
			case 4:
				return "blockWindGenerator";
			case 5:
				return "blockNuclearReactor";
			default:
				return null;
		}
	}
}
