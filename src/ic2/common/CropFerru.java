package ic2.common;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.server.Block;
import net.minecraft.server.ItemStack;

public class CropFerru extends CropCard {
	public String name() {
		return "Ferru";
	}

	public int tier() {
		return 6;
	}

	public int stat(int i) {
		switch (i) {
			case 0:
				return 2;
			case 1:
				return 0;
			case 2:
				return 0;
			case 3:
				return 1;
			case 4:
				return 0;
			default:
				return 0;
		}
	}

	public String[] attributes() {
		return new String[]{"Gray", "Leaves", "Metal"};
	}

	public int getSpriteIndex(TECrop tecrop) {
		return tecrop.size == 4 ? 35 : 31 + tecrop.size;
	}

	public boolean canGrow(TECrop tecrop) {
		if (tecrop.size < 3) {
			return true;
		}
		else {
			return tecrop.size == 3 && tecrop.isBlockBelow(Block.IRON_ORE);
		}
	}

	public boolean canBeHarvested(TECrop tecrop) {
		return tecrop.size == 4;
	}

	public ItemStack getGain(TECrop tecrop) {
		return new ItemStack(Ic2Items.smallIronDust.getItem());
	}

	public float dropGainChance() {
		return super.dropGainChance() / 2.0F;
	}

	public int growthDuration(TECrop tecrop) {
		return tecrop.size != 3 ? 800 : 2000;
	}

	public byte getSizeAfterHarvest(TECrop tecrop) {
		return 2;
	}
}
