package ic2.common;

import forge.ForgeHooks;
import forge.ITextureProvider;
import ic2.api.IElectricItem;
import ic2.platform.ItemToolCommon;
import net.minecraft.server.Block;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EnumToolMaterial;
import net.minecraft.server.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class ItemElectricTool extends ItemToolCommon implements IElectricItem, ITextureProvider {
  public int operationEnergyCost;
  public int maxCharge;
  public int transferLimit;
  public int tier;
  public Set mineableBlocks = new HashSet();

  public ItemElectricTool(int i, int j, EnumToolMaterial enumtoolmaterial, int k) {
    super(i, 0, enumtoolmaterial, new Block[0]);
    this.textureId = j;
    this.operationEnergyCost = k;
    this.setMaxDurability(27);
    this.e(1);
  }

  public float getDestroySpeed(ItemStack itemstack, Block block) {
    if (!ElectricItem.canUse(itemstack, this.operationEnergyCost)) {
      return 1.0F;
    }
    else if (ForgeHooks.isToolEffective(itemstack, block, 0)) {
      return this.a;
    }
    else {
      return this.canDestroySpecialBlock(block) ? this.a : 1.0F;
    }
  }

  public float getStrVsBlock(ItemStack itemstack, Block block, int i) {
    if (!ElectricItem.canUse(itemstack, this.operationEnergyCost)) {
      return 1.0F;
    }
    else if (ForgeHooks.isToolEffective(itemstack, block, i)) {
      return this.a;
    }
    else {
      return this.canDestroySpecialBlock(block) ? this.a : 1.0F;
    }
  }

  public boolean canDestroySpecialBlock(Block block) {
    return this.mineableBlocks.contains(block);
  }

  public boolean a(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
    return true;
  }

  public boolean a(ItemStack itemstack, int i, int j, int k, int l, EntityLiving entityliving) {
    return true;
  }

  public int c() {
    return 0;
  }

  public boolean isRepairable() {
    return false;
  }

  public String getTextureFile() {
    return "/ic2/sprites/item_0.png";
  }

  public boolean canProvideEnergy() {
    return false;
  }

  public int getChargedItemId() {
    return this.id;
  }

  public int getEmptyItemId() {
    return this.id;
  }

  public int getMaxCharge() {
    return this.maxCharge;
  }

  public int getTier() {
    return this.tier;
  }

  public int getTransferLimit() {
    return this.transferLimit;
  }

  public void addCreativeItems(ArrayList arraylist) {
    ItemStack itemstack = new ItemStack(this, 1);
    ElectricItem.charge(itemstack, Integer.MAX_VALUE, Integer.MAX_VALUE, true, false);
    arraylist.add(itemstack);
    arraylist.add(new ItemStack(this, 1, this.getMaxDurability()));
  }
}
