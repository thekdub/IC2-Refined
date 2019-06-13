package ic2.api;

import net.minecraft.server.ChunkCoordinates;
import net.minecraft.server.ItemStack;
import net.minecraft.server.World;

public interface IReactor {
	ChunkCoordinates getPosition();

	World getWorld();

	int getHeat();

	void setHeat(int var1);

	int addHeat(int var1);

	ItemStack getItemAt(int var1, int var2);

	void setItemAt(int var1, int var2, ItemStack var3);

	void explode();
}
