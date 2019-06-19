package ic2.common;

import ic2.platform.Platform;
import net.minecraft.server.NBTTagCompound;

public class TileEntityCableDetector extends TileEntityCable {
  public static int tickRate = 20;
  public long lastValue = -1L;
  public int ticker = 0;

  public TileEntityCableDetector(short word0) {
    super(word0);
  }

  public TileEntityCableDetector() {
  }

  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);
    this.setActiveWithoutNotify(nbttagcompound.getBoolean("active"));
  }

  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.setBoolean("active", this.getActive());
  }

  public boolean canUpdate() {
    return Platform.isSimulating();
  }

  public void q_() {
    super.q_();
    if (this.ticker++ % tickRate == 0) {
      long l = EnergyNet.getForWorld(this.world).getTotalEnergyConducted(this);
      if (this.lastValue != -1L) {
        if (l > this.lastValue) {
          if (!this.getActive()) {
            this.setActive(true);
            this.world.applyPhysics(this.x, this.y, this.z, this.world.getTypeId(this.x, this.y, this.z));
          }
        }
        else if (this.getActive()) {
          this.setActive(false);
          this.world.applyPhysics(this.x, this.y, this.z, this.world.getTypeId(this.x, this.y, this.z));
        }
      }

      this.lastValue = l;
    }

  }
}
