package ic2.common;

import forge.ITextureProvider;
import net.minecraft.server.BlockGlass;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Material;

import java.util.ArrayList;

public class BlockTexGlass extends BlockGlass implements ITextureProvider {
	protected boolean addToCreative = true;

	public BlockTexGlass(int i, int j, Material material, boolean flag) {
		super(i, j, material, flag);
	}

	public String getTextureFile() {
		return "/ic2/sprites/block_0.png";
	}

	public void addCreativeItems(ArrayList arraylist) {
		if (this.addToCreative) {
			arraylist.add(new ItemStack(this));
		}

	}
}
