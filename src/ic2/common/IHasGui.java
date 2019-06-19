package ic2.common;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.IInventory;

public interface IHasGui extends IInventory {
  ContainerIC2 getGuiContainer(EntityHuman var1);

  String getGuiClassName(EntityHuman var1);

  void onGuiClosed(EntityHuman var1);
}
