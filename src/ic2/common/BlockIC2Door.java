package ic2.common;

import forge.ITextureProvider;
import net.minecraft.server.BlockDoor;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.Material;

import java.util.ArrayList;
import java.util.Random;

public class BlockIC2Door extends BlockDoor implements ITextureProvider {
	public int spriteIndexTop;
	public int spriteIndexBottom;
	public int itemDropped;

	public BlockIC2Door(int i, int j, int k, Material material) {
		super(i, material);
		this.spriteIndexTop = j;
		this.spriteIndexBottom = k;
		this.textureId = 14;
	}

	public BlockIC2Door setItemDropped(int i) {
		this.itemDropped = i;
		return this;
	}

	public int a(int i, int j) {
		return (j & 8) == 8 ? this.spriteIndexTop : this.spriteIndexBottom;
	}

	public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return this.a(l, iblockaccess.getData(i, j, k));
	}

	public int getDropType(int i, Random random, int j) {
		return (i & 8) == 8 ? 0 : this.itemDropped;
	}

	public String getTextureFile() {
		return "/ic2/sprites/block_0.png";
	}

	public void addCreativeItems(ArrayList arraylist) {
	}
}
