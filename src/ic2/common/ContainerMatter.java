package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.Slot;
import net.minecraft.server.SlotResult2;

public class ContainerMatter extends ContainerIC2 {
	public TileEntityMatter tileEntity;
	public int energy = -1;
	public int scrap = -1;

	public ContainerMatter(EntityHuman entityhuman, TileEntityMatter tileentitymatter) {
		super(entityhuman, tileentitymatter);
		this.tileEntity = tileentitymatter;
		this.a(new SlotMatterScrap(tileentitymatter, 0, 114, 54));
		this.a(new SlotResult2(entityhuman, tileentitymatter, 1, 114, 18));

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
			if (this.energy != this.tileEntity.energy) {
				icrafting.setContainerData(this, 0, this.tileEntity.energy & '\uffff');
				icrafting.setContainerData(this, 1, this.tileEntity.energy >>> 16);
			}

			if (this.scrap != this.tileEntity.scrap) {
				icrafting.setContainerData(this, 2, this.tileEntity.scrap & '\uffff');
				icrafting.setContainerData(this, 3, this.tileEntity.scrap >>> 16);
			}
		}

		this.energy = this.tileEntity.energy;
		this.scrap = this.tileEntity.scrap;
	}

	public void updateProgressBar(int i, int j) {
		switch (i) {
			case 0:
				this.tileEntity.energy = this.tileEntity.energy & -65536 | j;
				break;
			case 1:
				this.tileEntity.energy = this.tileEntity.energy & '\uffff' | j << 16;
				break;
			case 2:
				this.tileEntity.scrap = this.tileEntity.scrap & -65536 | j;
				break;
			case 3:
				this.tileEntity.scrap = this.tileEntity.scrap & '\uffff' | j << 16;
		}

	}

	public boolean b(EntityHuman entityhuman) {
		return this.tileEntity.a(entityhuman);
	}

	public int guiInventorySize() {
		return 2;
	}

	public int getInput() {
		return 0;
	}
}
