package ic2.common;

import ic2.platform.Platform;
import net.minecraft.server.*;

import java.util.Random;

public class BlockGenerator extends BlockMultiID implements IRareBlock {
  public static Class tileEntityGeoGeneratorClass = TileEntityGeoGenerator.class;
  public static Class tileEntityWaterGeneratorClass = TileEntityWaterGenerator.class;

  public BlockGenerator(int i) {
    super(i, Material.ORE);
    this.c(3.0F);
    this.a(i);
    ModLoader.registerBlock(this, ItemGenerator.class);
    Ic2Items.generator = new ItemStack(this, 1, 0);
    Ic2Items.geothermalGenerator = new ItemStack(this, 1, 1);
    Ic2Items.waterMill = new ItemStack(this, 1, 2);
    Ic2Items.solarPanel = new ItemStack(this, 1, 3);
    Ic2Items.windMill = new ItemStack(this, 1, 4);
    Ic2Items.nuclearReactor = new ItemStack(this, 1, 5);
  }

  public String getTextureFile() {
    return "/ic2/sprites/block_generator.png";
  }

  public int getDropType(int i, Random random, int j) {
    return this.id;
  }

  protected int getDropData(int i) {
    switch (i) {
      case 2:
        return 2;
      default:
        return 0;
    }
  }

  public int a(Random random) {
    return 1;
  }

  public TileEntityBlock getBlockEntity(int i) {
    try {
      switch (i) {
        case 0:
          return new TileEntityGenerator();
        case 1:
          return (TileEntityBlock) tileEntityGeoGeneratorClass.newInstance();
        case 2:
          return (TileEntityBlock) tileEntityWaterGeneratorClass.newInstance();
        case 3:
          return new TileEntitySolarGenerator();
        case 4:
          return new TileEntityWindGenerator();
        case 5:
          return new TileEntityNuclearReactor();
        default:
          return null;
      }
    } catch (Exception var3) {
      throw new RuntimeException(var3);
    }
  }

  public void randomDisplayTick(World world, int i, int j, int k, Random random) {
    if (Platform.isRendering()) {
      int l = world.getData(i, j, k);
      if (l == 0 && isActive(world, i, j, k)) {
        TileEntityBlock tileentityblock = (TileEntityBlock) world.getTileEntity(i, j, k);
        short word0 = tileentityblock.getFacing();
        float f = (float) i + 0.5F;
        float f1 = (float) j + 0.0F + random.nextFloat() * 6.0F / 16.0F;
        float f2 = (float) k + 0.5F;
        float f3 = 0.52F;
        float f4 = random.nextFloat() * 0.6F - 0.3F;
        switch (word0) {
          case 2:
            world.a("smoke", (double) (f + f4), (double) f1, (double) (f2 - f3), 0.0D, 0.0D, 0.0D);
            world.a("flame", (double) (f + f4), (double) f1, (double) (f2 - f3), 0.0D, 0.0D, 0.0D);
            break;
          case 3:
            world.a("smoke", (double) (f + f4), (double) f1, (double) (f2 + f3), 0.0D, 0.0D, 0.0D);
            world.a("flame", (double) (f + f4), (double) f1, (double) (f2 + f3), 0.0D, 0.0D, 0.0D);
            break;
          case 4:
            world.a("smoke", (double) (f - f3), (double) f1, (double) (f2 + f4), 0.0D, 0.0D, 0.0D);
            world.a("flame", (double) (f - f3), (double) f1, (double) (f2 + f4), 0.0D, 0.0D, 0.0D);
            break;
          case 5:
            world.a("smoke", (double) (f + f3), (double) f1, (double) (f2 + f4), 0.0D, 0.0D, 0.0D);
            world.a("flame", (double) (f + f3), (double) f1, (double) (f2 + f4), 0.0D, 0.0D, 0.0D);
        }
      }
      else if (l == 5) {
        int i1 = ((TileEntityNuclearReactor) world.getTileEntity(i, j, k)).heat / 1000;
        if (i1 <= 0) {
          return;
        }

        i1 = world.random.nextInt(i1);

        int k1;
        for (k1 = 0; k1 < i1; ++k1) {
          world.a("smoke", (double) ((float) i + random.nextFloat()), (double) ((float) j + 0.95F), (double) ((float) k + random.nextFloat()), 0.0D, 0.0D, 0.0D);
        }

        i1 -= world.random.nextInt(4) + 3;

        for (k1 = 0; k1 < i1; ++k1) {
          world.a("flame", (double) ((float) i + random.nextFloat()), (double) ((float) j + 1.0F), (double) ((float) k + random.nextFloat()), 0.0D, 0.0D, 0.0D);
        }
      }

    }
  }

  public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman) {
    return (entityhuman.U() == null || !entityhuman.U().doMaterialsMatch(Ic2Items.reactorChamber)) && super.interact(world, i, j, k, entityhuman);
  }

  public int rarity(ItemStack itemstack) {
    return itemstack.getData() != 5 ? 0 : 1;
  }
}
