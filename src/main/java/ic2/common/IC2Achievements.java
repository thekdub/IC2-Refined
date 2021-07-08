package ic2.common;

import forge.AchievementPage;
import forge.Configuration;
import forge.MinecraftForge;
import ic2.platform.Ic2;
import net.minecraft.server.*;

import java.util.HashMap;

public class IC2Achievements {
  public static HashMap achievementList;
  private static final int achievementBaseX = -4;
  private static final int achievementBaseY = -5;
  
  public static void init() {
    achievementList = new HashMap();
    registerAchievement(736750, "acquireResin", 2, 0, Ic2Items.resin, AchievementList.g, false);
    if (Ic2Items.copperOre != null || Ic2Items.tinOre != null || Ic2Items.uraniumOre != null) {
      registerAchievement(736751, "mineOre", 4, 0, Ic2Items.copperOre != null ? Ic2Items.copperOre :
          (Ic2Items.tinOre != null ? Ic2Items.tinOre : Ic2Items.uraniumOre), AchievementList.o, false);
    }
    
    registerAchievement(736752, "acquireRefinedIron", 0, 0, Ic2Items.refinedIronIngot, AchievementList.k, false);
    registerAchievement(736753, "buildCable", 0, 2, Ic2Items.insulatedCopperCableItem, "acquireRefinedIron", false);
    registerAchievement(736754, "buildGenerator", 6, 2, Ic2Items.generator, "buildCable", false);
    registerAchievement(736755, "buildMacerator", 6, 0, Ic2Items.macerator, "buildGenerator", false);
    registerAchievement(736757, "buildCoalDiamond", 8, 0, Ic2Items.industrialDiamond, "buildMacerator", false);
    registerAchievement(736758, "buildElecFurnace", 8, 2, Ic2Items.electroFurnace, "buildGenerator", false);
    registerAchievement(736759, "buildIndFurnace", 10, 2, Ic2Items.inductionFurnace, "buildElecFurnace", false);
    registerAchievement(736761, "buildCompressor", 4, 4, Ic2Items.compressor, "buildGenerator", false);
    registerAchievement(736762, "compressUranium", 2, 4, Ic2Items.uraniumIngot, "buildCompressor", false);
    registerAchievement(736763, "dieFromOwnNuke", 0, 4, Ic2Items.nuke, "compressUranium", true);
    registerAchievement(736764, "buildExtractor", 8, 4, Ic2Items.extractor, "buildGenerator", false);
    registerAchievement(736760, "buildBatBox", 6, 6, Ic2Items.batBox, "buildGenerator", false);
    registerAchievement(736765, "buildDrill", 8, 6, Ic2Items.miningDrill, "buildBatBox", false);
    registerAchievement(736766, "buildDDrill", 10, 6, Ic2Items.diamondDrill, "buildDrill", false);
    registerAchievement(736767, "buildChainsaw", 4, 6, Ic2Items.chainsaw, "buildBatBox", false);
    registerAchievement(736768, "killCreeperChainsaw", 2, 6, Ic2Items.chainsaw, "buildChainsaw", true);
    registerAchievement(736769, "buildMFE", 6, 8, Ic2Items.mfeUnit, "buildBatBox", false);
    registerAchievement(736770, "buildMassFab", 8, 8, Ic2Items.massFabricator, "buildBatBox", false);
    registerAchievement(736771, "acquireMatter", 10, 8, Ic2Items.matter, "buildMassFab", false);
    registerAchievement(736772, "buildQArmor", 12, 8, Ic2Items.quantumBodyarmor, "acquireMatter", false);
    registerAchievement(736773, "starveWithQHelmet", 14, 8, Ic2Items.quantumHelmet, "buildQArmor", true);
    registerAchievement(736774, "buildMiningLaser", 4, 8, Ic2Items.miningLaser, "buildMFE", false);
    registerAchievement(736775, "killDragonMiningLaser", 2, 8, Ic2Items.miningLaser, "buildMiningLaser", true);
    registerAchievement(736776, "buildMFS", 6, 10, Ic2Items.mfsUnit, "buildMFE", false);
    registerAchievement(736777, "buildTeleporter", 4, 10, Ic2Items.teleporter, "buildMFS", false);
    registerAchievement(736778, "teleportFarAway", 2, 10, Ic2Items.teleporter, "buildTeleporter", true);
    registerAchievement(736779, "buildTerraformer", 8, 10, Ic2Items.terraformer, "buildMFS", false);
    registerAchievement(736780, "terraformEndCultivation", 10, 10, Ic2Items.cultivationTerraformerBlueprint,
        "buildTerraformer", true);
    MinecraftForge.registerAchievementPage(new AchievementPage("IndustrialCraft 2",
        (Achievement[]) achievementList.values().toArray(new Achievement[achievementList.size()])));
  }
  
