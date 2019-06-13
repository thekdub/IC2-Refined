package ic2.common;

import ic2.api.CropCard;
import ic2.api.TECrop;
import net.minecraft.server.*;

public class CropVenomilia extends CropCard {
	public String name() {
		return "Venomilia";
	}

	public String discoveredBy() {
		return "raGan";
	}

	public int tier() {
		return 3;
	}

	public int stat(int i) {
		switch (i) {
			case 0:
				return 3;
			case 1:
				return 1;
			case 2:
				return 3;
			case 3:
				return 3;
			case 4:
				return 3;
			default:
				return 0;
		}
	}

	public String[] attributes() {
		return new String[]{"Purple", "Flower", "Tulip", "Poison"};
	}

	public int getSpriteIndex(TECrop tecrop) {
		if (tecrop.size <= 3) {
			return tecrop.size + 11;
		}
		else {
			return tecrop.size != 4 ? 25 : 23;
		}
	}

	public boolean canGrow(TECrop tecrop) {
		return tecrop.size <= 4 && tecrop.getLightLevel() >= 12 || tecrop.size == 5;
	}

	public boolean canBeHarvested(TECrop tecrop) {
		return tecrop.size >= 4;
	}

	public ItemStack getGain(TECrop tecrop) {
		if (tecrop.size == 5) {
			return new ItemStack(Ic2Items.grinPowder.getItem(), 1);
		}
		else {
			return tecrop.size >= 4 ? new ItemStack(Item.INK_SACK, 1, 5) : null;
		}
	}

	public byte getSizeAfterHarvest(TECrop tecrop) {
		return 3;
	}

	public int growthDuration(TECrop tecrop) {
		return tecrop.size < 3 ? 400 : 600;
	}

	public boolean rightclick(TECrop tecrop, EntityHuman entityhuman) {
		if (!entityhuman.isSneaking()) {
			this.onEntityCollision(tecrop, entityhuman);
		}

		return tecrop.harvest(true);
	}

	public boolean leftclick(TECrop tecrop, EntityHuman entityhuman) {
		if (!entityhuman.isSneaking()) {
			this.onEntityCollision(tecrop, entityhuman);
		}

		return tecrop.pick(true);
	}

	public boolean onEntityCollision(TECrop tecrop, Entity entity) {
		if (tecrop.size == 5 && entity instanceof EntityLiving) {
			if (entity instanceof EntityHuman && entity.isSneaking() && mod_IC2.random.nextInt(50) != 0) {
				return super.onEntityCollision(tecrop, entity);
			}

			((EntityLiving) entity).addEffect(new MobEffect(19, (mod_IC2.random.nextInt(10) + 5) * 20, 0));
			tecrop.size = 4;
			tecrop.updateState();
		}

		return super.onEntityCollision(tecrop, entity);
	}

	public boolean isWeed(TECrop tecrop) {
		return tecrop.size == 5 && tecrop.statGrowth >= 8;
	}
}
