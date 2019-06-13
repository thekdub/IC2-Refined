package ic2.api;

import net.minecraft.server.World;

public interface IPaintableBlock {
	boolean colorBlock(World var1, int var2, int var3, int var4, int var5);
}
