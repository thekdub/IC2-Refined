package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.Slot;

public class ContainerPersonalChest extends ContainerIC2 {
	public TileEntityPersonalChest tileEntity;

	public ContainerPersonalChest(EntityHuman entityhuman, TileEntityPersonalChest tileentitypersonalchest) {
		super(entityhuman, tileentitypersonalchest);
		this.tileEntity = tileentitypersonalchest;

		int k;
		int i1;
		for (k = 0; k < 6; ++k) {
			for (i1 = 0; i1 < 9; ++i1) {
				this.a(new Slot(tileentitypersonalchest, i1 + k * 9, 8 + i1 * 18, 18 + k * 18));
			}
		}

		for (k = 0; k < 3; ++k) {
			for (i1 = 0; i1 < 9; ++i1) {
				this.a(new Slot(entityhuman.inventory, 9 + i1 + k * 9, 8 + i1 * 18, 140 + k * 18));
			}
		}

		for (k = 0; k < 9; ++k) {
			this.a(new Slot(entityhuman.inventory, k, 8 + k * 18, 198));
		}

	}

	public void updateProgressBar(int i, int j) {
	}

	public boolean b(EntityHuman entityhuman) {
		return this.tileEntity.a(entityhuman);
	}

	public int guiInventorySize() {
		return 54;
	}

	public int getInput() {
		return -1;
	}
}
