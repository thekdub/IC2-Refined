package ic2.common;

import ic2.api.Direction;
import ic2.api.IEnergySource;
import ic2.api.IReactorChamber;
import ic2.api.IWrenchable;
import ic2.platform.Platform;
import net.minecraft.server.*;

public class TileEntityReactorChamber extends TileEntity implements IWrenchable, IEnergySource, IInventory, IReactorChamber {
	public boolean addedToEnergyNet = false;

	public void m() {
		super.m();
		mod_IC2.addSingleTickCallback(this.world, new ITickCallback() {
			public void tickCallback(World world) {
				if (!TileEntityReactorChamber.this.l() && world != null) {
					TileEntityReactorChamber.this.onCreated();
				}
				else if (world == null) {
					System.out.println("[IC2] " + TileEntityReactorChamber.this + " (" + TileEntityReactorChamber.this.x + "," + TileEntityReactorChamber.this.y + "," + TileEntityReactorChamber.this.z + ") was not added because worldObj == null!");
				}

			}
		});
	}

	public boolean canUpdate() {
		return false;
	}

	public void onCreated() {
		EnergyNet.getForWorld(this.world).addTileEntity(this);
		this.addedToEnergyNet = true;
	}

	public void j() {
		if (Platform.isSimulating() && this.addedToEnergyNet) {
			EnergyNet.getForWorld(this.world).removeTileEntity(this);
			this.addedToEnergyNet = false;
		}

		super.j();
	}

	public boolean isAddedToEnergyNet() {
		return this.addedToEnergyNet;
	}

	public boolean wrenchCanSetFacing(EntityHuman entityhuman, int i) {
		return false;
	}

	public short getFacing() {
		return 0;
	}

	public void setFacing(short word0) {
	}

	public boolean wrenchCanRemove(EntityHuman entityhuman) {
		return true;
	}

	public float getWrenchDropRate() {
		return 0.8F;
	}

	public int getSize() {
		TileEntityNuclearReactor tileentitynuclearreactor = this.getReactor();
		return tileentitynuclearreactor == null ? 0 : tileentitynuclearreactor.getSize();
	}

	public ItemStack getItem(int i) {
		TileEntityNuclearReactor tileentitynuclearreactor = this.getReactor();
		return tileentitynuclearreactor == null ? null : tileentitynuclearreactor.getItem(i);
	}

	public ItemStack splitStack(int i, int j) {
		TileEntityNuclearReactor tileentitynuclearreactor = this.getReactor();
		return tileentitynuclearreactor == null ? null : tileentitynuclearreactor.splitStack(i, j);
	}

	public void setItem(int i, ItemStack itemstack) {
		TileEntityNuclearReactor tileentitynuclearreactor = this.getReactor();
		if (tileentitynuclearreactor != null) {
			tileentitynuclearreactor.setItem(i, itemstack);
		}
	}

	public String getName() {
		TileEntityNuclearReactor tileentitynuclearreactor = this.getReactor();
		return tileentitynuclearreactor == null ? "Nuclear Reactor" : tileentitynuclearreactor.getName();
	}

	public int getMaxStackSize() {
		TileEntityNuclearReactor tileentitynuclearreactor = this.getReactor();
		return tileentitynuclearreactor == null ? 64 : tileentitynuclearreactor.getMaxStackSize();
	}

	public void setMaxStackSize(int arg0) {
	}

	public boolean a(EntityHuman entityhuman) {
		TileEntityNuclearReactor tileentitynuclearreactor = this.getReactor();
		return tileentitynuclearreactor != null && tileentitynuclearreactor.a(entityhuman);
	}

	public void f() {
		TileEntityNuclearReactor tileentitynuclearreactor = this.getReactor();
		if (tileentitynuclearreactor != null) {
			tileentitynuclearreactor.f();
		}
	}

	public void g() {
		TileEntityNuclearReactor tileentitynuclearreactor = this.getReactor();
		if (tileentitynuclearreactor != null) {
			tileentitynuclearreactor.g();
		}
	}

	public ItemStack splitWithoutUpdate(int i) {
		TileEntityNuclearReactor tileentitynuclearreactor = this.getReactor();
		return tileentitynuclearreactor == null ? null : tileentitynuclearreactor.splitWithoutUpdate(i);
	}

	public boolean emitsEnergyTo(TileEntity tileentity, Direction direction) {
		return true;
	}

	public int getMaxEnergyOutput() {
		return 240 * TileEntityNuclearReactor.pulsePower();
	}

	public int sendEnergy(int i) {
		return EnergyNet.getForWorld(this.world).emitEnergyFrom(this, i);
	}

	public TileEntityNuclearReactor getReactor() {
		TileEntity tileentity = this.world.getTileEntity(this.x + 1, this.y, this.z);
		if (tileentity instanceof TileEntityNuclearReactor) {
			return (TileEntityNuclearReactor) tileentity;
		}
		else {
			tileentity = this.world.getTileEntity(this.x - 1, this.y, this.z);
			if (tileentity instanceof TileEntityNuclearReactor) {
				return (TileEntityNuclearReactor) tileentity;
			}
			else {
				tileentity = this.world.getTileEntity(this.x, this.y + 1, this.z);
				if (tileentity instanceof TileEntityNuclearReactor) {
					return (TileEntityNuclearReactor) tileentity;
				}
				else {
					tileentity = this.world.getTileEntity(this.x, this.y - 1, this.z);
					if (tileentity instanceof TileEntityNuclearReactor) {
						return (TileEntityNuclearReactor) tileentity;
					}
					else {
						tileentity = this.world.getTileEntity(this.x, this.y, this.z + 1);
						if (tileentity instanceof TileEntityNuclearReactor) {
							return (TileEntityNuclearReactor) tileentity;
						}
						else {
							tileentity = this.world.getTileEntity(this.x, this.y, this.z - 1);
							if (tileentity instanceof TileEntityNuclearReactor) {
								return (TileEntityNuclearReactor) tileentity;
							}
							else {
								Block block = Block.byId[this.world.getTypeId(this.x, this.y, this.z)];
								if (block != null) {
									block.doPhysics(this.world, this.x, this.y, this.z, block.id);
								}

								return null;
							}
						}
					}
				}
			}
		}
	}

	public ItemStack[] getContents() {
		return null;
	}
}
