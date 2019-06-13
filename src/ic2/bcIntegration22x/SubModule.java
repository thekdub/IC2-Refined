package ic2.bcIntegration22x;

import ic2.bcIntegration22x.common.TileEntityGeoGeneratorBc22x;
import ic2.common.BlockGenerator;
import net.minecraft.server.ModLoader;

public class SubModule {
	public static boolean init() {
		BlockGenerator.tileEntityGeoGeneratorClass = TileEntityGeoGeneratorBc22x.class;
		ModLoader.registerTileEntity(TileEntityGeoGeneratorBc22x.class, "Geothermal Generator");
		return true;
	}
}
