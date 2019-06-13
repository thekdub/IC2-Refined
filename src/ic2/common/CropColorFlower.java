package ic2.common;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;

public class CropColorFlower extends CropCard {
	public String name;
	public String[] attributes;
	public int sprite;
	public int color;

	public CropColorFlower(String s, String[] as, int i, int j) {
		this.name = s;
		this.attributes = as;
		this.sprite = i;
		this.color = j;
	}

	public String discoveredBy() {
		return !this.name.equals("Dandelion") && !this.name.equals("Rose") ? "Alblaka" : "Notch";
	}

	public String name() {
		return this.name;
	}

	public int tier() {
		return 2;
	}

	public int stat(int i) {
		switch (i) {
			case 0:
				return 1;
			case 1:
				return 1;
			case 2:
				return 0;
			case 3:
				return 5;
			case 4:
				return 1;
			default:
				return 0;
		}
	}

	public String[] attributes() {
		return this.attributes;
	}

	public int getSpriteIndex(TECrop tecrop) {
		switch (tecrop.size) {
			case 1:
				return 12;
			case 2:
				return 13;
			case 3:
				return 14;
			case 4:
				return this.sprite;
			default:
				return 0;
		}
	}

	public boolean canGrow(TECrop tecrop) {
		return tecrop.size <= 3 && tecrop.getLightLevel() >= 12;
	}

	public boolean canBeHarvested(TECrop tecrop) {
		return tecrop.size == 4;
	}

	public ItemStack getGain(TECrop tecrop) {
		return new ItemStack(Item.INK_SACK, 1, this.color);
	}

	public byte getSizeAfterHarvest(TECrop tecrop) {
		return 3;
	}

	public int growthDuration(TECrop tecrop) {
		return tecrop.size != 3 ? 400 : 600;
	}
}
