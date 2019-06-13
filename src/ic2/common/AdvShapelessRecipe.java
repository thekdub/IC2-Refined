package ic2.common;

import ic2.api.IElectricItem;
import net.minecraft.server.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftShapelessRecipe;
import org.bukkit.inventory.Recipe;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class AdvShapelessRecipe implements CraftingRecipe {
	public ItemStack output;
	public Object[] input;
	public boolean hidden;

	public AdvShapelessRecipe(ItemStack itemstack, Object[] aobj) {
		if (itemstack == null) {
			throw new RuntimeException("invalid recipe, result = null");
		}
		else {
			this.input = new Object[aobj.length - Util.countInArray(aobj, Boolean.class)];
			int i = 0;
			Object[] aobj1 = aobj;
			int j = aobj.length;

			for (int k = 0; k < j; ++k) {
				Object obj = aobj1[k];
				if (obj instanceof String) {
					this.input[i++] = obj;
				}
				else if (!(obj instanceof ItemStack) && !(obj instanceof Block) && !(obj instanceof Item)) {
					if (!(obj instanceof Boolean)) {
						throw new RuntimeException("invalid recipe, invalid object type in ingredient list");
					}

					this.hidden = (Boolean) obj;
				}
				else {
					if (obj instanceof Block) {
						obj = new ItemStack((Block) obj, 1, -1);
					}
					else if (obj instanceof Item) {
						obj = new ItemStack((Item) obj, 1, -1);
					}

					this.input[i++] = obj;
				}
			}

			if (i != this.input.length) {
				throw new RuntimeException("error during recipe length calculation");
			}
			else {
				this.output = itemstack;
			}
		}
	}

	public static void addAndRegister(ItemStack itemstack, Object[] aobj) {
		CraftingManager.getInstance().getRecipies().add(new AdvShapelessRecipe(itemstack, aobj));
	}

	public boolean a(InventoryCrafting inventorycrafting) {
		return this.b(inventorycrafting) != null;
	}

	public ItemStack b(InventoryCrafting inventorycrafting) {
		int i = inventorycrafting.getSize();
		if (i < this.input.length) {
			return null;
		}
		else {
			Vector vector = new Vector();
			Object[] aobj = this.input;
			int k = aobj.length;

			int j;
			for (j = 0; j < k; ++j) {
				Object obj = aobj[j];
				vector.add(obj);
			}

			j = 0;

			label63:
			for (int l = 0; l < i; ++l) {
				ItemStack itemstack1 = inventorycrafting.getItem(l);
				if (itemstack1 != null) {
					for (int j1 = 0; j1 < vector.size(); ++j1) {
						List list = AdvRecipe.resolveOreDict(vector.get(j1));
						Iterator iterator = list.iterator();

						while (iterator.hasNext()) {
							ItemStack itemstack2 = (ItemStack) iterator.next();
							if (!(itemstack1.getItem() instanceof IElectricItem)) {
								if (itemstack1.doMaterialsMatch(itemstack2) || itemstack2.getData() == -1 && itemstack1.id == itemstack2.id) {
									vector.remove(j1);
									continue label63;
								}
							}
							else if (itemstack1.id == itemstack2.id) {
								j += ElectricItem.discharge(itemstack1, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true);
								vector.remove(j1);
								continue label63;
							}
						}
					}

					return null;
				}
			}

			if (!vector.isEmpty()) {
				return null;
			}
			else {
				ItemStack itemstack = this.output.cloneItemStack();
				if (itemstack.getItem() instanceof IElectricItem) {
					ElectricItem.charge(itemstack, j, Integer.MAX_VALUE, true, false);
				}

				return itemstack;
			}
		}
	}

	public int a() {
		return this.input.length;
	}

	public ItemStack b() {
		return this.output;
	}

	public Recipe toBukkitRecipe() {
		CraftItemStack result = new CraftItemStack(this.output);
		CraftShapelessRecipe recipe = new CraftShapelessRecipe(result);
		Object[] var6;
		int var5 = (var6 = this.input).length;

		for (int var4 = 0; var4 < var5; ++var4) {
			Object inputObject = var6[var4];
			if (inputObject != null) {
				ItemStack stack = null;
				if (inputObject instanceof ItemStack) {
					stack = (ItemStack) inputObject;
				}
				else if (inputObject instanceof String) {
					AdvRecipe.oreDictionaryIC2 temp = AdvRecipe.oreDictionaryIC2.valueOf((String) inputObject);
					stack = temp.getItemStack();
				}

				recipe.addIngredient(Material.getMaterial(stack.id), stack.getData());
			}
		}

		return recipe;
	}
}
