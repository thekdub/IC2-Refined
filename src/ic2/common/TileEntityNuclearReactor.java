package ic2.common;

import ic2.api.Direction;
import ic2.api.IEnergySource;
import ic2.api.IReactor;
import ic2.platform.AudioManager;
import ic2.platform.AudioSource;
import ic2.platform.NetworkManager;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.List;
import java.util.Random;

public class TileEntityNuclearReactor extends TileEntityMachine implements IEnergySource, IHasGui, IReactor {
  public static Random randomizer = new Random();
  public short output = 0;
  public int updateTicker;
  public int heat = 0;
  public boolean addedToEnergyNet = false;
  public AudioSource audioSourceMain;
  public AudioSource audioSourceGeiger;
  private short lastOutput = 0;

  public TileEntityNuclearReactor() {
    super(54);
    this.updateTicker = randomizer.nextInt(this.tickRate());
  }

  public static boolean isUsefulItem(ItemStack itemstack) {
    if (itemstack == null) {
      return false;
    }
    else {
      int i = itemstack.id;
      return i == Ic2Items.uraniumCell.id || i == Ic2Items.coolingCell.id || i == Ic2Items.integratedReactorPlating.id || i == Block.ICE.id || i == Item.WATER_BUCKET.id || i == Item.LAVA_BUCKET.id || i == Item.BUCKET.id || i == Ic2Items.integratedHeatDisperser.id || i == Ic2Items.depletedIsotopeCell.id || i == Ic2Items.reEnrichedUraniumCell.id || i == Ic2Items.nearDepletedUraniumCell.id;
    }
  }

  public static int pulsePower() {
    return mod_IC2.energyGeneratorNuclear;
  }

  public void j() {
    if (Platform.isSimulating() && this.addedToEnergyNet) {
      EnergyNet.getForWorld(this.world).removeTileEntity(this);
      this.addedToEnergyNet = false;
    }

    if (Platform.isRendering()) {
      AudioManager.removeSources(this);
      this.audioSourceMain = null;
      this.audioSourceGeiger = null;
    }

    super.j();
  }

