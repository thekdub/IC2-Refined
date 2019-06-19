package ic2.common;

import forge.ForgeHooks;
import forge.ISidedInventory;
import ic2.api.FakePlayer;
import ic2.platform.AudioManager;
import ic2.platform.AudioSource;
import ic2.platform.Platform;
import net.minecraft.server.*;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TileEntityMiner extends TileEntityElecMachine implements IHasGui, ISidedInventory {
  public int targetX = 0;
  public int targetY = -1;
  public int targetZ = 0;
  public short miningTicker = 0;
  public String stuckOn = null;
  private AudioSource audioSource;

  public TileEntityMiner() {
    super(4, 0, 1000, 32, mod_IC2.enableMinerLapotron ? 3 : 1);
  }

  public void q_() {
    super.q_();
    boolean flag = this.isOperating();
    boolean flag1 = false;
    if (this.isOperating()) {
      --this.energy;
      if (this.inventory[1] != null && Item.byId[this.inventory[1].id] instanceof ItemScanner) {
        this.energy -= ElectricItem.charge(this.inventory[1], this.energy, 2, false, false);
      }

      if (this.inventory[3] != null && (Item.byId[this.inventory[3].id] instanceof ItemElectricToolDrill || Item.byId[this.inventory[3].id] instanceof ItemElectricToolDDrill)) {
        this.energy -= ElectricItem.charge(this.inventory[3], this.energy, 1, false, false);
      }
    }

    if (this.energy <= this.maxEnergy) {
      flag1 = this.provideEnergy();
    }

    if (flag) {
      flag1 = this.mine();
    }
    else if (this.inventory[3] == null) {
      if (this.energy >= 2 && this.canWithdraw()) {
        this.targetY = -1;
        ++this.miningTicker;
        this.energy -= 2;
        if (this.miningTicker >= 20) {
          this.miningTicker = 0;
          flag1 = this.withdrawPipe();
        }
      }
      else if (this.isStuck()) {
        this.miningTicker = 0;
      }
    }

    this.setActive(this.isOperating());
    if (flag != this.isOperating()) {
      flag1 = true;
    }

    if (flag1) {
      this.update();
    }

  }

  public void j() {
    if (Platform.isRendering() && this.audioSource != null) {
      AudioManager.removeSources(this);
      this.audioSource = null;
    }

    super.j();
  }

  public void a(NBTTagCompound nbttagcompound) {
    super.a(nbttagcompound);
    this.targetX = nbttagcompound.getInt("targetX");
    this.targetY = nbttagcompound.getInt("targetY");
    this.targetZ = nbttagcompound.getInt("targetZ");
    this.miningTicker = nbttagcompound.getShort("miningTicker");
  }

  public void b(NBTTagCompound nbttagcompound) {
    super.b(nbttagcompound);
    nbttagcompound.setInt("targetX", (short) this.targetX);
    nbttagcompound.setInt("targetY", (short) this.targetY);
    nbttagcompound.setInt("targetZ", (short) this.targetZ);
    nbttagcompound.setShort("miningTicker", this.miningTicker);
  }

  public boolean mine() {
    if (this.targetY < 0) {
      this.aquireTarget();
      return false;
    }
    else {
      int j;
      if (!this.canReachTarget(this.targetX, this.targetY, this.targetZ, true)) {
        j = this.targetX - this.x;
        int k = this.targetZ - this.z;
        if (Math.abs(j) > Math.abs(k)) {
          if (j > 0) {
            --this.targetX;
          }
          else {
            ++this.targetX;
          }
        }
        else if (k > 0) {
          --this.targetZ;
        }
        else {
          ++this.targetZ;
        }

        return false;
      }
      else if (this.canMine(this.world.getTypeId(this.targetX, this.targetY, this.targetZ), this.world.getData(this.targetX, this.targetY, this.targetZ))) {
        this.stuckOn = null;
        ++this.miningTicker;
        --this.energy;
        if (this.inventory[3].id == Ic2Items.diamondDrill.id) {
          this.miningTicker = (short) (this.miningTicker + 3);
          this.energy -= 14;
        }

        if (this.miningTicker >= 200) {
          Block block = this.world.getWorld().getBlockAt(this.targetX, this.targetY, this.targetZ);
          BlockBreakEvent event = new BlockBreakEvent(block, FakePlayer.getBukkitEntity(this.world));
          this.world.getServer().getPluginManager().callEvent(event);
          if (event.isCancelled()) {
            return false;
          }
          else {
            this.miningTicker = 0;
            this.mineBlock();
            return true;
          }
        }
        else {
          return false;
        }
      }
      else {
        j = this.world.getTypeId(this.targetX, this.targetY, this.targetZ);
        if ((j == net.minecraft.server.Block.WATER.id || j == net.minecraft.server.Block.STATIONARY_WATER.id || j == net.minecraft.server.Block.LAVA.id || j == net.minecraft.server.Block.STATIONARY_LAVA.id) && this.isAnyPumpConnected()) {
          return false;
        }
        else {
          this.miningTicker = -1;
          this.stuckOn = Platform.translateBlockName(net.minecraft.server.Block.byId[j]);
          return false;
        }
      }
    }
  }

  public void mineBlock() {
    if (this.inventory[3].getItem() instanceof ItemElectricToolDrill) {
      ElectricItem.use(this.inventory[3], 50, null);
    }
    else if (this.inventory[3].getItem() instanceof ItemElectricToolDDrill) {
      ElectricItem.use(this.inventory[3], 80, null);
    }

    int i = this.world.getTypeId(this.targetX, this.targetY, this.targetZ);
    int j = this.world.getData(this.targetX, this.targetY, this.targetZ);
    boolean flag = false;
    if (i == net.minecraft.server.Block.WATER.id || i == net.minecraft.server.Block.STATIONARY_WATER.id || i == net.minecraft.server.Block.LAVA.id || i == net.minecraft.server.Block.STATIONARY_LAVA.id) {
      flag = true;
      if (j != 0) {
        i = 0;
      }
    }

    if (i != 0) {
      if (!flag) {
        net.minecraft.server.Block block = net.minecraft.server.Block.byId[i];
        StackUtil.distributeDrop(this, block.getBlockDropped(this.world, this.targetX, this.targetY, this.targetZ, j, 0));
      }
      else {
        if (i == net.minecraft.server.Block.WATER.id || i == net.minecraft.server.Block.STATIONARY_WATER.id) {
          this.usePump(net.minecraft.server.Block.STATIONARY_WATER.id);
        }

        if (i == net.minecraft.server.Block.LAVA.id || i == net.minecraft.server.Block.STATIONARY_LAVA.id) {
          this.usePump(net.minecraft.server.Block.STATIONARY_LAVA.id);
        }
      }

      this.world.setTypeId(this.targetX, this.targetY, this.targetZ, 0);
      this.energy -= 2 * (this.y - this.targetY);
    }

    if (this.targetX == this.x && this.targetZ == this.z) {
      this.world.setRawTypeId(this.targetX, this.targetY, this.targetZ, Ic2Items.miningPipe.id);
      --this.inventory[2].count;
      if (this.inventory[2].count == 0) {
        this.inventory[2] = null;
      }

      this.energy -= 10;
    }

    this.updateMineTip(this.targetY);
    this.targetY = -1;
  }

  public boolean withdrawPipe() {
    int i = this.getPipeTip();
    int j = this.world.getTypeId(this.x, i, this.z);
    if (j != 0) {
      StackUtil.distributeDrop(this, net.minecraft.server.Block.byId[j].getBlockDropped(this.world, this.x, i, this.z, this.world.getData(this.x, i, this.z), 0));
      this.world.setTypeId(this.x, i, this.z, 0);
    }

    if (this.inventory[2] != null && this.inventory[2].id != Ic2Items.miningPipe.id && this.inventory[2].id < net.minecraft.server.Block.byId.length && net.minecraft.server.Block.byId[this.inventory[2].id] != null) {
      this.world.setTypeIdAndData(this.x, i, this.z, this.inventory[2].id, this.inventory[2].getData());
      --this.inventory[2].count;
      if (this.inventory[2].count == 0) {
        this.inventory[2] = null;
      }

      this.updateMineTip(i + 1);
      return true;
    }
    else {
      this.updateMineTip(i + 1);
      return false;
    }
  }

  public void updateMineTip(int i) {
    if (i != this.y) {
      int j = this.x;
      int k = this.y - 1;

      int l;
      for (l = this.z; k > i; --k) {
        if (this.world.getTypeId(j, k, l) != Ic2Items.miningPipe.id) {
          this.world.setTypeId(j, k, l, Ic2Items.miningPipe.id);
        }
      }

      this.world.setTypeId(j, i, l, Ic2Items.miningPipeTip.id);
    }
  }

  public boolean canReachTarget(int i, int j, int k, boolean flag) {
    if (this.x == i && this.z == k) {
      return true;
    }
    else if (!flag && !this.canPass(this.world.getTypeId(i, j, k))) {
      return false;
    }
    else {
      int l = i - this.x;
      int i1 = k - this.z;
      if (Math.abs(l) > Math.abs(i1)) {
        if (l > 0) {
          --i;
        }
        else {
          ++i;
        }
      }
      else if (i1 > 0) {
        --k;
      }
      else {
        ++k;
      }

      return this.canReachTarget(i, j, k, false);
    }
  }

  public void aquireTarget() {
    int i = this.getPipeTip();
    if (i < this.y && this.inventory[1] != null && this.inventory[1].getItem() instanceof ItemScanner) {
      int j = ((ItemScanner) this.inventory[1].getItem()).startLayerScan(this.inventory[1]);
      if (j > 0) {
        for (int k = this.x - j; k <= this.x + j; ++k) {
          for (int l = this.z - j; l <= this.z + j; ++l) {
            int i1 = this.world.getTypeId(k, i, l);
            int j1 = this.world.getData(k, i, l);
            if (ItemScanner.isValuable(i1, j1) && this.canMine(i1, j1) || this.isAnyPumpConnected() && this.world.getData(k, i, l) == 0 && (i1 == net.minecraft.server.Block.LAVA.id || i1 == net.minecraft.server.Block.STATIONARY_LAVA.id)) {
              this.setTarget(k, i, l);
              return;
            }
          }
        }
      }

      this.setTarget(this.x, i - 1, this.z);
    }
    else {
      this.setTarget(this.x, i - 1, this.z);
    }
  }

  public void setTarget(int i, int j, int k) {
    this.targetX = i;
    this.targetY = j;
    this.targetZ = k;
  }

  public int getPipeTip() {
    int i;
    for (i = this.y; this.world.getTypeId(this.x, i - 1, this.z) == Ic2Items.miningPipe.id || this.world.getTypeId(this.x, i - 1, this.z) == Ic2Items.miningPipeTip.id; --i) {
    }

    return i;
  }

  public boolean canPass(int i) {
    return i == 0 || i == net.minecraft.server.Block.WATER.id || i == net.minecraft.server.Block.STATIONARY_WATER.id || i == net.minecraft.server.Block.LAVA.id || i == net.minecraft.server.Block.STATIONARY_LAVA.id || i == Ic2Items.miner.id || i == Ic2Items.miningPipe.id || i == Ic2Items.miningPipeTip.id;
  }

  public boolean isOperating() {
    return this.energy > 100 && this.canOperate();
  }

  public boolean canOperate() {
    if (this.inventory[2] != null && this.inventory[3] != null) {
      if (this.inventory[2].id != Ic2Items.miningPipe.id) {
        return false;
      }
      else if (this.inventory[3].id != Ic2Items.miningDrill.id && this.inventory[3].id != Ic2Items.diamondDrill.id) {
        return false;
      }
      else {
        return !this.isStuck();
      }
    }
    else {
      return false;
    }
  }

  public boolean isStuck() {
    return this.miningTicker < 0;
  }

  public String getStuckOn() {
    return this.stuckOn;
  }

  public boolean canMine(int i, int j) {
    if (i == 0) {
      return true;
    }
    else if (i != Ic2Items.miningPipe.id && i != Ic2Items.miningPipeTip.id && i != net.minecraft.server.Block.CHEST.id) {
      if ((i == net.minecraft.server.Block.WATER.id || i == net.minecraft.server.Block.STATIONARY_WATER.id || i == net.minecraft.server.Block.LAVA.id || i == net.minecraft.server.Block.STATIONARY_LAVA.id) && this.isPumpConnected()) {
        return true;
      }
      else {
        net.minecraft.server.Block block = net.minecraft.server.Block.byId[i];
        if (block.getHardness(j) < 0.0F) {
          return false;
        }
        else if (block.a(j, false) && block.material.isAlwaysDestroyable()) {
          return true;
        }
        else if (i == net.minecraft.server.Block.WEB.id) {
          return true;
        }
        else if (this.inventory[3] == null || this.inventory[3].id == Ic2Items.miningDrill.id && this.inventory[3].id == Ic2Items.diamondDrill.id) {
          return false;
        }
        else {
          try {
            HashMap hashmap = ModLoader.getPrivateValue(ForgeHooks.class, null, "toolClasses");
            List list = (List) hashmap.get(this.inventory[3].id);
            if (list == null) {
              return this.inventory[3].b(block);
            }
            else {
              Object[] aobj = list.toArray();
              String s = (String) aobj[0];
              int k = (Integer) aobj[1];
              HashMap hashmap1 = ModLoader.getPrivateValue(ForgeHooks.class, null, "toolHarvestLevels");
              Integer integer = (Integer) hashmap1.get(Arrays.asList(block.id, j, s));
              if (integer == null) {
                return this.inventory[3].b(block);
              }
              else {
                return integer <= k && this.inventory[3].b(block);
              }
            }
          } catch (Throwable var11) {
            return false;
          }
        }
      }
    }
    else {
      return false;
    }
  }

  public boolean canWithdraw() {
    return this.world.getTypeId(this.x, this.y - 1, this.z) == Ic2Items.miningPipe.id || this.world.getTypeId(this.x, this.y - 1, this.z) == Ic2Items.miningPipeTip.id;
  }

  public boolean isPumpConnected() {
    if (this.world.getTileEntity(this.x, this.y + 1, this.z) instanceof TileEntityPump && ((TileEntityPump) this.world.getTileEntity(this.x, this.y + 1, this.z)).canHarvest()) {
      return true;
    }
    else if (this.world.getTileEntity(this.x, this.y - 1, this.z) instanceof TileEntityPump && ((TileEntityPump) this.world.getTileEntity(this.x, this.y - 1, this.z)).canHarvest()) {
      return true;
    }
    else if (this.world.getTileEntity(this.x + 1, this.y, this.z) instanceof TileEntityPump && ((TileEntityPump) this.world.getTileEntity(this.x + 1, this.y, this.z)).canHarvest()) {
      return true;
    }
    else if (this.world.getTileEntity(this.x - 1, this.y, this.z) instanceof TileEntityPump && ((TileEntityPump) this.world.getTileEntity(this.x - 1, this.y, this.z)).canHarvest()) {
      return true;
    }
    else if (this.world.getTileEntity(this.x, this.y, this.z + 1) instanceof TileEntityPump && ((TileEntityPump) this.world.getTileEntity(this.x, this.y, this.z + 1)).canHarvest()) {
      return true;
    }
    else {
      return this.world.getTileEntity(this.x, this.y, this.z - 1) instanceof TileEntityPump && ((TileEntityPump) this.world.getTileEntity(this.x, this.y, this.z - 1)).canHarvest();
    }
  }

  public boolean isAnyPumpConnected() {
    if (this.world.getTileEntity(this.x, this.y + 1, this.z) instanceof TileEntityPump) {
      return true;
    }
    else if (this.world.getTileEntity(this.x, this.y - 1, this.z) instanceof TileEntityPump) {
      return true;
    }
    else if (this.world.getTileEntity(this.x + 1, this.y, this.z) instanceof TileEntityPump) {
      return true;
    }
    else if (this.world.getTileEntity(this.x - 1, this.y, this.z) instanceof TileEntityPump) {
      return true;
    }
    else {
      return this.world.getTileEntity(this.x, this.y, this.z + 1) instanceof TileEntityPump || this.world.getTileEntity(this.x, this.y, this.z - 1) instanceof TileEntityPump;
    }
  }

  public void usePump(int i) {
    if (this.world.getTileEntity(this.x, this.y + 1, this.z) instanceof TileEntityPump && ((TileEntityPump) this.world.getTileEntity(this.x, this.y + 1, this.z)).canHarvest()) {
      ((TileEntityPump) this.world.getTileEntity(this.x, this.y + 1, this.z)).pumpThis(i);
    }
    else if (this.world.getTileEntity(this.x, this.y - 1, this.z) instanceof TileEntityPump && ((TileEntityPump) this.world.getTileEntity(this.x, this.y - 1, this.z)).canHarvest()) {
      ((TileEntityPump) this.world.getTileEntity(this.x, this.y - 1, this.z)).pumpThis(i);
    }
    else if (this.world.getTileEntity(this.x + 1, this.y, this.z) instanceof TileEntityPump && ((TileEntityPump) this.world.getTileEntity(this.x + 1, this.y, this.z)).canHarvest()) {
      ((TileEntityPump) this.world.getTileEntity(this.x + 1, this.y, this.z)).pumpThis(i);
    }
    else if (this.world.getTileEntity(this.x - 1, this.y, this.z) instanceof TileEntityPump && ((TileEntityPump) this.world.getTileEntity(this.x - 1, this.y, this.z)).canHarvest()) {
      ((TileEntityPump) this.world.getTileEntity(this.x - 1, this.y, this.z)).pumpThis(i);
    }
    else if (this.world.getTileEntity(this.x, this.y, this.z + 1) instanceof TileEntityPump && ((TileEntityPump) this.world.getTileEntity(this.x, this.y, this.z + 1)).canHarvest()) {
      ((TileEntityPump) this.world.getTileEntity(this.x, this.y, this.z + 1)).pumpThis(i);
    }
    else if (this.world.getTileEntity(this.x, this.y, this.z - 1) instanceof TileEntityPump && ((TileEntityPump) this.world.getTileEntity(this.x, this.y, this.z - 1)).canHarvest()) {
      ((TileEntityPump) this.world.getTileEntity(this.x, this.y, this.z - 1)).pumpThis(i);
    }
  }

  public String getName() {
    return "Miner";
  }

  public int gaugeEnergyScaled(int i) {
    if (this.energy <= 0) {
      return 0;
    }
    else {
      int j = this.energy * i / 1000;
      if (j > i) {
        j = i;
      }

      return j;
    }
  }

  public ContainerIC2 getGuiContainer(EntityHuman entityhuman) {
    return new ContainerMiner(entityhuman, this);
  }

  public String getGuiClassName(EntityHuman entityhuman) {
    return "GuiMiner";
  }

  public void onGuiClosed(EntityHuman entityhuman) {
  }

  public void onNetworkUpdate(String s) {
    if (s.equals("active") && this.prevActive != this.getActive()) {
      if (this.audioSource == null) {
        this.audioSource = AudioManager.createSource(this, PositionSpec.Center, "Machines/MinerOp.ogg", true, false, AudioManager.defaultVolume);
      }

      if (this.getActive()) {
        if (this.audioSource != null) {
          this.audioSource.play();
        }
      }
      else if (this.audioSource != null) {
        this.audioSource.stop();
      }
    }

    super.onNetworkUpdate(s);
  }

  public int getStartInventorySide(int i) {
    byte byte0;
    byte byte1;
    byte byte2;
    byte byte3;
    switch (this.getFacing()) {
      case 2:
        byte0 = 4;
        byte1 = 5;
        byte2 = 3;
        byte3 = 2;
        break;
      case 3:
        byte0 = 5;
        byte1 = 4;
        byte2 = 2;
        byte3 = 3;
        break;
      case 4:
        byte0 = 3;
        byte1 = 2;
        byte2 = 5;
        byte3 = 4;
        break;
      default:
        byte0 = 2;
        byte1 = 3;
        byte2 = 4;
        byte3 = 5;
    }

    if (i != byte0 && i != byte2) {
      if (i != byte1 && i != byte3) {
        switch (i) {
          case 0:
            return 0;
          default:
            return 2;
        }
      }
      else {
        return 1;
      }
    }
    else {
      return 3;
    }
  }

  public int getSizeInventorySide(int i) {
    return 1;
  }
}
