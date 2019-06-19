package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.Slot;
import net.minecraft.server.SlotResult2;

public class ContainerCanner extends ContainerIC2 {
  public TileEntityCanner tileEntity;
  public short progress = -1;
  public int energy = -1;

  public ContainerCanner(EntityHuman entityhuman, TileEntityCanner tileentitycanner) {
    super(entityhuman, tileentitycanner);
    this.tileEntity = tileentitycanner;
    this.a(new Slot(tileentitycanner, 0, 69, 17));
    this.a(new SlotDischarge(tileentitycanner, 1, 30, 45));
    this.a(new SlotResult2(entityhuman, tileentitycanner, 2, 119, 35));
    this.a(new Slot(tileentitycanner, 3, 69, 53));

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
    }

    this.progress = this.tileEntity.progress;
    this.energy = this.tileEntity.energy;
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
    }

  }

  public boolean b(EntityHuman entityhuman) {
    return this.tileEntity.a(entityhuman);
  }

  public int guiInventorySize() {
    return 4;
  }

  public int getInput() {
    return this.firstEmptyFrom(0, 2, this.tileEntity);
  }
}
