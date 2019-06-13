package ic2.common;

import forge.Configuration;
import forge.Property;
import ic2.api.Ic2Recipes;
import net.minecraft.server.Block;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class TileEntityRecycler extends TileEntityElectricMachine {
	public static List blacklist = new Vector();

	public TileEntityRecycler() {
		super(3, 1, 45, 32);
	}

	public static void init(Configuration configuration) {
		addBlacklistItem(Block.THIN_GLASS);
		addBlacklistItem(Item.STICK);
		addBlacklistItem(Item.SNOW_BALL);
		addBlacklistItem(Ic2Items.scaffold);
		Property property = configuration.getOrCreateProperty("recyclerBlacklist", "general", getRecyclerBlacklistString());
		property.comment = "List of blocks and items which should not be turned into scrap by the recycler. Comma separated, format is id-metadata";
		setRecyclerBlacklistFromString(property.value);
	}

	public static int recycleChance() {
		return 8;
	}

	public static void addBlacklistItem(Item item) {
		addBlacklistItem(new ItemStack(item));
	}

	public static void addBlacklistItem(Block block) {
		addBlacklistItem(new ItemStack(block));
	}

	public static void addBlacklistItem(ItemStack itemstack) {
		blacklist.add(itemstack);
	}

	public static boolean getIsItemBlacklisted(ItemStack itemstack) {
		Iterator iterator = blacklist.iterator();

		ItemStack itemstack1;
		do {
			if (!iterator.hasNext()) {
				return false;
			}

			itemstack1 = (ItemStack) iterator.next();
		} while (!itemstack.doMaterialsMatch(itemstack1) && (itemstack1.getData() != -1 || itemstack1.id != itemstack.id));

		return true;
	}

	private static String getRecyclerBlacklistString() {
		StringBuilder stringbuilder = new StringBuilder();
		boolean flag = true;
		Iterator iterator = Ic2Recipes.getRecyclerBlacklist().iterator();

		while (iterator.hasNext()) {
			ItemStack itemstack = (ItemStack) iterator.next();
			if (itemstack != null) {
				if (flag) {
					flag = false;
				}
				else {
					stringbuilder.append(", ");
				}

				stringbuilder.append(itemstack.id);
				if (itemstack.getData() != 0) {
					stringbuilder.append("-");
					stringbuilder.append(itemstack.getData());
				}
			}
		}

		return stringbuilder.toString();
	}

	private static void setRecyclerBlacklistFromString(String s) {
		String[] as = s.trim().split("\\s*,\\s*");
		String[] as1 = as;
		int i = as.length;

		for (int j = 0; j < i; ++j) {
			String s1 = as1[j];
			String[] as2 = s1.split("\\s*-\\s*");
			if (as2[0].length() != 0) {
				int k = Integer.parseInt(as2[0]);
				int l = -1;
				if (as2.length == 2) {
					l = Integer.parseInt(as2[1]);
				}

				ItemStack itemstack = new ItemStack(k, 1, l);
				Ic2Recipes.addRecyclerBlacklistItem(itemstack);
			}
		}

	}

	public void operate() {
		if (this.canOperate()) {
			boolean flag = getIsItemBlacklisted(this.inventory[0]);
			--this.inventory[0].count;
			if (this.inventory[0].count <= 0) {
				this.inventory[0] = null;
			}

			if (this.world.random.nextInt(recycleChance()) == 0 && !flag) {
				if (this.inventory[2] == null) {
					this.inventory[2] = Ic2Items.scrap.cloneItemStack();
				}
				else {
					++this.inventory[2].count;
				}
			}

		}
	}

	public boolean canOperate() {
		if (this.inventory[0] == null) {
			return false;
		}
		else {
			return this.inventory[2] == null || this.inventory[2].doMaterialsMatch(Ic2Items.scrap) && this.inventory[2].count < Ic2Items.scrap.getMaxStackSize();
		}
	}

	public ItemStack getResultFor(ItemStack itemstack, boolean flag) {
		return null;
	}

	public String getName() {
		return "Recycler";
	}

	public String getGuiClassName(EntityHuman entityhuman) {
		return "GuiRecycler";
	}

	public String getStartSoundFile() {
		return "Machines/RecyclerOp.ogg";
	}

	public String getInterruptSoundFile() {
		return "Machines/InterruptOne.ogg";
	}

	public float getWrenchDropRate() {
		return 0.85F;
	}
}
