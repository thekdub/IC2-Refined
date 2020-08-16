package ic2.common;

import forge.ITextureProvider;
import ic2.platform.ItemCommon;
import net.minecraft.server.ItemStack;

import java.util.ArrayList;

public class ItemIC2 extends ItemCommon implements ITextureProvider {
  private boolean addToCreative = true;
  private int creativeMeta = 0;
  private int rarity = 0;
  
  public ItemIC2(int i, int j) {
    super(i);
    this.d(j);
  }
  
  public String getTextureFile() {
    return "/ic2/sprites/item_0.png";
  }
  
  public ItemIC2 hideFromCreative() {
    this.addToCreative = false;
    return this;
  }
  
  public ItemIC2 setShownInCreative(boolean flag) {
    this.addToCreative = flag;
    return this;
  }
  
  public ItemIC2 setCreativeMeta(int i) {
    this.creativeMeta = i;
    return this;
  }
  
  public ItemIC2 setRarity(int i) {
    this.rarity = i;
    return this;
  }
  
  public int rarity(ItemStack itemstack) {
    return this.rarity;
  }
  
  public void addCreativeItems(ArrayList arraylist) {
    if (this.addToCreative) {
      arraylist.add(new ItemStack(this, 1, this.creativeMeta));
    }
    
  }
}
