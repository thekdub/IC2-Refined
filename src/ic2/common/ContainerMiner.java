package ic2.common;

import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.Slot;

public class ContainerMiner extends ContainerIC2 {
	public TileEntityMiner tileEntity;
	public short miningTicker = -1;
	public int energy = -1;

	public ContainerMiner(EntityHuman entityhuman, TileEntityMiner tileentityminer) {
		super(entityhuman, tileentityminer);
		this.tileEntity = tileentityminer;
		this.a(new SlotDischarge(tileentityminer, tileentityminer.tier, 0, 81, 59));
		this.a(new SlotCustom(tileentityminer, new Object[]{ItemScanner.class}, 1, 117, 22));
		this.a(new SlotCustom(tileentityminer, new Object[]{Block.class}, 2, 81, 22));
		this.a(new SlotCustom(tileentityminer, new Object[]{ItemElectricToolDrill.class, ItemElectricToolDDrill.class}, 3, 45, 22));

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
			if (this.miningTicker != this.tileEntity.miningTicker) {
				icrafting.setContainerData(this, 0, this.tileEntity.miningTicker);
			}

			if (this.energy != this.tileEntity.energy) {
				icrafting.setContainerData(this, 1, this.tileEntity.energy & '\uffff');
				icrafting.setContainerData(this, 2, this.tileEntity.energy >>> 16);
			}
		}

		this.miningTicker = this.tileEntity.miningTicker;
		this.energy = this.tileEntity.energy;
	}

	public void updateProgressBar(int i, int j) {
		switch (i) {
			case 0:
				this.tileEntity.miningTicker = (short) j;
				break;
			case 1:
				this.tileEntity.energy = this.tileEntity.energy & -65536 | j;
				break;
			case 2:
				this.tileEntity.energy = this.tileEntity.energy & '\uffff' | j << 16;
		}

	}

	public boolean b(EntityHuman entityhuman) {
		return this.tileEntity.a(entityhuman);
	}

	public int guiInventorySize() {
		return 4;
	}

	public int getInput() {
		return 0;
	}
}
