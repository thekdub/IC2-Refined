package ic2.common;

import ic2.platform.ItemBlockCommon;
import net.minecraft.server.*;

import java.util.ArrayList;

public class BlockRubberSheet extends BlockTex {
	public BlockRubberSheet(int i, int j) {
		super(i, j, Material.CLOTH);
		this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		this.c(0.8F);
		this.b(2.0F);
		this.a(k);
		this.a("blockRubber");
		ModLoader.registerBlock(this, ItemBlockCommon.class);
		Ic2Items.rubberTrampoline = new ItemStack(this);
	}

	public boolean a() {
		return false;
	}

	public boolean b() {
		return false;
	}

	public boolean canPlace(World world, int i, int j, int k) {
		return this.isBlockSupporter(world, i - 1, j, k) || this.isBlockSupporter(world, i + 1, j, k) || this.isBlockSupporter(world, i, j, k - 1) || this.isBlockSupporter(world, i, j, k + 1);
	}

	public boolean isBlockSupporter(World world, int i, int j, int k) {
		return world.r(i, j, k) || world.getTypeId(i, j, k) == this.id;
	}

	public boolean canSupportWeight(World world, int i, int j, int k) {
		if (world.getData(i, j, k) == 1) {
			return true;
		}
		else {
			boolean flag = false;
			boolean flag1 = false;
			boolean flag2 = false;
			boolean flag3 = false;
			int l = i;

			while (true) {
				if (world.r(l, j, k)) {
					flag1 = true;
					break;
				}

				if (world.getTypeId(l, j, k) != this.id) {
					break;
				}

				if (world.r(l, j - 1, k)) {
					flag1 = true;
					break;
				}

				--l;
			}

			l = i;

			while (true) {
				if (world.r(l, j, k)) {
					flag = true;
					break;
				}

				if (world.getTypeId(l, j, k) != this.id) {
					break;
				}

				if (world.r(l, j - 1, k)) {
					flag = true;
					break;
				}

				++l;
			}

			if (flag && flag1) {
				world.setRawData(i, j, k, 1);
				return true;
			}
			else {
				l = k;

				while (true) {
					if (world.r(i, j, l)) {
						flag3 = true;
						break;
					}

					if (world.getTypeId(i, j, l) != this.id) {
						break;
					}

					if (world.r(i, j - 1, l)) {
						flag3 = true;
						break;
					}

					--l;
				}

				l = k;

				while (true) {
					if (world.r(i, j, l)) {
						flag2 = true;
						break;
					}

					if (world.getTypeId(i, j, l) != this.id) {
						break;
					}

					if (world.r(i, j - 1, l)) {
						flag2 = true;
						break;
					}

					++l;
				}

				if (flag2 && flag3) {
					world.setRawData(i, j, k, 1);
					return true;
				}
				else {
					return false;
				}
			}
		}
	}

	public void doPhysics(World world, int i, int j, int k, int l) {
		if (world.getData(i, j, k) == 1) {
			world.setData(i, j, k, 0);
		}

		if (!this.canPlace(world, i, j, k)) {
			this.b(world, i, j, k, world.getData(i, j, k), 0);
			world.setTypeId(i, j, k, 0);
		}

	}

	public void a(World world, int i, int j, int k, Entity entity) {
		if (!world.e(i, j - 1, k)) {
			if (entity instanceof EntityLiving && !this.canSupportWeight(world, i, j, k)) {
				world.setTypeId(i, j, k, 0);
			}
			else {
				if (entity.motY <= -0.4000000059604645D) {
					mod_IC2.setFallDistanceOfEntity(entity, 0.0F);
					entity.motX *= 1.100000023841858D;
					if (entity instanceof EntityLiving) {
						if (mod_IC2.getIsJumpingOfEntityLiving((EntityLiving) entity)) {
							entity.motY *= -1.2999999523162842D;
						}
						else if (entity instanceof EntityHuman && entity.isSneaking()) {
							entity.motY *= -0.10000000149011612D;
						}
						else {
							entity.motY *= -0.800000011920929D;
						}
					}
					else {
						entity.motY *= -0.800000011920929D;
					}

					entity.motZ *= 1.100000023841858D;
				}

			}
		}
	}

	public void addCreativeItems(ArrayList arraylist) {
		arraylist.add(new ItemStack(this));
	}
}
