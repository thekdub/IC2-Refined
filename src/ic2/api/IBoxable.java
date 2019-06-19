package ic2.api;

import net.minecraft.server.ItemStack;

public interface IBoxable {
  boolean canBeStoredInToolbox(ItemStack var1);
}
