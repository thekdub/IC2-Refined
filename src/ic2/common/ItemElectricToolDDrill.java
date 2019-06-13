package ic2.common;

import net.minecraft.server.Block;
import net.minecraft.server.EnumToolMaterial;

public class ItemElectricToolDDrill extends ItemElectricToolDrill {
	public ItemElectricToolDDrill(int i, int j) {
		super(i, j, EnumToolMaterial.DIAMOND, 80);
		this.maxCharge = 10000;
		this.transferLimit = 100;
		this.tier = 1;
		this.a = 16.0F;
	}

	public void init() {
		super.init();
		this.mineableBlocks.add(Block.OBSIDIAN);
	}
}
