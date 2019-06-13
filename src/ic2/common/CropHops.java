package ic2.common;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.server.ItemStack;

public class CropHops extends CropCard {
	public String name() {
		return "Hops";
	}

	public int tier() {
		return 5;
	}

	public int stat(int i) {
		switch (i) {
			case 0:
				return 2;
			case 1:
				return 2;
			case 2:
				return 0;
			case 3:
				return 1;
			case 4:
				return 1;
			default:
				return 0;
		}
	}

	public String[] attributes() {
		return new String[]{"Green", "Ingredient", "Wheat"};
	}

	public int getSpriteIndex(TECrop tecrop) {
		return tecrop.size >= 5 ? tecrop.size + 39 : tecrop.size + 1;
	}

	public int growthDuration(TECrop tecrop) {
		return 600;
	}

	public boolean canGrow(TECrop tecrop) {
		return tecrop.size < 7 && tecrop.getLightLevel() >= 9;
	}

	public boolean canBeHarvested(TECrop tecrop) {
		return tecrop.size == 7;
	}

	public ItemStack getGain(TECrop tecrop) {
		return new ItemStack(Ic2Items.hops.getItem(), 1);
	}

	public byte getSizeAfterHarvest(TECrop tecrop) {
		return 3;
	}
}
