package ic2.common;

import forge.ISidedInventory;
import ic2.platform.Platform;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;
import net.minecraft.server.mod_IC2;

public class TileEntityGeoGenerator extends TileEntityBaseGenerator implements ISidedInventory {
	public int maxLava = 24000;

	public TileEntityGeoGenerator() {
//		super(2, mod_IC2.energyGeneratorGeo, mod_IC2.energyGeneratorGeo);
		super(2, mod_IC2.energyGeneratorGeo, 20000); //Increased maximum EU storage from 20 to 20,000.
	}

	public int gaugeFuelScaled(int i) {
		return this.fuel <= 0 ? 0 : this.fuel * i / this.maxLava;
	}

	public boolean gainFuel() {
		if (this.inventory[1] != null && this.maxLava - this.fuel >= 1000) {
			if (this.inventory[1].id == Item.LAVA_BUCKET.id) {
				this.fuel += 1000;
				this.inventory[1].id = Item.BUCKET.id;
				return true;
			}
			else if (this.inventory[1].id == Ic2Items.lavaCell.id) {
				this.fuel += 1000;
				--this.inventory[1].count;
				if (this.inventory[1].count <= 0) {
					this.inventory[1] = null;
				}

				return true;
			}
			else if (this.gainFuelSub(this.inventory[1])) {
				this.fuel += 1000;
				if (this.inventory[1].getItem().k()) {
					this.inventory[1] = new ItemStack(this.inventory[1].getItem().j());
				}
				else {
					--this.inventory[1].count;
					if (this.inventory[1].count <= 0) {
						this.inventory[1] = null;
					}
				}

				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	public boolean gainFuelSub(ItemStack itemstack) {
		return false;
	}

//	public boolean needsFuel() {
//		return this.fuel <= this.maxLava;
//	}

	public boolean needsFuel() {
		return this.fuel <= this.maxLava && this.storage + this.production <= this.maxStorage;
	}

	public int distributeLava(int i) {
		int j = this.maxLava - this.fuel;
		if (j > i) {
			j = i;
		}

		i -= j;
		this.fuel += j / 2;
		return i;
	}

	public String getName() {
		return Platform.isRendering() ? "Geothermal Generator" : "Geoth. Generator";
	}

	public String getOperationSoundFile() {
		return "Generators/GeothermalLoop.ogg";
	}

	public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
		return new ContainerBaseGenerator(entityhuman, this);
	}

	public String getGuiClassName(EntityHuman entityhuman) {
		return "GuiGeoGenerator";
	}

	public int getStartInventorySide(int i) {
		switch (i) {
			case 0:
				return 1;
			case 1:
			default:
				return 0;
		}
	}

	public int getSizeInventorySide(int i) {
		return 1;
	}
}
