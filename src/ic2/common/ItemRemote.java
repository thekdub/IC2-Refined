package ic2.common;

import ic2.platform.AudioManager;
import ic2.platform.Platform;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

public class ItemRemote extends ItemIC2 {
	public ItemRemote(int i, int j) {
		super(i, j);
		this.e(1);
	}

	public static void addRemote(int i, int j, int k, ItemStack itemstack) {
		NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
		if (!nbttagcompound.hasKey("coords")) {
			nbttagcompound.set("coords", new NBTTagList());
		}

		NBTTagList nbttaglist = nbttagcompound.getList("coords");
		NBTTagCompound nbttagcompound1 = new NBTTagCompound();
		nbttagcompound1.setInt("x", i);
		nbttagcompound1.setInt("y", j);
		nbttagcompound1.setInt("z", k);
		nbttaglist.add(nbttagcompound1);
		nbttagcompound.set("coords", nbttaglist);
	}

	public static void launchRemotes(World world, ItemStack itemstack) {
		NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
		if (nbttagcompound.hasKey("coords")) {
			NBTTagList nbttaglist = nbttagcompound.getList("coords");

			for (int i = 0; i < nbttaglist.size(); ++i) {
				NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.get(i);
				int j = nbttagcompound1.getInt("x");
				int k = nbttagcompound1.getInt("y");
				int l = nbttagcompound1.getInt("z");
				if (world.getTypeId(j, k, l) == Ic2Items.dynamiteStickWithRemote.id) {
					Block.byId[Ic2Items.dynamiteStickWithRemote.id].wasExploded(world, j, k, l);
					world.setTypeIdAndData(j, k, l, 0, 0);
				}
			}

			nbttagcompound.set("coords", new NBTTagList());
		}
	}

	public static int hasRemote(int i, int j, int k, ItemStack itemstack) {
		NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
		if (!nbttagcompound.hasKey("coords")) {
			return -1;
		}
		else {
			NBTTagList nbttaglist = nbttagcompound.getList("coords");

			for (int l = 0; l < nbttaglist.size(); ++l) {
				NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.get(l);
				int i1 = nbttagcompound1.getInt("x");
				int j1 = nbttagcompound1.getInt("y");
				int k1 = nbttagcompound1.getInt("z");
				if (i1 == i && j1 == j && k1 == k) {
					return l;
				}
			}

			return -1;
		}
	}

	public static void removeRemote(int i, ItemStack itemstack) {
		NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
		if (nbttagcompound.hasKey("coords")) {
			NBTTagList nbttaglist = nbttagcompound.getList("coords");
			NBTTagList nbttaglist1 = new NBTTagList();

			for (int j = 0; j < nbttaglist.size(); ++j) {
				if (j != i) {
					nbttaglist1.add(nbttaglist.get(j));
				}
			}

			nbttagcompound.set("coords", nbttaglist1);
		}
	}

	public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
		Entity ent = entityhuman.getBukkitEntity();
		if (ent instanceof Player) {
			Player player = (Player) ent;
			BlockBreakEvent event = new BlockBreakEvent(player.getWorld().getBlockAt(i, j, k), player);
			Bukkit.getPluginManager().callEvent(event);
			if (event.isCancelled()) {
				return false;
			}

			event.setCancelled(true);
		}

		if (!Platform.isSimulating()) {
			return world.getTypeId(i, j, k) == Ic2Items.dynamiteStick.id || world.getTypeId(i, j, k) == Ic2Items.dynamiteStickWithRemote.id;
		}
		else if (world.getTypeId(i, j, k) == Ic2Items.dynamiteStick.id) {
			addRemote(i, j, k, itemstack);
			world.setTypeId(i, j, k, Ic2Items.dynamiteStickWithRemote.id);
			return true;
		}
		else if (world.getTypeId(i, j, k) == Ic2Items.dynamiteStickWithRemote.id) {
			int i1 = hasRemote(i, j, k, itemstack);
			if (i1 > -1) {
				world.setTypeId(i, j, k, Ic2Items.dynamiteStick.id);
				removeRemote(i1, itemstack);
			}
			else {
				Platform.messagePlayer(entityhuman, "This dynamite stick is not linked to this remote, cannot unlink.");
			}

			return true;
		}
		else {
			return true;
		}
	}

	public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
		AudioManager.playOnce(entityhuman, PositionSpec.Hand, "Tools/dynamiteomote.ogg", true, AudioManager.defaultVolume);
		launchRemotes(world, itemstack);
		return itemstack;
	}

	public void addInformation(ItemStack itemstack, List list) {
		NBTTagCompound nbttagcompound = StackUtil.getOrCreateNbtData(itemstack);
		if (nbttagcompound.hasKey("coords")) {
			int i = nbttagcompound.getList("coords").size();
			if (i > 0) {
				list.add("Linked to " + i + " dynamite sticks");
			}
		}

	}
}
