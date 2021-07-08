package ic2.api;

import net.minecraft.server.EntityHuman;

public interface IWrenchable {
  boolean wrenchCanSetFacing(EntityHuman var1, int var2);
  
  short getFacing();
  
  void setFacing(short var1);
  
  boolean wrenchCanRemove(EntityHuman var1);
  
  float getWrenchDropRate();
}
