package ic2.api;

import net.minecraft.server.Block;
import net.minecraft.server.ItemStack;
import net.minecraft.server.TileEntity;

public abstract class TECrop extends TileEntity {
  public short id = -1;
  public byte size = 0;
  public byte statGrowth = 0;
  public byte statGain = 0;
  public byte statResistance = 0;
  public byte scanLevel = 0;
  public short[] custumData = new short[16];
  public int nutrientStorage = 0;
  public int waterStorage = 0;
  public int exStorage = 0;

  public abstract byte getHumidity();

  public abstract byte getNutrients();

  public abstract byte getAirQuality();

  public int getLightLevel() {
    return this.world.getLightLevel(this.x, this.y, this.z);
  }

  public abstract boolean pick(boolean var1);

  public abstract boolean harvest(boolean var1);

  public abstract void reset();

  public abstract void updateState();

  public abstract boolean isBlockBelow(Block var1);

  public abstract ItemStack generateSeeds(short var1, byte var2, byte var3, byte var4, byte var5);

  public abstract void addLocal(String var1, String var2);
}
