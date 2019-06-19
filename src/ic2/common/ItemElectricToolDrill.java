package ic2.common;

import ic2.platform.Platform;
import net.minecraft.server.*;

public class ItemElectricToolDrill extends ItemElectricTool {
  public int soundTicker;

  public ItemElectricToolDrill(int i, int j) {
    super(i, j, EnumToolMaterial.IRON, 50);
    this.soundTicker = 0;
    this.maxCharge = 10000;
    this.transferLimit = 100;
    this.tier = 1;
    this.a = 8.0F;
  }

  public ItemElectricToolDrill(int i, int j, EnumToolMaterial enumtoolmaterial, int k) {
    this(i, j);
    this.b = enumtoolmaterial;
    this.operationEnergyCost = k;
  }

  public void init() {
    this.mineableBlocks.add(Block.COBBLESTONE);
    this.mineableBlocks.add(Block.DOUBLE_STEP);
    this.mineableBlocks.add(Block.STEP);
    this.mineableBlocks.add(Block.STONE);
    this.mineableBlocks.add(Block.SANDSTONE);
    this.mineableBlocks.add(Block.MOSSY_COBBLESTONE);
    this.mineableBlocks.add(Block.IRON_ORE);
    this.mineableBlocks.add(Block.IRON_BLOCK);
    this.mineableBlocks.add(Block.COAL_ORE);
    this.mineableBlocks.add(Block.GOLD_BLOCK);
    this.mineableBlocks.add(Block.GOLD_ORE);
    this.mineableBlocks.add(Block.DIAMOND_ORE);
    this.mineableBlocks.add(Block.DIAMOND_BLOCK);
    this.mineableBlocks.add(Block.ICE);
    this.mineableBlocks.add(Block.NETHERRACK);
    this.mineableBlocks.add(Block.LAPIS_ORE);
    this.mineableBlocks.add(Block.LAPIS_BLOCK);
    this.mineableBlocks.add(Block.REDSTONE_ORE);
    this.mineableBlocks.add(Block.GLOWING_REDSTONE_ORE);
    this.mineableBlocks.add(Block.BRICK);
    this.mineableBlocks.add(Block.GLOWSTONE);
    this.mineableBlocks.add(Block.GRASS);
    this.mineableBlocks.add(Block.DIRT);
    this.mineableBlocks.add(Block.MYCEL);
    this.mineableBlocks.add(Block.SAND);
    this.mineableBlocks.add(Block.GRAVEL);
    this.mineableBlocks.add(Block.SNOW);
    this.mineableBlocks.add(Block.SNOW_BLOCK);
    this.mineableBlocks.add(Block.CLAY);
    this.mineableBlocks.add(Block.SOIL);
    this.mineableBlocks.add(Block.SOUL_SAND);
  }

  public boolean a(ItemStack itemstack, int i, int j, int k, int l, EntityLiving entityliving) {
    ElectricItem.use(itemstack, this.operationEnergyCost, (EntityHuman) entityliving);
    return true;
  }

  public boolean canDestroySpecialBlock(Block block) {
    return block.material == Material.STONE || block.material == Material.ORE || super.canDestroySpecialBlock(block);
  }

  public float getDestroySpeed(ItemStack itemstack, Block block) {
    ++this.soundTicker;
    if (this.soundTicker % 4 == 0) {
      Platform.playSoundSp(this.getRandomDrillSound(), 1.0F, 1.0F);
    }

    return super.getDestroySpeed(itemstack, block);
  }

  public float getStrVsBlock(ItemStack itemstack, Block block, int i) {
    ++this.soundTicker;
    if (this.soundTicker % 4 == 0) {
      Platform.playSoundSp(this.getRandomDrillSound(), 1.0F, 1.0F);
    }

    return super.getStrVsBlock(itemstack, block, i);
  }

  public String getRandomDrillSound() {
    switch (mod_IC2.random.nextInt(4)) {
      case 1:
        return "drillOne";
      case 2:
        return "drillTwo";
      case 3:
        return "drillThree";
      default:
        return "drill";
    }
  }
}
