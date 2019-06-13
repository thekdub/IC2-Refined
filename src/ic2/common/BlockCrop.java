package ic2.common;

import forge.ITextureProvider;
import ic2.platform.BlockContainerCommon;
import ic2.platform.ItemBlockCommon;
import net.minecraft.server.*;

import java.util.ArrayList;

public class BlockCrop extends BlockContainerCommon implements ITextureProvider {
	public static TileEntityCrop tempStore;

	public BlockCrop(int i) {
		super(i, Material.PLANT);
		this.c(0.8F);
		this.b(0.2F);
		this.a("blockCrop");
		this.a(g);
		ModLoader.registerBlock(this, ItemBlockCommon.class);
		Ic2Items.crop = new ItemStack(this, 1, 0);
	}

	public TileEntity a_() {
		return new TileEntityCrop();
	}

	public String getTextureFile() {
		return "/ic2/sprites/crops_0.png";
	}

	public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return ((TileEntityCrop) iblockaccess.getTileEntity(i, j, k)).getSprite();
	}

	public int a(int i, int j) {
		return 0;
	}

	public boolean canPlace(World world, int i, int j, int k) {
		return world.getTypeId(i, j - 1, k) == Block.SOIL.id && super.canPlace(world, i, j, k);
	}

	public void doPhysics(World world, int i, int j, int k, int l) {
		super.doPhysics(world, i, j, k, l);
		if (world.getTypeId(i, j - 1, k) != Block.SOIL.id) {
			world.setTypeId(i, j, k, 0);
			this.b(world, i, j, k, 0, 0);
		}
		else {
			((TileEntityCrop) world.getTileEntity(i, j, k)).onNeighbourChange();
		}

	}

	public AxisAlignedBB e(World world, int i, int j, int k) {
		double d = 0.2D;
		return AxisAlignedBB.b(d, 0.0D, d, 1.0D - d, 0.7D, 1.0D - d);
	}

	public void a(World world, int i, int j, int k, Entity entity) {
		((TileEntityCrop) world.getTileEntity(i, j, k)).onEntityCollision(entity);
	}

	public boolean a() {
		return false;
	}

	public boolean b() {
		return false;
	}

	public int c() {
		return mod_IC2.cropRenderId;
	}

	public boolean a(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return ((TileEntityCrop) iblockaccess.getTileEntity(i, j, k)).emitRedstone();
	}

	public void remove(World world, int i, int j, int k) {
		if (world != null) {
			tempStore = (TileEntityCrop) world.getTileEntity(i, j, k);
		}

		super.remove(world, i, j, k);
	}

	public void wasExploded(World world, int i, int j, int k) {
		if (tempStore != null) {
			tempStore.onBlockDestroyed();
		}

	}

	public int getLightValue(IBlockAccess iblockaccess, int i, int j, int k) {
		return ((TileEntityCrop) iblockaccess.getTileEntity(i, j, k)).getEmittedLight();
	}

	public void attack(World world, int i, int j, int k, EntityHuman entityhuman) {
		((TileEntityCrop) world.getTileEntity(i, j, k)).leftclick(entityhuman);
	}

	public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
		return ((TileEntityCrop) world.getTileEntity(i, j, k)).rightclick(entityhuman);
	}

	public void addCreativeItems(ArrayList arraylist) {
		arraylist.add(new ItemStack(this));
	}
}
