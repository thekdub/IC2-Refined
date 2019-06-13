package ic2.api;

import net.minecraft.server.Block;
import net.minecraft.server.Item;
import net.minecraft.server.ItemStack;

import java.lang.reflect.Array;
import java.util.AbstractMap.SimpleEntry;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public final class Ic2Recipes {
	static final boolean $assertionsDisabled = !Ic2Recipes.class.desiredAssertionStatus();

	public static void addCraftingRecipe(ItemStack itemstack, Object[] aobj) {
		try {
			Class.forName(getPackage() + ".common.AdvRecipe").getMethod("addAndRegister", ItemStack.class, Array.newInstance(Object.class, 0).getClass()).invoke(null, itemstack, aobj);
		} catch (Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public static void addShapelessCraftingRecipe(ItemStack itemstack, Object[] aobj) {
		try {
			Class.forName(getPackage() + ".common.AdvShapelessRecipe").getMethod("addAndRegister", ItemStack.class, Array.newInstance(Object.class, 0).getClass()).invoke(null, itemstack, aobj);
		} catch (Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public static List getCompressorRecipes() {
		try {
			return (List) Class.forName(getPackage() + ".common.TileEntityCompressor").getField("recipes").get(null);
		} catch (Exception var1) {
			throw new RuntimeException(var1);
		}
	}

	public static void addCompressorRecipe(ItemStack itemstack, ItemStack itemstack1) {
		getCompressorRecipes().add(new SimpleEntry(itemstack, itemstack1));
	}

	public static ItemStack getCompressorOutputFor(ItemStack itemstack, boolean flag) {
		return getOutputFor(itemstack, flag, getCompressorRecipes());
	}

	public static List getExtractorRecipes() {
		try {
			return (List) Class.forName(getPackage() + ".common.TileEntityExtractor").getField("recipes").get(null);
		} catch (Exception var1) {
			throw new RuntimeException(var1);
		}
	}

	public static void addExtractorRecipe(ItemStack itemstack, ItemStack itemstack1) {
		getExtractorRecipes().add(new SimpleEntry(itemstack, itemstack1));
	}

	public static ItemStack getExtractorOutputFor(ItemStack itemstack, boolean flag) {
		return getOutputFor(itemstack, flag, getExtractorRecipes());
	}

	public static List getMaceratorRecipes() {
		try {
			return (List) Class.forName(getPackage() + ".common.TileEntityMacerator").getField("recipes").get(null);
		} catch (Exception var1) {
			throw new RuntimeException(var1);
		}
	}

	public static void addMaceratorRecipe(ItemStack itemstack, ItemStack itemstack1) {
		getMaceratorRecipes().add(new SimpleEntry(itemstack, itemstack1));
	}

	public static ItemStack getMaceratorOutputFor(ItemStack itemstack, boolean flag) {
		return getOutputFor(itemstack, flag, getMaceratorRecipes());
	}

	private static ItemStack getOutputFor(ItemStack itemstack, boolean flag, List list) {
		if (!$assertionsDisabled && itemstack == null) {
			throw new AssertionError();
		}
		else {
			Iterator iterator = list.iterator();

			Entry entry;
			do {
				if (!iterator.hasNext()) {
					return null;
				}

				entry = (Entry) iterator.next();
			} while (!((ItemStack) entry.getKey()).doMaterialsMatch(itemstack) || itemstack.count < ((ItemStack) entry.getKey()).count);

			if (flag) {
				itemstack.count -= ((ItemStack) entry.getKey()).count;
			}

			return ((ItemStack) entry.getValue()).cloneItemStack();
		}
	}

	public static List getRecyclerBlacklist() {
		try {
			return (List) Class.forName(getPackage() + ".common.TileEntityRecycler").getField("blacklist").get(null);
		} catch (Exception var1) {
			throw new RuntimeException(var1);
		}
	}

	public static void addRecyclerBlacklistItem(ItemStack itemstack) {
		getRecyclerBlacklist().add(itemstack);
	}

	public static void addRecyclerBlacklistItem(Item item) {
		addRecyclerBlacklistItem(new ItemStack(item));
	}

	public static void addRecyclerBlacklistItem(Block block) {
		addRecyclerBlacklistItem(new ItemStack(block));
	}

	public static boolean isRecyclerInputBlacklisted(ItemStack itemstack) {
		Iterator iterator = getRecyclerBlacklist().iterator();

		while (iterator.hasNext()) {
			ItemStack itemstack1 = (ItemStack) iterator.next();
			if (itemstack.doMaterialsMatch(itemstack1)) {
				return true;
			}
		}

		return false;
	}

	public static List getScrapboxDrops() {
		try {
			return (List) Class.forName(getPackage() + ".common.ItemScrapbox").getMethod("getDropList").invoke(null);
		} catch (Exception var1) {
			throw new RuntimeException(var1);
		}
	}

	public static void addScrapboxDrop(ItemStack itemstack, float f) {
		try {
			Class.forName(getPackage() + ".common.ItemScrapbox").getMethod("addDrop", ItemStack.class, Float.TYPE).invoke(null, itemstack, f);
		} catch (Exception var3) {
			throw new RuntimeException(var3);
		}
	}

	public static void addScrapboxDrop(Item item, float f) {
		addScrapboxDrop(new ItemStack(item), f);
	}

	public static void addScrapboxDrop(Block block, float f) {
		addScrapboxDrop(new ItemStack(block), f);
	}

	public static List getMatterAmplifiers() {
		try {
			return (List) Class.forName(getPackage() + ".common.TileEntityMatter").getField("amplifiers").get(null);
		} catch (Exception var1) {
			throw new RuntimeException(var1);
		}
	}

	public static void addMatterAmplifier(ItemStack itemstack, int i) {
		getMatterAmplifiers().add(new SimpleEntry(itemstack, i));
	}

	public static void addMatterAmplifier(Item item, int i) {
		addMatterAmplifier(new ItemStack(item), i);
	}

	public static void addMatterAmplifier(Block block, int i) {
		addMatterAmplifier(new ItemStack(block), i);
	}

	private static String getPackage() {
		Package package1 = Ic2Recipes.class.getPackage();
		return package1 != null ? package1.getName().substring(0, package1.getName().lastIndexOf(46)) : "ic2";
	}
}
