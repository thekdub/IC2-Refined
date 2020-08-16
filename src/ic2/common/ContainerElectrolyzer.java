package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.Slot;

public class ContainerElectrolyzer extends ContainerIC2 {
  public TileEntityElectrolyzer tileEntity;
  public short energy = -1;
  
  public ContainerElectrolyzer(EntityHuman entityhuman, TileEntityElectrolyzer tileentityelectrolyzer) {
    super(entityhuman, tileentityelectrolyzer);
    this.tileEntity = tileentityelectrolyzer;
    this.a(new SlotCustom(tileentityelectrolyzer, new Object[]{Ic2Items.waterCell}, 0, 53, 35));
    this.a(new SlotCustom(tileentityelectrolyzer, new Object[]{Ic2Items.electrolyzedWaterCell}, 1, 112, 35));
    
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
        icrafting.setContainerData(this, 0, this.tileEntity.energy);
      }
    }
    
    this.energy = this.tileEntity.energy;
  }
  
  public void updateProgressBar(int i, int j) {
    switch (i) {
      case 0:
        this.tileEntity.energy = (short) j;
      default:
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
