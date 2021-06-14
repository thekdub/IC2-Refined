package ic2.common;

import forge.oredict.OreDictionary;
import ic2.api.IElectricItem;
import net.minecraft.server.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftShapedRecipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public class AdvRecipe implements CraftingRecipe {
  public ItemStack output;
  public Object[] input;
  public int inputWidth;
  public boolean hidden;
  private int width;
  private final int height;
  private final ItemStack[] items;
  
  public AdvRecipe(ItemStack itemstack, Object[] aobj) {
    if (itemstack == null) {
      throw new RuntimeException("invalid recipe, result = null");
    }
    else {
      HashMap hashmap = new HashMap();
      Vector vector = new Vector();
      Character character = null;
      Object[] aobj1 = aobj;
      int j = aobj.length;
      
      int ii1;
      Object iobj;
      for (ii1 = 0; ii1 < j; ++ii1) {
        iobj = aobj1[ii1];
        if (iobj instanceof String) {
          if (((String) iobj).length() < 4) {
            this.width = ((String) iobj).length();
          }
  
          if (character == null) {
            if (!hashmap.isEmpty()) {
              throw new RuntimeException("invalid recipe, ore dict name without preceding mapping char");
            }
    
            vector.add(iobj);
          }
          else {
            hashmap.put(character, iobj);
            character = null;
          }
        }
        else if (iobj instanceof Character) {
          if (character != null) {
            throw new RuntimeException("invalid recipe, 2 consecutive char definitions");
          }
  
          character = (Character) iobj;
        }
        else if (!(iobj instanceof ItemStack) && !(iobj instanceof Block) && !(iobj instanceof Item)) {
          if (!(iobj instanceof Boolean)) {
            throw new RuntimeException("invalid recipe, invalid object type in ingredient list");
          }
  
          this.hidden = (Boolean) iobj;
        }
        else {
          if (character == null) {
            throw new RuntimeException("invalid recipe, item without preceding mapping char");
          }
  
          if (iobj instanceof Block) {
            iobj = new ItemStack((Block) iobj, 1, -1);
          }
          else if (iobj instanceof Item) {
            iobj = new ItemStack((Item) iobj, 1, -1);
          }
  
          hashmap.put(character, iobj);
          character = null;
        }
      }
      
      if (character != null) {
        throw new RuntimeException("invalid recipe, unused mapping char");
      }
      else if (vector.size() != 0 && vector.size() <= 3) {
        if (hashmap.size() == 0) {
          throw new RuntimeException("invalid recipe, no char mappings defined");
        }
        else {
          this.height = vector.size();
          this.items = new ItemStack[this.width * this.height];
          ii1 = 0;
          Iterator iterator = hashmap.values().iterator();
  
          while (iterator.hasNext()) {
            iobj = iterator.next();
            if (iobj instanceof ItemStack) {
              this.items[ii1++] = (ItemStack) iobj;
            }
            else if (iobj instanceof String) {
              oreDictionaryIC2 temp = oreDictionaryIC2.valueOf((String) iobj);
              ItemStack stack = temp.getItemStack();
              if (stack != null) {
                this.items[ii1++] = stack;
              }
            }
          }
  
          this.inputWidth = ((String) vector.get(0)).length();
          this.input = new Object[this.inputWidth * vector.size()];
          int i = 0;
          iterator = vector.iterator();
  
          while (iterator.hasNext()) {
            String s = (String) iterator.next();
            if (s.length() != this.inputWidth) {
              throw new RuntimeException("invalid recipe, non-constant input ingredient list width");
            }
    
            for (int l = 0; l < s.length(); ++l) {
              char c = s.charAt(l);
              if (c == ' ') {
                this.input[i++] = null;
              }
              else {
                if (!hashmap.containsKey(c)) {
                  throw new RuntimeException("invalid recipe, char mapping missing for char: " + c);
                }
  
                this.input[i++] = hashmap.get(c);
              }
            }
          }
  
          this.output = itemstack;
        }
      }
      else {
        throw new RuntimeException("invalid recipe, zero or too many input recipe arrangements");
      }
    }
  }
  
  public static void addAndRegister(ItemStack itemstack, Object[] aobj) {
    CraftingManager.getInstance().getRecipies().add(new AdvRecipe(itemstack, aobj));
  }
  
  public static boolean recipeContains(Object[] aobj, ItemStack itemstack) {
    Object[] aobj1 = aobj;
    int i = aobj.length;
    
    for (int j = 0; j < i; ++j) {
      Object obj = aobj1[j];
      if (obj != null) {
        List list = resolveOreDict(obj);
        Iterator iterator = list.iterator();
  
        while (iterator.hasNext()) {
          ItemStack itemstack1 = (ItemStack) iterator.next();
          if (itemstack.doMaterialsMatch(itemstack1)) {
            return true;
          }
        }
      }
    }
    
    return false;
  }
  
  public static boolean canShow(Object[] aobj, ItemStack itemstack, boolean flag) {
    return (!mod_IC2.enableSecretRecipeHiding || !flag) && !recipeContains(aobj, Ic2Items.reBattery) &&
        (!recipeContains(aobj, Ic2Items.industrialDiamond) || itemstack.id != Item.DIAMOND.id);
  }
  
  public static boolean canShow(AdvRecipe advrecipe) {
    return canShow(advrecipe.input, advrecipe.output, advrecipe.hidden);
  }
  
  public static boolean canShow(AdvShapelessRecipe advshapelessrecipe) {
    return canShow(advshapelessrecipe.input, advshapelessrecipe.output, advshapelessrecipe.hidden);
  }
  
  public static List resolveOreDict(Object obj) {
    Object obj1;
    if (obj instanceof String) {
      obj1 = OreDictionary.getOres((String) obj);
    }
    else {
      if (!(obj instanceof ItemStack)) {
        throw new RuntimeException("Recipe contains invalid ingredient: " + obj);
      }
  
      obj1 = new Vector(1);
      ((List) obj1).add(obj);
    }
    
    return (List) obj1;
  }
  
  public boolean a(InventoryCrafting inventorycrafting) {
    return this.b(inventorycrafting) != null;
  }
  
  public ItemStack b(InventoryCrafting inventorycrafting) {
    int i = this.input.length / this.inputWidth;
    byte byte0 = (byte) (inventorycrafting.getSize() != 9 ? 2 : 3);
    if (byte0 >= this.inputWidth && byte0 >= i) {
      for (int j = 0; j <= byte0 - this.inputWidth; ++j) {
        label173:
        for (int k = 0; k <= byte0 - i; ++k) {
          int l = 0;
  
          int i2;
          int j3;
          for (i2 = 0; i2 < this.inputWidth; ++i2) {
            for (j3 = 0; j3 < i; ++j3) {
              ItemStack itemstack1 = inventorycrafting.b(i2 + j, j3 + k);
              Object obj = this.input[i2 + j3 * this.inputWidth];
              if (itemstack1 == null && obj != null) {
                continue label173;
              }
  
              if (itemstack1 != null) {
                if (obj == null) {
                  return null;
                }
    
                List list = resolveOreDict(obj);
                boolean flag = false;
                Iterator iterator = list.iterator();
    
                while (iterator.hasNext()) {
                  ItemStack itemstack2 = (ItemStack) iterator.next();
                  if (itemstack1.getItem() instanceof IElectricItem) {
                    if (itemstack1.id == itemstack2.id) {
                      l += ElectricItem.discharge(itemstack1, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true);
                      flag = true;
                      break;
                    }
                  }
                  else if (itemstack1.doMaterialsMatch(itemstack2) ||
                      itemstack2.getData() == -1 && itemstack1.id == itemstack2.id) {
                    flag = true;
                    break;
                  }
                }
    
                if (!flag) {
                  return null;
                }
              }
            }
          }
  
          for (i2 = 0; i2 < j; ++i2) {
            for (j3 = 0; j3 < byte0; ++j3) {
              if (inventorycrafting.b(i2, j3) != null) {
                return null;
              }
            }
          }
  
          for (i2 = 0; i2 < k; ++i2) {
            for (j3 = 0; j3 < byte0; ++j3) {
              if (inventorycrafting.b(j3, i2) != null) {
                return null;
              }
            }
          }
  
          for (i2 = j + this.inputWidth; i2 < byte0; ++i2) {
            for (j3 = 0; j3 < byte0; ++j3) {
              if (inventorycrafting.b(i2, j3) != null) {
                return null;
              }
            }
          }
  
          for (i2 = k + i; i2 < byte0; ++i2) {
            for (j3 = 0; j3 < byte0; ++j3) {
              if (inventorycrafting.b(j3, i2) != null) {
                return null;
              }
            }
          }
  
          ItemStack itemstack = this.output.cloneItemStack();
          if (itemstack.getItem() instanceof IElectricItem) {
            ElectricItem.charge(itemstack, l, Integer.MAX_VALUE, true, false);
          }
  
          return itemstack;
        }
      }
  
      return null;
    }
    else {
      return null;
    }
  }
  
  public int a() {
    return this.input.length;
  }
  
  public ItemStack b() {
    return this.output;
  }
  
  public ShapedRecipe toBukkitRecipe() {
    CraftShapedRecipe recipe;
    CraftItemStack result = new CraftItemStack(this.output);
    recipe = new CraftShapedRecipe(result);
    label47:
    switch (this.inputWidth) {
      case 1:
        switch (this.height) {
          case 1:
            recipe.shape("a");
            break label47;
          case 2:
            recipe.shape("ab");
            break label47;
          case 3:
            recipe.shape("abc");
          default:
            break label47;
        }
      case 2:
        switch (this.height) {
          case 1:
            recipe.shape("a", "b");
            break label47;
          case 2:
            recipe.shape("ab", "cd");
            break label47;
          case 3:
            recipe.shape("abc", "def");
          default:
            break label47;
        }
      case 3:
        switch (this.height) {
          case 1:
            recipe.shape("a", "b", "c");
            break;
          case 2:
            recipe.shape("ab", "cd", "ef");
            break;
          case 3:
            recipe.shape("abc", "def", "ghi");
        }
    }
    
    char c = 'a';
    Object[] var7;
    int var6 = (var7 = this.input).length;
    
    for (int var5 = 0; var5 < var6; ++var5) {
      Object inputObject = var7[var5];
      if (inputObject != null) {
        ItemStack stack = null;
        if (inputObject instanceof ItemStack) {
          stack = (ItemStack) inputObject;
        }
        else if (inputObject instanceof String) {
          oreDictionaryIC2 temp = oreDictionaryIC2.valueOf((String) inputObject);
          stack = temp.getItemStack();
        }
  
        recipe.setIngredient(c, Material.getMaterial(stack.id), stack.getData());
      }
      
      ++c;
    }
    
    return recipe;
  }
  
  public enum oreDictionaryIC2 {
    oreCopper(Ic2Items.copperOre),
    oreTin(Ic2Items.tinOre),
    oreUranium(Ic2Items.uraniumOre),
    itemDropUranium(Ic2Items.uraniumDrop),
    ingotBronze(Ic2Items.bronzeIngot),
    ingotCopper(Ic2Items.copperIngot),
    ingotRefinedIron(Ic2Items.refinedIronIngot),
    ingotTin(Ic2Items.tinIngot),
    ingotUranium(Ic2Items.uraniumIngot),
    ingotSilver((ItemStack) null),
    itemRubber(Ic2Items.rubber),
    dyeBlue(Item.INK_SACK);
    
    private final ItemStack value;
    
    oreDictionaryIC2(ItemStack value) {
      this.value = value;
    }
    
    oreDictionaryIC2(Item value) {
      this.value = new ItemStack(value);
    }
    
    public ItemStack getItemStack() {
      if (this.toString().equalsIgnoreCase("ingotSilver")) {
        ArrayList ingotSilvers = OreDictionary.getOres("ingotSilver");
        return ingotSilvers.size() != 0 ? (ItemStack) ingotSilvers.get(0) : null;
      }
      else {
        return this.value;
      }
    }
  }
}
