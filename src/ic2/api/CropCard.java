package ic2.api;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.HashMap;
import java.util.Iterator;

public abstract class CropCard {
	private static final CropCard[] cropCardList = new CropCard[256];
	public static TECrop nameReference;
	private static HashMap baseseeds = new HashMap();

	public static int cropCardListLength() {
		return cropCardList.length;
	}

	public static final CropCard getCrop(int i) {
		if (i >= 0 && i < cropCardList.length) {
			if (cropCardList[i] == null) {
				System.out.println("[IndustrialCraft] Something tried to access non-existant cropID #" + i + "!!!");
				return cropCardList[0];
			}
			else {
				return cropCardList[i];
			}
		}
		else {
			return cropCardList[0];
		}
	}

	public static final boolean idExists(int i) {
		return i >= 0 && i < cropCardList.length && cropCardList[i] != null;
	}

	public static final short registerCrop(CropCard cropcard) {
		for (short word0 = 0; word0 < cropCardList.length; ++word0) {
			if (cropCardList[word0] == null) {
				cropCardList[word0] = cropcard;
				nameReference.addLocal("item.cropSeed" + word0 + ".name", cropcard.name() + " Seeds");
				return word0;
			}
		}

		return -1;
	}

	public static final boolean registerCrop(CropCard cropcard, int i) {
		if (i >= 0 && i < cropCardList.length) {
			if (cropCardList[i] == null) {
				cropCardList[i] = cropcard;
				nameReference.addLocal("item.cropSeed" + i + ".name", cropcard.name() + " Seeds");
				return true;
			}
			else {
				System.out.println("[IndustrialCraft] Cannot add crop:" + cropcard.name() + " on ID #" + i + ", slot already occupied by crop:" + cropCardList[i].name());
				return false;
			}
		}
		else {
			return false;
		}
	}

	public static boolean registerBaseSeed(ItemStack itemstack, int i, int j, int k, int l, int i1) {
		Iterator iterator = baseseeds.keySet().iterator();

		ItemStack itemstack1;
		do {
			if (!iterator.hasNext()) {
				baseseeds.put(itemstack, new BaseSeed(i, j, k, l, i1, itemstack.count));
				return true;
			}

			itemstack1 = (ItemStack) iterator.next();
		} while (itemstack1.id != itemstack.id || itemstack1.getData() != itemstack.getData());

		return false;
	}

	public static BaseSeed getBaseSeed(ItemStack itemstack) {
		if (itemstack == null) {
			return null;
		}
		else {
			Iterator iterator = baseseeds.keySet().iterator();

			ItemStack itemstack1;
			do {
				do {
					if (!iterator.hasNext()) {
						return null;
					}

					itemstack1 = (ItemStack) iterator.next();
				} while (itemstack1.id != itemstack.id);
			} while (itemstack1.getData() != -1 && itemstack1.getData() != itemstack.getData());

			return (BaseSeed) baseseeds.get(itemstack1);
		}
	}

	public abstract String name();

	public String discoveredBy() {
		return "Alblaka";
	}

	public String desc(int i) {
		String[] as = this.attributes();
		if (as != null && as.length != 0) {
			String s1;
			if (i == 0) {
				s1 = as[0];
				if (as.length >= 2) {
					s1 = s1 + ", " + as[1];
					if (as.length >= 3) {
						s1 = s1 + ",";
					}
				}

				return s1;
			}
			else if (as.length < 3) {
				return "";
			}
			else {
				s1 = as[2];
				if (as.length >= 4) {
					s1 = s1 + ", " + as[3];
				}

				return s1;
			}
		}
		else {
			return "";
		}
	}

	public abstract int tier();

	public abstract int stat(int var1);

	public abstract String[] attributes();

	public abstract int getSpriteIndex(TECrop var1);

	public String getTextureFile() {
		return "/ic2/sprites/crops_0.png";
	}

	public int growthDuration(TECrop tecrop) {
		return this.tier() * 200;
	}

	public abstract boolean canGrow(TECrop var1);

	public int weightInfluences(TECrop tecrop, float f, float f1, float f2) {
		return (int) (f + f1 + f2);
	}

	public boolean canCross(TECrop tecrop) {
		return tecrop.size >= 3;
	}

	public boolean rightclick(TECrop tecrop, EntityHuman entityhuman) {
		return tecrop.harvest(true);
	}

	public abstract boolean canBeHarvested(TECrop var1);

	public float dropGainChance() {
		float f = 1.0F;

		for (int i = 0; i < this.tier(); ++i) {
			f = (float) ((double) f * 0.95D);
		}

		return f;
	}

	public abstract ItemStack getGain(TECrop var1);

	public byte getSizeAfterHarvest(TECrop tecrop) {
		return 1;
	}

	public boolean leftclick(TECrop tecrop, EntityHuman entityhuman) {
		return tecrop.pick(true);
	}

	public float dropSeedChance(TECrop tecrop) {
		if (tecrop.size == 1) {
			return 0.0F;
		}
		else {
			float f = 0.5F;
			if (tecrop.size == 2) {
				f /= 2.0F;
			}

			for (int i = 0; i < this.tier(); ++i) {
				f = (float) ((double) f * 0.8D);
			}

			return f;
		}
	}

	public ItemStack getSeeds(TECrop tecrop) {
		return tecrop.generateSeeds(tecrop.id, tecrop.statGrowth, tecrop.statGain, tecrop.statResistance, tecrop.scanLevel);
	}

	public void onNeighbourChange(TECrop tecrop) {
	}

	public boolean emitRedstone(TECrop tecrop) {
		return false;
	}

	public void onBlockDestroyed(TECrop tecrop) {
	}

	public int getEmittedLight(TECrop tecrop) {
		return 0;
	}

	public boolean onEntityCollision(TECrop tecrop, Entity entity) {
		if (entity instanceof EntityLiving) {
			org.bukkit.entity.Entity bukkitentity = entity.getBukkitEntity();
			if (bukkitentity instanceof Player) {
				Player player = (Player) bukkitentity;
				BlockBreakEvent breakev = new BlockBreakEvent(player.getWorld().getBlockAt(tecrop.x, tecrop.y, tecrop.z), player);
				Bukkit.getPluginManager().callEvent(breakev);
				if (breakev.isCancelled()) {
					return false;
				}

				breakev.setCancelled(true);
			}

			return ((EntityLiving) entity).motY < 0.1D || entity.isSprinting();
		}
		else {
			return false;
		}
	}

	public void tick(TECrop tecrop) {
	}

	public boolean isWeed(TECrop tecrop) {
		return tecrop.size >= 2 && (tecrop.id == 0 || tecrop.statGrowth >= 24);
	}

	public final int getId() {
		for (int i = 0; i < cropCardList.length; ++i) {
			if (this == cropCardList[i]) {
				return i;
			}
		}

		return -1;
	}
}
