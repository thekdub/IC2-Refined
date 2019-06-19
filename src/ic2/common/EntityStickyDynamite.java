package ic2.common;

import net.minecraft.server.EntityLiving;
import net.minecraft.server.World;

public class EntityStickyDynamite extends EntityDynamite {
  public EntityStickyDynamite(World world) {
    super(world, 0.0D, 0.0D, 0.0D);
    this.sticky = true;
  }

  public EntityStickyDynamite(World world, EntityLiving entityliving) {
    super(world, entityliving);
    this.sticky = true;
  }
}
