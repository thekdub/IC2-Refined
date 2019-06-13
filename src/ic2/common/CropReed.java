package ic2.common;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.server.Entity;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;

public class CropReed extends CropCard {
	public String name() {
		return "Reed";
	}

	public String discoveredBy() {
		return "Notch";
	}

	public int tier() {
		return 2;
	}

	public int stat(int i) {
		switch (i) {
			case 0:
				return 0;
			case 1:
				return 0;
			case 2:
				return 1;
			case 3:
				return 0;
			case 4:
				return 2;
			default:
				return 0;
		}
	}

	public String[] attributes() {
		return new String[]{"Reed"};
	}

	public int getSpriteIndex(TECrop tecrop) {
		return tecrop.size + 27;
	}

	public boolean canGrow(TECrop tecrop) {
		return tecrop.size < 3;
	}

	public int weightInfluences(TECrop tecrop, float f, float f1, float f2) {
		return (int) ((double) f * 1.2D + (double) f1 + (double) f2 * 0.8D);
	}

	public boolean canBeHarvested(TECrop tecrop) {
		return tecrop.size > 1;
	}

	public ItemStack getGain(TECrop tecrop) {
		return new ItemStack(Item.SUGAR_CANE, tecrop.size - 1);
	}

	public boolean onEntityCollision(TECrop tecrop, Entity entity) {
		return false;
	}

	public int growthDuration(TECrop tecrop) {
		return 200;
	}
}