  public static Achievement registerAchievement(int i, String s, int j, int k, ItemStack itemstack,
                                                Achievement achievement, boolean flag) {
    Achievement achievement1 =
        new Achievement(i, s, achievementBaseX + j, achievementBaseY + k, itemstack, achievement);
    if (flag) {
      achievement1.b();
    }
    
    achievement1.c();
    achievementList.put(s, achievement1);
    return achievement1;
  }
  
  public static Achievement registerAchievement(int i, String s, int j, int k, ItemStack itemstack, String s1,
                                                boolean flag) {
    Achievement achievement =
        new Achievement(i, s, achievementBaseX + j, achievementBaseY + k, itemstack, getAchievement(s1));
    if (flag) {
      achievement.b();
    }
    
    achievement.c();
    achievementList.put(s, achievement);
    return achievement;
  }
  
  public static void issueAchievement(EntityHuman entityhuman, String s) {
    if (achievementList.containsKey(s)) {
      entityhuman.a((Statistic) achievementList.get(s));
    }
    
  }
  
  public static Achievement getAchievement(String s) {
    return achievementList.containsKey(s) ? (Achievement) achievementList.get(s) : null;
  }
  
  public static void takenFromCrafting(EntityHuman entityhuman, ItemStack itemstack, IInventory iinventory) {
    if (itemstack.doMaterialsMatch(Ic2Items.generator)) {
      issueAchievement(entityhuman, "buildGenerator");
    }
    
    if (itemstack.id == Ic2Items.insulatedCopperCableItem.id) {
      issueAchievement(entityhuman, "buildCable");
    }
    
    if (itemstack.doMaterialsMatch(Ic2Items.macerator)) {
      issueAchievement(entityhuman, "buildMacerator");
    }
    
    if (itemstack.doMaterialsMatch(Ic2Items.electroFurnace)) {
      issueAchievement(entityhuman, "buildElecFurnace");
    }
    
    if (itemstack.doMaterialsMatch(Ic2Items.compressor)) {
      issueAchievement(entityhuman, "buildCompressor");
    }
    
    if (itemstack.doMaterialsMatch(Ic2Items.batBox)) {
      issueAchievement(entityhuman, "buildBatBox");
    }
    
    if (itemstack.doMaterialsMatch(Ic2Items.mfeUnit)) {
      issueAchievement(entityhuman, "buildMFE");
    }
    
    if (itemstack.doMaterialsMatch(Ic2Items.teleporter)) {
      issueAchievement(entityhuman, "buildTeleporter");
    }
    
    if (itemstack.doMaterialsMatch(Ic2Items.massFabricator)) {
      issueAchievement(entityhuman, "buildMassFab");
    }
    
    if (itemstack.id == Ic2Items.quantumBodyarmor.id || itemstack.id == Ic2Items.quantumBoots.id ||
        itemstack.id == Ic2Items.quantumHelmet.id || itemstack.id == Ic2Items.quantumLeggings.id) {
      issueAchievement(entityhuman, "buildQArmor");
    }
    
    if (itemstack.doMaterialsMatch(Ic2Items.extractor)) {
      issueAchievement(entityhuman, "buildExtractor");
    }
    
    if (itemstack.id == Ic2Items.miningDrill.id) {
      issueAchievement(entityhuman, "buildDrill");
    }
    
    if (itemstack.id == Ic2Items.diamondDrill.id) {
      issueAchievement(entityhuman, "buildDDrill");
    }
    
    if (itemstack.id == Ic2Items.chainsaw.id) {
      issueAchievement(entityhuman, "buildChainsaw");
    }
    
    if (itemstack.id == Ic2Items.miningLaser.id) {
      issueAchievement(entityhuman, "buildMiningLaser");
    }
    
    if (itemstack.doMaterialsMatch(Ic2Items.mfsUnit)) {
      issueAchievement(entityhuman, "buildMFS");
    }
    
    if (itemstack.doMaterialsMatch(Ic2Items.terraformer)) {
      issueAchievement(entityhuman, "buildTerraformer");
    }
    
    if (itemstack.doMaterialsMatch(Ic2Items.coalChunk)) {
      issueAchievement(entityhuman, "buildCoalDiamond");
    }
    
    if (itemstack.doMaterialsMatch(Ic2Items.inductionFurnace)) {
      issueAchievement(entityhuman, "buildIndFurnace");
    }
    
  }
  
