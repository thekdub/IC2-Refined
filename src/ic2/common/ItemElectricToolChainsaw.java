package ic2.common;

import forge.IEntityInteractHandler;
import forge.IShearable;
import forge.MinecraftForge;
import ic2.platform.AudioManager;
import ic2.platform.AudioSource;
import ic2.platform.Platform;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.Iterator;

public class ItemElectricToolChainsaw extends ItemElectricTool implements IHitSoundOverride, IEntityInteractHandler {
  public static boolean wasEquipped = false;
  public static AudioSource audioSource;

  public ItemElectricToolChainsaw(int i, int j) {
    super(i, j, EnumToolMaterial.IRON, 50);
    this.maxCharge = 10000;
    this.transferLimit = 100;
    this.tier = 1;
    this.a = 12.0F;
    this.bV = 1;
    MinecraftForge.registerEntityInteractHandler(this);
  }

  public void init() {
    this.mineableBlocks.add(Block.WOOD);
    this.mineableBlocks.add(Block.BOOKSHELF);
    this.mineableBlocks.add(Block.LOG);
    this.mineableBlocks.add(Block.CHEST);
    this.mineableBlocks.add(Block.LEAVES);
    this.mineableBlocks.add(Block.WEB);
    this.mineableBlocks.add(Block.byId[Ic2Items.crop.id]);
    if (Ic2Items.rubberLeaves != null) {
      this.mineableBlocks.add(Block.byId[Ic2Items.rubberLeaves.id]);
    }

  }

  public boolean a(ItemStack itemstack, int i, int j, int k, int l, EntityLiving entityliving) {
    ElectricItem.use(itemstack, this.operationEnergyCost, (EntityHuman) entityliving);
    return true;
  }

  public boolean a(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
    if (ElectricItem.use(itemstack, this.operationEnergyCost, (EntityHuman) entityliving1) && ElectricItem.use(itemstack, this.operationEnergyCost, (EntityHuman) entityliving1)) {
      entityliving.damageEntity(DamageSource.playerAttack((EntityHuman) entityliving1), 10);
    }
    else {
      entityliving.damageEntity(DamageSource.playerAttack((EntityHuman) entityliving1), 1);
    }

    if (entityliving instanceof EntityCreeper && entityliving.getHealth() <= 0) {
      IC2Achievements.issueAchievement((EntityHuman) entityliving1, "killCreeperChainsaw");
    }

    return false;
  }

  public boolean canDestroySpecialBlock(Block block) {
    return block.material == Material.WOOD || super.canDestroySpecialBlock(block);
  }

  public boolean onEntityInteract(EntityHuman entityhuman, Entity entity, boolean flag) {
    if (!entity.world.isStatic && !flag) {
      ItemStack itemstack = entityhuman.U();
      if (itemstack != null && itemstack.id == this.id && entity instanceof IShearable && ElectricItem.use(itemstack, this.operationEnergyCost, entityhuman) && ElectricItem.use(itemstack, this.operationEnergyCost, entityhuman)) {
        IShearable ishearable = (IShearable) entity;
        if (ishearable.isShearable(itemstack, entity.world, (int) entity.locX, (int) entity.locY, (int) entity.locZ)) {
          ArrayList arraylist = ishearable.onSheared(itemstack, entity.world, (int) entity.locX, (int) entity.locY, (int) entity.locZ, EnchantmentManager.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS.id, itemstack));

          EntityItem entityitem;
          for (Iterator iterator = arraylist.iterator(); iterator.hasNext(); entityitem.motZ += (double) ((c.nextFloat() - c.nextFloat()) * 0.1F)) {
            ItemStack itemstack1 = (ItemStack) iterator.next();
            entityitem = entity.a(itemstack1, 1.0F);
            entityitem.motY += (double) (c.nextFloat() * 0.05F);
            entityitem.motX += (double) ((c.nextFloat() - c.nextFloat()) * 0.1F);
          }
        }

        return false;
      }
      else {
        return true;
      }
    }
    else {
      return true;
    }
  }

  public boolean onBlockStartBreak(ItemStack itemstack, int i, int j, int k, EntityHuman entityhuman) {
    if (entityhuman.world.isStatic) {
      return false;
    }
    else {
      int l = entityhuman.world.getTypeId(i, j, k);
      if (Block.byId[l] != null && Block.byId[l] instanceof IShearable) {
        org.bukkit.entity.Entity bukkitentity = entityhuman.getBukkitEntity();
        if (bukkitentity instanceof Player) {
          Player player = (Player) bukkitentity;
          BlockBreakEvent breakev = new BlockBreakEvent(player.getWorld().getBlockAt(i, j, k), player);
          Bukkit.getPluginManager().callEvent(breakev);
          if (breakev.isCancelled()) {
            return false;
          }
        }

        IShearable ishearable = (IShearable) Block.byId[l];
        if (ishearable.isShearable(itemstack, entityhuman.world, i, j, k) && ElectricItem.use(itemstack, this.operationEnergyCost, null) && ElectricItem.use(itemstack, this.operationEnergyCost, null)) {
          ArrayList arraylist = ishearable.onSheared(itemstack, entityhuman.world, i, j, k, EnchantmentManager.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS.id, itemstack));
          Iterator iterator = arraylist.iterator();

          while (iterator.hasNext()) {
            ItemStack itemstack1 = (ItemStack) iterator.next();
            float f = 0.7F;
            double d = (double) (c.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d1 = (double) (c.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d2 = (double) (c.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(entityhuman.world, (double) i + d, (double) j + d1, (double) k + d2, itemstack1);
            entityitem.pickupDelay = 10;
            entityhuman.world.addEntity(entityitem);
          }

          entityhuman.a(StatisticList.C[l], 1);
        }
      }

      return false;
    }
  }

  public void a(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
    boolean flag1 = flag && entity instanceof EntityLiving;
    if (Platform.isRendering()) {
      if (flag1 && !wasEquipped) {
        if (audioSource == null) {
          audioSource = AudioManager.createSource(entity, PositionSpec.Hand, "Tools/Chainsaw/ChainsawIdle.ogg", true, false, AudioManager.defaultVolume);
        }

        if (audioSource != null) {
          audioSource.play();
        }
      }
      else if (!flag1 && audioSource != null) {
        audioSource.stop();
        audioSource.remove();
        audioSource = null;
        if (entity instanceof EntityLiving) {
          AudioManager.playOnce(entity, PositionSpec.Hand, "Tools/Chainsaw/ChainsawStop.ogg", true, AudioManager.defaultVolume);
        }
      }
      else if (audioSource != null) {
        audioSource.updatePosition();
      }

      wasEquipped = flag1;
    }

  }

  public String getHitSoundForBlock(int i, int j, int k) {
    String[] as = new String[]{"Tools/Chainsaw/ChainsawUseOne.ogg", "Tools/Chainsaw/ChainsawUseTwo.ogg"};
    return as[c.nextInt(as.length)];
  }
}
