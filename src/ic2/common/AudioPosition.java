package ic2.common;

import net.minecraft.server.Entity;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;

public class AudioPosition {
  public World world;
  public float x;
  public float y;
  public float z;

  public AudioPosition(World world1, float f, float f1, float f2) {
    this.world = world1;
    this.x = f;
    this.y = f1;
    this.z = f2;
  }

  public static AudioPosition getFrom(Object obj, PositionSpec positionspec) {
    if (obj instanceof AudioPosition) {
      return (AudioPosition) obj;
    }
    else if (obj instanceof Entity) {
      Entity entity = (Entity) obj;
      return new AudioPosition(entity.world, (float) entity.locX, (float) entity.locY, (float) entity.locZ);
    }
    else if (obj instanceof TileEntity) {
      TileEntity tileentity = (TileEntity) obj;
      return new AudioPosition(tileentity.world, (float) tileentity.x + 0.5F, (float) tileentity.y + 0.5F, (float) tileentity.z + 0.5F);
    }
    else {
      return null;
    }
  }
}
