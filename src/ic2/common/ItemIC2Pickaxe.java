package ic2.common;

import forge.ITextureProvider;
import ic2.platform.ItemPickaxeCommon;
import net.minecraft.server.EnumToolMaterial;

public class ItemIC2Pickaxe extends ItemPickaxeCommon implements ITextureProvider {
	public float a;

	public ItemIC2Pickaxe(int i, int j, EnumToolMaterial enumtoolmaterial, float f) {
		super(i, enumtoolmaterial);
		this.a = f;
		this.d(j);
	}

	public String getTextureFile() {
		return "/ic2/sprites/item_0.png";
	}

	public int c() {
		return 13;
	}
}
