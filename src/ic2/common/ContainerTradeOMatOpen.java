package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.Slot;

public class ContainerTradeOMatOpen extends ContainerIC2 {
	public TileEntityTradeOMat tileEntity;
	public int totalTradeCount = -1;

	public ContainerTradeOMatOpen(EntityHuman entityhuman, TileEntityTradeOMat tileentitytradeomat) {
		super(entityhuman, tileentitytradeomat);
		this.tileEntity = tileentitytradeomat;
		this.a(new Slot(tileentitytradeomat, 0, 24, 17));
		this.a(new Slot(tileentitytradeomat, 1, 24, 53));
		this.a(new Slot(tileentitytradeomat, 2, 80, 17));
		this.a(new Slot(tileentitytradeomat, 3, 80, 53));

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
			if (this.totalTradeCount != this.tileEntity.totalTradeCount) {
				icrafting.setContainerData(this, 0, this.tileEntity.totalTradeCount & '\uffff');
				icrafting.setContainerData(this, 1, this.tileEntity.totalTradeCount >>> 16);
			}
		}

		this.totalTradeCount = this.tileEntity.totalTradeCount;
	}

	public void updateProgressBar(int i, int j) {
		switch (i) {
			case 0:
				this.tileEntity.totalTradeCount = this.tileEntity.totalTradeCount & -65536 | j;
				break;
			case 1:
				this.tileEntity.totalTradeCount = this.tileEntity.totalTradeCount & '\uffff' | j << 16;
		}

	}

	public boolean b(EntityHuman entityhuman) {
		return this.tileEntity.a(entityhuman);
	}

	public int guiInventorySize() {
		return 4;
	}

	public int getInput() {
		return 2;
	}
}
