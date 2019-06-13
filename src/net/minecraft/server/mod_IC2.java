package net.minecraft.server;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import forge.*;
import forge.oredict.OreDictionary;
import ic2.api.ExplosionWhitelist;
import ic2.api.FakePlayer;
import ic2.api.Ic2Recipes;
import ic2.common.*;
import ic2.platform.NetworkManager;
import ic2.platform.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class mod_IC2 extends Ic2 implements IConnectionHandler, IOreHandler, ITickHandler {
	static final boolean $assertionsDisabled = !mod_IC2.class.desiredAssertionStatus();
	public static int cableRenderId;
	public static int fenceRenderId;
	public static int miningPipeRenderId;
	public static int luminatorRenderId;
	public static int cropRenderId;
	public static Random random = new Random();
	public static int windStrength;
	public static int windTicker;
	public static Map valuableOres = new TreeMap();
	public static boolean enableCraftingBucket = true;
	public static boolean enableCraftingCoin = true;
	public static boolean enableCraftingGlowstoneDust = true;
	public static boolean enableCraftingGunpowder = true;
	public static boolean enableCraftingITnt = true;
	public static boolean enableCraftingNuke = true;
	public static boolean enableCraftingRail = true;
	public static boolean enableDynamicIdAllocation = true;
	public static boolean enableLoggingWrench = true;
	public static boolean enableSecretRecipeHiding = true;
	public static boolean enableQuantumSpeedOnSprint = true;
	public static boolean enableMinerLapotron = false;
	public static boolean enableTeleporterInventory = true;
	public static boolean enableBurningScrap = true;
	public static boolean enableWorldGenTreeRubber = true;
	public static boolean enableWorldGenOreCopper = true;
	public static boolean enableWorldGenOreTin = true;
	public static boolean enableWorldGenOreUranium = true;
	public static float explosionPowerNuke = 35.0F;
	public static float explosionPowerReactorMax = 45.0F;
	public static int energyGeneratorBase = 10;
	public static int energyGeneratorGeo = 20;
	public static int energyGeneratorWater = 100;
	public static int energyGeneratorSolar = 100;
	public static int energyGeneratorWind = 100;
	public static int energyGeneratorNuclear = 10;
	public static boolean suddenlyHoes = false;
	public static boolean initialized = false;
	public static boolean portEnableCraftingQuantum = true;
	public static boolean portEnableCraftingNano = true;
	public static boolean portEnableCraftingNanoSaber = true;
	public static boolean portEnableCraftingJetpacks = true;
	public static boolean portEnableCraftingLappack = true;
	public static boolean portEnableCraftingMachines = true;
	public static boolean portEnableCraftingBatBox = true;
	public static boolean portEnableCraftingMFE = true;
	public static boolean portEnableCraftingMFS = true;
	public static boolean portEnableCraftingDrill = true;
	public static boolean portEnableCraftingDiamondDrill = true;
	public static boolean portEnableCraftingEnergyCrystal = true;
	public static boolean portEnableCraftingLapotronCrystal = true;
	public static boolean portEnableCraftingREBattery = true;
	public static boolean portEnableCraftingElCircuit = true;
	public static boolean portEnableCraftingAdCircuit = true;
	public static boolean portEnableCraftingIridiumPlate = true;
	public static boolean portEnableOVScanner = true;
	public static boolean portEnableCraftingDynamite = true;
	public static boolean portEnableCraftingLaser = true;
	public static boolean portEnableMiner = true;
	public static boolean portDebugMessages = false;
	public static Block blockScaffold;
	public static Block blockFoam;
	public static boolean portLosslessWrench = false;
	private static mod_IC2 instance = null;
	private static boolean silverDustSmeltingRegistered = false;
	private static Properties runtimeIdProperties = new Properties();
	private static Map singleTickCallbacks = new HashMap();
	private static Map continuousTickCallbacks = new HashMap();
	private static Map continuousTickCallbacksInUse = new HashMap();
	private static Map continuousTickCallbacksToAdd = new HashMap();
	private static Map continuousTickCallbacksToRemove = new HashMap();

	static {
		addValuableOre(Block.COAL_ORE.id, 1);
		addValuableOre(Block.GOLD_ORE.id, 3);
		addValuableOre(Block.REDSTONE_ORE.id, 3);
		addValuableOre(Block.LAPIS_ORE.id, 3);
		addValuableOre(Block.IRON_ORE.id, 4);
		addValuableOre(Block.DIAMOND_ORE.id, 5);
	}

	public mod_IC2() {
		instance = this;
	}

	public static mod_IC2 getInstance() {
		return instance;
	}

	private static boolean loadSubModule(String s) {
		System.out.println("[IC2] Loading IC2 submodule: " + s);

		try {
			Class class1 = mod_IC2.class.getClassLoader().loadClass("ic2." + s + ".SubModule");
			return (Boolean) class1.getMethod("init").invoke(null);
		} catch (Throwable var2) {
			System.out.println("[IC2] Submodule not loaded: " + var2 + (var2 instanceof InvocationTargetException ? ": " + var2.getCause() : ""));
			return false;
		}
	}

	private static void registerCraftingRecipes() {
		Ic2Recipes.addCraftingRecipe(Ic2Items.copperBlock, new Object[]{"MMM", "MMM", "MMM", 'M', "ingotCopper"});
		Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeBlock, new Object[]{"MMM", "MMM", "MMM", 'M', "ingotBronze"});
		Ic2Recipes.addCraftingRecipe(Ic2Items.tinBlock, new Object[]{"MMM", "MMM", "MMM", 'M', "ingotTin"});
		Ic2Recipes.addCraftingRecipe(Ic2Items.uraniumBlock, new Object[]{"MMM", "MMM", "MMM", 'M', "ingotUranium"});
		if (portEnableCraftingMachines) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.ironFurnace, new Object[]{"III", "I I", "III", 'I', Item.IRON_INGOT});
			Ic2Recipes.addCraftingRecipe(Ic2Items.ironFurnace, new Object[]{" I ", "I I", "IFI", 'I', Item.IRON_INGOT, 'F', Block.FURNACE});
			Ic2Recipes.addCraftingRecipe(Ic2Items.electroFurnace, new Object[]{" C ", "RFR", 'C', Ic2Items.electronicCircuit, 'R', Item.REDSTONE, 'F', Ic2Items.ironFurnace});
			Ic2Recipes.addCraftingRecipe(Ic2Items.macerator, new Object[]{"FFF", "SMS", " C ", 'F', Item.FLINT, 'S', Block.COBBLESTONE, 'M', Ic2Items.machine, 'C', Ic2Items.electronicCircuit});
			Ic2Recipes.addCraftingRecipe(Ic2Items.extractor, new Object[]{"TMT", "TCT", 'T', Ic2Items.treetap, 'M', Ic2Items.machine, 'C', Ic2Items.electronicCircuit});
			Ic2Recipes.addCraftingRecipe(Ic2Items.compressor, new Object[]{"S S", "SMS", "SCS", 'S', Block.STONE, 'M', Ic2Items.machine, 'C', Ic2Items.electronicCircuit});
			Ic2Recipes.addCraftingRecipe(Ic2Items.miner, new Object[]{"CMC", " P ", " P ", 'P', Ic2Items.miningPipe, 'M', Ic2Items.machine, 'C', Ic2Items.electronicCircuit});
			Ic2Recipes.addCraftingRecipe(Ic2Items.pump, new Object[]{"cCc", "cMc", "PTP", 'c', Ic2Items.cell, 'T', Ic2Items.treetap, 'P', Ic2Items.miningPipe, 'M', Ic2Items.machine, 'C', Ic2Items.electronicCircuit});
			Ic2Recipes.addCraftingRecipe(Ic2Items.magnetizer, new Object[]{"RFR", "RMR", "RFR", 'R', Item.REDSTONE, 'F', Ic2Items.ironFence, 'M', Ic2Items.machine});
			Ic2Recipes.addCraftingRecipe(Ic2Items.electrolyzer, new Object[]{"c c", "cCc", "EME", 'E', Ic2Items.cell, 'c', Ic2Items.insulatedCopperCableItem, 'M', Ic2Items.machine, 'C', Ic2Items.electronicCircuit});
		}

		Ic2Recipes.addCraftingRecipe(Ic2Items.advancedMachine, new Object[]{" A ", "CMC", " A ", 'A', Ic2Items.advancedAlloy, 'C', Ic2Items.carbonPlate, 'M', Ic2Items.machine});
		Ic2Recipes.addCraftingRecipe(Ic2Items.advancedMachine, new Object[]{" C ", "AMA", " C ", 'A', Ic2Items.advancedAlloy, 'C', Ic2Items.carbonPlate, 'M', Ic2Items.machine});
		if (portEnableCraftingMachines) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.personalSafe, new Object[]{"c", "M", "C", 'c', Ic2Items.electronicCircuit, 'C', Block.CHEST, 'M', Ic2Items.machine});
			Ic2Recipes.addCraftingRecipe(Ic2Items.tradeOMat, new Object[]{"RRR", "CMC", 'R', Item.REDSTONE, 'C', Block.CHEST, 'M', Ic2Items.machine});
			Ic2Recipes.addCraftingRecipe(Ic2Items.massFabricator, new Object[]{"GCG", "ALA", "GCG", 'A', Ic2Items.advancedMachine, 'L', Ic2Items.lapotronCrystal, 'G', Item.GLOWSTONE_DUST, 'C', Ic2Items.advancedCircuit});
			Ic2Recipes.addCraftingRecipe(Ic2Items.terraformer, new Object[]{"GTG", "DMD", "GDG", 'T', Ic2Items.terraformerBlueprint, 'G', Item.GLOWSTONE_DUST, 'D', Block.DIRT, 'M', Ic2Items.advancedMachine});
			Ic2Recipes.addCraftingRecipe(Ic2Items.teleporter, new Object[]{"GFG", "CMC", "GDG", 'M', Ic2Items.advancedMachine, 'C', Ic2Items.glassFiberCableItem, 'F', Ic2Items.frequencyTransmitter, 'G', Ic2Items.advancedCircuit, 'D', Ic2Items.industrialDiamond});
			Ic2Recipes.addCraftingRecipe(Ic2Items.teleporter, new Object[]{"GFG", "CMC", "GDG", 'M', Ic2Items.advancedMachine, 'C', Ic2Items.glassFiberCableItem, 'F', Ic2Items.frequencyTransmitter, 'G', Ic2Items.advancedCircuit, 'D', Item.DIAMOND});
			Ic2Recipes.addCraftingRecipe(Ic2Items.inductionFurnace, new Object[]{"CCC", "CFC", "CMC", 'C', "ingotCopper", 'F', Ic2Items.electroFurnace, 'M', Ic2Items.advancedMachine});
			Ic2Recipes.addCraftingRecipe(Ic2Items.recycler, new Object[]{" G ", "DMD", "IDI", 'D', Block.DIRT, 'G', Item.GLOWSTONE_DUST, 'M', Ic2Items.compressor, 'I', "ingotRefinedIron"});
			Ic2Recipes.addCraftingRecipe(Ic2Items.canner, new Object[]{"TCT", "TMT", "TTT", 'T', "ingotTin", 'M', Ic2Items.machine, 'C', Ic2Items.electronicCircuit});
			Ic2Recipes.addCraftingRecipe(Ic2Items.teslaCoil, new Object[]{"RRR", "RMR", "ICI", 'M', Ic2Items.mvTransformer, 'R', Item.REDSTONE, 'C', Ic2Items.electronicCircuit, 'I', "ingotRefinedIron"});
			Ic2Recipes.addCraftingRecipe(Ic2Items.generator, new Object[]{" B ", "III", " F ", 'B', Ic2Items.reBattery, 'F', Ic2Items.ironFurnace, 'I', "ingotRefinedIron"});
			Ic2Recipes.addCraftingRecipe(Ic2Items.generator, new Object[]{" B ", "III", " F ", 'B', Ic2Items.chargedReBattery, 'F', Ic2Items.ironFurnace, 'I', "ingotRefinedIron"});
			Ic2Recipes.addCraftingRecipe(Ic2Items.generator, new Object[]{"B", "M", "F", 'B', Ic2Items.reBattery, 'F', Block.FURNACE, 'M', Ic2Items.machine});
			Ic2Recipes.addCraftingRecipe(Ic2Items.generator, new Object[]{"B", "M", "F", 'B', Ic2Items.chargedReBattery, 'F', Block.FURNACE, 'M', Ic2Items.machine});
			Ic2Recipes.addCraftingRecipe(Ic2Items.reactorChamber, new Object[]{"ACA", "PMP", "APA", 'A', Ic2Items.advancedAlloy, 'C', Ic2Items.integratedHeatDisperser, 'P', Ic2Items.integratedReactorPlating, 'M', Ic2Items.machine});
			if (energyGeneratorWater > 0) {
				Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.waterMill, 2), new Object[]{"SPS", "PGP", "SPS", 'S', Item.STICK, 'P', Block.WOOD, 'G', Ic2Items.generator});
			}

			if (energyGeneratorSolar > 0) {
				Ic2Recipes.addCraftingRecipe(Ic2Items.solarPanel, new Object[]{"CgC", "gCg", "cGc", 'G', Ic2Items.generator, 'C', Ic2Items.coalDust, 'g', Block.GLASS, 'c', Ic2Items.electronicCircuit});
			}

			if (energyGeneratorWind > 0) {
				Ic2Recipes.addCraftingRecipe(Ic2Items.windMill, new Object[]{"I I", " G ", "I I", 'I', Item.IRON_INGOT, 'G', Ic2Items.generator});
			}

			if (energyGeneratorNuclear > 0) {
				Ic2Recipes.addCraftingRecipe(Ic2Items.nuclearReactor, new Object[]{"AcA", "CGC", "AcA", 'A', Ic2Items.advancedAlloy, 'C', Ic2Items.reactorChamber, 'c', Ic2Items.advancedCircuit, 'G', Ic2Items.generator});
			}

			if (energyGeneratorGeo > 0) {
				Ic2Recipes.addCraftingRecipe(Ic2Items.geothermalGenerator, new Object[]{"gCg", "gCg", "IGI", 'G', Ic2Items.generator, 'C', Ic2Items.cell, 'g', Block.GLASS, 'I', "ingotRefinedIron"});
			}
		}

		Ic2Recipes.addCraftingRecipe(Ic2Items.machine, new Object[]{"III", "I I", "III", 'I', "ingotRefinedIron"});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.luminator, 8), new Object[]{"ICI", "GTG", "GGG", 'G', Block.GLASS, 'I', "ingotRefinedIron", 'T', Ic2Items.tinCableItem, 'C', Ic2Items.insulatedCopperCableItem});
		if (portEnableCraftingBatBox) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.batBox, new Object[]{"PCP", "BBB", "PPP", 'P', Block.WOOD, 'C', Ic2Items.insulatedCopperCableItem, 'B', Ic2Items.reBattery});
			Ic2Recipes.addCraftingRecipe(Ic2Items.batBox, new Object[]{"PCP", "BBB", "PPP", 'P', Block.WOOD, 'C', Ic2Items.insulatedCopperCableItem, 'B', Ic2Items.chargedReBattery});
		}

		if (portEnableCraftingMFE) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.mfeUnit, new Object[]{"cCc", "CMC", "cCc", 'M', Ic2Items.machine, 'c', Ic2Items.doubleInsulatedGoldCableItem, 'C', Ic2Items.energyCrystal});
		}

		if (portEnableCraftingMFS) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.mfsUnit, new Object[]{"LCL", "LML", "LAL", 'M', Ic2Items.mfeUnit, 'A', Ic2Items.advancedMachine, 'C', Ic2Items.advancedCircuit, 'L', Ic2Items.lapotronCrystal});
		}

		Ic2Recipes.addCraftingRecipe(Ic2Items.lvTransformer, new Object[]{"PCP", "ccc", "PCP", 'P', Block.WOOD, 'C', Ic2Items.insulatedCopperCableItem, 'c', "ingotCopper"});
		Ic2Recipes.addCraftingRecipe(Ic2Items.mvTransformer, new Object[]{" C ", " M ", " C ", 'M', Ic2Items.machine, 'C', Ic2Items.doubleInsulatedGoldCableItem});
		Ic2Recipes.addCraftingRecipe(Ic2Items.hvTransformer, new Object[]{" c ", "CED", " c ", 'E', Ic2Items.mvTransformer, 'c', Ic2Items.trippleInsulatedIronCableItem, 'D', Ic2Items.energyCrystal, 'C', Ic2Items.electronicCircuit});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.reinforcedStone, 8), new Object[]{"SSS", "SAS", "SSS", 'S', Block.STONE, 'A', Ic2Items.advancedAlloy});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.reinforcedGlass, 7), new Object[]{"GAG", "GGG", "GAG", 'G', Block.GLASS, 'A', Ic2Items.advancedAlloy});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.reinforcedGlass, 7), new Object[]{"GGG", "AGA", "GGG", 'G', Block.GLASS, 'A', Ic2Items.advancedAlloy});
		Ic2Recipes.addCraftingRecipe(Ic2Items.remote, new Object[]{" c ", "GCG", "TTT", 'c', Ic2Items.insulatedCopperCableItem, 'G', Item.GLOWSTONE_DUST, 'C', Ic2Items.electronicCircuit, 'T', Block.TNT});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.rubberTrampoline, 3), new Object[]{"RRR", "RRR", 'R', "itemRubber"});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.TORCH, 4), new Object[]{"R", "I", 'I', Item.STICK, 'R', Ic2Items.resin, true});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.scaffold, 16), new Object[]{"PPP", " s ", "s s", 'P', Block.WOOD, 's', Item.STICK});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.ironScaffold, 16), new Object[]{"PPP", " s ", "s s", 'P', "ingotRefinedIron", 's', Ic2Items.ironFence.getItem()});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.ironFence, 12), new Object[]{"III", "III", 'I', "ingotRefinedIron"});
		if (enableCraftingITnt) {
			Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.industrialTnt, 4), new Object[]{"FFF", "TTT", "FFF", 'F', Item.FLINT, 'T', Block.TNT});
			Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.industrialTnt, 4), new Object[]{"FTF", "FTF", "FTF", 'F', Item.FLINT, 'T', Block.TNT});
		}

		if (enableCraftingNuke) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.nuke, new Object[]{"GUG", "UGU", "GUG", 'G', Item.SULPHUR, 'U', "ingotUranium", true});
		}

		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.STONE, 16), new Object[]{"   ", " M ", "   ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.GLASS, 32), new Object[]{" M ", "M M", " M ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.GRASS, 16), new Object[]{"   ", "M  ", "M  ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.MOSSY_COBBLESTONE, 16), new Object[]{"   ", " M ", "M M", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.SANDSTONE, 16), new Object[]{"   ", "  M", " M ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.SNOW_BLOCK, 4), new Object[]{"M M", "   ", "   ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.STATIONARY_WATER, 1), new Object[]{"   ", " M ", " M ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.STATIONARY_LAVA, 1), new Object[]{" M ", " M ", " M ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.IRON_ORE, 2), new Object[]{"M M", " M ", "M M", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.GOLD_ORE, 2), new Object[]{" M ", "MMM", " M ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.OBSIDIAN, 12), new Object[]{"M M", "M M", "   ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.NETHERRACK, 16), new Object[]{"  M", " M ", "M  ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.GLOWSTONE, 8), new Object[]{" M ", "M M", "MMM", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.LOG, 8), new Object[]{" M ", "   ", "   ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.CACTUS, 48), new Object[]{" M ", "MMM", "M M", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.VINE, 24), new Object[]{"M  ", "M  ", "M  ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.WOOL, 12), new Object[]{"M M", "   ", " M ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.COAL, 20), new Object[]{"  M", "M  ", "  M", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.DIAMOND, 1), new Object[]{"MMM", "MMM", "MMM", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.REDSTONE, 24), new Object[]{"   ", " M ", "MMM", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.INK_SACK, 9, 4), new Object[]{" M ", " M ", " MM", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.FEATHER, 32), new Object[]{" M ", " M ", "M M", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.SNOW_BALL, 16), new Object[]{"   ", "   ", "MMM", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.SULPHUR, 15), new Object[]{"MMM", "M  ", "MMM", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.CLAY_BALL, 48), new Object[]{"MM ", "M  ", "MM ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.INK_SACK, 32, 3), new Object[]{"MM ", "  M", "MM ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.INK_SACK, 48, 0), new Object[]{" MM", " MM", " M ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.SUGAR_CANE, 48), new Object[]{"M M", "M M", "M M", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.FLINT, 32), new Object[]{" M ", "MM ", "MM ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.BONE, 32), new Object[]{"M  ", "MM ", "M  ", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.resin, 21), new Object[]{"M M", "   ", "M M", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.iridiumOre, 1), new Object[]{"MMM", " M ", "MMM", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.MYCEL, 24), new Object[]{"   ", "M M", "MMM", 'M', Ic2Items.matter, true});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Block.SMOOTH_BRICK, 48, 3), new Object[]{"MM ", "MM ", "M  ", 'M', Ic2Items.matter, true});
		if (Ic2Items.copperOre != null) {
			Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.copperOre, 5), new Object[]{"  M", "M M", "   ", 'M', Ic2Items.matter, true});
		}
		else {
			Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.copperDust, 10), new Object[]{"  M", "M M", "   ", 'M', Ic2Items.matter, true});
		}

		if (Ic2Items.tinOre != null) {
			Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.tinOre, 5), new Object[]{"   ", "M M", "  M", 'M', Ic2Items.matter, true});
		}
		else {
			Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.tinDust, 10), new Object[]{"   ", "M M", "  M", 'M', Ic2Items.matter, true});
		}

		if (Ic2Items.rubberWood != null) {
			Ic2Recipes.addCraftingRecipe(new ItemStack(Block.WOOD, 3, 3), new Object[]{"W", 'W', Ic2Items.rubberWood});
		}

		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.insulatedCopperCableItem, 6), new Object[]{"RRR", "CCC", "RRR", 'C', "ingotCopper", 'R', "itemRubber"});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.insulatedCopperCableItem, 6), new Object[]{"RCR", "RCR", "RCR", 'C', "ingotCopper", 'R', "itemRubber"});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.copperCableItem, 6), new Object[]{"CCC", 'C', "ingotCopper"});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.goldCableItem, 12), new Object[]{"GGG", 'G', Item.GOLD_INGOT});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.insulatedGoldCableItem, 4), new Object[]{" R ", "RGR", " R ", 'G', Item.GOLD_INGOT, 'R', "itemRubber"});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.glassFiberCableItem, 4), new Object[]{"GGG", "RDR", "GGG", 'G', Block.GLASS, 'R', Item.REDSTONE, 'D', Item.DIAMOND});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.glassFiberCableItem, 4), new Object[]{"GGG", "RDR", "GGG", 'G', Block.GLASS, 'R', Item.REDSTONE, 'D', Ic2Items.industrialDiamond});
		Ic2Recipes.addCraftingRecipe(Ic2Items.detectorCableItem, new Object[]{" C ", "RIR", " R ", 'R', Item.REDSTONE, 'I', Ic2Items.trippleInsulatedIronCableItem, 'C', Ic2Items.electronicCircuit});
		Ic2Recipes.addCraftingRecipe(Ic2Items.splitterCableItem, new Object[]{" R ", "ILI", " R ", 'R', Item.REDSTONE, 'I', Ic2Items.trippleInsulatedIronCableItem, 'L', Block.LEVER});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.ironCableItem, 12), new Object[]{"III", 'I', "ingotRefinedIron"});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.insulatedIronCableItem, 4), new Object[]{" R ", "RIR", " R ", 'I', "ingotRefinedIron", 'R', "itemRubber"});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.tinCableItem, 9), new Object[]{"TTT", 'T', "ingotTin"});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.insulatedCopperCableItem, new Object[]{"itemRubber", Ic2Items.copperCableItem});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.insulatedGoldCableItem, new Object[]{"itemRubber", Ic2Items.goldCableItem});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.doubleInsulatedGoldCableItem, new Object[]{"itemRubber", Ic2Items.insulatedGoldCableItem});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.doubleInsulatedGoldCableItem, new Object[]{"itemRubber", "itemRubber", Ic2Items.goldCableItem});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.insulatedIronCableItem, new Object[]{"itemRubber", Ic2Items.ironCableItem});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.doubleInsulatedIronCableItem, new Object[]{"itemRubber", Ic2Items.insulatedIronCableItem});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.trippleInsulatedIronCableItem, new Object[]{"itemRubber", Ic2Items.doubleInsulatedIronCableItem});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.trippleInsulatedIronCableItem, new Object[]{"itemRubber", "itemRubber", Ic2Items.insulatedIronCableItem});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.doubleInsulatedIronCableItem, new Object[]{"itemRubber", "itemRubber", Ic2Items.ironCableItem});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.trippleInsulatedIronCableItem, new Object[]{"itemRubber", "itemRubber", "itemRubber", Ic2Items.ironCableItem});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.suBattery, 5), new Object[]{"C", "R", "D", 'D', Ic2Items.coalDust, 'R', Item.REDSTONE, 'C', Ic2Items.insulatedCopperCableItem});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.suBattery, 5), new Object[]{"C", "D", "R", 'D', Ic2Items.coalDust, 'R', Item.REDSTONE, 'C', Ic2Items.insulatedCopperCableItem});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.suBattery, 8), new Object[]{"c", "C", "R", 'R', Item.REDSTONE, 'C', Ic2Items.hydratedCoalDust, 'c', Ic2Items.insulatedCopperCableItem});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.suBattery, 8), new Object[]{"c", "R", "C", 'R', Item.REDSTONE, 'C', Ic2Items.hydratedCoalDust, 'c', Ic2Items.insulatedCopperCableItem});
		if (portEnableCraftingREBattery) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.reBattery, new Object[]{" C ", "TRT", "TRT", 'T', "ingotTin", 'R', Item.REDSTONE, 'C', Ic2Items.insulatedCopperCableItem});
		}

		if (portEnableCraftingEnergyCrystal) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.energyCrystal, new Object[]{"RRR", "RDR", "RRR", 'D', Item.DIAMOND, 'R', Item.REDSTONE});
			Ic2Recipes.addCraftingRecipe(Ic2Items.energyCrystal, new Object[]{"RRR", "RDR", "RRR", 'D', Ic2Items.industrialDiamond, 'R', Item.REDSTONE});
		}

		if (portEnableCraftingLapotronCrystal) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.lapotronCrystal, new Object[]{"LCL", "LDL", "LCL", 'D', Ic2Items.energyCrystal, 'C', Ic2Items.electronicCircuit, 'L', new ItemStack(Item.INK_SACK, 1, 4)});
		}

		Ic2Recipes.addCraftingRecipe(Ic2Items.treetap, new Object[]{" P ", "PPP", "P  ", 'P', Block.WOOD});
		Ic2Recipes.addCraftingRecipe(Ic2Items.painter, new Object[]{" CC", " IC", "I  ", 'C', Block.WOOL, 'I', Item.IRON_INGOT});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.DIAMOND_PICKAXE, 1), new Object[]{"DDD", " S ", " S ", 'S', Item.STICK, 'D', Ic2Items.industrialDiamond});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.DIAMOND_HOE, 1), new Object[]{"DD ", " S ", " S ", 'S', Item.STICK, 'D', Ic2Items.industrialDiamond});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.DIAMOND_SPADE, 1), new Object[]{"D", "S", "S", 'S', Item.STICK, 'D', Ic2Items.industrialDiamond});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.DIAMOND_AXE, 1), new Object[]{"DD ", "DS ", " S ", 'S', Item.STICK, 'D', Ic2Items.industrialDiamond});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Item.DIAMOND_SWORD, 1), new Object[]{"D", "D", "S", 'S', Item.STICK, 'D', Ic2Items.industrialDiamond});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 1601), new Object[]{"SS ", "Ss ", "  S", 'S', Block.COBBLESTONE, 's', Item.STICK});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 1), new Object[]{"PPP", "PSP", "PPP", 'P', Ic2Items.constructionFoamPellet, 'S', new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 1601)});
		Ic2Recipes.addCraftingRecipe(Ic2Items.bronzePickaxe, new Object[]{"BBB", " S ", " S ", 'B', "ingotBronze", 'S', Item.STICK});
		Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeAxe, new Object[]{"BB", "SB", "S ", 'B', "ingotBronze", 'S', Item.STICK});
		Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeHoe, new Object[]{"BB", "S ", "S ", 'B', "ingotBronze", 'S', Item.STICK});
		Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeSword, new Object[]{"B", "B", "S", 'B', "ingotBronze", 'S', Item.STICK});
		Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeShovel, new Object[]{" B ", " S ", " S ", 'B', "ingotBronze", 'S', Item.STICK});
		Ic2Recipes.addCraftingRecipe(Ic2Items.wrench, new Object[]{"B B", "BBB", " B ", 'B', "ingotBronze"});
		Ic2Recipes.addCraftingRecipe(Ic2Items.cutter, new Object[]{"A A", " A ", "I I", 'A', "ingotRefinedIron", 'I', Item.IRON_INGOT});
		Ic2Recipes.addCraftingRecipe(Ic2Items.toolbox, new Object[]{"ICI", "III", 'C', Block.CHEST, 'I', "ingotRefinedIron"});
		if (portEnableCraftingDrill) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.miningDrill, new Object[]{" I ", "ICI", "IBI", 'I', "ingotRefinedIron", 'B', Ic2Items.reBattery, 'C', Ic2Items.electronicCircuit});
			Ic2Recipes.addCraftingRecipe(Ic2Items.miningDrill, new Object[]{" I ", "ICI", "IBI", 'I', "ingotRefinedIron", 'B', Ic2Items.chargedReBattery, 'C', Ic2Items.electronicCircuit});
		}

		Ic2Recipes.addCraftingRecipe(Ic2Items.chainsaw, new Object[]{" II", "ICI", "BI ", 'I', "ingotRefinedIron", 'B', Ic2Items.reBattery, 'C', Ic2Items.electronicCircuit});
		Ic2Recipes.addCraftingRecipe(Ic2Items.chainsaw, new Object[]{" II", "ICI", "BI ", 'I', "ingotRefinedIron", 'B', Ic2Items.chargedReBattery, 'C', Ic2Items.electronicCircuit});
		if (portEnableCraftingDiamondDrill) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.diamondDrill, new Object[]{" D ", "DdD", 'D', Item.DIAMOND, 'd', Ic2Items.miningDrill});
			Ic2Recipes.addCraftingRecipe(Ic2Items.diamondDrill, new Object[]{" D ", "DdD", 'D', Item.DIAMOND, 'd', Ic2Items.miningDrill});
		}

		Ic2Recipes.addCraftingRecipe(Ic2Items.odScanner, new Object[]{" G ", "CBC", "ccc", 'B', Ic2Items.reBattery, 'c', Ic2Items.insulatedCopperCableItem, 'G', Item.GLOWSTONE_DUST, 'C', Ic2Items.electronicCircuit});
		Ic2Recipes.addCraftingRecipe(Ic2Items.odScanner, new Object[]{" G ", "CBC", "ccc", 'B', Ic2Items.chargedReBattery, 'c', Ic2Items.insulatedCopperCableItem, 'G', Item.GLOWSTONE_DUST, 'C', Ic2Items.electronicCircuit});
		if (portEnableOVScanner) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.ovScanner, new Object[]{" G ", "GCG", "cSc", 'S', Ic2Items.odScanner, 'c', Ic2Items.doubleInsulatedGoldCableItem, 'G', Item.GLOWSTONE_DUST, 'C', Ic2Items.advancedCircuit});
			Ic2Recipes.addCraftingRecipe(Ic2Items.ovScanner, new Object[]{" G ", "GCG", "cSc", 'S', Ic2Items.chargedReBattery, 'c', Ic2Items.doubleInsulatedGoldCableItem, 'G', Item.GLOWSTONE_DUST, 'C', Ic2Items.advancedCircuit});
		}

		Ic2Recipes.addCraftingRecipe(Ic2Items.electricWrench, new Object[]{"  W", " C ", "B  ", 'W', Ic2Items.wrench, 'B', Ic2Items.reBattery, 'C', Ic2Items.electronicCircuit});
		Ic2Recipes.addCraftingRecipe(Ic2Items.electricWrench, new Object[]{"  W", " C ", "B  ", 'W', Ic2Items.wrench, 'B', Ic2Items.chargedReBattery, 'C', Ic2Items.electronicCircuit});
		Ic2Recipes.addCraftingRecipe(Ic2Items.electricTreetap, new Object[]{"  W", " C ", "B  ", 'W', Ic2Items.treetap, 'B', Ic2Items.reBattery, 'C', Ic2Items.electronicCircuit});
		Ic2Recipes.addCraftingRecipe(Ic2Items.electricTreetap, new Object[]{"  W", " C ", "B  ", 'W', Ic2Items.treetap, 'B', Ic2Items.chargedReBattery, 'C', Ic2Items.electronicCircuit});
		Ic2Recipes.addCraftingRecipe(Ic2Items.ecMeter, new Object[]{" G ", "cCc", "c c", 'G', Item.GLOWSTONE_DUST, 'c', Ic2Items.insulatedCopperCableItem, 'C', Ic2Items.electronicCircuit});
		if (portEnableCraftingLaser) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.miningLaser, new Object[]{"Rcc", "AAC", " AA", 'A', Ic2Items.advancedAlloy, 'C', Ic2Items.advancedCircuit, 'c', Ic2Items.energyCrystal, 'R', Item.REDSTONE});
		}

		if (portEnableCraftingNanoSaber) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.nanoSaber, new Object[]{"GA ", "GA ", "CcC", 'C', Ic2Items.carbonPlate, 'c', Ic2Items.energyCrystal, 'G', Item.GLOWSTONE_DUST, 'A', Ic2Items.advancedAlloy});
		}

		Ic2Recipes.addCraftingRecipe(Ic2Items.electricHoe, new Object[]{"II ", " C ", " B ", 'I', "ingotRefinedIron", 'B', Ic2Items.reBattery, 'C', Ic2Items.electronicCircuit});
		Ic2Recipes.addCraftingRecipe(Ic2Items.electricHoe, new Object[]{"II ", " C ", " B ", 'I', "ingotRefinedIron", 'B', Ic2Items.chargedReBattery, 'C', Ic2Items.electronicCircuit});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.frequencyTransmitter, new Object[]{Ic2Items.electronicCircuit, Ic2Items.insulatedCopperCableItem});
		if (portEnableCraftingAdCircuit) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.advancedCircuit, new Object[]{"RGR", "LCL", "RGR", 'L', new ItemStack(Item.INK_SACK, 1, 4), 'G', Item.GLOWSTONE_DUST, 'R', Item.REDSTONE, 'C', Ic2Items.electronicCircuit});
			Ic2Recipes.addCraftingRecipe(Ic2Items.advancedCircuit, new Object[]{"RLR", "GCG", "RLR", 'L', new ItemStack(Item.INK_SACK, 1, 4), 'G', Item.GLOWSTONE_DUST, 'R', Item.REDSTONE, 'C', Ic2Items.electronicCircuit});
		}

		Ic2Recipes.addCraftingRecipe(Ic2Items.plantBall, new Object[]{"PPP", "P P", "PPP", 'P', Item.WHEAT});
		Ic2Recipes.addCraftingRecipe(Ic2Items.plantBall, new Object[]{"PPP", "P P", "PPP", 'P', Item.SUGAR_CANE});
		Ic2Recipes.addCraftingRecipe(Ic2Items.plantBall, new Object[]{"PPP", "P P", "PPP", 'P', Item.SEEDS});
		Ic2Recipes.addCraftingRecipe(Ic2Items.plantBall, new Object[]{"PPP", "P P", "PPP", 'P', Block.LEAVES});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.plantBall, 2), new Object[]{"PPP", "P P", "PPP", 'P', Block.SAPLING});
		Ic2Recipes.addCraftingRecipe(Ic2Items.plantBall, new Object[]{"PPP", "P P", "PPP", 'P', Block.LONG_GRASS});
		Ic2Recipes.addCraftingRecipe(Ic2Items.carbonFiber, new Object[]{"CC", "CC", 'C', Ic2Items.coalDust});
		if (portEnableCraftingIridiumPlate) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.iridiumPlate, new Object[]{"IAI", "ADA", "IAI", 'I', Ic2Items.iridiumOre, 'A', Ic2Items.advancedAlloy, 'D', Item.DIAMOND});
			Ic2Recipes.addCraftingRecipe(Ic2Items.iridiumPlate, new Object[]{"IAI", "ADA", "IAI", 'I', Ic2Items.iridiumOre, 'A', Ic2Items.advancedAlloy, 'D', Ic2Items.industrialDiamond});
		}

		Ic2Recipes.addCraftingRecipe(Ic2Items.coalBall, new Object[]{"CCC", "CFC", "CCC", 'C', Ic2Items.coalDust, 'F', Item.FLINT});
		Ic2Recipes.addCraftingRecipe(Ic2Items.coalChunk, new Object[]{"###", "#O#", "###", '#', Ic2Items.compressedCoalBall, 'O', Block.OBSIDIAN});
		Ic2Recipes.addCraftingRecipe(Ic2Items.coalChunk, new Object[]{"###", "#O#", "###", '#', Ic2Items.compressedCoalBall, 'O', Block.IRON_BLOCK, true});
		Ic2Recipes.addCraftingRecipe(Ic2Items.coalChunk, new Object[]{"###", "#O#", "###", '#', Ic2Items.compressedCoalBall, 'O', Block.BRICK, true});
		Ic2Recipes.addCraftingRecipe(Ic2Items.smallIronDust, new Object[]{"CTC", "TCT", "CTC", 'C', Ic2Items.copperDust, 'T', Ic2Items.tinDust, true});
		Ic2Recipes.addCraftingRecipe(Ic2Items.smallIronDust, new Object[]{"TCT", "CTC", "TCT", 'C', Ic2Items.copperDust, 'T', Ic2Items.tinDust, true});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.hydratedCoalDust, 8), new Object[]{"CCC", "CWC", "CCC", 'C', Ic2Items.coalDust, 'W', Item.WATER_BUCKET});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.hydratedCoalDust, 8), new Object[]{"CCC", "CWC", "CCC", 'C', Ic2Items.coalDust, 'W', Ic2Items.waterCell});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.refinedIronIngot, 8), new Object[]{"M", 'M', Ic2Items.machine});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.copperIngot, 9), new Object[]{"B", 'B', Ic2Items.copperBlock});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.tinIngot, 9), new Object[]{"B", 'B', Ic2Items.tinBlock});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.bronzeIngot, 9), new Object[]{"B", 'B', Ic2Items.bronzeBlock});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.uraniumIngot, 9), new Object[]{"B", 'B', Ic2Items.uraniumBlock});
		if (portEnableCraftingElCircuit) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.electronicCircuit, new Object[]{"CCC", "RIR", "CCC", 'I', "ingotRefinedIron", 'R', Item.REDSTONE, 'C', Ic2Items.insulatedCopperCableItem});
			Ic2Recipes.addCraftingRecipe(Ic2Items.electronicCircuit, new Object[]{"CRC", "CIC", "CRC", 'I', "ingotRefinedIron", 'R', Item.REDSTONE, 'C', Ic2Items.insulatedCopperCableItem});
		}

		Ic2Recipes.addCraftingRecipe(Ic2Items.compositeArmor, new Object[]{"A A", "ALA", "AIA", 'L', Item.LEATHER_CHESTPLATE, 'I', Item.IRON_CHESTPLATE, 'A', Ic2Items.advancedAlloy});
		Ic2Recipes.addCraftingRecipe(Ic2Items.compositeArmor, new Object[]{"A A", "AIA", "ALA", 'L', Item.LEATHER_CHESTPLATE, 'I', Item.IRON_CHESTPLATE, 'A', Ic2Items.advancedAlloy});
		if (portEnableCraftingNano) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.nanoHelmet, new Object[]{"CcC", "CGC", 'C', Ic2Items.carbonPlate, 'c', Ic2Items.energyCrystal, 'G', Block.GLASS});
			Ic2Recipes.addCraftingRecipe(Ic2Items.nanoBodyarmor, new Object[]{"C C", "CcC", "CCC", 'C', Ic2Items.carbonPlate, 'c', Ic2Items.energyCrystal});
			Ic2Recipes.addCraftingRecipe(Ic2Items.nanoLeggings, new Object[]{"CcC", "C C", "C C", 'C', Ic2Items.carbonPlate, 'c', Ic2Items.energyCrystal});
			Ic2Recipes.addCraftingRecipe(Ic2Items.nanoBoots, new Object[]{"C C", "CcC", 'C', Ic2Items.carbonPlate, 'c', Ic2Items.energyCrystal});
		}

		if (portEnableCraftingQuantum) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.quantumHelmet, new Object[]{" n ", "ILI", "CGC", 'n', Ic2Items.nanoHelmet, 'I', Ic2Items.iridiumPlate, 'L', Ic2Items.lapotronCrystal, 'G', Ic2Items.reinforcedGlass, 'C', Ic2Items.advancedCircuit});
			Ic2Recipes.addCraftingRecipe(Ic2Items.quantumBodyarmor, new Object[]{"AnA", "ILI", "IAI", 'n', Ic2Items.nanoBodyarmor, 'I', Ic2Items.iridiumPlate, 'L', Ic2Items.lapotronCrystal, 'A', Ic2Items.advancedAlloy});
			Ic2Recipes.addCraftingRecipe(Ic2Items.quantumLeggings, new Object[]{"MLM", "InI", "G G", 'n', Ic2Items.nanoLeggings, 'I', Ic2Items.iridiumPlate, 'L', Ic2Items.lapotronCrystal, 'G', Item.GLOWSTONE_DUST, 'M', Ic2Items.machine});
			Ic2Recipes.addCraftingRecipe(Ic2Items.quantumBoots, new Object[]{"InI", "RLR", 'n', Ic2Items.nanoBoots, 'I', Ic2Items.iridiumPlate, 'L', Ic2Items.lapotronCrystal, 'R', Ic2Items.rubberBoots});
		}

		Ic2Recipes.addCraftingRecipe(Ic2Items.rubberBoots, new Object[]{"R R", "R R", "RCR", 'R', "itemRubber", 'C', Block.WOOL});
		Ic2Recipes.addCraftingRecipe(Ic2Items.batPack, new Object[]{"BCB", "BTB", "B B", 'T', "ingotTin", 'C', Ic2Items.electronicCircuit, 'B', Ic2Items.chargedReBattery});
		Ic2Recipes.addCraftingRecipe(Ic2Items.batPack, new Object[]{"BCB", "BTB", "B B", 'T', "ingotTin", 'C', Ic2Items.electronicCircuit, 'B', Ic2Items.reBattery});
		Ic2Recipes.addCraftingRecipe(Ic2Items.lapPack, new Object[]{"LAL", "LBL", "L L", 'L', Block.LAPIS_BLOCK, 'A', Ic2Items.advancedCircuit, 'B', Ic2Items.batPack});
		Ic2Recipes.addCraftingRecipe(Ic2Items.solarHelmet, new Object[]{"III", "ISI", "CCC", 'I', Item.IRON_INGOT, 'S', Ic2Items.solarPanel, 'C', Ic2Items.insulatedCopperCableItem});
		Ic2Recipes.addCraftingRecipe(Ic2Items.solarHelmet, new Object[]{" H ", " S ", "CCC", 'H', Item.IRON_HELMET, 'S', Ic2Items.solarPanel, 'C', Ic2Items.insulatedCopperCableItem});
		Ic2Recipes.addCraftingRecipe(Ic2Items.staticBoots, new Object[]{"I I", "ISI", "CCC", 'I', Item.IRON_INGOT, 'S', Block.WOOL, 'C', Ic2Items.insulatedCopperCableItem});
		Ic2Recipes.addCraftingRecipe(Ic2Items.staticBoots, new Object[]{" H ", " S ", "CCC", 'H', Item.IRON_BOOTS, 'S', Block.WOOL, 'C', Ic2Items.insulatedCopperCableItem});
		Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeHelmet, new Object[]{"BBB", "B B", 'B', "ingotBronze"});
		Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeChestplate, new Object[]{"B B", "BBB", "BBB", 'B', "ingotBronze"});
		Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeLeggings, new Object[]{"BBB", "B B", "B B", 'B', "ingotBronze"});
		Ic2Recipes.addCraftingRecipe(Ic2Items.bronzeBoots, new Object[]{"B B", "B B", 'B', "ingotBronze"});
		if (portEnableCraftingJetpacks) {
			Ic2Recipes.addCraftingRecipe(new ItemStack(Ic2Items.jetpack.id, 1, 18001), new Object[]{"ICI", "IFI", "R R", 'I', "ingotRefinedIron", 'C', Ic2Items.electronicCircuit, 'F', Ic2Items.fuelCan, 'R', Item.REDSTONE});
			Ic2Recipes.addCraftingRecipe(Ic2Items.electricJetpack, new Object[]{"ICI", "IBI", "G G", 'I', "ingotRefinedIron", 'C', Ic2Items.advancedCircuit, 'B', Ic2Items.batBox, 'G', Item.GLOWSTONE_DUST});
		}

		Ic2Recipes.addCraftingRecipe(Ic2Items.terraformerBlueprint, new Object[]{" C ", " A ", "R R", 'C', Ic2Items.electronicCircuit, 'A', Ic2Items.advancedCircuit, 'R', Item.REDSTONE});
		Ic2Recipes.addCraftingRecipe(Ic2Items.cultivationTerraformerBlueprint, new Object[]{" S ", "S#S", " S ", '#', Ic2Items.terraformerBlueprint, 'S', Item.SEEDS});
		Ic2Recipes.addCraftingRecipe(Ic2Items.desertificationTerraformerBlueprint, new Object[]{" S ", "S#S", " S ", '#', Ic2Items.terraformerBlueprint, 'S', Block.SAND});
		Ic2Recipes.addCraftingRecipe(Ic2Items.irrigationTerraformerBlueprint, new Object[]{" W ", "W#W", " W ", '#', Ic2Items.terraformerBlueprint, 'W', Item.WATER_BUCKET});
		Ic2Recipes.addCraftingRecipe(Ic2Items.chillingTerraformerBlueprint, new Object[]{" S ", "S#S", " S ", '#', Ic2Items.terraformerBlueprint, 'S', Item.SNOW_BALL});
		Ic2Recipes.addCraftingRecipe(Ic2Items.flatificatorTerraformerBlueprint, new Object[]{" D ", "D#D", " D ", '#', Ic2Items.terraformerBlueprint, 'D', Block.DIRT});
		Ic2Recipes.addCraftingRecipe(Ic2Items.mushroomTerraformerBlueprint, new Object[]{"mMm", "M#M", "mMm", '#', Ic2Items.terraformerBlueprint, 'M', Block.BROWN_MUSHROOM, 'm', Block.MYCEL});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.terraformerBlueprint, new Object[]{Ic2Items.cultivationTerraformerBlueprint});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.terraformerBlueprint, new Object[]{Ic2Items.irrigationTerraformerBlueprint});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.terraformerBlueprint, new Object[]{Ic2Items.chillingTerraformerBlueprint});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.terraformerBlueprint, new Object[]{Ic2Items.desertificationTerraformerBlueprint});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.terraformerBlueprint, new Object[]{Ic2Items.flatificatorTerraformerBlueprint});
		Ic2Recipes.addCraftingRecipe(Ic2Items.overclockerUpgrade, new Object[]{"CCC", "WEW", 'C', Ic2Items.coolingCell, 'W', Ic2Items.insulatedCopperCableItem, 'E', Ic2Items.electronicCircuit});
		Ic2Recipes.addCraftingRecipe(Ic2Items.transformerUpgrade, new Object[]{"GGG", "WTW", "GEG", 'G', Block.GLASS, 'W', Ic2Items.doubleInsulatedGoldCableItem, 'T', Ic2Items.mvTransformer, 'E', Ic2Items.electronicCircuit});
		Ic2Recipes.addCraftingRecipe(Ic2Items.energyStorageUpgrade, new Object[]{"www", "WBW", "wEw", 'w', Block.WOOD, 'W', Ic2Items.insulatedCopperCableItem, 'B', Ic2Items.reBattery, 'E', Ic2Items.electronicCircuit});
		Ic2Recipes.addCraftingRecipe(Ic2Items.energyStorageUpgrade, new Object[]{"www", "WBW", "wEw", 'w', Block.WOOD, 'W', Ic2Items.insulatedCopperCableItem, 'B', Ic2Items.chargedReBattery, 'E', Ic2Items.electronicCircuit});
		Ic2Recipes.addCraftingRecipe(Ic2Items.reinforcedDoor, new Object[]{"SS", "SS", "SS", 'S', Ic2Items.reinforcedStone});
		Ic2Recipes.addCraftingRecipe(Ic2Items.scrapBox, new Object[]{"SSS", "SSS", "SSS", 'S', Ic2Items.scrap});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.stickyDynamite, 8), new Object[]{"DDD", "DRD", "DDD", 'D', Ic2Items.dynamite, 'R', Ic2Items.resin});
		Ic2Recipes.addShapelessCraftingRecipe(StackUtil.copyWithSize(Ic2Items.dynamite, 8), new Object[]{Ic2Items.industrialTnt, Item.STRING});
		Ic2Recipes.addShapelessCraftingRecipe(StackUtil.copyWithSize(Ic2Items.bronzeDust, 2), new Object[]{Ic2Items.tinDust, Ic2Items.copperDust, Ic2Items.copperDust, Ic2Items.copperDust});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.ironDust, new Object[]{Ic2Items.smallIronDust, Ic2Items.smallIronDust});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.carbonMesh, new Object[]{Ic2Items.carbonFiber, Ic2Items.carbonFiber});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Block.PISTON_STICKY, 1), new Object[]{Block.PISTON, Ic2Items.resin, true});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.miningPipe, 8), new Object[]{"I I", "I I", "ITI", 'I', "ingotRefinedIron", 'T', Ic2Items.treetap});
		if (Ic2Items.rubberSapling != null) {
			Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.plantBall, 2), new Object[]{"PPP", "P P", "PPP", 'P', Ic2Items.rubberSapling});
		}

		if (enableCraftingGlowstoneDust) {
			Ic2Recipes.addCraftingRecipe(new ItemStack(Item.GLOWSTONE_DUST, 1), new Object[]{"RGR", "GRG", "RGR", 'R', Item.REDSTONE, 'G', Ic2Items.goldDust, true});
		}

		if (enableCraftingGunpowder) {
			Ic2Recipes.addCraftingRecipe(new ItemStack(Item.SULPHUR, 3), new Object[]{"RCR", "CRC", "RCR", 'R', Item.REDSTONE, 'C', Ic2Items.coalDust, true});
		}

		if (enableCraftingBucket) {
			Ic2Recipes.addCraftingRecipe(new ItemStack(Item.BUCKET, 1), new Object[]{"T T", " T ", 'T', "ingotTin", true});
		}

		if (enableCraftingCoin) {
			Ic2Recipes.addCraftingRecipe(Ic2Items.refinedIronIngot, new Object[]{"III", "III", "III", 'I', Ic2Items.coin});
		}

		if (enableCraftingCoin) {
			Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.coin, 16), new Object[]{"II", "II", 'I', "ingotRefinedIron"});
		}

		if (enableCraftingRail) {
			Ic2Recipes.addCraftingRecipe(new ItemStack(Block.RAILS, 8), new Object[]{"B B", "BsB", "B B", 'B', "ingotBronze", 's', Item.STICK, true});
		}

		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.crop, 2), new Object[]{"S S", "S S", 'S', Item.STICK});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Ic2Items.cropnalyzer.getItem()), new Object[]{"cc ", "RGR", "RCR", 'G', Block.GLASS, 'c', Ic2Items.insulatedCopperCableItem, 'R', Item.REDSTONE, 'C', Ic2Items.electronicCircuit});
		Ic2Recipes.addShapelessCraftingRecipe(StackUtil.copyWithSize(Ic2Items.fertilizer, 2), new Object[]{Ic2Items.scrap, new ItemStack(Item.INK_SACK, 1, 15)});
		Ic2Recipes.addShapelessCraftingRecipe(StackUtil.copyWithSize(Ic2Items.fertilizer, 2), new Object[]{Ic2Items.scrap, Ic2Items.scrap, Ic2Items.fertilizer});
		Ic2Recipes.addCraftingRecipe(Ic2Items.weedEx, new Object[]{"R", "G", "C", 'R', Item.REDSTONE, 'G', Ic2Items.grinPowder, 'C', Ic2Items.cell});
		Ic2Recipes.addCraftingRecipe(Ic2Items.cropmatron, new Object[]{"cBc", "CMC", "CCC", 'M', Ic2Items.machine, 'C', Ic2Items.crop, 'c', Ic2Items.electronicCircuit, 'B', Block.CHEST});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Ic2Items.mugEmpty.getItem()), new Object[]{"SS ", "SSS", "SS ", 'S', Block.STONE});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.coffeePowder.getItem()), new Object[]{Ic2Items.coffeeBeans});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.mugCoffee.getItem()), new Object[]{Ic2Items.mugEmpty, Ic2Items.coffeePowder, Ic2Items.waterCell});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.mugCoffee.getItem()), new Object[]{Ic2Items.mugEmpty, Ic2Items.coffeePowder, Item.WATER_BUCKET});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.mugCoffee.getItem(), 1, 2), new Object[]{new ItemStack(Ic2Items.mugCoffee.getItem(), 1, 1), Item.SUGAR, Item.MILK_BUCKET});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Ic2Items.barrel.getItem()), new Object[]{"P", "W", "P", 'P', Block.WOOD, 'W', Ic2Items.rubberWood});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Ic2Items.mugEmpty.getItem()), new Object[]{"#", '#', new ItemStack(Ic2Items.mugBooze.getItem(), 1, -1)});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Ic2Items.barrel.getItem()), new Object[]{"#", '#', new ItemStack(Ic2Items.barrel.getItem(), 1, -1)});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.blackPainter, new Object[]{Ic2Items.painter, new ItemStack(Item.INK_SACK, 1, 0)});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.redPainter, new Object[]{Ic2Items.painter, new ItemStack(Item.INK_SACK, 1, 1)});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.greenPainter, new Object[]{Ic2Items.painter, new ItemStack(Item.INK_SACK, 1, 2)});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.brownPainter, new Object[]{Ic2Items.painter, new ItemStack(Item.INK_SACK, 1, 3)});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.bluePainter, new Object[]{Ic2Items.painter, new ItemStack(Item.INK_SACK, 1, 4)});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.bluePainter, new Object[]{Ic2Items.painter, "dyeBlue"});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.purplePainter, new Object[]{Ic2Items.painter, new ItemStack(Item.INK_SACK, 1, 5)});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.cyanPainter, new Object[]{Ic2Items.painter, new ItemStack(Item.INK_SACK, 1, 6)});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.lightGreyPainter, new Object[]{Ic2Items.painter, new ItemStack(Item.INK_SACK, 1, 7)});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.darkGreyPainter, new Object[]{Ic2Items.painter, new ItemStack(Item.INK_SACK, 1, 8)});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.pinkPainter, new Object[]{Ic2Items.painter, new ItemStack(Item.INK_SACK, 1, 9)});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.limePainter, new Object[]{Ic2Items.painter, new ItemStack(Item.INK_SACK, 1, 10)});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.yellowPainter, new Object[]{Ic2Items.painter, new ItemStack(Item.INK_SACK, 1, 11)});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.cloudPainter, new Object[]{Ic2Items.painter, new ItemStack(Item.INK_SACK, 1, 12)});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.magentaPainter, new Object[]{Ic2Items.painter, new ItemStack(Item.INK_SACK, 1, 13)});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.orangePainter, new Object[]{Ic2Items.painter, new ItemStack(Item.INK_SACK, 1, 14)});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.whitePainter, new Object[]{Ic2Items.painter, new ItemStack(Item.INK_SACK, 1, 15)});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.cell, 16), new Object[]{" T ", "T T", " T ", 'T', "ingotTin"});
		Ic2Recipes.addCraftingRecipe(Ic2Items.fuelCan, new Object[]{" TT", "T T", "TTT", 'T', "ingotTin"});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.tinCan, 4), new Object[]{"T T", "TTT", 'T', "ingotTin"});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.waterCell, new Object[]{Ic2Items.cell, Item.WATER_BUCKET});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.lavaCell, new Object[]{Ic2Items.cell, Item.LAVA_BUCKET});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Block.OBSIDIAN, 1), new Object[]{Ic2Items.waterCell, Ic2Items.waterCell, Ic2Items.lavaCell, Ic2Items.lavaCell});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.hydratedCoalDust, new Object[]{Ic2Items.coalDust, Item.WATER_BUCKET});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.hydratedCoalDust, new Object[]{Ic2Items.coalDust, Ic2Items.waterCell});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.hydratedCoalCell, new Object[]{Ic2Items.cell, Ic2Items.hydratedCoalClump});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.bioCell, new Object[]{Ic2Items.cell, Ic2Items.compressedPlantBall});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.uraniumCell, new Object[]{Ic2Items.reEnrichedUraniumCell, Ic2Items.coalDust});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.depletedIsotopeCell.id, 1, 9999), new Object[]{Ic2Items.nearDepletedUraniumCell, Ic2Items.coalDust});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.nearDepletedUraniumCell, 8), new Object[]{"CCC", "CUC", "CCC", 'C', Ic2Items.cell, 'U', "ingotUranium"});
		Ic2Recipes.addShapelessCraftingRecipe(Ic2Items.uraniumCell, new Object[]{Ic2Items.cell, "ingotUranium"});
		Ic2Recipes.addCraftingRecipe(new ItemStack(Ic2Items.cfPack.id, 1, 259), new Object[]{"SCS", "FTF", "F F", 'T', "ingotTin", 'C', Ic2Items.electronicCircuit, 'F', Ic2Items.fuelCan, 'S', new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 1601)});
		Ic2Recipes.addShapelessCraftingRecipe(StackUtil.copyWithSize(Ic2Items.constructionFoam, 3), new Object[]{Ic2Items.clayDust, Item.WATER_BUCKET, Item.REDSTONE, Ic2Items.coalDust});
		Ic2Recipes.addShapelessCraftingRecipe(StackUtil.copyWithSize(Ic2Items.constructionFoam, 3), new Object[]{Ic2Items.clayDust, Ic2Items.waterCell, Item.REDSTONE, Ic2Items.coalDust});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 1401), new Object[]{new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 1601), Ic2Items.constructionFoamPellet});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 1301), new Object[]{new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 1501), Ic2Items.constructionFoamPellet});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 1201), new Object[]{new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 1401), Ic2Items.constructionFoamPellet});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 1101), new Object[]{new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 1301), Ic2Items.constructionFoamPellet});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 1001), new Object[]{new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 1201), Ic2Items.constructionFoamPellet});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 901), new Object[]{new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 1101), Ic2Items.constructionFoamPellet});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 801), new Object[]{new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 1001), Ic2Items.constructionFoamPellet});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 701), new Object[]{new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 901), Ic2Items.constructionFoamPellet});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 601), new Object[]{new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 801), Ic2Items.constructionFoamPellet});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 501), new Object[]{new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 701), Ic2Items.constructionFoamPellet});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 401), new Object[]{new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 601), Ic2Items.constructionFoamPellet});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 301), new Object[]{new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 501), Ic2Items.constructionFoamPellet});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 201), new Object[]{new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 401), Ic2Items.constructionFoamPellet});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 101), new Object[]{new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 301), Ic2Items.constructionFoamPellet});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 1), new Object[]{new ItemStack(Ic2Items.constructionFoamSprayer.id, 1, 201), Ic2Items.constructionFoamPellet});
		Ic2Recipes.addShapelessCraftingRecipe(new ItemStack(Item.DIAMOND), new Object[]{Ic2Items.industrialDiamond});
		Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.mixedMetalIngot, 2), new Object[]{"III", "BBB", "TTT", 'I', "ingotRefinedIron", 'B', "ingotBronze", 'T', "ingotTin"});
		Ic2Recipes.addCraftingRecipe(Ic2Items.integratedHeatDisperser, new Object[]{"ici", "CPC", 'C', Ic2Items.coolingCell, 'c', Ic2Items.advancedCircuit, 'i', "ingotCopper", 'P', Ic2Items.integratedReactorPlating});
		Ic2Recipes.addCraftingRecipe(Ic2Items.integratedReactorPlating, new Object[]{" C ", "CAC", " C ", 'C', "ingotCopper", 'A', Ic2Items.advancedAlloy});
		Ic2Recipes.addCraftingRecipe(Ic2Items.remote, new Object[]{" C ", "TLT", " F ", 'C', Ic2Items.insulatedCopperCableItem, 'F', Ic2Items.frequencyTransmitter, 'L', new ItemStack(Item.INK_SACK, 1, 4), 'T', "ingotTin"});
	}

	public static void addSingleTickCallback(World world, ITickCallback itickcallback) {
		if (!singleTickCallbacks.containsKey(world)) {
			singleTickCallbacks.put(world, new ArrayDeque());
		}

		((Queue) singleTickCallbacks.get(world)).add(itickcallback);
	}

	public static void addContinuousTickCallback(World world, ITickCallback itickcallback) {
		if (world != null) {
			if (continuousTickCallbacksInUse.containsKey(world) && (Boolean) continuousTickCallbacksInUse.get(world)) {
				if (continuousTickCallbacksToRemove.containsKey(world)) {
					((List) continuousTickCallbacksToRemove.get(world)).remove(itickcallback);
				}

				if (!continuousTickCallbacksToAdd.containsKey(world)) {
					continuousTickCallbacksToAdd.put(world, new Vector());
				}

				((List) continuousTickCallbacksToAdd.get(world)).add(itickcallback);
			}
			else {
				if (!continuousTickCallbacks.containsKey(world)) {
					continuousTickCallbacks.put(world, new HashSet());
				}

				((Set) continuousTickCallbacks.get(world)).add(itickcallback);
			}

		}
	}

	public static void removeContinuousTickCallback(World world, ITickCallback itickcallback) {
		if (continuousTickCallbacksInUse.containsKey(world) && (Boolean) continuousTickCallbacksInUse.get(world)) {
			if (continuousTickCallbacksToAdd.containsKey(world)) {
				((List) continuousTickCallbacksToAdd.get(world)).remove(itickcallback);
			}

			if (!continuousTickCallbacksToRemove.containsKey(world)) {
				continuousTickCallbacksToRemove.put(world, new Vector());
			}

			((List) continuousTickCallbacksToRemove.get(world)).add(itickcallback);
		}
		else if (continuousTickCallbacks.containsKey(world)) {
			((Set) continuousTickCallbacks.get(world)).remove(itickcallback);
		}

	}

	public static void updateWind(World world) {
		if (world.worldProvider.dimension == 0) {
			int i = 10;
			int j = 10;
			if (windStrength > 20) {
				i -= windStrength - 20;
			}

			if (windStrength < 10) {
				j -= 10 - windStrength;
			}

			if (random.nextInt(100) <= i) {
				++windStrength;
			}
			else if (random.nextInt(100) <= j) {
				--windStrength;
			}
		}
	}

	public static int getBlockIdFor(Configuration configuration, String s, int i) {
		Integer integer;
		if (configuration == null) {
			integer = i;
		}
		else {
			try {
				if (enableDynamicIdAllocation) {
					integer = new Integer(configuration.getOrCreateBlockIdProperty(s, i).value);
				}
				else {
					integer = new Integer(configuration.getOrCreateIntProperty(s, "block", i).value);
				}
			} catch (Exception var5) {
				System.out.println("[IC2] Error while trying to access ID-List, config wasn't loaded properly!");
				integer = i;
			}
		}

		runtimeIdProperties.setProperty("block." + s, integer.toString());
		return integer;
	}

	public static int getItemIdFor(Configuration configuration, String s, int i) {
		Integer integer;
		if (configuration == null) {
			integer = i;
		}
		else {
			try {
				integer = new Integer(configuration.getOrCreateIntProperty(s, "item", i).value);
			} catch (Exception var5) {
				System.out.println("[IC2] Error while trying to access ID-List, config wasn't loaded properly!");
				integer = i;
			}
		}

		runtimeIdProperties.setProperty("item." + s, integer.toString());
		return integer;
	}

	public static float getFallDistanceOfEntity(Entity entity) {
		return entity.fallDistance;
	}

	public static void setFallDistanceOfEntity(Entity entity, float f) {
		entity.fallDistance = f;
	}

	public static boolean getIsJumpingOfEntityLiving(EntityLiving entityliving) {
		return entityliving.aZ;
	}

	public static void setIsJumpingOfEntityLiving(EntityLiving entityliving, boolean flag) {
		entityliving.aZ = flag;
	}

	public static void closeScreenOfEntityPlayer(EntityHuman entityhuman) {
		entityhuman.closeInventory();
	}

	public static void explodeMachineAt(World world, int i, int j, int k) {
		world.setTypeId(i, j, k, 0);
		ExplosionIC2 explosionic2 = new ExplosionIC2(world, null, 0.5D + (double) i, 0.5D + (double) j, 0.5D + (double) k, 2.5F, 0.75F, 0.75F);
		explosionic2.doExplosion();
	}

	public static int getSeaLevel(World world) {
		return world.worldProvider.getSeaLevel();
	}

	public static int getWorldHeight(World world) {
		return world.getHeight();
	}

	public static void addValuableOre(int i, int j) {
		addValuableOre(i, -1, j);
	}

	public static void addValuableOre(int i, int j, int k) {
		if (valuableOres.containsKey(i)) {
			Map map = (Map) valuableOres.get(i);
			if (map.containsKey(-1)) {
				return;
			}

			if (j == -1) {
				map.clear();
				map.put(-1, k);
			}
			else if (!map.containsKey(j)) {
				map.put(j, k);
			}
		}
		else {
			TreeMap treemap = new TreeMap();
			treemap.put(j, k);
			valuableOres.put(i, treemap);
		}

	}

	private static String getValuableOreString() {
		StringBuilder stringbuilder = new StringBuilder();
		boolean flag = true;
		Iterator iterator = valuableOres.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry entry = (Entry) iterator.next();
			Iterator iterator1 = ((Map) entry.getValue()).entrySet().iterator();

			while (iterator1.hasNext()) {
				Entry entry1 = (Entry) iterator1.next();
				if (flag) {
					flag = false;
				}
				else {
					stringbuilder.append(", ");
				}

				stringbuilder.append(entry.getKey());
				if ((Integer) entry1.getKey() != -1) {
					stringbuilder.append("-");
					stringbuilder.append(entry1.getKey());
				}

				stringbuilder.append(":");
				stringbuilder.append(entry1.getValue());
			}
		}

		return stringbuilder.toString();
	}

	private static void setValuableOreFromString(String s) {
		valuableOres.clear();
		String[] as = s.trim().split("\\s*,\\s*");
		String[] as1 = as;
		int i = as.length;

		for (int j = 0; j < i; ++j) {
			String s1 = as1[j];
			String[] as2 = s1.split("\\s*:\\s*");
			String[] as3 = as2[0].split("\\s*-\\s*");
			if (as3[0].length() != 0) {
				int k = Integer.parseInt(as3[0]);
				int l = -1;
				int i1 = 1;
				if (as3.length == 2) {
					l = Integer.parseInt(as3[1]);
				}

				if (as2.length == 2) {
					i1 = Integer.parseInt(as2[1]);
				}

				addValuableOre(k, l, i1);
			}
		}

	}

	public void load() {
		String s = "unknown";
		FakePlayer.setMethod("fakeplayer");

		try {
			s = "class MinecraftForge";
			MinecraftForge.class.getName();
			s = "hook Block.isGenMineableReplaceable";
			Block.class.getMethod("isGenMineableReplaceable", World.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
			s = "hook Item.setNoRepair";
			Item.class.getMethod("setNoRepair");
			s = "hook WorldProvider.getSaveFolder";
			WorldProvider.class.getMethod("getSaveFolder");
			s = "unknown";
		} catch (Throwable var10) {
			Platform.displayError("Minecraft Forge is not properly installed. Possible reasons include:\n\n* Minecraft Forge is not installed at all\n* Minecraft Forge is installed in the mods folder instead of inside the\nminecraft.jar or minecraft_server.jar file\n* One or more mods have been installed in the wrong order\n* One or more installed mods are incompatible with the Minecraft Forge\n\nIndustrialCraft 2 needs the Minecraft Forge to be properly installed\nin order to operate.\n\n(Technical information: Missing " + s + ")");
		}

		MinecraftForge.versionDetect("IndustrialCraft 2", 3, 3, 7);

		Configuration configuration;
		try {
			File file = new File(new File(Platform.getMinecraftDir(), "config"), "IC2.cfg");
			configuration = new Configuration(file);
			configuration.load();
			System.out.println("[IC2] Config loaded from " + file.getAbsolutePath());
		} catch (Exception var9) {
			System.out.println("[IC2] Error while trying to access configuration! " + var9);
			configuration = null;
		}

		if (configuration != null) {
			Property property = configuration.getOrCreateBooleanProperty("enableDynamicIdAllocation", "general", enableDynamicIdAllocation);
			property.comment = "Enable searching for free block ids, will get disabled after the next successful load";
			enableDynamicIdAllocation = Boolean.parseBoolean(property.value);
			property = configuration.getOrCreateBooleanProperty("enableCraftingBucket", "general", enableCraftingBucket);
			property.comment = "Enable crafting of buckets out of tin";
			enableCraftingBucket = Boolean.parseBoolean(property.value);
			property = configuration.getOrCreateBooleanProperty("enableCraftingCoin", "general", enableCraftingCoin);
			property.comment = "Enable crafting of Industrial Credit coins";
			enableCraftingCoin = Boolean.parseBoolean(property.value);
			property = configuration.getOrCreateBooleanProperty("enableCraftingGlowstoneDust", "general", enableCraftingGlowstoneDust);
			property.comment = "Enable crafting of glowstone dust out of dusts";
			enableCraftingGlowstoneDust = Boolean.parseBoolean(property.value);
			property = configuration.getOrCreateBooleanProperty("enableCraftingGunpowder", "general", enableCraftingGunpowder);
			property.comment = "Enable crafting of gunpowder out of dusts";
			enableCraftingGunpowder = Boolean.parseBoolean(property.value);
			property = configuration.getOrCreateBooleanProperty("enableCraftingITnt", "general", enableCraftingITnt);
			property.comment = "Enable crafting of ITNT";
			enableCraftingITnt = Boolean.parseBoolean(property.value);
			property = configuration.getOrCreateBooleanProperty("enableCraftingNuke", "general", enableCraftingNuke);
			property.comment = "Enable crafting of nukes";
			enableCraftingNuke = Boolean.parseBoolean(property.value);
			property = configuration.getOrCreateBooleanProperty("enableCraftingRail", "general", enableCraftingRail);
			property.comment = "Enable crafting of rails out of bronze";
			enableCraftingRail = Boolean.parseBoolean(property.value);
			property = configuration.getOrCreateBooleanProperty("enableSecretRecipeHiding", "general", enableSecretRecipeHiding);
			property.comment = "Enable hiding of secret recipes in CraftGuide/NEI";
			enableSecretRecipeHiding = Boolean.parseBoolean(property.value);
			property = configuration.getOrCreateBooleanProperty("enableQuantumSpeedOnSprint", "general", enableQuantumSpeedOnSprint);
			property.comment = "Enable activation of the quantum leggings' speed boost when sprinting instead of holding the boost key";
			enableQuantumSpeedOnSprint = Boolean.parseBoolean(property.value);
			property = configuration.getOrCreateBooleanProperty("enableMinerLapotron", "general", enableMinerLapotron);
			property.comment = "Enable usage of lapotron crystals on miners";
			enableMinerLapotron = Boolean.parseBoolean(property.value);
			property = configuration.getOrCreateBooleanProperty("enableTeleporterInventory", "general", enableTeleporterInventory);
			property.comment = "Enable calculation of inventory weight when going through a teleporter";
			enableTeleporterInventory = Boolean.parseBoolean(property.value);
			property = configuration.getOrCreateBooleanProperty("enableBurningScrap", "general", enableBurningScrap);
			property.comment = "Enable burning of scrap in a generator";
			enableBurningScrap = Boolean.parseBoolean(property.value);
			if (!Platform.isRendering()) {
				property = configuration.getOrCreateBooleanProperty("enableLoggingWrench", "general", enableLoggingWrench);
				property.comment = "Enable logging of players when they remove a machine using a wrench";
				enableLoggingWrench = Boolean.parseBoolean(property.value);
			}

			property = configuration.getOrCreateBooleanProperty("enableWorldGenTreeRubber", "general", enableWorldGenTreeRubber);
			property.comment = "Enable generation of rubber trees in the world";
			enableWorldGenTreeRubber = Boolean.parseBoolean(property.value);
			property = configuration.getOrCreateBooleanProperty("enableWorldGenOreCopper", "general", enableWorldGenOreCopper);
			property.comment = "Enable generation of copper in the world";
			enableWorldGenOreCopper = Boolean.parseBoolean(property.value);
			property = configuration.getOrCreateBooleanProperty("enableWorldGenOreTin", "general", enableWorldGenOreTin);
			property.comment = "Enable generation of tin in the world";
			enableWorldGenOreTin = Boolean.parseBoolean(property.value);
			property = configuration.getOrCreateBooleanProperty("enableWorldGenOreUranium", "general", enableWorldGenOreUranium);
			property.comment = "Enable generation of uranium in the world";
			enableWorldGenOreUranium = Boolean.parseBoolean(property.value);
			property = configuration.getOrCreateProperty("explosionPowerNuke", "general", Float.toString(explosionPowerNuke));
			property.comment = "Explosion power of a nuke, where TNT is 4";
			explosionPowerNuke = Float.parseFloat(property.value);
			property = configuration.getOrCreateProperty("explosionPowerReactorMax", "general", Float.toString(explosionPowerReactorMax));
			property.comment = "Maximum explosion power of a nuclear reactor, where TNT is 4";
			explosionPowerReactorMax = Float.parseFloat(property.value);
			property = configuration.getOrCreateIntProperty("energyGeneratorBase", "general", energyGeneratorBase);
			property.comment = "Base energy generation values - increase those for higher energy yield";
			energyGeneratorBase = Integer.parseInt(property.value);
			energyGeneratorGeo = Integer.parseInt(configuration.getOrCreateIntProperty("energyGeneratorGeo", "general", energyGeneratorGeo).value);
			energyGeneratorWater = Integer.parseInt(configuration.getOrCreateIntProperty("energyGeneratorWater", "general", energyGeneratorWater).value);
			energyGeneratorSolar = Integer.parseInt(configuration.getOrCreateIntProperty("energyGeneratorSolar", "general", energyGeneratorSolar).value);
			energyGeneratorWind = Integer.parseInt(configuration.getOrCreateIntProperty("energyGeneratorWind", "general", energyGeneratorWind).value);
			energyGeneratorNuclear = Integer.parseInt(configuration.getOrCreateIntProperty("energyGeneratorNuclear", "general", energyGeneratorNuclear).value);
			property = configuration.getOrCreateProperty("valuableOres", "general", getValuableOreString());
			property.comment = "List of valuable ores the miner should look for. Comma separated, format is id-metadata:value where value should be at least 1 to be considered by the miner";
			setValuableOreFromString(property.value);
			property = configuration.getOrCreateBooleanProperty("portDebugMessages", "general", portDebugMessages);
			property.comment = "Port related configuration";
			portDebugMessages = Boolean.parseBoolean(property.value);
			portEnableCraftingLaser = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingLaser", "general", portEnableCraftingLaser).value);
			portEnableCraftingDynamite = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingDynamite", "general", portEnableCraftingDynamite).value);
			portEnableMiner = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableMiner", "general", portEnableMiner).value);
			portEnableCraftingQuantum = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingQuantum", "general", portEnableCraftingQuantum).value);
			portEnableCraftingNano = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingNano", "general", portEnableCraftingNano).value);
			portEnableCraftingNanoSaber = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingNanoSaber", "general", portEnableCraftingNanoSaber).value);
			portEnableCraftingJetpacks = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingJetpacks", "general", portEnableCraftingJetpacks).value);
			portEnableCraftingMachines = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingMachines", "general", portEnableCraftingMachines).value);
			portEnableCraftingBatBox = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingBatBox", "general", portEnableCraftingBatBox).value);
			portEnableCraftingMFE = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingMFE", "general", portEnableCraftingMFE).value);
			portEnableCraftingMFS = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingMFS", "general", portEnableCraftingMFS).value);
			portEnableCraftingDrill = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingDrill", "general", portEnableCraftingDrill).value);
			portEnableCraftingDiamondDrill = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingDiamondDrill", "general", portEnableCraftingDiamondDrill).value);
			portEnableCraftingEnergyCrystal = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingEnergyCrystal", "general", portEnableCraftingEnergyCrystal).value);
			portEnableCraftingLapotronCrystal = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingLapotronCrystal", "general", portEnableCraftingLapotronCrystal).value);
			portEnableCraftingREBattery = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingREBattery", "general", portEnableCraftingREBattery).value);
			portEnableCraftingElCircuit = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingElCircuit", "general", portEnableCraftingElCircuit).value);
			portEnableCraftingAdCircuit = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingAdCircuit", "general", portEnableCraftingAdCircuit).value);
			portEnableCraftingIridiumPlate = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableCraftingIridiumPlate", "general", portEnableCraftingIridiumPlate).value);
			portEnableOVScanner = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portEnableOVScanner", "general", portEnableOVScanner).value);
			portLosslessWrench = Boolean.parseBoolean(configuration.getOrCreateBooleanProperty("portLosslessWrench", "general", portLosslessWrench).value);
			configuration.save();
		}

		AudioManager.initialize(configuration);
		runtimeIdProperties.put("initialVersion", this.getVersion());
		BlockTex bltex;
		if (enableWorldGenOreCopper) {
			bltex = (BlockTex) (new BlockTex(getBlockIdFor(configuration, "blockOreCopper", 249), 32, Material.STONE)).c(3.0F).b(5.0F).a("blockOreCopper");
			ModLoader.registerBlock(Block.byId[bltex.id]);
			Ic2Items.copperOre = new ItemStack(bltex);
		}

		if (enableWorldGenOreTin) {
			bltex = (BlockTex) (new BlockTex(getBlockIdFor(configuration, "blockOreTin", 248), 33, Material.STONE)).c(3.0F).b(5.0F).a("blockOreTin");
			ModLoader.registerBlock(Block.byId[bltex.id]);
			Ic2Items.tinOre = new ItemStack(bltex);
		}

		if (enableWorldGenOreUranium) {
			bltex = (BlockTex) (new BlockTex(getBlockIdFor(configuration, "blockOreUran", 247), 34, Material.STONE)).c(4.0F).b(6.0F).a("blockOreUran");
			ModLoader.registerBlock(Block.byId[bltex.id]);
			Ic2Items.uraniumOre = new ItemStack(bltex);
		}

		if (enableWorldGenTreeRubber) {
			new BlockRubWood(getBlockIdFor(configuration, "blockRubWood", 243));
			new BlockRubLeaves(getBlockIdFor(configuration, "blockRubLeaves", 242));
			new BlockRubSapling(getBlockIdFor(configuration, "blockRubSapling", 241), 38);
		}

		new BlockResin(getBlockIdFor(configuration, "blockHarz", 240), 43);
		new BlockRubberSheet(getBlockIdFor(configuration, "blockRubber", 234), 40);
		new BlockPoleFence(getBlockIdFor(configuration, "blockFenceIron", 232), 1);
		bltex = (BlockTex) (new BlockTex(getBlockIdFor(configuration, "blockAlloy", 231), 12, Material.ORE)).c(80.0F).b(150.0F).a(Block.i).a("blockAlloy");
		ModLoader.registerBlock(Block.byId[bltex.id]);
		Ic2Items.reinforcedStone = new ItemStack(bltex);
		BlockTexGlass bltexglas = (BlockTexGlass) (new BlockTexGlass(getBlockIdFor(configuration, "blockAlloyGlass", 230), 13, Material.SHATTERABLE, false)).c(5.0F).b(150.0F).a(Block.j).a("blockAlloyGlass");
		ModLoader.registerBlock(Block.byId[bltexglas.id]);
		Ic2Items.reinforcedGlass = new ItemStack(bltexglas);
		BlockIC2Door blic2door = (BlockIC2Door) (new BlockIC2Door(getBlockIdFor(configuration, "blockDoorAlloy", 229), 14, 15, Material.ORE)).c(50.0F).b(150.0F).a(Block.i).a("blockDoorAlloy").s().j();
		ModLoader.registerBlock(Block.byId[blic2door.id]);
		Ic2Items.reinforcedDoorBlock = new ItemStack(blic2door);
		blockFoam = new BlockFoam(getBlockIdFor(configuration, "blockFoam", 222), 37);
		new BlockWall(getBlockIdFor(configuration, "blockWall", 221), 96);
		new BlockScaffold(getBlockIdFor(configuration, "blockScaffold", 220), Material.WOOD);
		new BlockScaffold(getBlockIdFor(configuration, "blockIronScaffold", 216), Material.ORE);
		new BlockMetal(getBlockIdFor(configuration, "blockMetal", 224));
		new BlockCable(getBlockIdFor(configuration, "blockCable", 228));
		new BlockGenerator(getBlockIdFor(configuration, "blockGenerator", 246));
		new BlockReactorChamber(getBlockIdFor(configuration, "blockReactorChamber", 233));
		new BlockElectric(getBlockIdFor(configuration, "blockElectric", 227));
		new BlockMachine(getBlockIdFor(configuration, "blockMachine", 250));
		new BlockMachine2(getBlockIdFor(configuration, "blockMachine2", 223));
		Ic2Items.luminator = new ItemStack((new BlockLuminator(getBlockIdFor(configuration, "blockLuminatorDark", 219), false)).a("blockLuminatorD"));
		Ic2Items.activeLuminator = new ItemStack((new BlockLuminator(getBlockIdFor(configuration, "blockLuminator", 226), true)).a("blockLuminator").a(1.0F));
		new BlockMiningPipe(getBlockIdFor(configuration, "blockMiningPipe", 245), 35);
		new BlockMiningTip(getBlockIdFor(configuration, "blockMiningTip", 244), 36);
		new BlockPersonal(getBlockIdFor(configuration, "blockPersonal", 225));
		Ic2Items.industrialTnt = new ItemStack((new BlockITNT(getBlockIdFor(configuration, "blockITNT", 239), 58, true)).c(0.0F).a(Block.g).a("blockITNT"));
		Ic2Items.nuke = new ItemStack((new BlockITNT(getBlockIdFor(configuration, "blockNuke", 237), 61, false)).c(0.0F).a(Block.g).a("blockNuke"));
		Ic2Items.dynamiteStick = new ItemStack((new BlockDynamite(getBlockIdFor(configuration, "blockDynamite", 236), 57)).c(0.0F).a(Block.g).a("blockDynamite"));
		Ic2Items.dynamiteStickWithRemote = new ItemStack((new BlockDynamite(getBlockIdFor(configuration, "blockDynamiteRemote", 235), 56)).c(0.0F).a(Block.g).a("blockDynamiteRemote"));
		new BlockCrop(getBlockIdFor(configuration, "blockCrop", 218));
		new BlockBarrel(getBlockIdFor(configuration, "blockBarrel", 217));
		Ic2Items.resin = new ItemStack((new ItemResin(getItemIdFor(configuration, "itemHarz", 29961), 64)).a("itemHarz"));
		Ic2Items.rubber = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemRubber", 29960), 65)).a("itemRubber"));
		Ic2Items.uraniumDrop = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemOreUran", 29987), 13)).a("itemOreUran"));
		Ic2Items.bronzeDust = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemDustBronze", 29995), 5)).a("itemDustBronze"));
		Ic2Items.clayDust = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemDustClay", 29877), 14)).a("itemDustClay"));
		Ic2Items.coalDust = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemDustCoal", 30000), 0)).a("itemDustCoal"));
		Ic2Items.copperDust = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemDustCopper", 29997), 3)).a("itemDustCopper"));
		Ic2Items.goldDust = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemDustGold", 29998), 2)).a("itemDustGold"));
		Ic2Items.ironDust = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemDustIron", 29999), 1)).a("itemDustIron"));
		Ic2Items.silverDust = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemDustSilver", 29874), 240)).a("itemDustSilver"));
		Ic2Items.smallIronDust = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemDustIronSmall", 29994), 6)).hideFromCreative().a("itemDustIronSmall"));
		Ic2Items.tinDust = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemDustTin", 29996), 4)).a("itemDustTin"));
		Ic2Items.hydratedCoalDust = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemFuelCoalDust", 29970), 53)).a("itemFuelCoalDust"));
		Ic2Items.refinedIronIngot = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemIngotAdvIron", 29993), 7)).a("itemIngotAdvIron"));
		Ic2Items.copperIngot = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemIngotCopper", 29992), 8)).a("itemIngotCopper"));
		Ic2Items.tinIngot = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemIngotTin", 29991), 9)).a("itemIngotTin"));
		Ic2Items.bronzeIngot = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemIngotBronze", 29990), 10)).a("itemIngotBronze"));
		Ic2Items.mixedMetalIngot = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemIngotAlloy", 29989), 11)).a("itemIngotAlloy"));
		Ic2Items.uraniumIngot = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemIngotUran", 29988), 12)).a("itemIngotUran"));
		Ic2Items.treetap = new ItemStack((new ItemTreetap(getItemIdFor(configuration, "itemTreetap", 29956), 66)).a("itemTreetap"));
		Ic2Items.bronzePickaxe = new ItemStack((new ItemIC2Pickaxe(getItemIdFor(configuration, "itemToolBronzePickaxe", 29944), 80, EnumToolMaterial.IRON, 5.0F)).a("itemToolBronzePickaxe").setMaxDurability(350));
		Ic2Items.bronzeAxe = new ItemStack((new ItemIC2Axe(getItemIdFor(configuration, "itemToolBronzeAxe", 29943), 81, EnumToolMaterial.IRON, 5.0F)).a("itemToolBronzeAxe").setMaxDurability(350));
		Ic2Items.bronzeSword = new ItemStack((new ItemIC2Sword(getItemIdFor(configuration, "itemToolBronzeSword", 29942), 82, EnumToolMaterial.IRON, 7)).a("itemToolBronzeSword").setMaxDurability(350));
		Ic2Items.bronzeShovel = new ItemStack((new ItemIC2Spade(getItemIdFor(configuration, "itemToolBronzeSpade", 29941), 83, EnumToolMaterial.IRON, 5.0F)).a("itemToolBronzeSpade").setMaxDurability(350));
		Ic2Items.bronzeHoe = new ItemStack((new ItemIC2Hoe(getItemIdFor(configuration, "itemToolBronzeHoe", 29940), 84, EnumToolMaterial.IRON)).a("itemToolBronzeHoe").setMaxDurability(350));
		Ic2Items.wrench = new ItemStack((new ItemToolWrench(getItemIdFor(configuration, "itemToolWrench", 29927), 89)).a("itemToolWrench"));
		Ic2Items.cutter = new ItemStack((new ItemToolCutter(getItemIdFor(configuration, "itemToolCutter", 29897), 92)).a("itemToolCutter"));
		Ic2Items.constructionFoamSprayer = new ItemStack((new ItemSprayer(getItemIdFor(configuration, "itemFoamSprayer", 29875), 45)).a("itemFoamSprayer"));
		Ic2Items.toolbox = new ItemStack((new ItemToolbox(getItemIdFor(configuration, "itemToolbox", 29861), 162)).a("itemToolbox"));
		Ic2Items.miningDrill = new ItemStack((new ItemElectricToolDrill(getItemIdFor(configuration, "itemToolDrill", 29979), 48)).a("itemToolDrill"));
		Ic2Items.diamondDrill = new ItemStack((new ItemElectricToolDDrill(getItemIdFor(configuration, "itemToolDDrill", 29978), 49)).a("itemToolDDrill"));
		Ic2Items.chainsaw = new ItemStack((new ItemElectricToolChainsaw(getItemIdFor(configuration, "itemToolChainsaw", 29977), 50)).a("itemToolChainsaw"));
		Ic2Items.electricWrench = new ItemStack((new ItemToolWrenchElectric(getItemIdFor(configuration, "itemToolWrenchElectric", 29884), 94)).a("itemToolWrenchElectric"));
		Ic2Items.electricTreetap = new ItemStack((new ItemTreetapElectric(getItemIdFor(configuration, "itemTreetapElectric", 29868), 165)).a("itemTreetapElectric"));
		Ic2Items.miningLaser = new ItemStack((new ItemToolMiningLaser(getItemIdFor(configuration, "itemToolMiningLaser", 29952), 70)).a("itemToolMiningLaser"));
		Ic2Items.ecMeter = new ItemStack((new ItemToolMeter(getItemIdFor(configuration, "itemToolMEter", 29926), 90)).a("itemToolMeter"));
		Ic2Items.odScanner = new ItemStack((new ItemScanner(getItemIdFor(configuration, "itemScanner", 29964), 59, 1)).a("itemScanner"));
		Ic2Items.ovScanner = new ItemStack((new ItemScannerAdv(getItemIdFor(configuration, "itemScannerAdv", 29963), 60, 2)).a("itemScannerAdv"));
		Ic2Items.frequencyTransmitter = new ItemStack((new ItemFrequencyTransmitter(getItemIdFor(configuration, "itemFreq", 29878), 95)).a("itemFreq").e(1));
		Ic2Items.nanoSaber = new ItemStack((new ItemNanoSaber(getItemIdFor(configuration, "itemNanoSaberOff", 29892), 77, false)).a("itemNanoSaber"));
		Ic2Items.enabledNanoSaber = new ItemStack((new ItemNanoSaber(getItemIdFor(configuration, "itemNanoSaber", 29893), 78, true)).a("itemNanoSaber"));
		Ic2Items.rubberBoots = new ItemStack((new ItemArmorRubBoots(getItemIdFor(configuration, "itemArmorRubBoots", 29955), 67, ModLoader.addArmor("ic2/rubber"))).a("itemArmorRubBoots"));
		Ic2Items.bronzeHelmet = new ItemStack((new ItemArmorIC2(getItemIdFor(configuration, "itemArmorBronzeHelmet", 29939), 85, EnumArmorMaterial.IRON, ModLoader.addArmor("ic2/bronze"), 0, 15)).setEnchantability(9).a("itemArmorBronzeHelmet"));
		Ic2Items.bronzeChestplate = new ItemStack((new ItemArmorIC2(getItemIdFor(configuration, "itemArmorBronzeChestplate", 29938), 86, EnumArmorMaterial.IRON, ModLoader.addArmor("ic2/bronze"), 1, 15)).setEnchantability(9).a("itemArmorBronzeChestplate"));
		Ic2Items.bronzeLeggings = new ItemStack((new ItemArmorIC2(getItemIdFor(configuration, "itemArmorBronzeLegs", 29937), 87, EnumArmorMaterial.IRON, ModLoader.addArmor("ic2/bronze"), 2, 15)).setEnchantability(9).a("itemArmorBronzeLegs"));
		Ic2Items.bronzeBoots = new ItemStack((new ItemArmorIC2(getItemIdFor(configuration, "itemArmorBronzeBoots", 29936), 88, EnumArmorMaterial.IRON, ModLoader.addArmor("ic2/bronze"), 3, 15)).setEnchantability(9).a("itemArmorBronzeBoots"));
		Ic2Items.compositeArmor = new ItemStack((new ItemArmorIC2(getItemIdFor(configuration, "itemArmorAlloyChestplate", 29923), 103, EnumArmorMaterial.IRON, ModLoader.addArmor("ic2/alloy"), 1, 50)).setEnchantability(12).a("itemArmorAlloyChestplate"));
		Ic2Items.nanoHelmet = new ItemStack((new ItemArmorNanoSuit(getItemIdFor(configuration, "itemArmorNanoHelmet", 29922), 104, ModLoader.addArmor("ic2/nano"), 0)).a("itemArmorNanoHelmet"));
		Ic2Items.nanoBodyarmor = new ItemStack((new ItemArmorNanoSuit(getItemIdFor(configuration, "itemArmorNanoChestplate", 29921), 105, ModLoader.addArmor("ic2/nano"), 1)).a("itemArmorNanoChestplate"));
		Ic2Items.nanoLeggings = new ItemStack((new ItemArmorNanoSuit(getItemIdFor(configuration, "itemArmorNanoLegs", 29920), 106, ModLoader.addArmor("ic2/nano"), 2)).a("itemArmorNanoLegs"));
		Ic2Items.nanoBoots = new ItemStack((new ItemArmorNanoSuit(getItemIdFor(configuration, "itemArmorNanoBoots", 29919), 107, ModLoader.addArmor("ic2/nano"), 3)).a("itemArmorNanoBoots"));
		Ic2Items.quantumHelmet = new ItemStack((new ItemArmorQuantumSuit(getItemIdFor(configuration, "itemArmorQuantumHelmet", 29918), 108, ModLoader.addArmor("ic2/quantum"), 0)).a("itemArmorQuantumHelmet"));
		Ic2Items.quantumBodyarmor = new ItemStack((new ItemArmorQuantumSuit(getItemIdFor(configuration, "itemArmorQuantumChestplate", 29917), 109, ModLoader.addArmor("ic2/quantum"), 1)).a("itemArmorQuantumChestplate"));
		Ic2Items.quantumLeggings = new ItemStack((new ItemArmorQuantumSuit(getItemIdFor(configuration, "itemArmorQuantumLegs", 29916), 110, ModLoader.addArmor("ic2/quantum"), 2)).a("itemArmorQuantumLegs"));
		Ic2Items.quantumBoots = new ItemStack((new ItemArmorQuantumSuit(getItemIdFor(configuration, "itemArmorQuantumBoots", 29915), 111, ModLoader.addArmor("ic2/quantum"), 3)).a("itemArmorQuantumBoots"));
		Ic2Items.jetpack = new ItemStack((new ItemArmorJetpack(getItemIdFor(configuration, "itemArmorJetpack", 29954), 68, ModLoader.addArmor("ic2/jetpack"))).a("itemArmorJetpack"));
		Ic2Items.electricJetpack = new ItemStack((new ItemArmorJetpackElectric(getItemIdFor(configuration, "itemArmorJetpackElectric", 29953), 69, ModLoader.addArmor("ic2/jetpack"))).a("itemArmorJetpackElectric"));
		Ic2Items.batPack = new ItemStack((new ItemArmorBatpack(getItemIdFor(configuration, "itemArmorBatpack", 29924), 73, ModLoader.addArmor("ic2/batpack"))).a("itemArmorBatpack"));
		Ic2Items.lapPack = new ItemStack((new ItemArmorLappack(getItemIdFor(configuration, "itemArmorLappack", 29871), 150, ModLoader.addArmor("ic2/lappack"))).a("itemArmorLappack"));
		Ic2Items.cfPack = new ItemStack((new ItemArmorCFPack(getItemIdFor(configuration, "itemArmorCFPack", 29873), 46, ModLoader.addArmor("ic2/batpack"))).a("itemArmorCFPack"));
		Ic2Items.solarHelmet = new ItemStack((new ItemArmorSolarHelmet(getItemIdFor(configuration, "itemSolarHelmet", 29860), 164, ModLoader.addArmor("ic2/solar"))).a("itemSolarHelmet"));
		Ic2Items.staticBoots = new ItemStack((new ItemArmorStaticBoots(getItemIdFor(configuration, "itemStaticBoots", 29859), 67, ModLoader.addArmor("ic2/rubber"))).a("itemStaticBoots"));
		Ic2Items.reBattery = new ItemStack((new ItemBatteryDischarged(getItemIdFor(configuration, "itemBatREDischarged", 29983), 16, 10000, 100, 1)).a("itemBatRE"));
		Ic2Items.chargedReBattery = new ItemStack((new ItemBattery(getItemIdFor(configuration, "itemBatRE", 29986), 16, 10000, 100, 1)).a("itemBatRE"));
		Ic2Items.energyCrystal = new ItemStack((new ItemBattery(getItemIdFor(configuration, "itemBatCrystal", 29985), 21, 100000, 250, 2)).a("itemBatCrystal"));
		Ic2Items.lapotronCrystal = new ItemStack((new ItemBattery(getItemIdFor(configuration, "itemBatLamaCrystal", 29984), 26, 1000000, 600, 3)).setRarity(1).a("itemBatLamaCrystal"));
		Ic2Items.suBattery = new ItemStack((new ItemBatterySU(getItemIdFor(configuration, "itemBatSU", 29982), 31, 1, 1)).a("itemBatSU"));
		new ItemCable(getItemIdFor(configuration, "itemCable", 29928), 112);
		Ic2Items.cell = new ItemStack((new ItemCell(getItemIdFor(configuration, "itemCellEmpty", 29981), 32)).a("itemCellEmpty"));
		Ic2Items.lavaCell = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemCellLava", 29980), 33)).a("itemCellLava"));
		Ic2Items.hydratedCoalCell = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemCellCoal", 29974), 34)).a("itemCellCoal"));
		Ic2Items.bioCell = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemCellBio", 29973), 35)).a("itemCellBio"));
		Ic2Items.coalfuelCell = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemCellCoalRef", 29972), 34)).a("itemCellCoalRef"));
		Ic2Items.biofuelCell = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemCellBioRef", 29971), 35)).a("itemCellBioRef"));
		Ic2Items.waterCell = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemCellWater", 29962), 37)).a("itemCellWater"));
		Ic2Items.electrolyzedWaterCell = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemCellWaterElectro", 29925), 43)).a("itemCellWaterElectro"));
		Ic2Items.fuelCan = new ItemStack((new ItemFuelCanEmpty(getItemIdFor(configuration, "itemFuelCanEmpty", 29975), 51)).a("itemFuelCanEmpty"));
		Ic2Items.filledFuelCan = new ItemStack((new ItemFuelCanFilled(getItemIdFor(configuration, "itemFuelCan", 29976), 52)).setCreativeMeta(15288).a("itemFuelCan").e(1).a(Ic2Items.fuelCan.getItem()));
		Ic2Items.tinCan = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemTinCan", 29966), 57)).a("itemTinCan"));
		Ic2Items.filledTinCan = new ItemStack((new ItemTinCan(getItemIdFor(configuration, "itemTinCanFilled", 29965), 58)).a("itemTinCanFilled"));
		Ic2Items.uraniumCell = new ItemStack((new ItemGradual(getItemIdFor(configuration, "itemCellUran", 29951), 38)).a("itemCellUran"));
		Ic2Items.coolingCell = new ItemStack((new ItemGradual(getItemIdFor(configuration, "itemCellCoolant", 29950), 39)).a("itemCellCoolant"));
		Ic2Items.depletedIsotopeCell = new ItemStack((new ItemGradual(getItemIdFor(configuration, "itemCellUranDepleted", 29947), 40)).a("itemCellUranDepleted"));
		Ic2Items.reEnrichedUraniumCell = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemCellUranEnriched", 29946), 41)).a("itemCellUranEnriched"));
		Ic2Items.nearDepletedUraniumCell = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemCellUranEmpty", 29945), 42)).a("itemCellUranEmpty"));
		Ic2Items.integratedReactorPlating = new ItemStack((new ItemGradual(getItemIdFor(configuration, "itemReactorPlating", 29949), 71)).a("itemReactorPlating"));
		Ic2Items.integratedHeatDisperser = new ItemStack((new ItemGradual(getItemIdFor(configuration, "itemReactorCooler", 29948), 72)).a("itemReactorCooler"));
		Ic2Items.terraformerBlueprint = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemTFBP", 29890), 144)).a("itemTFBP"));
		Ic2Items.cultivationTerraformerBlueprint = new ItemStack((new ItemTFBPCultivation(getItemIdFor(configuration, "itemTFBPCultivation", 29889), 145)).a("itemTFBPCultivation"));
		Ic2Items.irrigationTerraformerBlueprint = new ItemStack((new ItemTFBPIrrigation(getItemIdFor(configuration, "itemTFBPIrrigation", 29888), 146)).a("itemTFBPIrrigation"));
		Ic2Items.chillingTerraformerBlueprint = new ItemStack((new ItemTFBPChilling(getItemIdFor(configuration, "itemTFBPChilling", 29887), 147)).a("itemTFBPChilling"));
		Ic2Items.desertificationTerraformerBlueprint = new ItemStack((new ItemTFBPDesertification(getItemIdFor(configuration, "itemTFBPDesertification", 29886), 148)).a("itemTFBPDesertification"));
		Ic2Items.flatificatorTerraformerBlueprint = new ItemStack((new ItemTFBPFlatification(getItemIdFor(configuration, "itemTFBPFlatification", 29885), 149)).a("itemTFBPFlatification"));
		Ic2Items.mushroomTerraformerBlueprint = new ItemStack((new ItemTFBPMushroom(getItemIdFor(configuration, "itemTFBPMushroom", 29862), 161)).a("itemTFBPMushroom"));
		Ic2Items.coalBall = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemPartCoalBall", 29882), 158)).a("itemPartCoalBall"));
		Ic2Items.compressedCoalBall = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemPartCoalBlock", 29881), 157)).a("itemPartCoalBlock"));
		Ic2Items.coalChunk = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemPartCoalChunk", 29880), 156)).a("itemPartCoalChunk"));
		Ic2Items.industrialDiamond = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemPartIndustrialDiamond", 29879), 155)).a("itemPartIndustrialDiamond"));
		Ic2Items.scrap = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemScrap", 29933), 98)).a("itemScrap"));
		Ic2Items.scrapBox = new ItemStack((new ItemScrapbox(getItemIdFor(configuration, "itemScrapbox", 29883), 159)).a("itemScrapbox"));
		Ic2Items.hydratedCoalClump = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemFuelCoalCmpr", 29969), 54)).a("itemFuelCoalCmpr"));
		Ic2Items.plantBall = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemFuelPlantBall", 29968), 55)).a("itemFuelPlantBall"));
		Ic2Items.compressedPlantBall = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemFuelPlantCmpr", 29967), 56)).a("itemFuelPlantCmpr"));
		Ic2Items.painter = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemToolPainter", 29914), 91)).a("itemToolPainter"));
		Ic2Items.blackPainter = new ItemStack((new ItemToolPainter(getItemIdFor(configuration, "itemToolPainterBlack", 29913), 0)).a("itemToolPainter"));
		Ic2Items.redPainter = new ItemStack((new ItemToolPainter(getItemIdFor(configuration, "itemToolPainterRed", 29912), 1)).a("itemToolPainter"));
		Ic2Items.greenPainter = new ItemStack((new ItemToolPainter(getItemIdFor(configuration, "itemToolPainterGreen", 29911), 2)).a("itemToolPainter"));
		Ic2Items.brownPainter = new ItemStack((new ItemToolPainter(getItemIdFor(configuration, "itemToolPainterBrown", 29910), 3)).a("itemToolPainter"));
		Ic2Items.bluePainter = new ItemStack((new ItemToolPainter(getItemIdFor(configuration, "itemToolPainterBlue", 29909), 4)).a("itemToolPainter"));
		Ic2Items.purplePainter = new ItemStack((new ItemToolPainter(getItemIdFor(configuration, "itemToolPainterPurple", 29908), 5)).a("itemToolPainter"));
		Ic2Items.cyanPainter = new ItemStack((new ItemToolPainter(getItemIdFor(configuration, "itemToolPainterCyan", 29907), 6)).a("itemToolPainter"));
		Ic2Items.lightGreyPainter = new ItemStack((new ItemToolPainter(getItemIdFor(configuration, "itemToolPainterLightGrey", 29906), 7)).a("itemToolPainter"));
		Ic2Items.darkGreyPainter = new ItemStack((new ItemToolPainter(getItemIdFor(configuration, "itemToolPainterDarkGrey", 29905), 8)).a("itemToolPainter"));
		Ic2Items.pinkPainter = new ItemStack((new ItemToolPainter(getItemIdFor(configuration, "itemToolPainterPink", 29904), 9)).a("itemToolPainter"));
		Ic2Items.limePainter = new ItemStack((new ItemToolPainter(getItemIdFor(configuration, "itemToolPainterLime", 29903), 10)).a("itemToolPainter"));
		Ic2Items.yellowPainter = new ItemStack((new ItemToolPainter(getItemIdFor(configuration, "itemToolPainterYellow", 29902), 11)).a("itemToolPainter"));
		Ic2Items.cloudPainter = new ItemStack((new ItemToolPainter(getItemIdFor(configuration, "itemToolPainterCloud", 29901), 12)).a("itemToolPainter"));
		Ic2Items.magentaPainter = new ItemStack((new ItemToolPainter(getItemIdFor(configuration, "itemToolPainterMagenta", 29900), 13)).a("itemToolPainter"));
		Ic2Items.orangePainter = new ItemStack((new ItemToolPainter(getItemIdFor(configuration, "itemToolPainterOrange", 29899), 14)).a("itemToolPainter"));
		Ic2Items.whitePainter = new ItemStack((new ItemToolPainter(getItemIdFor(configuration, "itemToolPainterWhite", 29898), 15)).a("itemToolPainter"));
		Ic2Items.dynamite = new ItemStack((new ItemDynamite(getItemIdFor(configuration, "itemDynamite", 29959), 62, false)).a("itemDynamite"));
		Ic2Items.stickyDynamite = new ItemStack((new ItemDynamite(getItemIdFor(configuration, "itemDynamiteSticky", 29958), 63, true)).a("itemDynamiteSticky"));
		Ic2Items.remote = new ItemStack((new ItemRemote(getItemIdFor(configuration, "itemRemote", 29957), 61)).a("itemRemote"));
		Ic2Items.electronicCircuit = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemPartCircuit", 29935), 96)).a("itemPartCircuit"));
		Ic2Items.advancedCircuit = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemPartCircuitAdv", 29934), 97)).setRarity(1).a("itemPartCircuitAdv"));
		Ic2Items.advancedAlloy = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemPartAlloy", 29931), 100)).a("itemPartAlloy"));
		Ic2Items.carbonFiber = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemPartCarbonFibre", 29896), 74)).a("itemPartCarbonFibre"));
		Ic2Items.carbonMesh = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemPartCarbonMesh", 29895), 75)).a("itemPartCarbonMesh"));
		Ic2Items.carbonPlate = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemPartCarbonPlate", 29894), 76)).a("itemPartCarbonPlate"));
		Ic2Items.matter = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemMatter", 29932), 99)).setRarity(2).a("itemMatter"));
		Ic2Items.iridiumOre = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemOreIridium", 29872), 151)).setRarity(2).a("itemOreIridium"));
		Ic2Items.iridiumPlate = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemPartIridium", 29891), 93)).setRarity(2).a("itemPartIridium"));
		new ItemUpgradeModule(getItemIdFor(configuration, "upgradeModule", 29869));
		Ic2Items.coin = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemCoin", 29930), 101)).setShownInCreative(enableCraftingCoin).a("itemCoin"));
		Ic2Items.reinforcedDoor = new ItemStack((new ItemIC2Door(getItemIdFor(configuration, "itemDoorAlloy", 29929), 102, Block.byId[Ic2Items.reinforcedDoorBlock.id])).a("itemDoorAlloy"));
		Ic2Items.constructionFoamPellet = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemPartPellet", 29876), 44)).a("itemPartPellet"));
		Ic2Items.grinPowder = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemGrinPowder", 29850), 198)).a("itemGrinPowder"));
		new ItemDebug(getItemIdFor(configuration, "itemDebug", 29848));
		Ic2Items.cropSeed = new ItemStack((new ItemCropSeed(getItemIdFor(configuration, "itemCropSeed", 29870), 152)).a("itemCropSeed"));
		Ic2Items.cropnalyzer = new ItemStack((new ItemCropnalyzer(getItemIdFor(configuration, "itemCropnalyzer", 29866), 153)).a("itemCropnalyzer"));
		Ic2Items.fertilizer = new ItemStack((new ItemFertilizer(getItemIdFor(configuration, "itemFertilizer", 29865), 160)).a("itemFertilizer"));
		Ic2Items.hydratingCell = new ItemStack((new ItemGradual(getItemIdFor(configuration, "itemCellHydrant", 29864), 39)).a("itemCellHydrant"));
		Ic2Items.electricHoe = new ItemStack((new ItemElectricToolHoe(getItemIdFor(configuration, "itemToolHoe", 29863), 154)).a("itemToolHoe"));
		Ic2Items.terraWart = new ItemStack((new ItemTerraWart(getItemIdFor(configuration, "itemTerraWart", 29858), 166)).a("itemTerraWart"));
		Ic2Items.weedEx = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemWeedEx", 29849), 199)).a("itemWeedEx").e(1).setMaxDurability(64));
		Ic2Items.mugEmpty = new ItemStack((new ItemMug(getItemIdFor(configuration, "itemMugEmpty", 29855), 169)).a("itemMugEmpty"));
		Ic2Items.coffeeBeans = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemCofeeBeans", 29857), 167)).a("itemCoffeeBeans"));
		Ic2Items.coffeePowder = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemCofeePowder", 29856), 168)).a("itemCoffeePowder"));
		Ic2Items.mugCoffee = new ItemStack(new ItemMugCoffee(getItemIdFor(configuration, "itemMugCoffee", 29854), 170));
		Ic2Items.hops = new ItemStack((new ItemIC2(getItemIdFor(configuration, "itemHops", 29853), 174)).a("itemHops"));
		Ic2Items.barrel = new ItemStack((new ItemBarrel(getItemIdFor(configuration, "itemBarrel", 29852), 173)).a("itemBarrel"));
		Ic2Items.mugBooze = new ItemStack(new ItemBooze(getItemIdFor(configuration, "itemMugBooze", 29851), 192));
		Block.OBSIDIAN.b(60.0F);
		Block.ENCHANTMENT_TABLE.b(60.0F);
		Block.WATER.b(30.0F);
		Block.STATIONARY_WATER.b(30.0F);
		Block.STATIONARY_LAVA.b(30.0F);
		((BlockIC2Door) Block.byId[Ic2Items.reinforcedDoorBlock.id]).setItemDropped(Ic2Items.reinforcedDoor.id);
		ExplosionWhitelist.addWhitelistedBlock(Block.BEDROCK);
		FurnaceRecipes furnacerecipes = FurnaceRecipes.getInstance();
		if (Ic2Items.rubberWood != null) {
			furnacerecipes.addSmelting(Ic2Items.rubberWood.id, Ic2Items.rubberWood.getData(), new ItemStack(Block.LOG, 1, 3));
		}

		if (Ic2Items.tinOre != null) {
			furnacerecipes.addSmelting(Ic2Items.tinOre.id, Ic2Items.tinOre.getData(), Ic2Items.tinIngot);
		}

		if (Ic2Items.copperOre != null) {
			furnacerecipes.addSmelting(Ic2Items.copperOre.id, Ic2Items.copperOre.getData(), Ic2Items.copperIngot);
		}

		furnacerecipes.addSmelting(Item.IRON_INGOT.id, 0, Ic2Items.refinedIronIngot);
		furnacerecipes.addSmelting(Ic2Items.ironDust.id, Ic2Items.ironDust.getData(), new ItemStack(Item.IRON_INGOT, 1));
		furnacerecipes.addSmelting(Ic2Items.goldDust.id, Ic2Items.goldDust.getData(), new ItemStack(Item.GOLD_INGOT, 1));
		furnacerecipes.addSmelting(Ic2Items.tinDust.id, Ic2Items.tinDust.getData(), Ic2Items.tinIngot);
		furnacerecipes.addSmelting(Ic2Items.copperDust.id, Ic2Items.copperDust.getData(), Ic2Items.copperIngot);
		furnacerecipes.addSmelting(Ic2Items.hydratedCoalDust.id, Ic2Items.hydratedCoalDust.getData(), Ic2Items.coalDust);
		furnacerecipes.addSmelting(Ic2Items.bronzeDust.id, Ic2Items.bronzeDust.getData(), Ic2Items.bronzeIngot);
		furnacerecipes.addSmelting(Ic2Items.resin.id, Ic2Items.resin.getData(), Ic2Items.rubber);
		furnacerecipes.registerRecipe(Ic2Items.mugCoffee.id, new ItemStack(Ic2Items.mugCoffee.getItem(), 1, 1));
		((ItemElectricToolChainsaw) Ic2Items.chainsaw.getItem()).init();
		((ItemElectricToolDrill) Ic2Items.miningDrill.getItem()).init();
		((ItemElectricToolDDrill) Ic2Items.diamondDrill.getItem()).init();
		((ItemNanoSaber) Ic2Items.nanoSaber.getItem()).init();
		ItemScrapbox.init();
		ItemTFBPCultivation.init();
		ItemTFBPFlatification.init();
		TileEntityCompressor.init();
		TileEntityExtractor.init();
		TileEntityMacerator.init();
		MinecraftForge.setToolClass(Ic2Items.bronzePickaxe.getItem(), "pickaxe", 2);
		MinecraftForge.setToolClass(Ic2Items.bronzeAxe.getItem(), "axe", 2);
		MinecraftForge.setToolClass(Ic2Items.bronzeShovel.getItem(), "shovel", 2);
		MinecraftForge.setToolClass(Ic2Items.chainsaw.getItem(), "axe", 2);
		MinecraftForge.setToolClass(Ic2Items.miningDrill.getItem(), "pickaxe", 2);
		MinecraftForge.setToolClass(Ic2Items.diamondDrill.getItem(), "pickaxe", 3);
		MinecraftForge.setBlockHarvestLevel(Block.byId[Ic2Items.reinforcedStone.id], "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(Block.byId[Ic2Items.reinforcedDoorBlock.id], "pickaxe", 2);
		MinecraftForge.setBlockHarvestLevel(Block.byId[Ic2Items.insulatedCopperCableBlock.id], "axe", 0);
		MinecraftForge.setBlockHarvestLevel(Block.byId[Ic2Items.constructionFoamWall.id], "pickaxe", 1);
		if (Ic2Items.copperOre != null) {
			MinecraftForge.setBlockHarvestLevel(Block.byId[Ic2Items.copperOre.id], "pickaxe", 1);
		}

		if (Ic2Items.tinOre != null) {
			MinecraftForge.setBlockHarvestLevel(Block.byId[Ic2Items.tinOre.id], "pickaxe", 1);
		}

		if (Ic2Items.uraniumOre != null) {
			MinecraftForge.setBlockHarvestLevel(Block.byId[Ic2Items.uraniumOre.id], "pickaxe", 2);
		}

		if (Ic2Items.rubberWood != null) {
			MinecraftForge.setBlockHarvestLevel(Block.byId[Ic2Items.rubberWood.id], "axe", 0);
		}

		windStrength = 10 + random.nextInt(10);
		windTicker = 0;
		Block.setBurnProperties(Ic2Items.scaffold.id, 8, 20);
		if (Ic2Items.rubberLeaves != null) {
			Block.setBurnProperties(Ic2Items.rubberLeaves.id, 30, 20);
		}

		if (Ic2Items.rubberWood != null) {
			Block.setBurnProperties(Ic2Items.rubberWood.id, 4, 20);
		}

		registerCraftingRecipes();
		OreDictionary.registerOreHandler(this);
		if (!$assertionsDisabled && Ic2Items.uraniumDrop == null) {
			throw new AssertionError();
		}
		else if (!$assertionsDisabled && Ic2Items.bronzeIngot == null) {
			throw new AssertionError();
		}
		else if (!$assertionsDisabled && Ic2Items.copperIngot == null) {
			throw new AssertionError();
		}
		else if (!$assertionsDisabled && Ic2Items.refinedIronIngot == null) {
			throw new AssertionError();
		}
		else if (!$assertionsDisabled && Ic2Items.tinIngot == null) {
			throw new AssertionError();
		}
		else if (!$assertionsDisabled && Ic2Items.uraniumIngot == null) {
			throw new AssertionError();
		}
		else if (!$assertionsDisabled && Ic2Items.rubber == null) {
			throw new AssertionError();
		}
		else {
			if (Ic2Items.copperOre != null) {
				OreDictionary.registerOre("oreCopper", Ic2Items.copperOre);
			}

			if (Ic2Items.tinOre != null) {
				OreDictionary.registerOre("oreTin", Ic2Items.tinOre);
			}

			if (Ic2Items.uraniumOre != null) {
				OreDictionary.registerOre("oreUranium", Ic2Items.uraniumOre);
			}

			OreDictionary.registerOre("itemDropUranium", Ic2Items.uraniumDrop);
			OreDictionary.registerOre("ingotBronze", Ic2Items.bronzeIngot);
			OreDictionary.registerOre("ingotCopper", Ic2Items.copperIngot);
			OreDictionary.registerOre("ingotRefinedIron", Ic2Items.refinedIronIngot);
			OreDictionary.registerOre("ingotTin", Ic2Items.tinIngot);
			OreDictionary.registerOre("ingotUranium", Ic2Items.uraniumIngot);
			OreDictionary.registerOre("itemRubber", Ic2Items.rubber);
			if (Ic2Items.rubberWood != null) {
				OreDictionary.registerOre("woodRubber", Ic2Items.rubberWood);
			}

			IC2Crops.init();
			IC2Achievements.init();
			enableDynamicIdAllocation = false;
			if (configuration != null) {
				configuration.save();
			}

			ModLoader.setInGameHook(this, true, true);
			MinecraftForge.registerConnectionHandler(this);
			MinecraftForge.registerEntity(EntityMiningLaser.class, this, 0, 160, 40, false);
			MinecraftForge.registerEntity(EntityDynamite.class, this, 1, 160, 5, true);
			MinecraftForge.registerEntity(EntityStickyDynamite.class, this, 2, 160, 5, true);
			MinecraftForge.registerEntity(EntityItnt.class, this, 3, 160, 5, true);
			MinecraftForge.registerEntity(EntityNuke.class, this, 4, 160, 5, true);
			int i = Integer.parseInt((new SimpleDateFormat("Mdd")).format(new Date()));
			suddenlyHoes = (double) i > Math.cbrt(6.4E7D) && (double) i < Math.cbrt(6.5939264E7D);
			super.load();
			FMLCommonHandler.instance().registerTickHandler(this);
			TileEntityRecycler.init(configuration);
			initialized = true;
		}
	}

	public void modsLoaded() {
		if (!initialized) {
			Platform.displayError("IndustrialCraft 2 has failed to initialize properly.");
		}

		super.modsLoaded();
		if (loadSubModule("bcIntegration22x")) {
			System.out.println("[IC2] BuildCraft integration module loaded");
		}

		if (Platform.isRendering()) {
			if (loadSubModule("cgIntegration14x")) {
				System.out.println("[IC2] CraftGuide integration module loaded");
			}

			if (loadSubModule("neiIntegration11x")) {
				System.out.println("[IC2] NEI integration module loaded");
			}
		}

		String s = "";

		try {
			Class class1 = Class.forName("mod_PortalGun");
			Method method1 = class1.getMethod("addBlockIDToGrabList", Integer.TYPE, int[].class);
			Method method2 = class1.getMethod("addBlockIDToGrabList", Integer.TYPE);
			if (Ic2Items.rubberWood != null) {
				method1.invoke(null, Ic2Items.rubberWood.id, new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11});
			}

			method2.invoke(null, Ic2Items.miningPipe.id);
			method2.invoke(null, Ic2Items.miningPipeTip.id);
			method2.invoke(null, Ic2Items.batBox.id);
			method2.invoke(null, Ic2Items.machine.id);
			method2.invoke(null, Ic2Items.teleporter.id);
			method2.invoke(null, Ic2Items.luminator.id);
			method2.invoke(null, Ic2Items.activeLuminator.id);
			method2.invoke(null, Ic2Items.scaffold.id);
			method2.invoke(null, Ic2Items.rubberTrampoline.id);
			s = s + ", Portal Gun";
		} catch (Throwable var8) {
		}

		try {
			Method method = Class.forName("mod_Gibbing").getMethod("addCustomItem", Integer.TYPE, Integer.TYPE);
			method.invoke(null, Ic2Items.nanoSaber.id, 2);
			method.invoke(null, Ic2Items.chainsaw.id, 2);
			method.invoke(null, Ic2Items.miningDrill.id, 4);
			method.invoke(null, Ic2Items.diamondDrill.id, 4);
			s = s + ", Mob Amputation";
		} catch (Throwable var7) {
		}

		Field field1;
		try {
			field1 = Class.forName("mod_Timber").getDeclaredField("axes");
			field1.set(null, field1.get(null) + ", " + Ic2Items.bronzeAxe.id + ", " + Ic2Items.chainsaw.id);
			s = s + ", Timber";
		} catch (Throwable var6) {
		}

		try {
			field1 = Class.forName("mod_treecapitator").getDeclaredField("IDList");
			field1.set(null, field1.get(null) + "; " + Ic2Items.bronzeAxe.id + "; " + Ic2Items.chainsaw.id);
			s = s + ", Treecapitator";
		} catch (Throwable var5) {
		}

		System.out.println("[IC2] Loaded minor compatibility modules: " + (s.isEmpty() ? "none" : s.substring(2)));
		ModLoader.registerTileEntity(TileEntityBlock.class, "Empty Management TileEntity");
		ModLoader.registerTileEntity(TileEntityIronFurnace.class, "Iron Furnace");
		ModLoader.registerTileEntity(TileEntityElecFurnace.class, "Electric Furnace");
		ModLoader.registerTileEntity(TileEntityMacerator.class, "Macerator");
		ModLoader.registerTileEntity(TileEntityExtractor.class, "Extractor");
		ModLoader.registerTileEntity(TileEntityCompressor.class, "Compressor");
		ModLoader.registerTileEntity(TileEntityGenerator.class, "Generator");
		if (BlockGenerator.tileEntityGeoGeneratorClass == TileEntityGeoGenerator.class) {
			ModLoader.registerTileEntity(TileEntityGeoGenerator.class, "Geothermal Generator");
		}

		if (BlockGenerator.tileEntityWaterGeneratorClass == TileEntityWaterGenerator.class) {
			ModLoader.registerTileEntity(TileEntityWaterGenerator.class, "Water Mill");
		}

		ModLoader.registerTileEntity(TileEntitySolarGenerator.class, "Solar Panel");
		ModLoader.registerTileEntity(TileEntityWindGenerator.class, "Wind Mill");
		ModLoader.registerTileEntity(TileEntityCanner.class, "Canning Machine");
		ModLoader.registerTileEntity(TileEntityMiner.class, "Miner");
		ModLoader.registerTileEntity(TileEntityPump.class, "Pump");
		ModLoader.registerTileEntity(TileEntityNuclearReactor.class, "Nuclear Reactor");
		ModLoader.registerTileEntity(TileEntityReactorChamber.class, "Reactor Chamber");
		ModLoader.registerTileEntity(TileEntityMagnetizer.class, "Magnetizer");
		ModLoader.registerTileEntity(TileEntityCable.class, "Cable");
		ModLoader.registerTileEntity(TileEntityElectricBatBox.class, "BatBox");
		ModLoader.registerTileEntity(TileEntityElectricMFE.class, "MFE");
		ModLoader.registerTileEntity(TileEntityElectricMFSU.class, "MFSU");
		ModLoader.registerTileEntity(TileEntityTransformerLV.class, "LV-Transformer");
		ModLoader.registerTileEntity(TileEntityTransformerMV.class, "MV-Transformer");
		ModLoader.registerTileEntity(TileEntityTransformerHV.class, "HV-Transformer");
		ModLoader.registerTileEntity(TileEntityLuminator.class, "Luminator");
		ModLoader.registerTileEntity(TileEntityElectrolyzer.class, "Electrolyzer");
		ModLoader.registerTileEntity(TileEntityPersonalChest.class, "Personal Safe");
		ModLoader.registerTileEntity(TileEntityTradeOMat.class, "Trade-O-Mat");
		ModLoader.registerTileEntity(TileEntityRecycler.class, "Recycler");
		ModLoader.registerTileEntity(TileEntityInduction.class, "Induction Furnace");
		ModLoader.registerTileEntity(TileEntityMatter.class, "Mass Fabricator");
		ModLoader.registerTileEntity(TileEntityTerra.class, "Terraformer");
		ModLoader.registerTileEntity(TileEntityTeleporter.class, "Teleporter");
		ModLoader.registerTileEntity(TileEntityTesla.class, "Tesla Coil");
		ModLoader.registerTileEntity(TileEntityCableDetector.class, "Detector Cable");
		ModLoader.registerTileEntity(TileEntityCableSplitter.class, "SplitterCable");
		ModLoader.registerTileEntity(TileEntityCrop.class, "TECrop");
		ModLoader.registerTileEntity(TileEntityBarrel.class, "TEBarrel");
		ModLoader.registerTileEntity(TileEntityCropmatron.class, "Crop-Matron");
	}

	public int addFuel(int i, int j) {
		if (Ic2Items.rubberSapling != null && i == Ic2Items.rubberSapling.id) {
			return 80;
		}
		else if (i == Item.SUGAR_CANE.id) {
			return 50;
		}
		else if (i == Block.CACTUS.id) {
			return 50;
		}
		else {
			return i != Ic2Items.scrap.id ? 0 : 350;
		}
	}

	public void takenFromCrafting(EntityHuman entityhuman, ItemStack itemstack, IInventory iinventory) {
		IC2Achievements.takenFromCrafting(entityhuman, itemstack, iinventory);
	}

	public void generateSurface(World world, Random random1, int i, int j) {
		int l;
		if (enableWorldGenTreeRubber) {
			BiomeBase biomebase = world.getWorldChunkManager().getBiome(i + 16, j + 16);
			l = 0;
			if (biomebase.y.toLowerCase().contains("taiga")) {
				l += random1.nextInt(3);
			}

			if (biomebase.y.toLowerCase().contains("forest") || biomebase.y.toLowerCase().contains("jungle")) {
				l += random1.nextInt(5) + 1;
			}

			if (biomebase.y.toLowerCase().contains("swamp")) {
				l += random1.nextInt(10) + 5;
			}

			if (random1.nextInt(100) + 1 <= l * 2) {
				(new WorldGenRubTree()).a(world, random1, i + random1.nextInt(16), l, j + random1.nextInt(16));
			}
		}

		int k = getSeaLevel(world) + 1;
		int j2;
		int i3;
		int l3;
		int k4;
		int j5;
		if (enableWorldGenOreCopper && Ic2Items.copperOre != null) {
			l = 15 * k / 64;
			j2 = (int) Math.round(random1.nextGaussian() * Math.sqrt((double) l) + (double) l);

			for (i3 = 0; i3 < j2; ++i3) {
				l3 = i + random1.nextInt(16);
				k4 = random1.nextInt(40 * k / 64) + random1.nextInt(20 * k / 64) + 10 * k / 64;
				j5 = j + random1.nextInt(16);
				(new WorldGenMinable(Ic2Items.copperOre.id, 10)).a(world, random1, l3, k4, j5);
			}
		}

		if (enableWorldGenOreTin && Ic2Items.tinOre != null) {
			l = 25 * k / 64;
			j2 = (int) Math.round(random1.nextGaussian() * Math.sqrt((double) l) + (double) l);

			for (i3 = 0; i3 < j2; ++i3) {
				l3 = i + random1.nextInt(16);
				k4 = random1.nextInt(40 * k / 64);
				j5 = j + random1.nextInt(16);
				(new WorldGenMinable(Ic2Items.tinOre.id, 6)).a(world, random1, l3, k4, j5);
			}
		}

		if (enableWorldGenOreUranium && Ic2Items.uraniumOre != null) {
			l = 20 * k / 64;
			j2 = (int) Math.round(random1.nextGaussian() * Math.sqrt((double) l) + (double) l);

			for (i3 = 0; i3 < j2; ++i3) {
				l3 = i + random1.nextInt(16);
				k4 = random1.nextInt(64 * k / 64);
				j5 = j + random1.nextInt(16);
				(new WorldGenMinable(Ic2Items.uraniumOre.id, 3)).a(world, random1, l3, k4, j5);
			}
		}

	}

	public void tickStart(EnumSet enumset, Object[] aobj) {
		if (enumset.contains(TickType.WORLD)) {
			World world;
			if (Platform.isRendering()) {
				if (!$assertionsDisabled && !(aobj[2] instanceof World)) {
					throw new AssertionError();
				}

				world = (World) aobj[2];
			}
			else {
				if (!$assertionsDisabled && !(aobj[0] instanceof World)) {
					throw new AssertionError();
				}

				world = (World) aobj[0];
			}

			Platform.profilerStartSection("Init");
			if (!singleTickCallbacks.containsKey(world)) {
				singleTickCallbacks.put(world, new ArrayDeque());
				continuousTickCallbacks.put(world, new HashSet());
				continuousTickCallbacksInUse.put(world, false);
				continuousTickCallbacksToRemove.put(world, new Vector());
			}

			Platform.profilerEndStartSection("PlayerUpdate");
			Iterator iterator = world.players.iterator();

			while (iterator.hasNext()) {
				EntityHuman entityhuman = (EntityHuman) iterator.next();
				Platform.profilerStartSection("ArmorTick");
				boolean flag = false;

				for (int l = 0; l < 4; ++l) {
					if (entityhuman.inventory.armor[l] != null && entityhuman.inventory.armor[l].getItem() instanceof IItemTickListener && ((IItemTickListener) entityhuman.inventory.armor[l].getItem()).onTick(entityhuman, entityhuman.inventory.armor[l])) {
						flag = true;
					}
				}

				if (flag && !Platform.isRendering()) {
					entityhuman.activeContainer.a();
				}

				Platform.profilerEndStartSection("NanoSaber");
				ItemNanoSaber.timedLoss(entityhuman);
				Platform.profilerEndSection();
			}

			if (Platform.isSimulating()) {
				Platform.profilerEndStartSection("Wind");
				if (windTicker % 128 == 0) {
					updateWind(world);
				}

				++windTicker;
				Platform.profilerEndStartSection("EnergyNet");
				EnergyNet.onTick(world);
			}

			if (Platform.isRendering()) {
				Platform.profilerEndStartSection("AudioManager");
				AudioManager.onTick();
			}

			Platform.profilerEndStartSection("Networking");
			Keyboard.sendKeyUpdate();
			NetworkManager.onTick(world);
			Platform.profilerEndStartSection("SingleTickCallback");
			Queue queue1;
			ITickCallback itickcallback1;
			if (singleTickCallbacks.containsKey(world)) {
				queue1 = (Queue) singleTickCallbacks.get(world);

				while ((itickcallback1 = (ITickCallback) queue1.poll()) != null) {
					Platform.profilerStartSection(itickcallback1.getClass().getName());
					itickcallback1.tickCallback(world);
					Platform.profilerEndSection();
				}
			}

			if (singleTickCallbacks.containsKey(null)) {
				queue1 = (Queue) singleTickCallbacks.get(null);

				while ((itickcallback1 = (ITickCallback) queue1.poll()) != null) {
					Platform.profilerStartSection(itickcallback1.getClass().getName());
					itickcallback1.tickCallback(world);
					Platform.profilerEndSection();
				}
			}

			Platform.profilerEndStartSection("ContTickCallback");
			if (continuousTickCallbacks.containsKey(world)) {
				Set set = (Set) continuousTickCallbacks.get(world);
				continuousTickCallbacksInUse.put(world, true);
				Iterator iterator1 = set.iterator();

				while (iterator1.hasNext()) {
					ITickCallback itickcallback2 = (ITickCallback) iterator1.next();
					Platform.profilerStartSection(itickcallback2.getClass().getName());
					itickcallback2.tickCallback(world);
					Platform.profilerEndSection();
				}

				continuousTickCallbacksInUse.put(world, false);
				if (continuousTickCallbacksToAdd.containsKey(world)) {
					set.addAll((Collection) continuousTickCallbacksToAdd.get(world));
					((List) continuousTickCallbacksToAdd.get(world)).clear();
				}

				if (continuousTickCallbacksToRemove.containsKey(world)) {
					set.removeAll((Collection) continuousTickCallbacksToRemove.get(world));
					((List) continuousTickCallbacksToRemove.get(world)).clear();
				}
			}

			Platform.profilerEndSection();
		}

		if (enumset.contains(TickType.WORLDLOAD)) {
			if (Platform.isSimulating()) {
				Integer[] ainteger = DimensionManager.getIDs();
				int i = ainteger.length;

				for (int j = 0; j < i; ++j) {
					int k = ainteger[j];
					World world1 = DimensionManager.getProvider(k).a;
					if (world1 != null && world1.getDataManager() instanceof WorldNBTStorage) {
						WorldNBTStorage worldnbtstorage = (WorldNBTStorage) world1.getDataManager();
						File file = null;
						Field[] afield = WorldNBTStorage.class.getDeclaredFields();
						int i1 = afield.length;

						for (int j1 = 0; j1 < i1; ++j1) {
							Field field = afield[j1];
							if (field.getType() == File.class) {
								field.setAccessible(true);

								try {
									File file2 = (File) field.get(worldnbtstorage);
									if (file == null || file.getParentFile() == file2) {
										file = file2;
									}
								} catch (Exception var24) {
								}
							}
						}

						if (file != null) {
							try {
								Properties properties = new Properties() {
									public Set keySet() {
										return Collections.unmodifiableSet(new TreeSet(super.keySet()));
									}

									public synchronized Enumeration keys() {
										return Collections.enumeration(new TreeSet(super.keySet()));
									}
								};
								properties.putAll(runtimeIdProperties);
								File file1 = new File(file, "ic2_map.cfg");
								if (file1.exists()) {
									FileInputStream fileinputstream = new FileInputStream(file1);
									Properties properties1 = new Properties();
									properties1.load(fileinputstream);
									fileinputstream.close();
									Iterator iterator2 = properties1.entrySet().iterator();

									while (true) {
										while (iterator2.hasNext()) {
											Entry entry = (Entry) iterator2.next();
											String s = (String) entry.getKey();
											String s1 = (String) entry.getValue();
											if (!runtimeIdProperties.containsKey(s)) {
												properties1.remove(s);
											}
											else {
												int k1 = s.indexOf(46);
												if (k1 != -1) {
													String s2 = s.substring(0, k1);
													String s3 = s.substring(k1 + 1);
													if ((s2.equals("block") || s2.equals("item")) && !s1.equals(runtimeIdProperties.get(s))) {
														Platform.displayError("IC2 detected an ID conflict between your IC2.cfg and the map you are\ntrying to load.\n\nMap: " + file.getName() + "\n" + "\n" + "Config section: " + s2 + "\n" + "Config entry: " + s3 + "\n" + "Config value: " + runtimeIdProperties.get(s) + "\n" + "Map value: " + s1 + "\n" + "\n" + "Adjust your config to match the IDs used by the map or convert your\n" + "map to use the IDs specified in the config.\n" + "\n" + "See also: config/IC2.cfg " + (Platform.isRendering() ? "saves/" : "") + file.getName() + "/ic2_map.cfg");
													}
												}
											}
										}

										properties.putAll(properties1);
										break;
									}
								}

								FileOutputStream fileoutputstream = new FileOutputStream(file1);
								properties.store(fileoutputstream, "ic2 map related configuration data");
								fileoutputstream.close();
							} catch (IOException var23) {
								var23.printStackTrace();
							}
							break;
						}
					}
				}
			}

			if (Platform.isRendering()) {
				TextureIndex.reset();
			}
		}

	}

	public void tickEnd(EnumSet enumset, Object[] aobj) {
	}

	public EnumSet ticks() {
		return EnumSet.of(TickType.WORLD, TickType.WORLDLOAD);
	}

	public String getLabel() {
		return "IC2";
	}

	public void takenFromFurnace(EntityHuman entityhuman, ItemStack itemstack) {
		IC2Achievements.takenFromFurnace(entityhuman, itemstack);
	}

	public void onItemPickup(EntityHuman entityhuman, ItemStack itemstack) {
		IC2Achievements.onItemPickup(entityhuman, itemstack);
	}

	public boolean dispenseEntity(World world, double d, double d1, double d2, int i, int j, ItemStack itemstack) {
		double d4;
		if (itemstack.doMaterialsMatch(Ic2Items.scrapBox)) {
			double var10004 = d1 - 0.3D;
			ItemScrapbox var10006 = (ItemScrapbox) Ic2Items.scrapBox.getItem();
			EntityItem entityitem = new EntityItem(world, d, var10004, d2, ItemScrapbox.getDrop(world));
			d4 = random.nextDouble() * 0.1D + 0.2D;
			entityitem.motX = (double) i * d4;
			entityitem.motY = 0.2000000029802322D;
			entityitem.motZ = (double) j * d4;
			entityitem.motX += random.nextGaussian() * 0.007499999832361937D * 6.0D;
			entityitem.motY += random.nextGaussian() * 0.007499999832361937D * 6.0D;
			entityitem.motZ += random.nextGaussian() * 0.007499999832361937D * 6.0D;
			world.addEntity(entityitem);
			world.triggerEffect(1000, (int) d, (int) d1, (int) d2, 0);
			return true;
		}
		else if (!itemstack.doMaterialsMatch(Ic2Items.dynamite) && !itemstack.doMaterialsMatch(Ic2Items.stickyDynamite)) {
			return false;
		}
		else {
			EntityDynamite entitydynamite = new EntityDynamite(world, d, d1 - 0.3D, d2);
			entitydynamite.sticky = itemstack.doMaterialsMatch(Ic2Items.stickyDynamite);
			d4 = random.nextDouble() * 0.1D + 0.2D;
			entitydynamite.motX = (double) i * d4;
			entitydynamite.motY = 0.2000000029802322D;
			entitydynamite.motZ = (double) j * d4;
			entitydynamite.motX += random.nextGaussian() * 0.007499999832361937D * 6.0D;
			entitydynamite.motY += random.nextGaussian() * 0.007499999832361937D * 6.0D;
			entitydynamite.motZ += random.nextGaussian() * 0.007499999832361937D * 6.0D;
			world.addEntity(entitydynamite);
			world.triggerEffect(1000, (int) d, (int) d1, (int) d2, 0);
			return true;
		}
	}

	public void registerOre(String s, ItemStack itemstack) {
		if (s.equals("ingotCopper")) {
			Ic2Recipes.addMaceratorRecipe(itemstack, Ic2Items.copperDust);
		}
		else if (s.equals("ingotRefinedIron")) {
			Ic2Recipes.addMaceratorRecipe(itemstack, Ic2Items.ironDust);
		}
		else if (s.equals("ingotSilver")) {
			Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.glassFiberCableItem, 6), new Object[]{"GGG", "SDS", "GGG", 'G', Block.GLASS, 'S', "ingotSilver", 'R', Item.REDSTONE, 'D', Item.DIAMOND});
			Ic2Recipes.addCraftingRecipe(StackUtil.copyWithSize(Ic2Items.glassFiberCableItem, 6), new Object[]{"GGG", "SDS", "GGG", 'G', Block.GLASS, 'S', "ingotSilver", 'R', Item.REDSTONE, 'D', Ic2Items.industrialDiamond});
			Ic2Recipes.addMaceratorRecipe(itemstack, Ic2Items.silverDust);
			if (!silverDustSmeltingRegistered) {
				FurnaceRecipes furnacerecipes = FurnaceRecipes.getInstance();
				furnacerecipes.addSmelting(Ic2Items.silverDust.id, Ic2Items.silverDust.getData(), itemstack);
				silverDustSmeltingRegistered = true;
			}
		}
		else if (s.equals("ingotTin")) {
			Ic2Recipes.addMaceratorRecipe(itemstack, Ic2Items.tinDust);
		}
		else if (s.equals("itemDropUranium")) {
			Ic2Recipes.addCompressorRecipe(itemstack, Ic2Items.uraniumIngot);
		}
		else if (s.equals("oreCopper")) {
			Ic2Recipes.addMaceratorRecipe(itemstack, StackUtil.copyWithSize(Ic2Items.copperDust, 2));
			addValuableOre(itemstack.id, itemstack.getData(), 2);
		}
		else if (!s.equals("oreGemRuby") && !s.equals("oreGemEmerald") && !s.equals("oreGemSapphire")) {
			if (s.equals("oreSilver")) {
				Ic2Recipes.addMaceratorRecipe(itemstack, StackUtil.copyWithSize(Ic2Items.silverDust, 2));
				addValuableOre(itemstack.id, itemstack.getData(), 3);
			}
			else if (s.equals("oreTin")) {
				Ic2Recipes.addMaceratorRecipe(itemstack, StackUtil.copyWithSize(Ic2Items.tinDust, 2));
				addValuableOre(itemstack.id, itemstack.getData(), 2);
			}
			else if (s.equals("oreUranium")) {
				Ic2Recipes.addCompressorRecipe(itemstack, Ic2Items.uraniumIngot);
				addValuableOre(itemstack.id, itemstack.getData(), 4);
			}
			else if (s.equals("oreTungsten")) {
				addValuableOre(itemstack.id, itemstack.getData(), 5);
			}
			else if (s.equals("woodRubber")) {
				Ic2Recipes.addExtractorRecipe(itemstack, Ic2Items.rubber);
			}
			else if (s.startsWith("ore")) {
				addValuableOre(itemstack.id, itemstack.getData(), 1);
			}
		}
		else {
			addValuableOre(itemstack.id, itemstack.getData(), 4);
		}

	}

	public void onConnect(net.minecraft.server.NetworkManager networkmanager) {
		MessageManager.getInstance().registerChannel(networkmanager, new NetworkManager(), "ic2");
	}

	public void onLogin(net.minecraft.server.NetworkManager networkmanager, Packet1Login packet1login) {
	}

	public void onDisconnect(net.minecraft.server.NetworkManager networkmanager, String s, Object[] aobj) {
	}

	public boolean clientSideRequired() {
		return true;
	}

	public boolean serverSideRequired() {
		return false;
	}

	public String getVersion() {
		return "v1.97";
	}
}
