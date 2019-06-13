package ic2.common;

import forge.ISidedInventory;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.mod_IC2;

import java.util.Random;

public class TileEntityWindGenerator extends TileEntityBaseGenerator implements ISidedInventory {
	public static Random randomizer = new Random();
	public double subproduction = 0.0D;
	public double substorage = 0.0D;
	public int ticker;
	public int obscuratedBlockCount;

	public TileEntityWindGenerator() {
		super(1, 4, 4);
		this.ticker = randomizer.nextInt(this.tickRate());
	}

	public int gaugeFuelScaled(int i) {
		double d = this.subproduction / 3.0D;
		int j = (int) (d * (double) i);
		if (j < 0) {
			return 0;
		}
		else {
			return j > i ? i : j;
		}
	}

	public int getOverheatScaled(int i) {
		double d = (this.subproduction - 5.0D) / 5.0D;
		if (this.subproduction <= 5.0D) {
			return 0;
		}
		else {
			return this.subproduction >= 10.0D ? i : (int) (d * (double) i);
		}
	}

	public void onCreated() {
		super.onCreated();
		this.updateObscuratedBlockCount();
	}

	public boolean gainEnergy() {
		++this.ticker;
		if (this.ticker % this.tickRate() == 0) {
			if (this.ticker % (8 * this.tickRate()) == 0) {
				this.updateObscuratedBlockCount();
			}

			this.subproduction = (double) (mod_IC2.windStrength * (this.y - 64 - this.obscuratedBlockCount)) / 750.0D;
			if (this.subproduction <= 0.0D) {
				return false;
			}

			if (this.world.w()) {
				this.subproduction *= 1.5D;
			}
			else if (this.world.x()) {
				this.subproduction *= 1.2D;
			}

			if (this.subproduction > 5.0D && (double) this.world.random.nextInt(5000) <= this.subproduction - 5.0D) {
				this.subproduction = 0.0D;
				this.world.setTypeIdAndData(this.x, this.y, this.z, Ic2Items.generator.id, Ic2Items.generator.getData());

				for (int i = this.world.random.nextInt(5); i > 0; --i) {
					StackUtil.dropAsEntity(this.world, this.x, this.y, this.z, new ItemStack(Item.IRON_INGOT));
				}

				return false;
			}

			this.subproduction *= (double) mod_IC2.energyGeneratorWind;
			this.subproduction /= 100.0D;
		}

		this.substorage += this.subproduction;
		this.production = (short) ((int) this.substorage);
		if (this.storage + this.production >= this.maxStorage) {
			this.substorage = 0.0D;
			return false;
		}
		else {
			this.storage = (short) (this.storage + this.production);
			this.substorage -= (double) this.production;
			return true;
		}
	}

	public boolean gainFuel() {
		return false;
	}

	public void updateObscuratedBlockCount() {
		this.obscuratedBlockCount = -1;

		for (int i = -4; i < 5; ++i) {
			for (int j = -2; j < 5; ++j) {
				for (int k = -4; k < 5; ++k) {
					if (this.world.getTypeId(i + this.x, j + this.y, k + this.z) != 0) {
						++this.obscuratedBlockCount;
					}
				}
			}
		}

	}

	public boolean needsFuel() {
		return true;
	}

	public int getMaxEnergyOutput() {
		return 10;
	}

	public String getName() {
		return "Wind Mill";
	}

	public String getGuiClassName(EntityHuman entityhuman) {
		return "GuiWindGenerator";
	}

	public int tickRate() {
		return 128;
	}

	public String getOperationSoundFile() {
		return "Generators/WindGenLoop.ogg";
	}

	public boolean delayActiveUpdate() {
		return true;
	}

	public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
		return new ContainerWindGenerator(entityhuman, this);
	}

	public int getStartInventorySide(int i) {
		return 0;
	}

	public int getSizeInventorySide(int i) {
		return 1;
	}
}
