package ic2.common;

import ic2.api.Direction;
import ic2.api.*;
import ic2.platform.NetworkManager;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.List;
import java.util.Vector;

public class TileEntityCable extends TileEntityBlock implements IEnergyConductor, INetworkTileEntityEventListener {
	private static final int EventRemoveConductor = 0;
	public short cableType = 0;
	public short color = 0;
	public byte foamed = 0;
	public byte foamColor = 0;
	public boolean addedToEnergyNet = false;

	public TileEntityCable(short word0) {
		this.cableType = word0;
	}

	public TileEntityCable() {
	}

	public static float getCableThickness(int i) {
		float f = 1.0F;
		switch (i) {
			case 0:
				f = 6.0F;
				break;
			case 1:
				f = 4.0F;
				break;
			case 2:
				f = 3.0F;
				break;
			case 3:
				f = 5.0F;
				break;
			case 4:
				f = 6.0F;
				break;
			case 5:
				f = 6.0F;
				break;
			case 6:
				f = 8.0F;
				break;
			case 7:
				f = 10.0F;
				break;
			case 8:
				f = 12.0F;
				break;
			case 9:
				f = 4.0F;
				break;
			case 10:
				f = 5.0F;
				break;
			case 11:
				f = 8.0F;
				break;
			case 12:
				f = 8.0F;
				break;
			case 13:
				f = 16.0F;
		}

		return f / 16.0F;
	}

	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		this.cableType = nbttagcompound.getShort("cableType");
		this.color = nbttagcompound.getShort("color");
		this.foamColor = nbttagcompound.getByte("foamColor");
		byte byte0 = nbttagcompound.getByte("foamed");
		if (byte0 == 1) {
			this.changeFoam(byte0, true);
		}
		else {
			this.foamed = byte0;
		}

	}

	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setShort("cableType", this.cableType);
		nbttagcompound.setShort("color", this.color);
		nbttagcompound.setByte("foamed", this.foamed);
		nbttagcompound.setByte("foamColor", this.foamColor);
	}

	public void onCreated() {
		super.onCreated();
		if (Platform.isSimulating()) {
			try {
				EnergyNet.getForWorld(this.world).addTileEntity(this);
				this.addedToEnergyNet = true;
			} catch (NullPointerException var2) {
			}
		}

	}

	public void j() {
		if (Platform.isSimulating() && this.addedToEnergyNet) {
			EnergyNet.getForWorld(this.world).removeTileEntity(this);
			this.addedToEnergyNet = false;
		}

		super.j();
	}

	public boolean changeColor(int i) {
		if (this.foamed == 0 && (this.color == i || this.cableType == 1 || this.cableType == 2 || this.cableType == 5 || this.cableType == 10 || this.cableType == 11 || this.cableType == 12) || this.foamed > 0 && this.foamColor == i) {
			return false;
		}
		else {
			if (Platform.isSimulating()) {
				if (this.foamed == 0) {
					if (this.addedToEnergyNet) {
						EnergyNet.getForWorld(this.world).removeTileEntity(this);
					}

					this.addedToEnergyNet = false;
					this.color = (short) i;
					EnergyNet.getForWorld(this.world).addTileEntity(this);
					this.addedToEnergyNet = true;
					NetworkManager.updateTileEntityField(this, "color");
				}
				else {
					this.foamColor = (byte) i;
					NetworkManager.updateTileEntityField(this, "foamColor");
				}
			}

			return true;
		}
	}

	public boolean changeFoam(byte byte0) {
		return this.changeFoam(byte0, false);
	}

	public boolean tryAddInsulation() {
		short word0;
		switch (this.cableType) {
			case 1:
				word0 = 0;
				break;
			case 2:
				word0 = 3;
				break;
			case 3:
				word0 = 4;
				break;
			case 4:
			default:
				word0 = this.cableType;
				break;
			case 5:
				word0 = 6;
				break;
			case 6:
				word0 = 7;
				break;
			case 7:
				word0 = 8;
		}

		if (word0 != this.cableType) {
			if (Platform.isSimulating()) {
				this.changeType(word0);
			}

			return true;
		}
		else {
			return false;
		}
	}

	public boolean tryRemoveInsulation() {
		short word0;
		switch (this.cableType) {
			case 0:
				word0 = 1;
				break;
			case 1:
			case 2:
			case 5:
			default:
				word0 = this.cableType;
				break;
			case 3:
				word0 = 2;
				break;
			case 4:
				word0 = 3;
				break;
			case 6:
				word0 = 5;
				break;
			case 7:
				word0 = 6;
				break;
			case 8:
				word0 = 7;
		}

		if (word0 != this.cableType) {
			if (Platform.isSimulating()) {
				this.changeType(word0);
			}

			return true;
		}
		else {
			return false;
		}
	}

	public void changeType(short word0) {
		this.world.setRawData(this.x, this.y, this.z, word0);
		if (this.addedToEnergyNet) {
			EnergyNet.getForWorld(this.world).removeTileEntity(this);
		}

		this.addedToEnergyNet = false;
		this.cableType = word0;
		EnergyNet.getForWorld(this.world).addTileEntity(this);
		this.addedToEnergyNet = true;
		NetworkManager.updateTileEntityField(this, "cableType");
	}

	public boolean wrenchCanSetFacing(EntityHuman entityhuman, int i) {
		return false;
	}

	public boolean wrenchCanRemove(EntityHuman entityhuman) {
		return false;
	}

	public boolean isAddedToEnergyNet() {
		return this.addedToEnergyNet;
	}

	public boolean acceptsEnergyFrom(TileEntity tileentity, Direction direction) {
		return !(tileentity instanceof TileEntityCable) || this.canInteractWithCable((TileEntityCable) tileentity);
	}

	public boolean emitsEnergyTo(TileEntity tileentity, Direction direction) {
		if (tileentity instanceof TileEntityCable && !this.canInteractWithCable((TileEntityCable) tileentity)) {
			return false;
		}
		else {
			return !(tileentity instanceof TileEntityLuminator) || ((TileEntityLuminator) tileentity).canCableConnectFrom(this.x, this.y, this.z);
		}
	}

	public boolean canInteractWith(TileEntity tileentity) {
		if (tileentity instanceof TileEntityCable) {
			return this.canInteractWithCable((TileEntityCable) tileentity);
		}
		else if (tileentity instanceof TileEntityLuminator) {
			return ((TileEntityLuminator) tileentity).canCableConnectFrom(this.x, this.y, this.z);
		}
		else {
			return tileentity instanceof IEnergySink || tileentity instanceof IEnergySource || tileentity instanceof IEnergyConductor;
		}
	}

	public boolean canInteractWithCable(TileEntityCable tileentitycable) {
		return this.color == 0 || tileentitycable.color == 0 || this.color == tileentitycable.color;
	}

	public float getCableThickness() {
		return this.foamed == 2 ? 1.0F : getCableThickness(this.cableType);
	}

	public double getConductionLoss() {
		switch (this.cableType) {
			case 0:
				return 0.2D;
			case 1:
				return 0.3D;
			case 2:
				return 0.5D;
			case 3:
				return 0.45D;
			case 4:
				return 0.4D;
			case 5:
				return 1.0D;
			case 6:
				return 0.95D;
			case 7:
				return 0.9D;
			case 8:
				return 0.8D;
			case 9:
				return 0.025D;
			case 10:
				return 0.025D;
			case 11:
				return 0.5D;
			case 12:
				return 0.5D;
			default:
				return 0.025D;
		}
	}

	public int getInsulationEnergyAbsorption() {
		switch (this.cableType) {
			case 0:
				return 32;
			case 1:
				return 8;
			case 2:
				return 8;
			case 3:
				return 32;
			case 4:
				return 128;
			case 5:
				return 0;
			case 6:
				return 128;
			case 7:
				return 512;
			case 8:
				return 9001;
			case 9:
				return 9001;
			case 10:
				return 3;
			case 11:
				return 9001;
			case 12:
				return 9001;
			default:
				return 0;
		}
	}

	public int getInsulationBreakdownEnergy() {
		return 9001;
	}

	public int getConductorBreakdownEnergy() {
		switch (this.cableType) {
			case 0:
				return 33;
			case 1:
				return 33;
			case 2:
				return 129;
			case 3:
				return 129;
			case 4:
				return 129;
			case 5:
				return 2049;
			case 6:
				return 2049;
			case 7:
				return 2049;
			case 8:
				return 2049;
			case 9:
				return 513;
			case 10:
				return 6;
			case 11:
				return 2049;
			case 12:
				return 2049;
			default:
				return 0;
		}
	}

	public void removeInsulation() {
	}

	public void removeConductor() {
		this.world.setTypeId(this.x, this.y, this.z, 0);
		NetworkManager.initiateTileEntityEvent(this, 0, true);
	}

	public List getNetworkedFields() {
		Vector vector = new Vector();
		vector.add("cableType");
		vector.add("color");
		vector.add("foamed");
		vector.add("foamColor");
		vector.addAll(super.getNetworkedFields());
		return vector;
	}

	public void onNetworkUpdate(String s) {
		if (s.equals("cableType") || s.equals("color") || s.equals("foamed") || s.equals("foamColor")) {
			this.world.notify(this.x, this.y, this.z);
		}

		super.onNetworkUpdate(s);
	}

	public void onNetworkEvent(int i) {
		switch (i) {
			case 0:
				this.world.makeSound((double) ((float) this.x + 0.5F), (double) ((float) this.y + 0.5F), (double) ((float) this.z + 0.5F), "random.fizz", 0.5F, 2.6F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.8F);

				for (int j = 0; j < 8; ++j) {
					this.world.a("largesmoke", (double) this.x + Math.random(), (double) this.y + 1.2D, (double) this.z + Math.random(), 0.0D, 0.0D, 0.0D);
				}

				return;
			default:
				Platform.displayError("An unknown event type was received over multiplayer.\nThis could happen due to corrupted data or a bug.\n\n(Technical information: event ID " + i + ", tile entity below)\n" + "T: " + this + " (" + this.x + "," + this.y + "," + this.z + ")");
		}
	}

	public float getWrenchDropRate() {
		return 0.0F;
	}

	private boolean changeFoam(byte byte0, boolean flag) {
		if (this.foamed == byte0) {
			return false;
		}
		else {
			if (Platform.isSimulating()) {
				this.foamed = byte0;
				if (byte0 == 1) {
					this.foamColor = 7;
					mod_IC2.addContinuousTickCallback(this.world, new ITickCallback() {
						public void tickCallback(World world) {
							if (TileEntityCable.this.l() || TileEntityCable.this.foamed != 1 || world.random.nextInt(500) == 0 && world.getLightLevel(TileEntityCable.this.x, TileEntityCable.this.y, TileEntityCable.this.z) * 6 >= world.random.nextInt(1000)) {
								TileEntityCable.this.changeFoam((byte) 2);
								mod_IC2.removeContinuousTickCallback(world, this);
							}

						}
					});
				}
				else if (byte0 == 2) {
					this.world.setData(this.x, this.y, this.z, 13);
					NetworkManager.announceBlockUpdate(this.world, this.x, this.y, this.z);
				}

				if (!flag) {
					NetworkManager.updateTileEntityField(this, "foamed");
				}
			}

			return true;
		}
	}
}
