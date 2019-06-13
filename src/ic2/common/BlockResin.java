package ic2.common;

import ic2.platform.ItemBlockCommon;
import net.minecraft.server.*;

import java.util.Random;

public class BlockResin extends BlockTex {
	public BlockResin(int i, int j) {
		super(i, j, Material.ORIENTABLE);
		this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		this.c(1.6F);
		this.b(0.5F);
		this.a(l);
		this.a("blockHarz");
		this.addToCreative = false;
		ModLoader.registerBlock(this, ItemBlockCommon.class);
		Ic2Items.resinSheet = new ItemStack(this);
	}

	public AxisAlignedBB e(World world, int i, int j, int k) {
		return null;
	}

	public boolean a() {
		return false;
	}

	public boolean b() {
		return false;
	}

	public int getDropType(int i, Random random, int j) {
		return Ic2Items.resin.id;
	}

	public int a(Random random) {
		return random.nextInt(5) != 0 ? 1 : 0;
	}

	public boolean canPlace(World world, int i, int j, int k) {
		int l = world.getTypeId(i, j - 1, k);
		return (l != 0 && Block.byId[l].a()) && world.getMaterial(i, j - 1, k).isBuildable();
	}

	public void doPhysics(World world, int i, int j, int k, int l) {
		if (!this.canPlace(world, i, j, k)) {
			this.b(world, i, j, k, world.getData(i, j, k), 0);
			world.setTypeId(i, j, k, 0);
		}

	}

	public void a(World world, int i, int j, int k, Entity entity) {
		mod_IC2.setFallDistanceOfEntity(entity, mod_IC2.getFallDistanceOfEntity(entity) * 0.75F);
		entity.motX *= 0.6000000238418579D;
		entity.motY *= 0.8500000238418579D;
		entity.motZ *= 0.6000000238418579D;
	}
}
