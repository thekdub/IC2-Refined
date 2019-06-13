package ic2.common;

import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.Random;

public class BlockMachine extends BlockMultiID implements IRareBlock {
	public BlockMachine(int i) {
		super(i, Material.ORE);
		this.c(2.0F);
		this.a(i);
		ModLoader.registerBlock(this, ItemMachine.class);
		Ic2Items.machine = new ItemStack(this, 1, 0);
		Ic2Items.advancedMachine = new ItemStack(this, 1, 12);
		Ic2Items.ironFurnace = new ItemStack(this, 1, 1);
		Ic2Items.electroFurnace = new ItemStack(this, 1, 2);
		Ic2Items.macerator = new ItemStack(this, 1, 3);
		Ic2Items.extractor = new ItemStack(this, 1, 4);
		Ic2Items.compressor = new ItemStack(this, 1, 5);
		Ic2Items.canner = new ItemStack(this, 1, 6);
		Ic2Items.miner = new ItemStack(this, 1, 7);
		Ic2Items.pump = new ItemStack(this, 1, 8);
		Ic2Items.magnetizer = new ItemStack(this, 1, 9);
		Ic2Items.electrolyzer = new ItemStack(this, 1, 10);
		Ic2Items.recycler = new ItemStack(this, 1, 11);
		Ic2Items.inductionFurnace = new ItemStack(this, 1, 13);
		Ic2Items.massFabricator = new ItemStack(this, 1, 14);
		Ic2Items.terraformer = new ItemStack(this, 1, 15);
	}

	public String getTextureFile() {
		return "/ic2/sprites/block_machine.png";
	}

	public int getDropType(int i, Random random, int j) {
		return this.id;
	}

	protected int getDropData(int i) {
		switch (i) {
			case 1:
				return i;
			case 2:
				return i;
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 10:
			case 11:
			default:
				return 0;
			case 9:
				return i;
			case 12:
				return 12;
			case 13:
				return 12;
			case 14:
				return 12;
			case 15:
				return 12;
		}
	}

	public TileEntityBlock getBlockEntity(int i) {
		switch (i) {
			case 1:
				return new TileEntityIronFurnace();
			case 2:
				return new TileEntityElecFurnace();
			case 3:
				return new TileEntityMacerator();
			case 4:
				return new TileEntityExtractor();
			case 5:
				return new TileEntityCompressor();
			case 6:
				return new TileEntityCanner();
			case 7:
				return new TileEntityMiner();
			case 8:
				return new TileEntityPump();
			case 9:
				return new TileEntityMagnetizer();
			case 10:
				return new TileEntityElectrolyzer();
			case 11:
				return new TileEntityRecycler();
			case 12:
			default:
				return new TileEntityBlock();
			case 13:
				return new TileEntityInduction();
			case 14:
				return new TileEntityMatter();
			case 15:
				return new TileEntityTerra();
		}
	}

	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
		if (Platform.isRendering()) {
			int l = world.getData(i, j, k);
			float f3;
			float f6;
			float f8;
			float f10;
			if (l == 1 && isActive(world, i, j, k)) {
				TileEntity tileentity = world.getTileEntity(i, j, k);
				short word0 = tileentity instanceof TileEntityBlock ? ((TileEntityBlock) tileentity).getFacing() : 0;
				f3 = (float) i + 0.5F;
				float f4 = (float) j + 0.0F + random.nextFloat() * 6.0F / 16.0F;
				f6 = (float) k + 0.5F;
				f8 = 0.52F;
				f10 = random.nextFloat() * 0.6F - 0.3F;
				switch (word0) {
					case 2:
						world.a("smoke", (double) (f3 + f10), (double) f4, (double) (f6 - f8), 0.0D, 0.0D, 0.0D);
						world.a("flame", (double) (f3 + f10), (double) f4, (double) (f6 - f8), 0.0D, 0.0D, 0.0D);
						break;
					case 3:
						world.a("smoke", (double) (f3 + f10), (double) f4, (double) (f6 + f8), 0.0D, 0.0D, 0.0D);
						world.a("flame", (double) (f3 + f10), (double) f4, (double) (f6 + f8), 0.0D, 0.0D, 0.0D);
						break;
					case 4:
						world.a("smoke", (double) (f3 - f8), (double) f4, (double) (f6 + f10), 0.0D, 0.0D, 0.0D);
						world.a("flame", (double) (f3 - f8), (double) f4, (double) (f6 + f10), 0.0D, 0.0D, 0.0D);
						break;
					case 5:
						world.a("smoke", (double) (f3 + f8), (double) f4, (double) (f6 + f10), 0.0D, 0.0D, 0.0D);
						world.a("flame", (double) (f3 + f8), (double) f4, (double) (f6 + f10), 0.0D, 0.0D, 0.0D);
				}
			}

			if (l == 3 && isActive(world, i, j, k)) {
				float f = (float) i + 1.0F;
				float f1 = (float) j + 1.0F;
				f3 = (float) k + 1.0F;

				for (int i1 = 0; i1 < 4; ++i1) {
					f6 = -0.2F - random.nextFloat() * 0.6F;
					f8 = -0.1F + random.nextFloat() * 0.2F;
					f10 = -0.2F - random.nextFloat() * 0.6F;
					world.a("smoke", (double) (f + f6), (double) (f1 + f8), (double) (f3 + f10), 0.0D, 0.0D, 0.0D);
				}
			}

		}
	}

	public int rarity(ItemStack itemstack) {
		return itemstack.getData() != 14 ? (itemstack.getData() != 15 && itemstack.getData() != 13 && itemstack.getData() != 12 ? 0 : 1) : 2;
	}
}
