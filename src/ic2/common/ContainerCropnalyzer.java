package ic2.common;

import ic2.platform.Platform;
import net.minecraft.server.*;

public class ContainerCropnalyzer extends ContainerIC2 {
	public HandHeldCropnalyzer cropnalyzer;

	public ContainerCropnalyzer(EntityHuman entityhuman, HandHeldCropnalyzer handheldcropnalyzer) {
		super(entityhuman, handheldcropnalyzer);
		this.cropnalyzer = handheldcropnalyzer;
		this.a(new SlotCustom(handheldcropnalyzer, new Object[]{ItemCropSeed.class}, 0, 8, 7));
		this.a(new SlotCustom(handheldcropnalyzer, new Object[0], 1, 41, 7));
		this.a(new SlotDischarge(handheldcropnalyzer, 2, 152, 7));

		for (int i = 0; i < 9; ++i) {
			this.a(new Slot(entityhuman.inventory, i, 8 + i * 18, 142));
		}

	}

	public int guiInventorySize() {
		return 3;
	}

	public int getInput() {
		return 0;
	}

	public void updateProgressBar(int i, int j) {
	}

	public boolean b(EntityHuman entityhuman) {
		return this.cropnalyzer.a(entityhuman);
	}

	public ItemStack clickItem(int i, int j, boolean flag, EntityHuman entityhuman) {
		if (Platform.isSimulating() && i == -999 && (j == 0 || j == 1)) {
			ItemStack itemstack = entityhuman.inventory.getCarried();
			if (itemstack != null) {
				NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
				if (this.cropnalyzer.matchesUid(nbttagcompound.getInt("uid"))) {
					mod_IC2.closeScreenOfEntityPlayer(entityhuman);
				}
			}
		}

		if (entityhuman != null && i >= 0 && i < 3 && this.cropnalyzer.getItem(i) != null) {
			this.cropnalyzer.portOnClick(entityhuman);
		}

		return super.clickItem(i, j, flag, entityhuman);
	}

	public void a(EntityHuman entityhuman) {
		this.cropnalyzer.onGuiClosed(entityhuman);
		super.a(entityhuman);
	}
}
