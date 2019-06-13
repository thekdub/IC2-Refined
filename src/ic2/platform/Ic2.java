package ic2.platform;

import forge.Configuration;
import forge.NetworkMod;
import ic2.common.IC2DamageSource;

public abstract class Ic2 extends NetworkMod {
	public void load() {
		IC2DamageSource.addLocalization(null, this);
	}

	public void addLocalization(Configuration configuration, String s, String s1) {
		Platform.AddLocalization(s, s1);
	}
}
