package ic2.platform;

import net.minecraft.server.ItemDoor;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;

public class ItemDoorCommon extends ItemDoor {
  public ItemDoorCommon(int i, Material material) {
    super(i, material);
  }

  public int rarity(ItemStack itemstack) {
    return 0;
  }
}
