package ic2.common;

import ic2.api.INetworkDataProvider;
import ic2.api.INetworkUpdateListener;
import ic2.api.IWrenchable;
import ic2.platform.NetworkManager;
import ic2.platform.Platform;
import ic2.platform.TextureIndex;
import net.minecraft.server.*;

import java.util.List;
import java.util.Vector;

public class TileEntityBlock extends TileEntity implements INetworkDataProvider, INetworkUpdateListener, IWrenchable {
  public boolean prevActive = false;
  public short prevFacing = 0;
  private boolean active = false;
  private short facing = 0;
  
  public void m() {
    super.m();
    mod_IC2.addSingleTickCallback(this.world, new ITickCallback() {
      public void tickCallback(World world) {
        if (!TileEntityBlock.this.l() && world != null) {
          TileEntityBlock.this.onCreated();
        }
        else {
          System.out.println(
              "[IC2] " + TileEntityBlock.this + " (" + TileEntityBlock.this.x + "," + TileEntityBlock.this.y + "," +
                  TileEntityBlock.this.z + ") was not added, isInvalid=" + TileEntityBlock.this.l() + ", worldObj=" +
                  world);
        }
  
      }
    });
  }
  
  public void onCreated() {
    if (!Platform.isSimulating()) {
      NetworkManager.requestInitialData(this);
    }
    
  }
  
  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);
    this.prevFacing = this.facing = nbttagcompound.getShort("facing");
  }
  
  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.setShort("facing", this.facing);
  }
  
  public boolean canUpdate() {
    return false;
  }
  
  public boolean getActive() {
    return this.active;
  }
  
  public void setActive(boolean flag) {
    this.active = flag;
    if (this.prevActive != flag) {
      NetworkManager.updateTileEntityField(this, "active");
    }
    
    this.prevActive = flag;
  }
  
  public void setActiveWithoutNotify(boolean flag) {
    this.active = flag;
    this.prevActive = flag;
  }
  
  public short getFacing() {
    return this.facing;
  }
  
  public void setFacing(short word0) {
    this.facing = word0;
    if (this.prevFacing != word0) {
      NetworkManager.updateTileEntityField(this, "facing");
    }
    
    this.prevFacing = word0;
  }
  
  public List getNetworkedFields() {
    Vector vector = new Vector(2);
    vector.add("active");
    vector.add("facing");
    return vector;
  }
  
  public void onNetworkUpdate(String s) {
    if (s.equals("active") && this.prevActive != this.active || s.equals("facing") && this.prevFacing != this.facing) {
      int i = this.world.getTypeId(this.x, this.y, this.z);
      if (i < Block.byId.length && Block.byId[i] != null) {
        Block block = Block.byId[i];
        boolean flag = this.active;
        short word0 = this.facing;
        this.active = this.prevActive;
        this.facing = this.prevFacing;
        int[] ai = new int[6];
  
        for (int j = 0; j < 6; ++j) {
          ai[j] = Platform.getBlockTexture(block, this.world, this.x, this.y, this.z, j);
        }
  
        this.active = flag;
        this.facing = word0;
        boolean flag1 = false;
  
        for (int k = 0; k < 6; ++k) {
          int l = Platform.getBlockTexture(block, this.world, this.x, this.y, this.z, k);
          if (ai[k] != l && TextureIndex.get(i, ai[k]) != TextureIndex.get(i, l)) {
            this.world.notify(this.x, this.y, this.z);
            boolean var10 = true;
            break;
          }
        }
      }
      else {
        System.out.println("[IC2] Invalid TE at " + this.x + "/" + this.y + "/" + this.z + ", no corresponding block");
      }
  
      this.prevActive = this.active;
      this.prevFacing = this.facing;
    }
    
  }
  
  public boolean wrenchCanSetFacing(EntityHuman entityhuman, int i) {
    return false;
  }
  
  public boolean wrenchCanRemove(EntityHuman entityhuman) {
    return true;
  }
  
  public float getWrenchDropRate() {
    return 1.0F;
  }
}
