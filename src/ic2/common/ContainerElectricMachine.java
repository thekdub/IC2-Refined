package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ICrafting;
import net.minecraft.server.Slot;
import net.minecraft.server.SlotResult2;

public class ContainerElectricMachine extends ContainerIC2 {
  public TileEntityElectricMachine tileEntity;
  public float lastChargeLevel = -1.0F;
  public float lastProgress = -1.0F;

  public ContainerElectricMachine(EntityHuman entityhuman, TileEntityElectricMachine tileentityelectricmachine) {
    super(entityhuman, tileentityelectricmachine);
    this.tileEntity = tileentityelectricmachine;
    this.a(new Slot(tileentityelectricmachine, 0, 56, 17));
    this.a(new SlotDischarge(tileentityelectricmachine, Integer.MAX_VALUE, 1, 56, 53));
    this.a(new SlotResult2(entityhuman, tileentityelectricmachine, 2, 116, 35));

    int k;
    for (k = 0; k < 4; ++k) {
      this.a(new SlotCustom(tileentityelectricmachine, new Object[]{ItemUpgradeModule.class}, 3 + k, 152, 8 + k * 18));
    }

    for (k = 0; k < 3; ++k) {
      for (int l = 0; l < 9; ++l) {
        this.a(new Slot(entityhuman.inventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
      }
    }

    for (k = 0; k < 9; ++k) {
      this.a(new Slot(entityhuman.inventory, k, 8 + k * 18, 142));
    }

  }

  public void a() {
    super.a();
    float f = this.tileEntity.getChargeLevel();
    float f1 = this.tileEntity.getProgress();

    for (int i = 0; i < this.listeners.size(); ++i) {
      ICrafting icrafting = (ICrafting) this.listeners.get(i);
      if (this.lastChargeLevel != f) {
        icrafting.setContainerData(this, 0, (short) ((int) (f * 32767.0F)));
      }

      if (this.lastProgress != f1) {
        icrafting.setContainerData(this, 1, (short) ((int) (f1 * 32767.0F)));
      }
    }

    this.lastChargeLevel = f;
    this.lastProgress = f1;
  }

  public void updateProgressBar(int i, int j) {
    switch (i) {
      case 0:
        this.tileEntity.setChargeLevel((float) j / 32767.0F);
        break;
      case 1:
        this.tileEntity.setProgress((float) j / 32767.0F);
    }

  }

  public boolean b(EntityHuman entityhuman) {
    return this.tileEntity.a(entityhuman);
  }

  public int guiInventorySize() {
    return 7;
  }

  public int getInput() {
    return 0;
  }
}
