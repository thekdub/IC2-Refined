package ic2.common;

import forge.IConnectRedstone;
import forge.ISpecialResistance;
import ic2.api.Direction;
import ic2.api.IPaintableBlock;
import ic2.platform.ItemBlockCommon;
import ic2.platform.Platform;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;

public class BlockCable extends BlockMultiID implements IPaintableBlock, ISpecialResistance, IConnectRedstone {
	public BlockCable(int i) {
		super(i, Material.ORE);
		this.c(0.2F);
		this.a(k);
		ModLoader.registerBlock(this, ItemBlockCommon.class);
		Ic2Items.copperCableBlock = new ItemStack(this, 1, 1);
		Ic2Items.insulatedCopperCableBlock = new ItemStack(this, 1, 0);
		Ic2Items.goldCableBlock = new ItemStack(this, 1, 2);
		Ic2Items.insulatedGoldCableBlock = new ItemStack(this, 1, 3);
		Ic2Items.doubleInsulatedGoldCableBlock = new ItemStack(this, 1, 4);
		Ic2Items.ironCableBlock = new ItemStack(this, 1, 5);
		Ic2Items.insulatedIronCableBlock = new ItemStack(this, 1, 6);
		Ic2Items.doubleInsulatedIronCableBlock = new ItemStack(this, 1, 7);
		Ic2Items.trippleInsulatedIronCableBlock = new ItemStack(this, 1, 8);
		Ic2Items.glassFiberCableBlock = new ItemStack(this, 1, 9);
		Ic2Items.tinCableBlock = new ItemStack(this, 1, 10);
		Ic2Items.detectorCableBlock = new ItemStack(this, 1, 11);
		Ic2Items.splitterCableBlock = new ItemStack(this, 1, 12);
	}

	public static int getCableColor(IBlockAccess iblockaccess, int i, int j, int k) {
		TileEntity tileentity = iblockaccess.getTileEntity(i, j, k);
		return tileentity instanceof TileEntityCable ? ((TileEntityCable) tileentity).color : 0;
	}

	public String getTextureFile() {
		return "/ic2/sprites/block_cable.png";
	}

	public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		short word1 = 0;
		TileEntity tileentity = iblockaccess.getTileEntity(i, j, k);
		short word0;
		if (tileentity instanceof TileEntityCable) {
			TileEntityCable tileentitycable = (TileEntityCable) tileentity;
			if (tileentitycable.foamed != 0) {
				if (tileentitycable.foamed == 1) {
					return 178;
				}

				return 208 + tileentitycable.foamColor;
			}

			word0 = tileentitycable.cableType;
			if (!(tileentity instanceof TileEntityCableDetector) && !(tileentity instanceof TileEntityCableSplitter)) {
				word1 = tileentitycable.color;
			}
			else {
				word1 = (short) (tileentitycable.getActive() ? 1 : 0);
			}
		}
		else {
			word0 = (short) iblockaccess.getData(i, j, k);
		}

