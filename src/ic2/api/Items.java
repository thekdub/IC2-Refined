package ic2.api;

import net.minecraft.server.ItemStack;

public final class Items {
	public static ItemStack getItem(String s) {
		try {
			Object obj = Class.forName(getPackage() + ".common.Ic2Items").getField(s).get(null);
			return obj instanceof ItemStack ? (ItemStack) obj : null;
		} catch (Exception var2) {
			System.out.println("IC2 API: Call getItem failed for " + s);
			return null;
		}
	}

	private static String getPackage() {
		Package package1 = Items.class.getPackage();
		return package1 != null ? package1.getName().substring(0, package1.getName().lastIndexOf(46)) : "ic2";
	}
}
