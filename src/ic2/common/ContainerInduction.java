package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.Slot;
import net.minecraft.server.SlotResult2;

public class ContainerInduction extends ContainerIC2 {
  public TileEntityInduction tileEntity;
  public short progress = -1;
  public int energy = -1;
  public short heat = -1;

  public ContainerInduction(EntityHuman entityhuman, TileEntityInduction tileentityinduction) {
    super(entityhuman, tileentityinduction);
    this.tileEntity = tileentityinduction;
    this.a(new Slot(tileentityinduction, 0, 47, 17));
    this.a(new Slot(tileentityinduction, 1, 63, 17));
    this.a(new SlotDischarge(tileentityinduction, tileentityinduction.tier, 2, 56, 53));
    this.a(new SlotResult2(entityhuman, tileentityinduction, 3, 113, 35));
    this.a(new SlotResult2(entityhuman, tileentityinduction, 4, 131, 35));

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

      if (this.energy != this.tileEntity.energy) {
        icrafting.setContainerData(this, 1, this.tileEntity.energy & '\uffff');
        icrafting.setContainerData(this, 2, this.tileEntity.energy >>> 16);
      }

      if (this.heat != this.tileEntity.heat) {
        icrafting.setContainerData(this, 3, this.tileEntity.heat);
      }
    }

    this.progress = this.tileEntity.progress;
    this.energy = this.tileEntity.energy;
    this.heat = this.tileEntity.heat;
  }

  public void updateProgressBar(int i, int j) {
    switch (i) {
      case 0:
        this.tileEntity.progress = (short) j;
        break;
      case 1:
        this.tileEntity.energy = this.tileEntity.energy & -65536 | j;
        break;
      case 2:
        this.tileEntity.energy = this.tileEntity.energy & '\uffff' | j << 16;
        break;
      case 3:
        this.tileEntity.heat = (short) j;
    }

  }

  public boolean b(EntityHuman entityhuman) {
    return this.tileEntity.a(entityhuman);
  }

  public int guiInventorySize() {
    return 5;
  }

  public int getInput() {
    return this.tileEntity.getItem(0) == null ? 0 : 1;
  }
}
