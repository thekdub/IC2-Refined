package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.Slot;

public class ContainerWindGenerator extends ContainerIC2 {
  public TileEntityWindGenerator tileEntity;
  public short storage = -1;
  public int fuel = -1;
  
  public ContainerWindGenerator(EntityHuman entityhuman, TileEntityWindGenerator tileentitywindgenerator) {
    super(entityhuman, tileentitywindgenerator);
    this.tileEntity = tileentitywindgenerator;
    this.a(new SlotCharge(tileentitywindgenerator, tileentitywindgenerator.tier, 0, 80, 26));
    
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
      if (this.storage != this.tileEntity.storage) {
        icrafting.setContainerData(this, 0, this.tileEntity.storage);
      }
      
      if (this.fuel != this.tileEntity.fuel) {
        icrafting.setContainerData(this, 1, this.tileEntity.fuel & '\uffff');
        icrafting.setContainerData(this, 2, this.tileEntity.fuel >>> 16);
      }
    }
    
    this.storage = this.tileEntity.storage;
    this.fuel = this.tileEntity.fuel;
  }
  
  public void updateProgressBar(int i, int j) {
    switch (i) {
      case 0:
        this.tileEntity.storage = (short) j;
        break;
      case 1:
        this.tileEntity.fuel = this.tileEntity.fuel & -65536 | j;
        break;
      case 2:
        this.tileEntity.fuel = this.tileEntity.fuel & '\uffff' | j << 16;
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
