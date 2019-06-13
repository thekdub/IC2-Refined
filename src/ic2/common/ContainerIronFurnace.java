package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.Slot;
import net.minecraft.server.SlotResult2;

public class ContainerIronFurnace extends ContainerIC2 {
	public TileEntityIronFurnace tileEntity;
	public short progress = -1;
	public int fuel = -1;
	public int maxFuel = -1;

	public ContainerIronFurnace(EntityHuman entityhuman, TileEntityIronFurnace tileentityironfurnace) {
		super(entityhuman, tileentityironfurnace);
		this.tileEntity = tileentityironfurnace;
		this.a(new Slot(tileentityironfurnace, 0, 56, 17));
		this.a(new Slot(tileentityironfurnace, 1, 56, 53));
		this.a(new SlotResult2(entityhuman, tileentityironfurnace, 2, 116, 35));

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
			if (this.progress != this.tileEntity.progress) {
				icrafting.setContainerData(this, 0, this.tileEntity.progress);
			}

			if (this.fuel != this.tileEntity.fuel) {
				icrafting.setContainerData(this, 1, this.tileEntity.fuel);
				icrafting.setContainerData(this, 2, this.tileEntity.fuel);
			}

			if (this.maxFuel != this.tileEntity.maxFuel) {
				icrafting.setContainerData(this, 3, this.tileEntity.maxFuel);
				icrafting.setContainerData(this, 4, this.tileEntity.maxFuel);
			}
		}

		this.progress = this.tileEntity.progress;
		this.fuel = this.tileEntity.fuel;
		this.maxFuel = this.tileEntity.maxFuel;
	}

	public void updateProgressBar(int i, int j) {
		switch (i) {
			case 0:
				this.tileEntity.progress = (short) j;
				break;
			case 1:
				this.tileEntity.fuel = this.tileEntity.fuel & -65536 | j;
				break;
			case 2:
				this.tileEntity.fuel = this.tileEntity.fuel & '\uffff' | j << 16;
				break;
			case 3:
				this.tileEntity.maxFuel = this.tileEntity.maxFuel & -65536 | j;
				break;
			case 4:
				this.tileEntity.maxFuel = this.tileEntity.maxFuel & '\uffff' | j << 16;
		}

	}

	public boolean b(EntityHuman entityhuman) {
		return this.tileEntity.a(entityhuman);
	}

	public int guiInventorySize() {
		return 3;
	}

	public int getInput() {
		return 0;
	}
}
