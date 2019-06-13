package ic2.common;

import forge.ITextureProvider;
import ic2.api.IElectricItem;
import ic2.platform.BlockContainerCommon;
import ic2.platform.ItemBlockCommon;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.ArrayList;
import java.util.Random;

public class BlockLuminator extends BlockContainerCommon implements ITextureProvider {
	boolean light;

	public BlockLuminator(int i, boolean flag) {
		super(i, 31, Material.SHATTERABLE);
		this.a(j);
		this.light = flag;
		this.c(0.3F);
		this.b(0.5F);
		ModLoader.registerBlock(this, ItemBlockCommon.class);
	}

	public static boolean isSupportingBlock(World world, int i, int j, int k) {
		if (world.getTypeId(i, j, k) == 0) {
			return false;
		}
		else {
			return world.r(i, j, k) || isSpecialSupporter(world, i, j, k);
		}
	}

	public static boolean isSpecialSupporter(IBlockAccess iblockaccess, int i, int j, int k) {
		Block block = Block.byId[iblockaccess.getTypeId(i, j, k)];
		if (block == null) {
			return false;
		}
		else if (!(block instanceof BlockFence) && !(block instanceof BlockPoleFence) && !(block instanceof BlockCable)) {
			return block.id == Ic2Items.reinforcedGlass.id || block == Block.GLASS;
		}
		else {
			return true;
		}
	}

	public static float[] getBoxOfLuminator(IBlockAccess iblockaccess, int i, int j, int k) {
		int l = iblockaccess.getData(i, j, k);
		float f = 0.0625F;
		switch (l) {
			case 0:
				++j;
				break;
			case 1:
				--j;
				break;
			case 2:
				++k;
				break;
			case 3:
				--k;
				break;
			case 4:
				++i;
				break;
			case 5:
				--i;
		}

		boolean flag = isSpecialSupporter(iblockaccess, i, j, k);
		switch (l) {
			case 1:
				return new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F * f, 1.0F};
			case 2:
				if (flag) {
					return new float[]{0.0F, 0.0F, 15.0F * f, 1.0F, 1.0F, 1.0F};
				}

				return new float[]{6.0F * f, 3.0F * f, 14.0F * f, 1.0F - 6.0F * f, 1.0F - 3.0F * f, 1.0F};
			case 3:
				if (flag) {
					return new float[]{0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F * f};
				}

				return new float[]{6.0F * f, 3.0F * f, 0.0F, 1.0F - 6.0F * f, 1.0F - 3.0F * f, 2.0F * f};
			case 4:
				if (flag) {
					return new float[]{15.0F * f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F};
				}

				return new float[]{14.0F * f, 3.0F * f, 6.0F * f, 1.0F, 1.0F - 3.0F * f, 1.0F - 6.0F * f};
			case 5:
				if (flag) {
					return new float[]{0.0F, 0.0F, 0.0F, 1.0F * f, 1.0F, 1.0F};
				}

				return new float[]{0.0F, 3.0F * f, 6.0F * f, 2.0F * f, 1.0F - 3.0F * f, 1.0F - 6.0F * f};
			default:
				return flag ? new float[]{0.0F, 15.0F * f, 0.0F, 1.0F, 1.0F, 1.0F} : new float[]{4.0F * f, 13.0F * f, 4.0F * f, 1.0F - 4.0F * f, 1.0F, 1.0F - 4.0F * f};
		}
	}

	public int a(Random random) {
		return 0;
	}

	public void postPlace(World world, int i, int j, int k, int l) {
		world.setRawData(i, j, k, l);
		super.postPlace(world, i, j, k, l);
	}

	public boolean canPlace(World world, int i, int j, int k, int l) {
		if (world.getTypeId(i, j, k) != 0) {
			return false;
		}
		else {
			switch (l) {
				case 0:
					++j;
					break;
				case 1:
					--j;
					break;
				case 2:
					++k;
					break;
				case 3:
					--k;
					break;
				case 4:
					++i;
					break;
				case 5:
					--i;
			}

			return isSupportingBlock(world, i, j, k);
		}
	}

	public boolean f(World world, int i, int j, int k) {
		TileEntity tileentity = world.getTileEntity(i, j, k);
		if (tileentity != null && ((TileEntityLuminator) tileentity).ignoreBlockStay) {
			return true;
		}
		else {
			int l = world.getData(i, j, k);
			switch (l) {
				case 0:
					++j;
					break;
				case 1:
					--j;
					break;
				case 2:
					++k;
					break;
				case 3:
					--k;
					break;
				case 4:
					++i;
					break;
				case 5:
					--i;
			}

			return isSupportingBlock(world, i, j, k);
		}
	}

	public void doPhysics(World world, int i, int j, int k, int l) {
		if (!this.f(world, i, j, k)) {
			world.setTypeId(i, j, k, 0);
		}

		super.doPhysics(world, i, j, k, l);
	}

	public boolean b() {
		return false;
	}

	public int c() {
		return mod_IC2.luminatorRenderId;
	}

	public AxisAlignedBB e(World world, int i, int j, int k) {
		float[] af = getBoxOfLuminator(world, i, j, k);
		return AxisAlignedBB.b((double) (af[0] + (float) i), (double) (af[1] + (float) j), (double) (af[2] + (float) k), (double) (af[3] + (float) i), (double) (af[4] + (float) j), (double) (af[5] + (float) k));
	}

	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
		float[] af = getBoxOfLuminator(world, i, j, k);
		return AxisAlignedBB.b((double) (af[0] + (float) i), (double) (af[1] + (float) j), (double) (af[2] + (float) k), (double) (af[3] + (float) i), (double) (af[4] + (float) j), (double) (af[5] + (float) k));
	}

	public boolean a() {
		return false;
	}

	public boolean isBlockNormalCube(World world, int i, int j, int k) {
		return false;
	}

	public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
		ItemStack itemstack = entityhuman.U();
		if (itemstack != null && itemstack.getItem() instanceof IElectricItem) {
			IElectricItem ielectricitem = (IElectricItem) itemstack.getItem();
			TileEntityLuminator tileentityluminator = (TileEntityLuminator) world.getTileEntity(i, j, k);
			int l = tileentityluminator.getMaxEnergy() - tileentityluminator.energy;
			if (l <= 0) {
				return false;
			}
			else {
				l = ElectricItem.discharge(itemstack, l, 2, true, false);
				if (!this.light) {
					world.setRawTypeIdAndData(i, j, k, Ic2Items.activeLuminator.id, world.getData(i, j, k));
					tileentityluminator = (TileEntityLuminator) world.getTileEntity(i, j, k);
				}

				tileentityluminator.energy += l;
				return true;
			}
		}
		else {
			return false;
		}
	}

	public void a(World world, int i, int j, int k, Entity entity) {
		if (this.light && entity instanceof EntityMonster) {
			boolean flag = entity instanceof EntitySkeleton || entity instanceof EntityZombie;
			Platform.setEntityOnFire(entity, flag ? 20 : 10);
		}

	}

	public TileEntity a_() {
		return new TileEntityLuminator();
	}

	public String getTextureFile() {
		return "/ic2/sprites/block_0.png";
	}

	public void addCreativeItems(ArrayList arraylist) {
		if (!this.light) {
			arraylist.add(new ItemStack(this));
		}

	}
}
