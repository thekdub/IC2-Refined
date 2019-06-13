package ic2.common;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.server.Block;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;

public class CropNetherWart extends CropCard {
	public String name() {
		return "Nether Wart";
	}

	public String discoveredBy() {
		return "Notch";
	}

	public int tier() {
		return 5;
	}

	public int stat(int i) {
		switch (i) {
			case 0:
				return 4;
			case 1:
				return 2;
			case 2:
				return 0;
			case 3:
				return 2;
			case 4:
				return 1;
			default:
				return 0;
		}
	}

	public String[] attributes() {
		return new String[]{"Red", "Nether", "Ingredient", "Soulsand"};
	}

	public int getSpriteIndex(TECrop tecrop) {
		return tecrop.size + 36;
	}

	public boolean canGrow(TECrop tecrop) {
		return tecrop.size < 3;
	}

	public boolean canBeHarvested(TECrop tecrop) {
		return tecrop.size == 3;
	}

	public float dropGainChance() {
		return 2.0F;
	}

	public ItemStack getGain(TECrop tecrop) {
		return new ItemStack(Item.NETHER_STALK, 1);
	}

	public void tick(TECrop tecrop) {
		TileEntityCrop tileentitycrop = (TileEntityCrop) tecrop;
		if (tileentitycrop.isBlockBelow(Block.SOUL_SAND)) {
			if (this.canGrow(tileentitycrop)) {
				tileentitycrop.growthPoints = (int) ((double) tileentitycrop.growthPoints + (double) tileentitycrop.calcGrowthRate() * 0.5D);
			}
		}
		else if (tileentitycrop.isBlockBelow(Block.SNOW_BLOCK) && tecrop.world.random.nextInt(300) == 0) {
			tileentitycrop.id = (short) IC2Crops.cropTerraWart.getId();
		}

	}
}
