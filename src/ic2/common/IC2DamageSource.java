package ic2.common;

import forge.Configuration;
import ic2.platform.Ic2;
import net.minecraft.server.DamageSource;

public class IC2DamageSource extends DamageSource {
  public static IC2DamageSource electricity = new IC2DamageSource("electricity");
  public static IC2DamageSource nuke = new IC2DamageSource("nuke");
  public static IC2DamageSource radiation = (IC2DamageSource) (new IC2DamageSource("radiation")).h();
  
  public IC2DamageSource(String s) {
    super(s);
  }
  
  public static void addLocalization(Configuration configuration, Ic2 ic2) {
    ic2.addLocalization(configuration, "death.electricity", "%1$s was electrocuted");
    ic2.addLocalization(configuration, "death.nuke", "%1$s was nuked");
    ic2.addLocalization(configuration, "death.radiation", "%1$s died from radiation");
  }
}
