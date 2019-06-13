package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.Slot;

public class ContainerSolarGenerator extends ContainerIC2 {
	public TileEntitySolarGenerator tileEntity;
	public boolean sunIsVisible = false;
	public boolean initialized = false;

	public ContainerSolarGenerator(EntityHuman entityhuman, TileEntitySolarGenerator tileentitysolargenerator) {
		super(entityhuman, tileentitysolargenerator);
		this.tileEntity = tileentitysolargenerator;
		this.a(new SlotCharge(tileentitysolargenerator, tileentitysolargenerator.tier, 0, 80, 26));

		int j;
		for (j = 0; j < 3; ++j) {
			for (int k = 0; k < 9; ++k) {
				this.a(new Slot(entityhuman.inventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
			}
		}

		for (j = 0; j < 9; ++j) {
			this.a(new Slot(entityhuman.inventory, j, 8 + j * 18, 142));
		}

	}

	public void a() {
		super.a();

		for (int i = 0; i < this.listeners.size(); ++i) {
			ICrafting icrafting = (ICrafting) this.listeners.get(i);
			if (this.sunIsVisible != this.tileEntity.sunIsVisible || !this.initialized) {
				icrafting.setContainerData(this, 0, this.tileEntity.sunIsVisible ? 1 : 0);
				this.initialized = true;
			}
		}

		this.sunIsVisible = this.tileEntity.sunIsVisible;
	}

	public void updateProgressBar(int i, int j) {
		switch (i) {
			case 0:
				this.tileEntity.sunIsVisible = j != 0;
			default:
		}
	}

	public boolean b(EntityHuman entityhuman) {
		return this.tileEntity.a(entityhuman);
	}

	public int guiInventorySize() {
		return 1;
	}

	public int getInput() {
		return 0;
	}
}
