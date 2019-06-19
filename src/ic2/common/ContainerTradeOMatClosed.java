package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.Slot;

public class ContainerTradeOMatClosed extends ContainerIC2 {
  public TileEntityTradeOMat tileEntity;
  public int stock = -1;

  public ContainerTradeOMatClosed(EntityHuman entityhuman, TileEntityTradeOMat tileentitytradeomat) {
    super(entityhuman, tileentitytradeomat);
    this.tileEntity = tileentitytradeomat;
    tileentitytradeomat.updateStock();
    this.a(new SlotDisplay(tileentitytradeomat, 0, 50, 19));
    this.a(new SlotDisplay(tileentitytradeomat, 1, 50, 38));
    this.a(new Slot(tileentitytradeomat, 2, 143, 17));
    this.a(new Slot(tileentitytradeomat, 3, 143, 53));

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
      if (this.stock != this.tileEntity.stock) {
        icrafting.setContainerData(this, 0, this.tileEntity.stock & '\uffff');
        icrafting.setContainerData(this, 1, this.tileEntity.stock >>> 16);
      }
    }

  }

  public void updateProgressBar(int i, int j) {
    switch (i) {
      case 0:
        this.tileEntity.stock = this.tileEntity.stock & -65536 | j;
        break;
      case 1:
        this.tileEntity.stock = this.tileEntity.stock & '\uffff' | j << 16;
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
