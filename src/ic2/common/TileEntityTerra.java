package ic2.common;

import ic2.api.Direction;
import ic2.api.ITerraformingBP;
import ic2.platform.AudioManager;
import ic2.platform.AudioSource;
import ic2.platform.Platform;
import net.minecraft.server.*;

public class TileEntityTerra extends TileEntityElecMachine {
  public int failedAttempts = 0;
  public int lastX = -1;
  public int lastY = -1;
  public int lastZ = -1;
  public AudioSource audioSource;
  public int inactiveTicks = 0;
  
  public TileEntityTerra() {
    super(1, 0, 100000, 512);
  }
  
  public static int getFirstSolidBlockFrom(World world, int i, int j, int k) {
    while (k > 0) {
      if (world.r(i, k, j)) {
        return k;
      }
  
      --k;
    }
    
    return -1;
  }
  
  public static int getFirstBlockFrom(World world, int i, int j, int k) {
    while (k > 0) {
      if (world.getTypeId(i, k, j) != 0) {
        return k;
      }
  
      --k;
    }
    
    return -1;
  }
  
  public static boolean switchGround(World world, Block block, Block block1, int i, int j, int k, boolean flag) {
    int l;
    if (flag) {
      ++j;
      l = j;
  
      while (true) {
        int k1 = world.getTypeId(i, j - 1, k);
        if (k1 == 0 || Block.byId[k1] != block) {
          if (l == j) {
            return false;
          }
          else {
            world.setTypeId(i, j, k, block1.id);
            return true;
          }
        }
    
        --j;
      }
    }
    else {
      while (true) {
        l = world.getTypeId(i, j, k);
        if (l == 0 || Block.byId[l] != block1) {
          l = world.getTypeId(i, j, k);
          if (l != 0 && Block.byId[l] == block) {
            world.setTypeId(i, j, k, block1.id);
            return true;
          }
          else {
            return false;
          }
        }
  
        --j;
      }
    }
  }
  
  public static BiomeBase getBiomeAt(World world, int i, int j) {
    Chunk chunk = world.getChunkAtWorldCoords(i, j);
    return Platform.getBiomeAt(chunk, i & 16, j & 16, world.getWorldChunkManager());
  }
  
  public static void setBiomeAt(World world, int i, int j, BiomeBase biomebase) {
    Chunk chunk = world.getChunkAtWorldCoords(i, j);
    byte[] abyte0 = chunk.l();
    abyte0[(j & 15) << 4 | i & 15] = (byte) (biomebase.id & 255);
    chunk.a(abyte0);
  }
  
  public String getName() {
    return "Terraformer";
  }
  
  public void q_() {
    super.q_();
    boolean flag = false;
    if (this.inventory[0] != null && this.inventory[0].getItem() instanceof ITerraformingBP) {
      ITerraformingBP iterraformingbp = (ITerraformingBP) this.inventory[0].getItem();
      if (this.energy >= iterraformingbp.getConsume()) {
        flag = true;
        int i = this.x;
        int j = this.z;
        boolean flag1 = true;
        int l;
        if (this.lastY > -1) {
          l = iterraformingbp.getRange() / 10;
          i = this.lastX - this.world.random.nextInt(l + 1) + this.world.random.nextInt(l + 1);
          j = this.lastZ - this.world.random.nextInt(l + 1) + this.world.random.nextInt(l + 1);
        }
        else {
          if (this.failedAttempts > 4) {
            this.failedAttempts = 4;
          }
  
          l = iterraformingbp.getRange() * (this.failedAttempts + 1) / 5;
          i = i - this.world.random.nextInt(l + 1) + this.world.random.nextInt(l + 1);
          j = j - this.world.random.nextInt(l + 1) + this.world.random.nextInt(l + 1);
        }
  
        if (iterraformingbp.terraform(this.world, i, j, this.y)) {
          this.energy -= iterraformingbp.getConsume();
          this.failedAttempts = 0;
          this.lastX = i;
          this.lastZ = j;
          this.lastY = this.y;
        }
        else {
          this.energy -= iterraformingbp.getConsume() / 10;
          ++this.failedAttempts;
          this.lastY = -1;
        }
      }
    }
    
    if (flag) {
      this.inactiveTicks = 0;
      this.setActive(true);
    }
    else if (!flag && this.getActive() && this.inactiveTicks++ > 30) {
      this.setActive(false);
    }
    
  }
  
  public void j() {
    if (Platform.isRendering() && this.audioSource != null) {
      AudioManager.removeSources(this);
      this.audioSource = null;
    }
    
    super.j();
  }
  
  public int injectEnergy(Direction direction, int i) {
    if (i > 512) {
      mod_IC2.explodeMachineAt(this.world, this.x, this.y, this.z);
      return 0;
    }
    else if (this.energy + i > this.maxEnergy) {
      int j = this.energy + i - this.maxEnergy;
      this.energy = this.maxEnergy;
      return j;
    }
    else {
      this.energy += i;
      return 0;
    }
  }
  
  public boolean ejectBlueprint() {
    if (this.inventory[0] == null) {
      return false;
    }
    else {
      if (Platform.isSimulating()) {
        StackUtil.dropAsEntity(this.world, this.x, this.y, this.z, this.inventory[0]);
        this.inventory[0] = null;
      }
  
      return true;
    }
  }
  
  public void insertBlueprint(ItemStack itemstack) {
    this.ejectBlueprint();
    this.inventory[0] = itemstack;
  }
  
  public void onNetworkUpdate(String s) {
    if (s.equals("active") && this.prevActive != this.getActive()) {
      if (this.audioSource == null) {
        this.audioSource = AudioManager
            .createSource(this, PositionSpec.Center, "Terraformers/TerraformerGenericloop.ogg", true, false,
                AudioManager.defaultVolume);
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
}
