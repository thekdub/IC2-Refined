package ic2.common;

import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.ArrayList;
import java.util.Random;

public class ItemNanoSaber extends ItemElectricTool {
	public static int ticker = 0;
	public static Random shinyrand = new Random();
	public boolean active;
	public int soundTicker = 0;

	public ItemNanoSaber(int i, int j, boolean flag) {
		super(i, j, EnumToolMaterial.IRON, 10);
		this.maxCharge = 40000;
		this.transferLimit = 128;
		this.tier = 2;
		this.active = flag;
	}

	public static void drainSaber(ItemStack itemstack, int i, EntityHuman entityhuman) {
		if (!ElectricItem.use(itemstack, i * 8, entityhuman)) {
			itemstack.id = Ic2Items.nanoSaber.id;
		}

	}

	public static void timedLoss(EntityHuman entityhuman) {
		++ticker;
		if (ticker % 16 == 0) {
			ItemStack[] aitemstack = entityhuman.inventory.items;
			int j;
			if (ticker % 64 == 0) {
				for (j = 9; j < aitemstack.length; ++j) {
					if (aitemstack[j] != null && aitemstack[j].id == Ic2Items.enabledNanoSaber.id) {
						drainSaber(aitemstack[j], 64, entityhuman);
					}
				}
			}

			for (j = 0; j < 9; ++j) {
				if (aitemstack[j] != null && aitemstack[j].id == Ic2Items.enabledNanoSaber.id) {
					drainSaber(aitemstack[j], 16, entityhuman);
				}
			}
		}

	}

	public void init() {
		this.mineableBlocks.add(Block.WEB);
	}

	public float getDestroySpeed(ItemStack itemstack, Block block) {
		if (this.active) {
			++this.soundTicker;
			if (this.soundTicker % 4 == 0) {
				Platform.playSoundSp(this.getRandomSwingSound(), 1.0F, 1.0F);
			}

			return 4.0F;
		}
		else {
			return 1.0F;
		}
	}

	public boolean a(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
		if (!this.active) {
			return true;
		}
		else {
			if (Platform.isSimulating()) {
				EntityHuman entityhuman = null;
				if (entityliving1 instanceof EntityHuman) {
					entityhuman = (EntityHuman) entityliving1;
				}

				if (entityliving instanceof EntityHuman) {
					EntityHuman entityhuman1 = (EntityHuman) entityliving;

					for (int i = 0; i < 4; ++i) {
						if (entityhuman1.inventory.armor[i] != null && entityhuman1.inventory.armor[i].getItem() instanceof ItemArmorNanoSuit) {
							int c = entityhuman1.inventory.armor[i].getItem() instanceof ItemArmorQuantumSuit ? 30000 : 4800;
							ElectricItem.discharge(entityhuman1.inventory.armor[i], c, this.tier, true, false);
							if (!ElectricItem.canUse(entityhuman1.inventory.armor[i], 1)) {
								entityhuman1.inventory.armor[i] = null;
							}

							drainSaber(itemstack, 2, entityhuman);
						}
					}
				}

				drainSaber(itemstack, 5, entityhuman);
			}

			if (Platform.isRendering()) {
				Platform.playSoundSp(this.getRandomSwingSound(), 1.0F, 1.0F);
			}

			return true;
		}
	}

	public String getRandomSwingSound() {
		switch (mod_IC2.random.nextInt(3)) {
			case 1:
				return "nanosabreSwingOne";
			case 2:
				return "nanosabreSwingTwo";
			default:
				return "nanosabreSwing";
		}
	}

	public boolean a(ItemStack itemstack, int i, int j, int k, int l, EntityLiving entityliving) {
		EntityHuman entityhuman = null;
		if (entityliving instanceof EntityHuman) {
			entityhuman = (EntityHuman) entityliving;
		}

		if (this.active) {
			drainSaber(itemstack, 10, entityhuman);
		}

		return true;
	}

	public int a(Entity entity) {
		return !this.active ? 4 : 20;
	}

	public boolean isFull3D() {
		return true;
	}

	public boolean canDestroySpecialBlock(Block block) {
		return block.id == Block.WEB.id;
	}

	public ItemStack a(ItemStack itemstack, World world, EntityHuman entityhuman) {
		if (this.active) {
			itemstack.id = Ic2Items.nanoSaber.id;
		}
		else if (itemstack.getData() < itemstack.i() - 1) {
			itemstack.id = Ic2Items.enabledNanoSaber.id;
			world.makeSound(entityhuman, "nanosabrePower", 1.0F, 1.0F);
		}

		return itemstack;
	}

	public int getIconFromDamage(int i) {
		return this.active && shinyrand.nextBoolean() ? this.textureId + 1 : this.textureId;
	}

	public int rarity(ItemStack itemstack) {
		return 1;
	}

	public void addCreativeItems(ArrayList arraylist) {
		if (!this.active) {
			super.addCreativeItems(arraylist);
		}

	}
}