  public static void takenFromFurnace(EntityHuman entityhuman, ItemStack itemstack) {
    if (itemstack.doMaterialsMatch(Ic2Items.refinedIronIngot)) {
      issueAchievement(entityhuman, "acquireRefinedIron");
    }
    
    if (itemstack.doMaterialsMatch(Ic2Items.uraniumIngot)) {
      issueAchievement(entityhuman, "compressUranium");
    }
    
    if (itemstack.doMaterialsMatch(Ic2Items.matter)) {
      issueAchievement(entityhuman, "acquireMatter");
    }
    
  }
  
  public static void onItemPickup(EntityHuman entityhuman, ItemStack itemstack) {
    if (Ic2Items.copperOre != null && itemstack.doMaterialsMatch(Ic2Items.copperOre) ||
        Ic2Items.tinOre != null && itemstack.doMaterialsMatch(Ic2Items.tinOre) ||
        Ic2Items.uraniumDrop != null && itemstack.doMaterialsMatch(Ic2Items.uraniumDrop)) {
      issueAchievement(entityhuman, "mineOre");
    }
    
  }
  
  public static void addLocalization(Configuration configuration, Ic2 ic2) {
    ic2.addLocalization(configuration, "achievement.acquireRefinedIron", "Better than Iron");
    ic2.addLocalization(configuration, "achievement.acquireRefinedIron.desc", "Smelt a refined iron ingot");
    ic2.addLocalization(configuration, "achievement.buildGenerator", "Suitable Power");
    ic2.addLocalization(configuration, "achievement.buildGenerator.desc", "Build a generator");
    ic2.addLocalization(configuration, "achievement.acquireResin", "Sticky Situation");
    ic2.addLocalization(configuration, "achievement.acquireResin.desc", "Get resin from a rubber tree with a treetap");
    ic2.addLocalization(configuration, "achievement.buildCable", "Energy Flow");
    ic2.addLocalization(configuration, "achievement.buildCable.desc", "Make some cables");
    ic2.addLocalization(configuration, "achievement.mineOre", "New Ore Miner");
    ic2.addLocalization(configuration, "achievement.mineOre.desc", "Mine copper, tin or uranium");
    ic2.addLocalization(configuration, "achievement.buildMacerator", "Double Trouble");
    ic2.addLocalization(configuration, "achievement.buildMacerator.desc", "Build a macerator");
    ic2.addLocalization(configuration, "achievement.buildCoalDiamond", "Shiny");
    ic2.addLocalization(configuration, "achievement.buildCoalDiamond.desc",
        "Make a coal chunk to compress into a diamond");
    ic2.addLocalization(configuration, "achievement.buildElecFurnace", "Even Hotter Topic");
    ic2.addLocalization(configuration, "achievement.buildElecFurnace.desc", "Build an electric furnace");
    ic2.addLocalization(configuration, "achievement.buildIndFurnace", "Hyperspeed");
    ic2.addLocalization(configuration, "achievement.buildIndFurnace.desc", "Build an induction furnace");
    ic2.addLocalization(configuration, "achievement.buildBatBox", "Stash It");
    ic2.addLocalization(configuration, "achievement.buildBatBox.desc", "Build a BatBox");
    ic2.addLocalization(configuration, "achievement.buildCompressor", "Epic Squeeze");
    ic2.addLocalization(configuration, "achievement.buildCompressor.desc", "Build a compressor");
    ic2.addLocalization(configuration, "achievement.compressUranium", "Radioactivity");
    ic2.addLocalization(configuration, "achievement.compressUranium.desc",
        "Compress uranium into an uranium fuel ingot");
    ic2.addLocalization(configuration, "achievement.dieFromOwnNuke", "Crazy Ivan");
    ic2.addLocalization(configuration, "achievement.dieFromOwnNuke.desc", "Die from a nuke you have placed");
    ic2.addLocalization(configuration, "achievement.buildExtractor", "Super Treetap");
    ic2.addLocalization(configuration, "achievement.buildExtractor.desc", "Build an extractor");
    ic2.addLocalization(configuration, "achievement.buildChainsaw", "Lumberjack Assassin");
    ic2.addLocalization(configuration, "achievement.buildChainsaw.desc", "Build a chainsaw");
    ic2.addLocalization(configuration, "achievement.killCreeperChainsaw", "Creeper Chainsaw Massacre");
    ic2.addLocalization(configuration, "achievement.killCreeperChainsaw.desc", "Kill a creeper with a chainsaw");
    ic2.addLocalization(configuration, "achievement.buildDrill", "Meet the Dentist");
    ic2.addLocalization(configuration, "achievement.buildDrill.desc", "Build a mining drill");
    ic2.addLocalization(configuration, "achievement.buildDDrill", "Valuable Upgrade");
    ic2.addLocalization(configuration, "achievement.buildDDrill.desc",
        "Upgrade your mining drill to a diamond-tipped mining drill");
    ic2.addLocalization(configuration, "achievement.buildMFE", "Storage Upgrade");
    ic2.addLocalization(configuration, "achievement.buildMFE.desc", "Build a MFE Unit");
    ic2.addLocalization(configuration, "achievement.buildMiningLaser", "Laser Time");
    ic2.addLocalization(configuration, "achievement.buildMiningLaser.desc", "Build a mining laser");
    ic2.addLocalization(configuration, "achievement.killDragonMiningLaser", "Like A Boss");
    ic2.addLocalization(configuration, "achievement.killDragonMiningLaser.desc",
        "Kill the enderdragon with a mining laser");
    ic2.addLocalization(configuration, "achievement.buildMassFab", "Energy To Matter");
    ic2.addLocalization(configuration, "achievement.buildMassFab.desc", "Build a Mass Fabricator");
    ic2.addLocalization(configuration, "achievement.acquireMatter", "Pink Blob");
    ic2.addLocalization(configuration, "achievement.acquireMatter.desc", "Produce some UU-Matter");
    ic2.addLocalization(configuration, "achievement.buildQArmor", "Hi-Tech Wonder");
    ic2.addLocalization(configuration, "achievement.buildQArmor.desc", "Build a piece of the quantum suit");
    ic2.addLocalization(configuration, "achievement.starveWithQHelmet", "Forgot to Recharge");
    ic2.addLocalization(configuration, "achievement.starveWithQHelmet.desc", "Starve to death with a quantum helmet");
    ic2.addLocalization(configuration, "achievement.buildMFS", "Just Too Much");
    ic2.addLocalization(configuration, "achievement.buildMFS.desc", "Build a MFS Unit");
    ic2.addLocalization(configuration, "achievement.buildTeleporter", "Intradimensional Warp");
    ic2.addLocalization(configuration, "achievement.buildTeleporter.desc", "Build a Teleporter");
    ic2.addLocalization(configuration, "achievement.teleportFarAway", "Far Far Away");
    ic2.addLocalization(configuration, "achievement.teleportFarAway.desc", "Teleport for at least 1 km");
    ic2.addLocalization(configuration, "achievement.buildTerraformer", "Change The World");
    ic2.addLocalization(configuration, "achievement.buildTerraformer.desc", "Build a terraformer");
    ic2.addLocalization(configuration, "achievement.terraformEndCultivation", "Endgame Paradise");
    ic2.addLocalization(configuration, "achievement.terraformEndCultivation.desc",
        "Terraform the End to look like the overworld");
  }
}
