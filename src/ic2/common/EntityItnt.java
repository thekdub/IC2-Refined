package ic2.common;

import net.minecraft.server.Block;
import net.minecraft.server.World;

public class EntityItnt extends EntityIC2Explosive {
  public EntityItnt(World world, double d, double d1, double d2) {
    super(world, d, d1, d2, 60, 5.5F, 0.9F, 0.3F, Block.byId[Ic2Items.industrialTnt.id]);
  }

  public EntityItnt(World world) {
    this(world, 0.0D, 0.0D, 0.0D);
  }
}