  public String getName() {
    return "Nuclear Reactor";
  }

  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);

    try {
      this.heat = nbttagcompound.getInt("heat");
    } catch (Exception var3) {
      this.heat = nbttagcompound.getShort("heat");
    }

    this.output = nbttagcompound.getShort("output");
  }

  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.setInt("heat", this.heat);
    nbttagcompound.setShort("output", this.output);
  }

  public void q_() {
    super.q_();
    if (!this.addedToEnergyNet) {
      EnergyNet.getForWorld(this.world).addTileEntity(this);
      this.addedToEnergyNet = true;
    }

    this.sendEnergy(this.output);
    if (this.updateTicker++ % this.tickRate() == 0) {
      if (!this.world.areChunksLoaded(this.x, this.y, this.z, 2)) {
        this.output = 0;
      }
      else {
        this.dropAllUnfittingStuff();
        if (this.heat > 0) {
          this.heat -= this.coolReactorFromOutside();
          if (this.heat <= 0) {
            this.heat = 0;
          }
          else if (this.calculateHeatEffects()) {
            return;
          }
        }

        this.output = 0;
        this.processChambers();
        this.setActive(this.heat >= 1000 || this.output > 0);
        this.update();
      }

      NetworkManager.updateTileEntityField(this, "output");
    }
  }

  public void dropAllUnfittingStuff() {
    short word0 = this.getReactorSize();

    for (int i = 0; i < 9; ++i) {
      for (int j = 0; j < 6; ++j) {
        ItemStack itemstack = this.getMatrixCoord(i, j);
        if (itemstack != null) {
          if (itemstack.count <= 0) {
            this.setMatrixCoord(i, j, null);
          }
          else if (i >= word0 || !isUsefulItem(itemstack)) {
            this.eject(itemstack);
            this.setMatrixCoord(i, j, null);
          }
        }
      }
    }

  }

  public void eject(ItemStack itemstack) {
    if (Platform.isSimulating() && itemstack != null) {
      float f = 0.7F;
      double d = (double) (this.world.random.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
      double d1 = (double) (this.world.random.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
      double d2 = (double) (this.world.random.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
      EntityItem entityitem = new EntityItem(this.world, (double) this.x + d, (double) this.y + d1, (double) this.z + d2, itemstack);
      entityitem.pickupDelay = 10;
      this.world.addEntity(entityitem);
    }
  }

  public int coolReactorFromOutside() {
    int i = 1;
    int j = 0;

    for (int k = this.x - 1; k <= this.x + 1; ++k) {
      for (int l = this.y - 1; l <= this.y + 1; ++l) {
        for (int i1 = this.z - 1; i1 <= this.z + 1; ++i1) {
          if (this.world.getTypeId(k, l, i1) == Ic2Items.reactorChamber.id) {
            i += 2;
          }

          if (this.world.getMaterial(k, l, i1) == Material.WATER) {
            ++i;
          }

          if (this.world.getTypeId(k, l, i1) == 0) {
            ++j;
          }

          if (this.world.getTypeId(k, l, i1) == Block.FIRE.id) {
            j -= 2;
          }

          if (this.world.getMaterial(k, l, i1) == Material.LAVA) {
            i -= 3;
          }
        }
      }
    }

    i += j / 4;
    if (i < 0) {
      return 0;
    }
    else {
      return i;
    }
  }

  public boolean calculateHeatEffects() {
    if (this.heat >= 4000 && Platform.isSimulating() && mod_IC2.explosionPowerReactorMax > 0.0F) {
      short word0 = this.getReactorSize();
      int i = 10000;
      i = i + 1000 * (word0 - 3); //Removed int declaration: Already declared.

      for (int j = 0; j < 6; ++j) {
        for (int k = 0; k < word0; ++k) {
          if (this.getMatrixCoord(k, j) != null && this.getMatrixCoord(k, j).id == Ic2Items.integratedReactorPlating.id) {
            i += 100;
          }
        }
      }

      float f = (float) this.heat / (float) i;
      if (f >= 1.0F) {
        this.explode();
        return true;
      }
      else {
        int k1;
        Material material1;
        int[] ai2;
        if (f >= 0.85F && this.world.random.nextFloat() <= 4.0F * (f - 0.7F)) {
          ai2 = this.getRandCoord(2);
          if (ai2 != null) {
            k1 = this.world.getTypeId(ai2[0], ai2[1], ai2[2]);
            if (k1 == 0) {
              this.world.setTypeId(ai2[0], ai2[1], ai2[2], Block.FIRE.id);
            }
            else {
              material1 = Block.byId[k1].material;
              if (k1 != Block.BEDROCK.id) {
                if (material1 != Material.STONE && material1 != Material.ORE && material1 != Material.LAVA && material1 != Material.EARTH && material1 != Material.CLAY) {
                  this.world.setTypeId(ai2[0], ai2[1], ai2[2], Block.FIRE.id);
                }
                else {
                  this.world.setTypeIdAndData(ai2[0], ai2[1], ai2[2], Block.LAVA.id, 15);
                }
              }
            }
          }
        }

        if (f >= 0.7F) {
          List list = this.world.a(EntityLiving.class, AxisAlignedBB.a((double) (this.x - 3), (double) (this.y - 3), (double) (this.z - 3), (double) (this.x + 4), (double) (this.y + 4), (double) (this.z + 4)));

          for (k1 = 0; k1 < list.size(); ++k1) {
            Entity entity = (Entity) list.get(k1);
            entity.damageEntity(IC2DamageSource.radiation, 1);
          }
        }

        if (f >= 0.5F) {
          ai2 = this.getRandCoord(2);
          if (ai2 != null) {
            k1 = this.world.getTypeId(ai2[0], ai2[1], ai2[2]);
            if (k1 > 0 && Block.byId[k1].material == Material.WATER) {
              this.world.setTypeId(ai2[0], ai2[1], ai2[2], 0);
            }
          }
        }

        if (f >= 0.4F && this.world.random.nextFloat() <= 1.5F * (f - 0.4F)) {
          ai2 = this.getRandCoord(2);
          if (ai2 != null) {
            k1 = this.world.getTypeId(ai2[0], ai2[1], ai2[2]);
            if (k1 > 0) {
              material1 = Block.byId[k1].material;
              if (material1 == Material.WOOD || material1 == Material.LEAVES || material1 == Material.CLOTH) {
                this.world.setTypeId(ai2[0], ai2[1], ai2[2], Block.FIRE.id);
              }
            }
          }
        }

        return false;
      }
    }
    else {
      return false;
    }
  }

  public int[] getRandCoord(int i) {
    if (i <= 0) {
      return null;
    }
    else {
      int[] ai = new int[]{this.x + this.world.random.nextInt(2 * i + 1) - i, this.y + this.world.random.nextInt(2 * i + 1) - i, this.z + this.world.random.nextInt(2 * i + 1) - i};
      return ai[0] == this.x && ai[1] == this.y && ai[2] == this.z ? null : ai;
    }
  }

  public void processChambers() {
    short word0 = this.getReactorSize();

    for (int i = 0; i < 6; ++i) {
      for (int j = 0; j < word0; ++j) {
        this.processChamber(j, i);
      }
    }

  }

  public void processChamber(int i, int j) {
    if (this.getMatrixCoord(i, j) != null) {
      int k = this.getMatrixCoord(i, j).id;
      if (k == Ic2Items.coolingCell.id && this.getMatrixCoord(i, j).getData() > 0) {
        this.damageReactorItem(this.getMatrixCoord(i, j), -1);
      }

      if (k == Ic2Items.integratedReactorPlating.id && this.getMatrixCoord(i, j).getData() > 0 && this.world.random.nextInt(10) == 0) {
        this.damageReactorItem(this.getMatrixCoord(i, j), -1);
      }

      if (k == Ic2Items.nearDepletedUraniumCell.id || k == Ic2Items.depletedIsotopeCell.id || k == Ic2Items.reEnrichedUraniumCell.id) {
        ++this.heat;
      }

      if (k == Item.WATER_BUCKET.id && this.heat > 4000) {
        this.heat -= 250;
        this.getMatrixCoord(i, j).id = Item.BUCKET.id;
      }

      if (k == Item.LAVA_BUCKET.id) {
        this.heat += 2000;
        this.getMatrixCoord(i, j).id = Item.BUCKET.id;
      }

      if (k == Block.ICE.id && this.heat > 300) {
        this.heat -= 300;
        --this.getMatrixCoord(i, j).count;
        if (this.getMatrixCoord(i, j).count <= 0) {
          this.setMatrixCoord(i, j, null);
        }
      }

      if (k == Ic2Items.integratedHeatDisperser.id) {
        this.disperseHeat(i, j);
      }

      if (k == Ic2Items.uraniumCell.id && this.produceEnergy()) {
        this.generateEnergy(i, j);
      }

    }
  }

  public void disperseHeat(int i, int j) {
    this.switchHeat(i, j, i - 1, j);
    this.switchHeat(i, j, i + 1, j);
    this.switchHeat(i, j, i, j - 1);
    this.switchHeat(i, j, i, j + 1);
    int k = (this.getMatrixCoord(i, j).getData() - this.heat + 1) / 2;
    if (k > 0) {
      if (k > 25) {
        k = 25;
      }

      this.heat += k;
      this.damageReactorItem(this.getMatrixCoord(i, j), -1 * k);
    }
    else {
      k *= -1;
      if (k > 25) {
        k = 25;
      }

      this.heat -= k;
      this.damageReactorItem(this.getMatrixCoord(i, j), k);
    }

  }

  public void switchHeat(int i, int j, int k, int l) {
    if (this.getMatrixCoord(k, l) != null) {
      int i1 = this.getMatrixCoord(k, l).id;
      if (i1 == Ic2Items.coolingCell.id || i1 == Ic2Items.integratedReactorPlating.id) {
        int j1 = this.getMatrixCoord(i, j).getData();
        int k1 = this.getMatrixCoord(k, l).getData();
        int l1 = (j1 - k1) / 2;
        if (l1 > 0) {
          if (l1 > 6) {
            l1 = 6;
          }

          this.getMatrixCoord(i, j).setData(j1 - l1);
          if (i1 == Ic2Items.coolingCell.id) {
            this.damageReactorItem(this.getMatrixCoord(k, l), l1);
          }
          else {
            this.spreadHeat(k, l, l1, false);
          }
        }
        else {
          l1 *= -1;
          if (l1 > 6) {
            l1 = 6;
          }

          this.damageReactorItem(this.getMatrixCoord(i, j), l1);
          this.getMatrixCoord(k, l).setData(k1 - l1);
        }

      }
    }
  }

  public void generateEnergy(int i, int j) {
    int k = 1 + this.isUranium(i + 1, j) + this.isUranium(i - 1, j) + this.isUranium(i, j + 1) + this.isUranium(i, j - 1);
    this.output = (short) (this.output + k * pulsePower());

    for (k += this.enrichDepleted(i + 1, j) + this.enrichDepleted(i - 1, j) + this.enrichDepleted(i, j + 1) + this.enrichDepleted(i, j - 1); k > 0; --k) {
      int l = this.canTakeHeat(i + 1, j, true, true) + this.canTakeHeat(i - 1, j, true, true) + this.canTakeHeat(i, j + 1, true, true) + this.canTakeHeat(i, j - 1, true, true);
      byte byte0;
      switch (l) {
        case 2:
          byte0 = 4;
          break;
        case 3:
          byte0 = 2;
          break;
        case 4:
          byte0 = 1;
          break;
        default:
          byte0 = 10;
      }

      if (l == 0) {
        this.heat += byte0;
      }
      else {
        this.giveHeatTo(i + 1, j, byte0);
        this.giveHeatTo(i - 1, j, byte0);
        this.giveHeatTo(i, j + 1, byte0);
        this.giveHeatTo(i, j - 1, byte0);
      }
    }

    if (this.getMatrixCoord(i, j).getData() == 9999 && this.world.random.nextInt(3) == 0) {
      this.setMatrixCoord(i, j, Ic2Items.nearDepletedUraniumCell.cloneItemStack());
    }
    else {
      this.damageReactorItem(this.getMatrixCoord(i, j), 1);
    }

  }

  public int isUranium(int i, int j) {
    return this.getMatrixCoord(i, j) != null && this.getMatrixCoord(i, j).id == Ic2Items.uraniumCell.id ? 1 : 0;
  }

  public int enrichDepleted(int i, int j) {
    if (this.getMatrixCoord(i, j) != null && this.getMatrixCoord(i, j).id == Ic2Items.depletedIsotopeCell.id) {
      byte byte0 = 8;
      if (this.heat >= 3000) {
        byte0 = 4;
      }

      if (this.heat >= 6000) {
        byte0 = 2;
      }

      if (this.heat >= 9000) {
        byte0 = 1;
      }

      if (this.world.random.nextInt(byte0) != 0) {
        return 1;
      }
      else {
        if (this.getMatrixCoord(i, j).getData() <= 0) {
          this.setMatrixCoord(i, j, Ic2Items.reEnrichedUraniumCell.cloneItemStack());
        }
        else {
          this.damageReactorItem(this.getMatrixCoord(i, j), -2);
        }

        return 1;
      }
    }
    else {
      return 0;
    }
  }

  public int canTakeHeat(int i, int j, boolean flag, boolean flag1) {
    if (this.getMatrixCoord(i, j) == null) {
      return 0;
    }
    else {
      int k = this.getMatrixCoord(i, j).id;
      return k == Ic2Items.coolingCell.id || k == Ic2Items.integratedReactorPlating.id && flag || k == Ic2Items.integratedHeatDisperser.id && flag1 ? 1 : 0;
    }
  }

  public void giveHeatTo(int i, int j, int k) {
    if (this.canTakeHeat(i, j, true, true) != 0) {
      if (this.getMatrixCoord(i, j).id == Ic2Items.integratedReactorPlating.id) {
        this.spreadHeat(i, j, k, true);
      }
      else {
        this.damageReactorItem(this.getMatrixCoord(i, j), k);
      }

    }
  }

  public void spreadHeat(int i, int j, int k, boolean flag) {
    int l = this.canTakeHeat(i + 1, j, flag, false) + this.canTakeHeat(i - 1, j, flag, false) + this.canTakeHeat(i, j + 1, flag, false) + this.canTakeHeat(i, j - 1, flag, false);
    if (l == 0) {
      this.damageReactorItem(this.getMatrixCoord(i, j), k);
    }
    else {
      while (k % l != 0 && this.getMatrixCoord(i, j).getData() > 0) {
        ++k;
        this.damageReactorItem(this.getMatrixCoord(i, j), -1);
      }

      int i1 = k / l;
      k -= i1 * l;
      if (k > 0) {
        this.damageReactorItem(this.getMatrixCoord(i, j), k);
      }

      this.spreadHeatTo(i - 1, j, i1, flag);
      this.spreadHeatTo(i + 1, j, i1, flag);
      this.spreadHeatTo(i, j - 1, i1, flag);
      this.spreadHeatTo(i, j + 1, i1, flag);
    }
  }

  public void spreadHeatTo(int i, int j, int k, boolean flag) {
    if (this.canTakeHeat(i, j, flag, false) != 0) {
      if (this.getMatrixCoord(i, j).id == Ic2Items.integratedReactorPlating.id && flag) {
        this.spreadHeat(i, j, k, false);
      }
      else {
        this.damageReactorItem(this.getMatrixCoord(i, j), k);
      }

    }
  }

  public boolean produceEnergy() {
    return !this.world.isBlockIndirectlyPowered(this.x, this.y, this.z);
  }

  public ItemStack getMatrixCoord(int i, int j) {
    return i >= 0 && i < 9 && j >= 0 && j < 6 ? super.getItem(i + j * 9) : null;
  }

  public void damageReactorItem(ItemStack itemstack, int i) {
    if (itemstack.d()) {
      itemstack.setData(itemstack.getData() + i);
      if (itemstack.getData() > itemstack.i()) {
        --itemstack.count;
        if (itemstack.count < 0) {
          itemstack.count = 0;
        }

        itemstack.setData(0);
      }

    }
  }

  public ItemStack getItem(int i) {
    int j = i % 9;
    short word0 = this.getReactorSize();
    return j >= word0 ? this.getMatrixCoord(word0 - 1, i / 9) : super.getItem(i);
  }

  public void setMatrixCoord(int i, int j, ItemStack itemstack) {
    if (i >= 0 && i < 9 && j >= 0 && j < 6) {
      super.setItem(i + j * 9, itemstack);
    }
  }

  public void setItem(int i, ItemStack itemstack) {
    int j = i % 9;
    short word0 = this.getReactorSize();
    if (j >= word0) {
      this.setMatrixCoord(word0 - 1, i / 9, itemstack);
    }
    else {
      super.setItem(i, itemstack);
    }

  }

  public short getReactorSize() {
    if (this.world == null) {
      return 9;
    }
    else {
      short word0 = 3;
      Direction[] adirection = Direction.values();
      int i = adirection.length;

      for (int j = 0; j < i; ++j) {
        Direction direction = adirection[j];
        TileEntity tileentity = direction.applyToTileEntity(this);
        if (tileentity instanceof TileEntityReactorChamber) {
          ++word0;
        }
      }

      return word0;
    }
  }

  public int tickRate() {
    return 20;
  }

  public boolean isAddedToEnergyNet() {
    return this.addedToEnergyNet;
  }

  public boolean emitsEnergyTo(TileEntity tileentity, Direction direction) {
    return true;
  }

  public int getMaxEnergyOutput() {
    return 240 * pulsePower();
  }

  public int sendEnergy(int i) {
    i = EnergyNet.getForWorld(this.world).emitEnergyFrom(this, i);
    if (i > 0 && this.world.getTileEntity(this.x, this.y + 1, this.z) instanceof TileEntityReactorChamber) {
      i = ((TileEntityReactorChamber) this.world.getTileEntity(this.x, this.y + 1, this.z)).sendEnergy(i);
    }

    if (i > 0 && this.world.getTileEntity(this.x, this.y - 1, this.z) instanceof TileEntityReactorChamber) {
      i = ((TileEntityReactorChamber) this.world.getTileEntity(this.x, this.y - 1, this.z)).sendEnergy(i);
    }

    if (i > 0 && this.world.getTileEntity(this.x + 1, this.y, this.z) instanceof TileEntityReactorChamber) {
      i = ((TileEntityReactorChamber) this.world.getTileEntity(this.x + 1, this.y, this.z)).sendEnergy(i);
    }

    if (i > 0 && this.world.getTileEntity(this.x - 1, this.y, this.z) instanceof TileEntityReactorChamber) {
      i = ((TileEntityReactorChamber) this.world.getTileEntity(this.x - 1, this.y, this.z)).sendEnergy(i);
    }

    if (i > 0 && this.world.getTileEntity(this.x, this.y, this.z + 1) instanceof TileEntityReactorChamber) {
      i = ((TileEntityReactorChamber) this.world.getTileEntity(this.x, this.y, this.z + 1)).sendEnergy(i);
    }

    if (i > 0 && this.world.getTileEntity(this.x, this.y, this.z - 1) instanceof TileEntityReactorChamber) {
      i = ((TileEntityReactorChamber) this.world.getTileEntity(this.x, this.y, this.z - 1)).sendEnergy(i);
    }

    return i;
  }

  public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
    return new ContainerNuclearReactor(entityhuman, this);
  }

  public String getGuiClassName(EntityHuman entityhuman) {
    return "GuiNuclearReactor";
  }

  public void onGuiClosed(EntityHuman entityhuman) {
  }

  public void onNetworkUpdate(String s) {
    if (s.equals("output")) {
      if (this.output > 0) {
        if (this.lastOutput <= 0) {
          if (this.audioSourceMain == null) {
            this.audioSourceMain = AudioManager.createSource(this, PositionSpec.Center, "Generators/NuclearReactor/NuclearReactorLoop.ogg", true, false, AudioManager.defaultVolume);
          }

          if (this.audioSourceMain != null) {
            this.audioSourceMain.play();
          }
        }

        if (this.output < 40) {
          if (this.lastOutput <= 0 || this.lastOutput >= 40) {
            if (this.audioSourceGeiger != null) {
              this.audioSourceGeiger.remove();
            }

            this.audioSourceGeiger = AudioManager.createSource(this, PositionSpec.Center, "Generators/NuclearReactor/GeigerLowEU.ogg", true, false, AudioManager.defaultVolume);
            if (this.audioSourceGeiger != null) {
              this.audioSourceGeiger.play();
            }
          }
        }
        else if (this.output < 80) {
          if (this.lastOutput < 40 || this.lastOutput >= 80) {
            if (this.audioSourceGeiger != null) {
              this.audioSourceGeiger.remove();
            }

            this.audioSourceGeiger = AudioManager.createSource(this, PositionSpec.Center, "Generators/NuclearReactor/GeigerMedEU.ogg", true, false, AudioManager.defaultVolume);
            if (this.audioSourceGeiger != null) {
              this.audioSourceGeiger.play();
            }
          }
        }
        else if (this.output >= 80 && this.lastOutput < 80) {
          if (this.audioSourceGeiger != null) {
            this.audioSourceGeiger.remove();
          }

          this.audioSourceGeiger = AudioManager.createSource(this, PositionSpec.Center, "Generators/NuclearReactor/GeigerHighEU.ogg", true, false, AudioManager.defaultVolume);
          if (this.audioSourceGeiger != null) {
            this.audioSourceGeiger.play();
          }
        }
      }
      else if (this.lastOutput > 0) {
        if (this.audioSourceMain != null) {
          this.audioSourceMain.stop();
        }

        if (this.audioSourceGeiger != null) {
          this.audioSourceGeiger.stop();
        }
      }

      this.lastOutput = this.output;
    }

    super.onNetworkUpdate(s);
  }

  public float getWrenchDropRate() {
    return 0.8F;
  }

  public ChunkCoordinates getPosition() {
    return new ChunkCoordinates(this.x, this.y, this.z);
  }

  public World getWorld() {
    return this.world;
  }

  public int getHeat() {
    return this.heat;
  }

  public void setHeat(int i) {
    this.heat = i;
  }

  public int addHeat(int i) {
    this.heat += i;
    return this.heat;
  }

  public ItemStack getItemAt(int i, int j) {
    return this.getMatrixCoord(i, j);
  }

  public void setItemAt(int i, int j, ItemStack itemstack) {
    this.setMatrixCoord(i, j, itemstack);
  }

  public void explode() {
    float f = 10.0F;

    for (int i = 0; i < 6; ++i) {
      for (int j = 0; j < this.getReactorSize(); ++j) {
        if (this.getMatrixCoord(j, i) != null && this.getMatrixCoord(j, i).id == Ic2Items.uraniumCell.id) {
          f += 3.0F;
        }
        else if (this.getMatrixCoord(j, i) != null && this.getMatrixCoord(j, i).id == Ic2Items.integratedReactorPlating.id) {
          --f;
        }
      }
    }

    if (f > mod_IC2.explosionPowerReactorMax) {
      f = mod_IC2.explosionPowerReactorMax;
    }

    this.world.setTypeId(this.x, this.y, this.z, 0);
    ExplosionIC2 explosionic2 = new ExplosionIC2(this.world, null, (double) this.x, (double) this.y, (double) this.z, f, 0.01F, 1.5F, IC2DamageSource.nuke);
    explosionic2.doExplosion();
  }
}
