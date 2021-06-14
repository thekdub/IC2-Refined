package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.Slot;

public class ContainerCropmatron extends ContainerIC2 {
  public TileEntityCropmatron tileEntity;
  public int energy = -1;
  
  public ContainerCropmatron(EntityHuman entityhuman, TileEntityCropmatron tileentitycropmatron) {
    super(entityhuman, tileentitycropmatron);
    this.tileEntity = tileentitycropmatron;
    this.a(new SlotCustom(tileentitycropmatron, new Object[]{Ic2Items.fertilizer.getItem()}, 0, 62, 20));
    this.a(new SlotCustom(tileentitycropmatron, new Object[]{Ic2Items.fertilizer.getItem()}, 1, 62, 38));
    this.a(new SlotCustom(tileentitycropmatron, new Object[]{Ic2Items.fertilizer.getItem()}, 2, 62, 56));
    this.a(new SlotCustom(tileentitycropmatron, new Object[]{Ic2Items.hydratingCell.getItem()}, 3, 98, 20));
    this.a(new SlotCustom(tileentitycropmatron, new Object[]{Ic2Items.hydratingCell.getItem()}, 4, 98, 38));
    this.a(new SlotCustom(tileentitycropmatron, new Object[]{Ic2Items.hydratingCell.getItem()}, 5, 98, 56));
    this.a(new SlotCustom(tileentitycropmatron, new Object[]{Ic2Items.weedEx.getItem()}, 6, 134, 20));
    this.a(new SlotCustom(tileentitycropmatron, new Object[]{Ic2Items.weedEx.getItem()}, 7, 134, 38));
    this.a(new SlotCustom(tileentitycropmatron, new Object[]{Ic2Items.weedEx.getItem()}, 8, 134, 56));
    
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
        icrafting.setContainerData(this, 1, this.tileEntity.energy & '\uffff');
        icrafting.setContainerData(this, 2, this.tileEntity.energy >>> 16);
      }
    }
    
    this.energy = this.tileEntity.energy;
  }
  
  public void updateProgressBar(int i, int j) {
    switch (i) {
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
    return 9;
  }
  
  public int getInput() {
    return 0;
  }
}
