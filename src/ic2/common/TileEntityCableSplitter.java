package ic2.common;

import ic2.platform.Platform;

public class TileEntityCableSplitter extends TileEntityCable {
	public static final int tickRate = 20;
	public int ticksUntilNextUpdate = 0;

	public TileEntityCableSplitter(short word0) {
		super(word0);
	}

	public TileEntityCableSplitter() {
	}

	public boolean canUpdate() {
		return Platform.isSimulating();
	}

	public void q_() {
		if (this.ticksUntilNextUpdate == 0) {
			this.ticksUntilNextUpdate = 20;
			if (this.world.isBlockPowered(this.x, this.y, this.z) == this.addedToEnergyNet) {
				if (this.addedToEnergyNet) {
					EnergyNet.getForWorld(this.world).removeTileEntity(this);
					this.addedToEnergyNet = false;
				}
				else {
					EnergyNet.getForWorld(this.world).addTileEntity(this);
					this.addedToEnergyNet = true;
				}
			}

			this.setActive(this.addedToEnergyNet);
		}

		--this.ticksUntilNextUpdate;
	}
}
