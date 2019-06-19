package ic2.common;

import ic2.api.Direction;
import ic2.api.IEnergyConductor;
import ic2.api.IEnergySink;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.List;

public class TileEntityLuminatorOLD extends TileEntity implements IEnergySink, IEnergyConductor {
  public int energy = 0;
  public int mode = 0;
  public boolean powered = false;
  public int ticker = 0;
  public boolean addedToEnergyNet = false;

  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);
    this.energy = nbttagcompound.getShort("energy");
    this.mode = nbttagcompound.getShort("mode");
    this.powered = nbttagcompound.getBoolean("powered");
  }

  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.setShort("energy", (short) this.energy);
    nbttagcompound.setShort("mode", (short) this.mode);
    nbttagcompound.setBoolean("poweredy", this.powered);
  }

  public void j() {
    if (Platform.isSimulating() && this.addedToEnergyNet) {
      EnergyNet.getForWorld(this.world).removeTileEntity(this);
      this.addedToEnergyNet = false;
    }

    super.j();
  }

  public void q_() {
    if (Platform.isSimulating()) {
      if (!this.addedToEnergyNet) {
        EnergyNet.getForWorld(this.world).addTileEntity(this);
        this.addedToEnergyNet = true;
      }

      ++this.ticker;
      if (this.ticker % 20 == 0) {
        if (this.ticker % 160 == 0) {
          System.out.println("Consume for Mode: " + this.mode);
          byte byte0 = 5;
          switch (this.mode) {
            case 1:
              boolean var2 = true;
            case 2:
              byte0 = 40;
            default:
              if (byte0 > this.energy) {
                this.energy = 0;
                this.powered = false;
                System.out.println("Out of energy");
              }
              else {
                System.out.println("Energized");
                this.energy -= byte0;
                this.powered = true;
              }

              this.updateLightning();
          }
        }

        if (this.powered) {
          this.burnMobs();
        }
      }
    }

  }

  public float getLightLevel() {
    if (this.powered) {
      System.out.println("get powered");
    }

    System.out.println("get unpowered");
    return 0.9375F;
  }

  public void switchStrength() {
    this.mode = (this.mode + 1) % 3;
    this.updateLightning();
  }

  public void updateLightning() {
    System.out.println("Update Lightning");
    this.world.b(EnumSkyBlock.SKY, this.x, this.y, this.z);
    this.world.b(EnumSkyBlock.BLOCK, this.x, this.y, this.z);
  }

  public boolean isAddedToEnergyNet() {
    return this.addedToEnergyNet;
  }

  public boolean acceptsEnergyFrom(TileEntity tileentity, Direction direction) {
    return true;
  }

  public boolean emitsEnergyTo(TileEntity tileentity, Direction direction) {
    return true;
  }

  public double getConductionLoss() {
    return 0.0D;
  }

  public int getInsulationEnergyAbsorption() {
    return 32;
  }

  public int getInsulationBreakdownEnergy() {
    return 33;
  }

  public int getConductorBreakdownEnergy() {
    return 33;
  }

  public void removeInsulation() {
    System.out.println("REmove Insulation");
    this.poof();
  }

  public void removeConductor() {
    System.out.println("REmove Confuctor");
    this.poof();
  }

  public boolean demandsEnergy() {
    return this.energy < this.getMaxEnergy();
  }

  public int injectEnergy(Direction direction, int i) {
    if (i > 32) {
      System.out.println("Injecting > 32");
      this.poof();
      return 0;
    }
    else {
      this.energy += i;
      int j = 0;
      if (this.energy > this.getMaxEnergy()) {
        j = this.energy - this.getMaxEnergy();
        this.energy = this.getMaxEnergy();
      }

      return j;
    }
  }

  public int getMaxEnergy() {
    switch (this.mode) {
      case 1:
        return 20;
      case 2:
        return 80;
      default:
        return 10;
    }
  }

  public void poof() {
    this.world.setTypeId(this.x, this.y, this.z, 0);
    ExplosionIC2 explosionic2 = new ExplosionIC2(this.world, null, 0.5D + (double) this.x, 0.5D + (double) this.y, 0.5D + (double) this.z, 0.5F, 0.85F, 2.0F);
    explosionic2.doExplosion();
  }

  public void burnMobs() {
    int i = this.x;
    int j = this.y;
    int k = this.z;
    boolean flag = false;
    boolean flag1 = false;
    boolean flag2 = false;
    boolean flag3 = false;
    boolean flag4 = false;
    boolean flag5 = false;
    if (this.world.getTypeId(i + 1, j, k) == 0 || this.world.getTypeId(i + 1, j, k) == Block.GLASS.id || this.world.getTypeId(i + 1, j, k) == Ic2Items.reinforcedGlass.id) {
      flag = true;
    }

    if (this.world.getTypeId(i - 1, j, k) == 0 || this.world.getTypeId(i - 1, j, k) == Block.GLASS.id || this.world.getTypeId(i - 1, j, k) == Ic2Items.reinforcedGlass.id) {
      flag1 = true;
    }

    if (this.world.getTypeId(i, j + 1, k) == 0 || this.world.getTypeId(i, j + 1, k) == Block.GLASS.id || this.world.getTypeId(i, j + 1, k) == Ic2Items.reinforcedGlass.id) {
      flag2 = true;
    }

    if (this.world.getTypeId(i, j - 1, k) == 0 || this.world.getTypeId(i, j - 1, k) == Block.GLASS.id || this.world.getTypeId(i, j - 1, k) == Ic2Items.reinforcedGlass.id) {
      flag3 = true;
    }

    if (this.world.getTypeId(i, j, k + 1) == 0 || this.world.getTypeId(i, j, k + 1) == Block.GLASS.id || this.world.getTypeId(i, j, k + 1) == Ic2Items.reinforcedGlass.id) {
      flag4 = true;
    }

    if (this.world.getTypeId(i, j, k - 1) == 0 || this.world.getTypeId(i, j, k - 1) == Block.GLASS.id || this.world.getTypeId(i, j, k - 1) == Ic2Items.reinforcedGlass.id) {
      flag5 = true;
    }

    int l = 0;
    int i1 = 0;
    int j1 = 0;
    int k1 = 0;
    int l1 = 0;
    int i2 = 0;
    if (flag) {
      l = 3;
    }
    else if (flag2 || flag3 || flag4 || flag5) {
      l = 1;
    }

    if (flag1) {
      i1 = 3;
    }
    else if (flag2 || flag3 || flag4 || flag5) {
      i1 = 1;
    }

    if (flag2) {
      j1 = 3;
    }
    else if (flag || flag1 || flag4 || flag5) {
      j1 = 1;
    }

    if (flag3) {
      k1 = 3;
    }
    else if (flag || flag1 || flag4 || flag5) {
      k1 = 1;
    }

    if (flag4) {
      l1 = 3;
    }
    else if (flag2 || flag3 || flag || flag1) {
      l1 = 1;
    }

    if (flag5) {
      i2 = 3;
    }
    else if (flag2 || flag3 || flag || flag1) {
      i2 = 1;
    }

    i1 = i - i1; //Removed int declaration: Already declared.
    k1 = j - k1; //Removed int declaration: Already declared.
    i2 = k - i2; //Removed int declaration: Already declared.
    l = i + l; //Removed int declaration: Already declared.
    j1 = j + j1; //Removed int declaration: Already declared.
    l1 = k + l1; //Removed int declaration: Already declared.
    AxisAlignedBB axisalignedbb = AxisAlignedBB.a(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    List list = this.world.getEntities(null, axisalignedbb.a((double) i, (double) j, (double) k).grow(3.0D, 3.0D, 3.0D));

    for (int j2 = 0; j2 < list.size(); ++j2) {
      Entity entity = (Entity) list.get(j2);
      if (entity instanceof EntityMonster) {
        double d = entity.locX;
        double d1 = entity.locY;
        double d2 = entity.locZ;
        if (d >= (double) i1 && d <= (double) (l + 1) && d1 >= (double) k1 && d1 <= (double) (j1 + 2) && d2 >= (double) i2 && d2 <= (double) (l1 + 1)) {
          Platform.setEntityOnFire(entity, 10);
        }
      }
    }

  }
}
