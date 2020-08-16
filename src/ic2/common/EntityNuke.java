package ic2.common;

import net.minecraft.server.Block;
import net.minecraft.server.World;
import net.minecraft.server.mod_IC2;

public class EntityNuke extends EntityIC2Explosive {
  public EntityNuke(World world, double d, double d1, double d2) {
    super(world, d, d1, d2, 300, mod_IC2.explosionPowerNuke, 0.05F, 1.5F, Block.byId[Ic2Items.nuke.id],
        IC2DamageSource.nuke);
  }
  
  public EntityNuke(World world) {
    this(world, 0.0D, 0.0D, 0.0D);
  }
}