		return word0 * 16 + word1;
	}

	public int a(int i, int j) {
		return j * 16;
	}

	public MovingObjectPosition a(World world, int i, int j, int k, Vec3D vec3d, Vec3D vec3d1) {
		TileEntity tileentity = world.getTileEntity(i, j, k);
		if (!(tileentity instanceof TileEntityCable)) {
			return null;
		}
		else {
			TileEntityCable tileentitycable = (TileEntityCable) tileentity;
			Vec3D vec3d2 = Vec3D.a(vec3d1.a - vec3d.a, vec3d1.b - vec3d.b, vec3d1.c - vec3d.c);
			double d = vec3d2.c();
			double d1 = (double) tileentitycable.getCableThickness() / 2.0D;
			boolean flag = false;
			Vec3D vec3d3 = Vec3D.a(0.0D, 0.0D, 0.0D);
			Direction direction = AabbUtil.getIntersection(vec3d, vec3d2, AxisAlignedBB.a((double) i + 0.5D - d1, (double) j + 0.5D - d1, (double) k + 0.5D - d1, (double) i + 0.5D + d1, (double) j + 0.5D + d1, (double) k + 0.5D + d1), vec3d3);
			if (direction != null && vec3d3.b(vec3d) <= d) {
				flag = true;
			}

			Direction[] adirection = Direction.values();
			int l = adirection.length;

			for (int i1 = 0; i1 < l; ++i1) {
				Direction direction1 = adirection[i1];
				if (flag) {
					break;
				}

				TileEntity tileentity1 = direction1.applyToTileEntity(tileentitycable);
				if (tileentitycable.canInteractWith(tileentity1)) {
					AxisAlignedBB axisalignedbb = null;
					switch (direction1.ordinal() + 1) {
						case 1:
							axisalignedbb = AxisAlignedBB.a((double) i, (double) j + 0.5D - d1, (double) k + 0.5D - d1, (double) i + 0.5D, (double) j + 0.5D + d1, (double) k + 0.5D + d1);
							break;
						case 2:
							axisalignedbb = AxisAlignedBB.a((double) i + 0.5D, (double) j + 0.5D - d1, (double) k + 0.5D - d1, (double) i + 1.0D, (double) j + 0.5D + d1, (double) k + 0.5D + d1);
							break;
						case 3:
							axisalignedbb = AxisAlignedBB.a((double) i + 0.5D - d1, (double) j, (double) k + 0.5D - d1, (double) i + 0.5D + d1, (double) j + 0.5D, (double) k + 0.5D + d1);
							break;
						case 4:
							axisalignedbb = AxisAlignedBB.a((double) i + 0.5D - d1, (double) j + 0.5D, (double) k + 0.5D - d1, (double) i + 0.5D + d1, (double) j + 1.0D, (double) k + 0.5D + d1);
							break;
						case 5:
							axisalignedbb = AxisAlignedBB.a((double) i + 0.5D - d1, (double) j + 0.5D - d1, (double) k, (double) i + 0.5D + d1, (double) j + 0.5D, (double) k + 0.5D);
							break;
						case 6:
							axisalignedbb = AxisAlignedBB.a((double) i + 0.5D - d1, (double) j + 0.5D - d1, (double) k + 0.5D, (double) i + 0.5D + d1, (double) j + 0.5D + d1, (double) k + 1.0D);
					}

					direction = AabbUtil.getIntersection(vec3d, vec3d2, axisalignedbb, vec3d3);
					if (direction != null && vec3d3.b(vec3d) <= d) {
						flag = true;
					}
				}
			}

			return flag ? new MovingObjectPosition(i, j, k, direction.toSideValue(), vec3d3) : null;
		}
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k, int l) {
		double d = (double) TileEntityCable.getCableThickness(l);
		return AxisAlignedBB.b((double) i + 0.5D - d, (double) j + 0.5D - d, (double) k + 0.5D - d, (double) i + 0.5D + d, (double) j + 0.5D + d, (double) k + 0.5D + d);
	}

	public AxisAlignedBB e(World world, int i, int j, int k) {
		return this.getCommonBoundingBoxFromPool(world, i, j, k, false);
	}

	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
		return this.getCommonBoundingBoxFromPool(world, i, j, k, true);
	}

	public AxisAlignedBB getCommonBoundingBoxFromPool(World world, int i, int j, int k, boolean flag) {
		TileEntity tileentity = world.getTileEntity(i, j, k);
		if (!(tileentity instanceof TileEntityCable)) {
			return this.getCollisionBoundingBoxFromPool(world, i, j, k, 3);
		}
		else {
			TileEntityCable tileentitycable = (TileEntityCable) tileentity;
			double d = tileentitycable.foamed == 1 && flag ? 0.5D : (double) tileentitycable.getCableThickness() / 2.0D;
			double d1 = (double) i + 0.5D - d;
			double d2 = (double) j + 0.5D - d;
			double d3 = (double) k + 0.5D - d;
			double d4 = (double) i + 0.5D + d;
			double d5 = (double) j + 0.5D + d;
			double d6 = (double) k + 0.5D + d;
			if (tileentitycable.canInteractWith(world.getTileEntity(i - 1, j, k))) {
				d1 = (double) i;
			}

			if (tileentitycable.canInteractWith(world.getTileEntity(i, j - 1, k))) {
				d2 = (double) j;
			}

			if (tileentitycable.canInteractWith(world.getTileEntity(i, j, k - 1))) {
				d3 = (double) k;
			}

			if (tileentitycable.canInteractWith(world.getTileEntity(i + 1, j, k))) {
				d4 = (double) (i + 1);
			}

			if (tileentitycable.canInteractWith(world.getTileEntity(i, j + 1, k))) {
				d5 = (double) (j + 1);
			}

			if (tileentitycable.canInteractWith(world.getTileEntity(i, j, k + 1))) {
				d6 = (double) (k + 1);
			}

			return AxisAlignedBB.b(d1, d2, d3, d4, d5, d6);
		}
	}

	public boolean isBlockNormalCube(World world, int i, int j, int k) {
		TileEntity tileentity = world.getTileEntity(i, j, k);
		if (tileentity instanceof TileEntityCable) {
			TileEntityCable tileentitycable = (TileEntityCable) tileentity;
			return tileentitycable.foamed > 0;
		}

		return false;
	}

	public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
		ItemStack itemstack = entityhuman.U();
		if (itemstack != null && itemstack.id == Block.SAND.id) {
			if (!Platform.isSimulating()) {
				return true;
			}

			TileEntity tileentity = world.getTileEntity(i, j, k);
			if (tileentity instanceof TileEntityCable) {
				TileEntityCable tileentitycable = (TileEntityCable) tileentity;
				if (tileentitycable.foamed == 1 && tileentitycable.changeFoam((byte) 2)) {
					--itemstack.count;
					if (itemstack.count <= 0) {
						entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
					}

					return true;
				}
			}
		}

		return false;
	}

	public boolean colorBlock(World world, int i, int j, int k, int l) {
		return ((TileEntityCable) world.getTileEntity(i, j, k)).changeColor(l);
	}

	public boolean canHarvestBlock(EntityHuman entityhuman, int i) {
		return true;
	}

	public ArrayList getBlockDropped(World world, int i, int j, int k, int l, int i1) {
		ArrayList arraylist = new ArrayList();
		TileEntity tileentity = world.getTileEntity(i, j, k);
		if (tileentity instanceof TileEntityCable) {
			TileEntityCable tileentitycable = (TileEntityCable) tileentity;
			arraylist.add(new ItemStack(Ic2Items.insulatedCopperCableItem.id, 1, tileentitycable.cableType));
		}
		else if (l != 13) {
			arraylist.add(new ItemStack(Ic2Items.insulatedCopperCableItem.id, 1, l));
		}

		return arraylist;
	}

	public void remove(World world, int i, int j, int k) {
		if (world.getData(i, j, k) == 13) {
			TileEntity tileentity = world.getTileEntity(i, j, k);
			if (tileentity instanceof TileEntityCable) {
				TileEntityCable tileentitycable = (TileEntityCable) tileentity;
				StackUtil.dropAsEntity(world, i, j, k, new ItemStack(Ic2Items.insulatedCopperCableItem.id, 1, tileentitycable.cableType));
			}
		}

	}

	public TileEntityBlock getBlockEntity(int i) {
		if (i == 11) {
			return new TileEntityCableDetector((short) i);
		}
		else {
			return i == 12 ? new TileEntityCableSplitter((short) i) : new TileEntityCable((short) i);
		}
	}

	public int getMaxMeta() {
		return 13;
	}

	public boolean b() {
		return false;
	}

	public int c() {
		return mod_IC2.cableRenderId;
	}

	public boolean a() {
		return false;
	}

	public void attack(World world, int i, int j, int k, EntityHuman entityhuman) {
		if (entityhuman.U() != null && entityhuman.U().getItem() instanceof ItemToolCutter) {
			Entity bukkitentity = entityhuman.getBukkitEntity();
			if (bukkitentity instanceof Player) {
				Player player = (Player) bukkitentity;
				BlockBreakEvent breakev = new BlockBreakEvent(player.getWorld().getBlockAt(i, j, k), player);
				Bukkit.getPluginManager().callEvent(breakev);
				if (breakev.isCancelled()) {
					return;
				}

				breakev.setCancelled(true);
			}

			ItemToolCutter.cutInsulationFrom(entityhuman.U(), world, i, j, k);
		}

	}

	public boolean a(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		TileEntity tileentity = iblockaccess.getTileEntity(i, j, k);
		return tileentity instanceof TileEntityCableDetector && ((TileEntityCableDetector) tileentity).getActive();
	}

	public void addCreativeItems(ArrayList arraylist) {
	}

	public float getHardness(int i) {
		return i != 13 ? 0.2F : 3.0F;
	}

	public float getSpecialExplosionResistance(World world, int i, int j, int k, double d, double d1, double d2, net.minecraft.server.Entity entity) {
		TileEntity tileentity = world.getTileEntity(i, j, k);
		if (tileentity instanceof TileEntityCable) {
			TileEntityCable tileentitycable = (TileEntityCable) tileentity;
			if (tileentitycable.foamed == 2) {
				return 90.0F;
			}
		}

		return 6.0F;
	}

	public boolean canConnectRedstone(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		int i1 = iblockaccess.getData(i, j, k);
		return i1 == 11 || i1 == 12;
	}
}
