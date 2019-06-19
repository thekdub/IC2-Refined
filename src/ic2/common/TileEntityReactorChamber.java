package ic2.common;

import ic2.api.Direction;
import ic2.api.IEnergySource;
import ic2.api.IReactorChamber;
import ic2.api.IWrenchable;
import ic2.platform.Platform;
import net.minecraft.server.*;

public class TileEntityReactorChamber extends TileEntity implements IWrenchable, IEnergySource, IInventory, IReactorChamber {
  public boolean addedToEnergyNet = false;
  public TileEntityNuclearReactor reactor;

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
    if (reactor == null)
      getReactor();
    return reactor == null ? 0 : reactor.getSize();
  }

  public ItemStack getItem(int i) {
    if (reactor == null)
      getReactor();
    return reactor == null ? null : reactor.getItem(i);
  }

  public ItemStack splitStack(int i, int j) {
    if (reactor == null)
      getReactor();
    return reactor == null ? null : reactor.splitStack(i, j);
  }

  public void setItem(int i, ItemStack itemstack) {
    if (reactor == null)
      getReactor();
    if (reactor != null) {
      reactor.setItem(i, itemstack);
    }
  }

  public String getName() {
    if (reactor == null)
      getReactor();
    return reactor == null ? "Nuclear Reactor" : reactor.getName();
  }

  public int getMaxStackSize() {
    if (reactor == null)
      getReactor();
    return reactor == null ? 64 : reactor.getMaxStackSize();
  }

  public void setMaxStackSize(int arg0) {
  }

  public boolean a(EntityHuman entityhuman) {
    if (reactor == null)
      getReactor();
    return reactor != null && reactor.a(entityhuman);
  }

  public void f() {
    if (reactor == null)
      getReactor();
    if (reactor != null) {
      reactor.f();
    }
  }

  public void g() {
    if (reactor == null)
      getReactor();
    if (reactor != null) {
      reactor.g();
    }
  }

  public ItemStack splitWithoutUpdate(int i) {
    if (reactor == null)
      getReactor();
    return reactor == null ? null : reactor.splitWithoutUpdate(i);
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

  public void getReactor() {
    TileEntity tileEntity = this.world.getTileEntity(this.x + 1, this.y, this.z);
    if (tileEntity instanceof TileEntityNuclearReactor) {
      reactor = (TileEntityNuclearReactor) tileEntity;
      return;
    }
    tileEntity = this.world.getTileEntity(this.x - 1, this.y, this.z);
    if (tileEntity instanceof TileEntityNuclearReactor) {
      reactor = (TileEntityNuclearReactor) tileEntity;
      return;
    }
    tileEntity = this.world.getTileEntity(this.x, this.y + 1, this.z);
    if (tileEntity instanceof TileEntityNuclearReactor) {
      reactor = (TileEntityNuclearReactor) tileEntity;
      return;
    }
    tileEntity = this.world.getTileEntity(this.x, this.y - 1, this.z);
    if (tileEntity instanceof TileEntityNuclearReactor) {
      reactor = (TileEntityNuclearReactor) tileEntity;
      return;
    }
    tileEntity = this.world.getTileEntity(this.x, this.y, this.z + 1);
    if (tileEntity instanceof TileEntityNuclearReactor) {
      reactor = (TileEntityNuclearReactor) tileEntity;
      return;
    }
    tileEntity = this.world.getTileEntity(this.x, this.y, this.z - 1);
    if (tileEntity instanceof TileEntityNuclearReactor) {
      reactor = (TileEntityNuclearReactor) tileEntity;
      return;
    }
  }

  public ItemStack[] getContents() {
    return null;
  }
}
