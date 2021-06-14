package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.Slot;

public class ContainerNuclearReactor extends ContainerIC2 {
  public TileEntityNuclearReactor tileEntity;
  public short output = -1;
  public int heat = -1;
  public short size;
  
  public ContainerNuclearReactor(EntityHuman entityhuman, TileEntityNuclearReactor tileentitynuclearreactor) {
    super(entityhuman, tileentitynuclearreactor);
    this.tileEntity = tileentitynuclearreactor;
    this.size = tileentitynuclearreactor.getReactorSize();
    int i = 89 - 9 * this.size;
    byte byte0 = 18;
    int j = 0;
    int k = 0;
    
    int j1;
    for (j1 = 0; j1 < 54; ++j1) {
      if (j < this.size) {
        this.a(new SlotReactor(tileentitynuclearreactor, j1, i + 18 * j, byte0 + 18 * k));
      }
  
      ++j;
      if (j >= 9) {
        ++k;
        j = 0;
      }
    }
    
    for (j1 = 0; j1 < 3; ++j1) {
      for (int k1 = 0; k1 < 9; ++k1) {
        this.a(new Slot(entityhuman.inventory, k1 + j1 * 9 + 9, 8 + k1 * 18, 140 + j1 * 18));
      }
    }
    
    for (j1 = 0; j1 < 9; ++j1) {
      this.a(new Slot(entityhuman.inventory, j1, 8 + j1 * 18, 198));
    }
    
  }
  
  public void a() {
    super.a();
    
    for (int i = 0; i < this.listeners.size(); ++i) {
      ICrafting icrafting = (ICrafting) this.listeners.get(i);
      if (this.output != this.tileEntity.output) {
        icrafting.setContainerData(this, 0, this.tileEntity.output);
      }
      
      if (this.heat != this.tileEntity.heat) {
        icrafting.setContainerData(this, 1, this.tileEntity.heat & '\uffff');
        icrafting.setContainerData(this, 2, this.tileEntity.heat >>> 16);
      }
    }
    
    this.output = this.tileEntity.output;
    this.heat = this.tileEntity.heat;
  }
  
  public void updateProgressBar(int i, int j) {
    switch (i) {
      case 0:
        this.tileEntity.output = (short) j;
        break;
      case 1:
        this.tileEntity.heat = this.tileEntity.heat & -65536 | j;
        break;
      case 2:
        this.tileEntity.heat = this.tileEntity.heat & '\uffff' | j << 16;
    }
    
  }
  
  public boolean b(EntityHuman entityhuman) {
    return this.tileEntity.a(entityhuman);
  }
  
  public int guiInventorySize() {
    return 6 * this.size;
  }
  
  public int getInput() {
    return -1;
  }
}
