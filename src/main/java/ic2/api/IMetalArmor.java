package ic2.api;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;

public interface IMetalArmor {
  boolean isMetalArmor(ItemStack var1, EntityHuman var2);
}
