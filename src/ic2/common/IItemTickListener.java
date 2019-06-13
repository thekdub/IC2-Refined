package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.ItemStack;

public interface IItemTickListener {
	boolean onTick(EntityHuman var1, ItemStack var2);
}
