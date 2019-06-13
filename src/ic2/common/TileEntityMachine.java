package ic2.common;

import ic2.platform.Platform;
import net.minecraft.server.*;

public abstract class TileEntityMachine extends TileEntityBlock implements IInventory {
	public ItemStack[] inventory;

	public TileEntityMachine(int i) {
		this.inventory = new ItemStack[i];
	}

	public int getSize() {
		return this.inventory.length;
	}

	public ItemStack getItem(int i) {
		return this.inventory[i];
	}

	public ItemStack splitStack(int i, int j) {
		if (this.inventory[i] != null) {
			ItemStack itemstack1;
			if (this.inventory[i].count <= j) {
				itemstack1 = this.inventory[i];
				this.inventory[i] = null;
				return itemstack1;
			}
			else {
				itemstack1 = this.inventory[i].a(j);
				if (this.inventory[i].count == 0) {
					this.inventory[i] = null;
				}

				return itemstack1;
			}
		}
		else {
			return null;
		}
	}

	public void setItem(int i, ItemStack itemstack) {
		this.inventory[i] = itemstack;
		if (itemstack != null && itemstack.count > this.getMaxStackSize()) {
			itemstack.count = this.getMaxStackSize();
		}

	}

	public int getMaxStackSize() {
		return 64;
	}

	public void setMaxStackSize(int arg0) {
	}

	public boolean a(EntityHuman entityhuman) {
		if (this.world.getTileEntity(this.x, this.y, this.z) != this) {
			return false;
		}
		else {
			return entityhuman.f((double) this.x + 0.5D, (double) this.y + 0.5D, (double) this.z + 0.5D) <= 64.0D;
		}
	}

	public abstract String getName();

	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		NBTTagList nbttaglist = nbttagcompound.getList("Items");
		this.inventory = new ItemStack[this.getSize()];

		for (int i = 0; i < nbttaglist.size(); ++i) {
			NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.get(i);
			byte byte0 = nbttagcompound1.getByte("Slot");
			if (byte0 >= 0 && byte0 < this.inventory.length) {
				this.inventory[byte0] = ItemStack.a(nbttagcompound1);
			}
		}

	}

	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.inventory.length; ++i) {
			if (this.inventory[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.inventory[i].save(nbttagcompound1);
				nbttaglist.add(nbttagcompound1);
			}
		}

		nbttagcompound.set("Items", nbttaglist);
	}

	public boolean canUpdate() {
		return Platform.isSimulating();
	}

	public void q_() {
		super.q_();
	}

	public void f() {
	}

	public void g() {
	}

	public ItemStack splitWithoutUpdate(int i) {
		return null;
	}

	public ItemStack[] getContents() {
		return this.inventory;
	}
}
