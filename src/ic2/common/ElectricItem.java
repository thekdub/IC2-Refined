package ic2.common;

import ic2.api.IElectricItem;
import ic2.platform.Platform;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;
import net.minecraft.server.NBTTagCompound;

import java.util.ArrayList;

public abstract class ElectricItem extends ItemIC2 implements IElectricItem {
	public int maxCharge;
	public int transferLimit = 100;
	public int tier = 1;

	public ElectricItem(int i, int j) {
		super(i, j);
		this.setMaxDurability(27);
		this.e(1);
	}

	public static int charge(ItemStack itemstack, int i, int j, boolean flag, boolean flag1) {
		if (itemstack.getItem() instanceof IElectricItem && i >= 0 && itemstack.count <= 1) {
			IElectricItem ielectricitem = (IElectricItem) itemstack.getItem();
			if (ielectricitem.getTier() > j) {
				return 0;
			}
			else {
				if (i > ielectricitem.getTransferLimit() && !flag) {
					i = ielectricitem.getTransferLimit();
				}

				NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
				int k = nbttagcompound.getInt("charge");
				if (i > ielectricitem.getMaxCharge() - k) {
					i = ielectricitem.getMaxCharge() - k;
				}

				k += i;
				if (!flag1) {
					nbttagcompound.setInt("charge", k);
					itemstack.id = k <= 0 ? ielectricitem.getEmptyItemId() : ielectricitem.getChargedItemId();
					if (itemstack.getItem() instanceof IElectricItem) {
						IElectricItem ielectricitem1 = (IElectricItem) itemstack.getItem();
						if (itemstack.i() > 2) {
							itemstack.setData(1 + (ielectricitem1.getMaxCharge() - k) * (itemstack.i() - 2) / ielectricitem1.getMaxCharge());
						}
						else {
							itemstack.setData(0);
						}
					}
					else {
						itemstack.setData(0);
					}
				}

				return i;
			}
		}
		else {
			return 0;
		}
	}

	public static int discharge(ItemStack itemstack, int i, int j, boolean flag, boolean flag1) {
		if (itemstack.getItem() instanceof IElectricItem && i >= 0 && itemstack.count <= 1) {
			IElectricItem ielectricitem = (IElectricItem) itemstack.getItem();
			if (ielectricitem.getTier() > j) {
				return 0;
			}
			else {
				if (i > ielectricitem.getTransferLimit() && !flag) {
					i = ielectricitem.getTransferLimit();
				}

				NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
				int k = nbttagcompound.getInt("charge");
				if (i > k) {
					i = k;
				}

				k -= i;
				if (!flag1) {
					nbttagcompound.setInt("charge", k);
					itemstack.id = k <= 0 ? ielectricitem.getEmptyItemId() : ielectricitem.getChargedItemId();
					if (itemstack.getItem() instanceof IElectricItem) {
						IElectricItem ielectricitem1 = (IElectricItem) itemstack.getItem();
						if (itemstack.i() > 2) {
							itemstack.setData(1 + (ielectricitem1.getMaxCharge() - k) * (itemstack.i() - 2) / ielectricitem1.getMaxCharge());
						}
						else {
							itemstack.setData(0);
						}
					}
					else {
						itemstack.setData(0);
					}
				}

				return i;
			}
		}
		else {
			return 0;
		}
	}

	public static boolean canUse(ItemStack itemstack, int i) {
		NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
		return nbttagcompound.getInt("charge") >= i;
	}

	public static boolean use(ItemStack itemstack, int i, EntityHuman entityhuman) {
		if (Platform.isSimulating() && itemstack.getItem() instanceof IElectricItem) {
			chargeFromArmor(itemstack, entityhuman);
			int j = discharge(itemstack, i, Integer.MAX_VALUE, true, true);
			if (j == i) {
				discharge(itemstack, i, Integer.MAX_VALUE, true, false);
				chargeFromArmor(itemstack, entityhuman);
				return true;
			}
		}

		return false;
	}

	public static void chargeFromArmor(ItemStack itemstack, EntityHuman entityhuman) {
		if (Platform.isSimulating() && entityhuman != null && itemstack.getItem() instanceof IElectricItem) {
			boolean flag = false;

			for (int i = 0; i < 4; ++i) {
				ItemStack itemstack1 = entityhuman.inventory.armor[i];
				if (itemstack1 != null && itemstack1.getItem() instanceof IElectricItem) {
					IElectricItem ielectricitem = (IElectricItem) itemstack1.getItem();
					if (ielectricitem.canProvideEnergy() && ielectricitem.getTier() >= ((IElectricItem) itemstack.getItem()).getTier()) {
						int j = charge(itemstack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true);
						j = discharge(itemstack1, j, Integer.MAX_VALUE, true, false);
						if (j > 0) {
							charge(itemstack, j, Integer.MAX_VALUE, true, false);
							flag = true;
						}
					}
				}
			}

			if (flag) {
				entityhuman.inventory.update();
			}

		}
	}

	public boolean canProvideEnergy() {
		return false;
	}

	public int getChargedItemId() {
		return this.id;
	}

	public int getEmptyItemId() {
		return this.id;
	}

	public int getMaxCharge() {
		return this.maxCharge;
	}

	public int getTier() {
		return this.tier;
	}

	public int getTransferLimit() {
		return this.transferLimit;
	}

	public void addCreativeItems(ArrayList arraylist) {
		ItemStack itemstack = new ItemStack(this, 1);
		charge(itemstack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
		arraylist.add(itemstack);
		arraylist.add(new ItemStack(this, 1, this.getMaxDurability()));
	}
}
