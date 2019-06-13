package ic2.common;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;

public class CropCocoa extends CropCard {
	public String name() {
		return "Cocoa";
	}

	public String discoveredBy() {
		return "Notch";
	}

	public int tier() {
		return 3;
	}

	public int stat(int i) {
		switch (i) {
			case 0:
				return 1;
			case 1:
				return 3;
			case 2:
				return 0;
			case 3:
				return 4;
			case 4:
				return 0;
			default:
				return 0;
		}
	}

	public String[] attributes() {
		return new String[]{"Brown", "Food", "Stem"};
	}

	public int getSpriteIndex(TECrop tecrop) {
		return tecrop.size == 4 ? 26 : tecrop.size + 15;
	}

	public boolean canGrow(TECrop tecrop) {
		return tecrop.size <= 3 && tecrop.getNutrients() >= 3;
	}

	public int weightInfluences(TECrop tecrop, float f, float f1, float f2) {
		return (int) ((double) f * 0.8D + (double) f1 * 1.3D + (double) f2 * 0.9D);
	}

	public boolean canBeHarvested(TECrop tecrop) {
		return tecrop.size == 4;
	}

	public ItemStack getGain(TECrop tecrop) {
		return new ItemStack(Item.INK_SACK, 1, 3);
	}

	public int growthDuration(TECrop tecrop) {
		return tecrop.size != 3 ? 400 : 900;
	}

	public byte getSizeAfterHarvest(TECrop tecrop) {
		return 3;
	}
}
