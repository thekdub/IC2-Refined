package ic2.common;

import ic2.api.*;
import ic2.platform.NetworkManager;
import ic2.platform.Platform;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class TileEntityCrop extends TECrop implements INetworkDataProvider, INetworkUpdateListener {
	public static char tickRate = 256;
	public int growthPoints = 0;
	public boolean upgraded = false;
	public char ticker;
	public boolean dirty;
	public byte humidity;
	public byte nutrients;
	public byte airQuality;
	private boolean created;

	public TileEntityCrop() {
		this.ticker = (char) mod_IC2.random.nextInt(tickRate);
		this.dirty = true;
		this.created = false;
		this.humidity = -1;
		this.nutrients = -1;
		this.airQuality = -1;
	}

	public void a(NBTTagCompound nbttagcompound) {
		super.a(nbttagcompound);
		this.id = nbttagcompound.getShort("cropid");
		this.size = nbttagcompound.getByte("size");
		this.statGrowth = nbttagcompound.getByte("statGrowth");
		this.statGain = nbttagcompound.getByte("statGain");
		this.statResistance = nbttagcompound.getByte("statResistance");

		for (int i = 0; i < this.custumData.length; ++i) {
			this.custumData[i] = nbttagcompound.getShort("data" + i);
		}

		this.growthPoints = nbttagcompound.getInt("growthPoints");

		try {
			this.nutrientStorage = nbttagcompound.getInt("nutrientStorage");
			this.waterStorage = nbttagcompound.getInt("waterStorage");
		} catch (Throwable var3) {
			this.nutrientStorage = nbttagcompound.getByte("nutrientStorage");
			this.waterStorage = nbttagcompound.getByte("waterStorage");
		}

		this.upgraded = nbttagcompound.getBoolean("upgraded");
		this.scanLevel = nbttagcompound.getByte("scanLevel");
	}

	public void b(NBTTagCompound nbttagcompound) {
		super.b(nbttagcompound);
		nbttagcompound.setShort("cropid", this.id);
		nbttagcompound.setByte("size", this.size);
		nbttagcompound.setByte("statGrowth", this.statGrowth);
		nbttagcompound.setByte("statGain", this.statGain);
		nbttagcompound.setByte("statResistance", this.statResistance);

		for (int i = 0; i < this.custumData.length; ++i) {
			nbttagcompound.setShort("data" + i, this.custumData[i]);
		}

		nbttagcompound.setInt("growthPoints", this.growthPoints);
		nbttagcompound.setInt("nutrientStorage", this.nutrientStorage);
		nbttagcompound.setInt("waterStorage", this.waterStorage);
		nbttagcompound.setBoolean("upgraded", this.upgraded);
		nbttagcompound.setByte("scanLevel", this.scanLevel);
	}

	public void q_() {
		super.q_();
		if (!this.created) {
			NetworkManager.requestInitialData(this);
			this.created = true;
		}

		++this.ticker;
		if (this.ticker % tickRate == 0) {
			this.tick();
		}

		if (this.dirty) {
			this.dirty = false;
			this.world.notify(this.x, this.y, this.z);
			this.world.b(EnumSkyBlock.BLOCK, this.x, this.y, this.z);
			if (Platform.isSimulating()) {
				NetworkManager.announceBlockUpdate(this.world, this.x, this.y, this.z);
				if (!Platform.isRendering()) {
					Iterator iterator = this.getNetworkedFields().iterator();

					while (iterator.hasNext()) {
						String s = (String) iterator.next();
						NetworkManager.updateTileEntityField(this, s);
					}
				}
			}
		}

	}

	public List getNetworkedFields() {
		Vector vector = new Vector(2);
		vector.add("id");
		vector.add("size");
		vector.add("upgraded");
		vector.add("custumData");
		return vector;
	}

	public void tick() {
		if (Platform.isSimulating()) {
			if (this.ticker % (tickRate << 2) == 0) {
				this.humidity = this.updateHumidity();
			}

			if ((this.ticker + tickRate) % (tickRate << 2) == 0) {
				this.nutrients = this.updateNutrients();
			}

			if ((this.ticker + tickRate * 2) % (tickRate << 2) == 0) {
				this.airQuality = this.updateAirQuality();
			}

			if (this.id < 0 && (!this.upgraded || !this.attemptCrossing())) {
				if (mod_IC2.random.nextInt(100) != 0 || this.hasEx()) {
					if (this.exStorage > 0 && mod_IC2.random.nextInt(10) == 0) {
						--this.exStorage;
					}

					return;
				}

				this.reset();
				this.id = (short) IC2Crops.weed.getId();
				this.size = 1;
			}

			this.crop().tick(this);
			if (this.crop().canGrow(this)) {
				this.growthPoints += this.calcGrowthRate();
				if (this.id > -1 && this.growthPoints >= this.crop().growthDuration(this)) {
					this.growthPoints = 0;
					++this.size;
					this.dirty = true;
				}
			}

			if (this.nutrientStorage > 0) {
				--this.nutrientStorage;
			}

			if (this.waterStorage > 0) {
				--this.waterStorage;
			}

			if (this.crop().isWeed(this) && mod_IC2.random.nextInt(50) - this.statGrowth <= 2) {
				this.generateWeed();
			}

		}
	}

	public void generateWeed() {
		int i = this.x;
		int j = this.y;
		int k = this.z;
		switch (mod_IC2.random.nextInt(4)) {
			case 0:
				++i;
			case 1:
				--i;
			case 2:
				++k;
			case 3:
				--k;
		}

		if (this.world.getTileEntity(i, j, k) instanceof TileEntityCrop) {
			TileEntityCrop tileentitycrop = (TileEntityCrop) this.world.getTileEntity(i, j, k);
			if (tileentitycrop.id == -1 || !tileentitycrop.crop().isWeed(tileentitycrop) && mod_IC2.random.nextInt(32) >= tileentitycrop.statResistance && !tileentitycrop.hasEx()) {
				byte byte0 = this.statGrowth;
				if (tileentitycrop.statGrowth > byte0) {
					byte0 = tileentitycrop.statGrowth;
				}

				if (byte0 < 31 && mod_IC2.random.nextBoolean()) {
					++byte0;
				}

				tileentitycrop.reset();
				tileentitycrop.id = 0;
				tileentitycrop.size = 1;
				tileentitycrop.statGrowth = byte0;
			}
		}
		else if (this.world.getTypeId(i, j, k) == 0) {
			int l = this.world.getTypeId(i, j - 1, k);
			if (l == Block.DIRT.id || l == Block.GRASS.id || l == Block.SOIL.id) {
				this.world.setTypeId(i, j - 1, k, Block.GRASS.id);
				this.world.setTypeIdAndData(i, j, k, Block.LONG_GRASS.id, 1);
			}
		}

	}

	public boolean hasEx() {
		if (this.exStorage > 0) {
			this.exStorage -= 5;
			return true;
		}
		else {
			return false;
		}
	}

	public boolean attemptCrossing() {
		if (mod_IC2.random.nextInt(3) != 0) {
			return false;
		}
		else {
			LinkedList linkedlist = new LinkedList();
			this.askCropJoinCross(this.x - 1, this.y, this.z, linkedlist);
			this.askCropJoinCross(this.x + 1, this.y, this.z, linkedlist);
			this.askCropJoinCross(this.x, this.y, this.z - 1, linkedlist);
			this.askCropJoinCross(this.x, this.y, this.z + 1, linkedlist);
			if (linkedlist.size() < 2) {
				return false;
			}
			else {
				int[] ai = new int[256];

				int j;
				int i1;
				for (j = 1; j < ai.length; ++j) {
					if (CropCard.idExists(j) && CropCard.getCrop(j).canGrow(this)) {
						for (i1 = 0; i1 < linkedlist.size(); ++i1) {
							ai[j] += this.calculateRatioFor(CropCard.getCrop(j), ((TileEntityCrop) linkedlist.get(i1)).crop());
						}
					}
				}

				j = 0;

				for (i1 = 0; i1 < ai.length; ++i1) {
					j += ai[i1];
				}

				j = mod_IC2.random.nextInt(j);

				for (i1 = 0; i1 < ai.length; ++i1) {
					if (ai[i1] > 0 && ai[i1] > j) {
						j = i1;
						break;
					}

					j -= ai[i1];
				}

				this.upgraded = false;
				this.id = (short) j;
				this.dirty = true;
				this.size = 1;
				this.statGrowth = 0;
				this.statResistance = 0;
				this.statGain = 0;

				for (i1 = 0; i1 < linkedlist.size(); ++i1) {
					this.statGrowth += ((TileEntityCrop) linkedlist.get(i1)).statGrowth;
					this.statResistance += ((TileEntityCrop) linkedlist.get(i1)).statResistance;
					this.statGain += ((TileEntityCrop) linkedlist.get(i1)).statGain;
				}

				i1 = linkedlist.size();
				this.statGrowth = (byte) (this.statGrowth / i1);
				this.statResistance = (byte) (this.statResistance / i1);
				this.statGain = (byte) (this.statGain / i1);
				this.statGrowth = (byte) (this.statGrowth + (mod_IC2.random.nextInt(1 + 2 * i1) - i1));
				if (this.statGrowth < 0) {
					this.statGrowth = 0;
				}

				if (this.statGrowth > 31) {
					this.statGrowth = 31;
				}

				this.statGain = (byte) (this.statGain + (mod_IC2.random.nextInt(1 + 2 * i1) - i1));
				if (this.statGain < 0) {
					this.statGain = 0;
				}

				if (this.statGain > 31) {
					this.statGain = 31;
				}

				this.statResistance = (byte) (this.statResistance + (mod_IC2.random.nextInt(1 + 2 * i1) - i1));
				if (this.statResistance < 0) {
					this.statResistance = 0;
				}

				if (this.statResistance > 31) {
					this.statResistance = 31;
				}

				return true;
			}
		}
	}

	public int calculateRatioFor(CropCard cropcard, CropCard cropcard1) {
		if (cropcard == cropcard1) {
			return 500;
		}
		else {
			int i = 0;
			int j = 0;

			int k;
			while (j < 5) {
				k = cropcard.stat(j) - cropcard1.stat(j);
				if (k < 0) {
					k *= -1;
				}

				switch (k) {
					default:
						--i;
					case 0:
						i += 2;
					case 1:
						++i;
					case 2:
						++j;
				}
			}

			for (k = 0; k < cropcard.attributes().length; ++k) {
				for (int i1 = 0; i1 < cropcard1.attributes().length; ++i1) {
					if (cropcard.attributes()[k].equalsIgnoreCase(cropcard1.attributes()[i1])) {
						i += 5;
					}
				}
			}

			if (cropcard1.tier() < cropcard.tier() - 1) {
				i -= 2 * (cropcard.tier() - cropcard1.tier());
			}

			if (cropcard1.tier() - 3 > cropcard.tier()) {
				i -= cropcard1.tier() - cropcard.tier();
			}

			if (i < 0) {
				i = 0;
			}

			return i;
		}
	}

	public void askCropJoinCross(int i, int j, int k, LinkedList linkedlist) {
		if (this.world.getTileEntity(i, j, k) instanceof TileEntityCrop) {
			TileEntityCrop tileentitycrop = (TileEntityCrop) this.world.getTileEntity(i, j, k);
			if (tileentitycrop.id > 0) {
				if (tileentitycrop.crop().canGrow(this) && tileentitycrop.crop().canCross(tileentitycrop)) {
					int l = 4;
					if (tileentitycrop.statGrowth >= 16) {
						++l;
					}

					if (tileentitycrop.statGrowth >= 30) {
						++l;
					}

					if (tileentitycrop.statResistance >= 28) {
						l += 27 - tileentitycrop.statResistance;
					}

					if (l >= mod_IC2.random.nextInt(20)) {
						linkedlist.add(tileentitycrop);
					}

				}
			}
		}
	}

	public boolean leftclick(EntityHuman entityhuman) {
		Entity bukkitentity = entityhuman.getBukkitEntity();
		if (bukkitentity instanceof Player) {
			Player player = (Player) bukkitentity;
			BlockBreakEvent breakev = new BlockBreakEvent(player.getWorld().getBlockAt(this.x, this.y, this.z), player);
			Bukkit.getPluginManager().callEvent(breakev);
			if (breakev.isCancelled()) {
				return false;
			}

			breakev.setCancelled(true);
		}

		if (this.id < 0) {
			if (this.upgraded) {
				this.upgraded = false;
				this.dirty = true;
				if (Platform.isSimulating()) {
					StackUtil.dropAsEntity(this.world, this.x, this.y, this.z, new ItemStack(Ic2Items.crop.getItem()));
				}

				return true;
			}
			else {
				return false;
			}
		}
		else {
			return this.crop().leftclick(this, entityhuman);
		}
	}

	public boolean pick(boolean flag) {
		if (this.id < 0) {
			return false;
		}
		else {
			boolean flag1 = this.harvest(false);
			float f = this.crop().dropSeedChance(this);

			int j;
			for (j = 0; j < this.statResistance; ++j) {
				f *= 1.1F;
			}

			j = 0;
			int i1;
			if (flag1) {
				if (mod_IC2.random.nextFloat() <= (f + 1.0F) * 0.8F) {
					++j;
				}

				float f1 = this.crop().dropSeedChance(this) + (float) this.statGrowth / 100.0F;
				if (!flag) {
					f1 *= 0.8F;
				}

				for (i1 = 23; i1 < this.statGain; ++i1) {
					f1 *= 0.95F;
				}

				if (mod_IC2.random.nextFloat() <= f1) {
					++j;
				}
			}
			else if (mod_IC2.random.nextFloat() <= f * 1.5F) {
				++j;
			}

			ItemStack[] aitemstack = new ItemStack[j];

			for (i1 = 0; i1 < j; ++i1) {
				aitemstack[i1] = this.crop().getSeeds(this);
			}

			this.reset();
			if (Platform.isSimulating() && aitemstack != null && aitemstack.length > 0) {
				for (i1 = 0; i1 < aitemstack.length; ++i1) {
					if (aitemstack[i1].id != Ic2Items.cropSeed.id) {
						aitemstack[i1].tag = null;
					}

					StackUtil.dropAsEntity(this.world, this.x, this.y, this.z, aitemstack[i1]);
				}
			}

			return true;
		}
	}

	public boolean rightclick(EntityHuman entityhuman) {
		ItemStack itemstack = entityhuman.U();
		Entity bukkitentity = entityhuman.getBukkitEntity();
		if (bukkitentity instanceof Player) {
			Player player = (Player) bukkitentity;
			BlockBreakEvent breakev = new BlockBreakEvent(player.getWorld().getBlockAt(this.x, this.y, this.z), player);
			Bukkit.getPluginManager().callEvent(breakev);
			if (breakev.isCancelled()) {
				return false;
			}

			breakev.setCancelled(true);
		}

		if (itemstack != null) {
			if (this.id < 0) {
				if (itemstack.id == Ic2Items.crop.id && !this.upgraded) {
					if (!entityhuman.abilities.canInstantlyBuild) {
						--itemstack.count;
						if (itemstack.count <= 0) {
							entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
						}
					}

					this.upgraded = true;
					this.dirty = true;
					return true;
				}

				if (this.applyBaseSeed(itemstack, entityhuman)) {
					return true;
				}
			}
			else if (itemstack.id == Ic2Items.cropnalyzer.id) {
				if (Platform.isSimulating()) {
					String s = this.getScanned();
					if (s == null) {
						s = "Unknown Crop";
					}

					Platform.messagePlayer(entityhuman, s);
				}

				return true;
			}

			if (itemstack.id == Item.WATER_BUCKET.id || itemstack.id == Ic2Items.waterCell.getItem().id) {
				if (this.waterStorage < 10) {
					this.waterStorage = 10;
					return true;
				}
				else {
					return itemstack.id == Item.WATER_BUCKET.id;
				}
			}

			if (itemstack.id == Item.SEEDS.id) {
				if (this.nutrientStorage <= 50) {
					this.nutrientStorage += 25;
					--itemstack.count;
					if (itemstack.count <= 0) {
						entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
					}

					return true;
				}

				return false;
			}

			if (itemstack.id == Item.INK_SACK.id && itemstack.getData() == 15 || itemstack.id == Ic2Items.fertilizer.id) {
				if (this.applyFertilizer(true)) {
					--itemstack.count;
					if (itemstack.count <= 0) {
						entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
					}

					return true;
				}

				return false;
			}

			if (itemstack.id == Ic2Items.hydratingCell.id) {
				if (this.applyHydration(true, itemstack)) {
					if (itemstack.count <= 0) {
						entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
					}

					return true;
				}

				return false;
			}

			if (itemstack.id == Ic2Items.weedEx.id && this.applyWeedEx(true)) {
				itemstack.damage(1, entityhuman);
				if (itemstack.getData() >= itemstack.i()) {
					--itemstack.count;
				}

				if (itemstack.count <= 0) {
					entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
				}

				return true;
			}
		}

		if (this.id < 0) {
			return false;
		}
		else {
			return this.crop().rightclick(this, entityhuman);
		}
	}

	public boolean applyBaseSeed(ItemStack itemstack, EntityHuman entityhuman) {
		BaseSeed baseseed = CropCard.getBaseSeed(itemstack);
		if (baseseed != null) {
			if (itemstack.count < baseseed.stackSize) {
				return false;
			}

			if (this.tryPlantIn(baseseed.id, baseseed.size, baseseed.statGrowth, baseseed.statGain, baseseed.statResistance, 1)) {
				if (itemstack.getItem().k()) {
					itemstack.id = itemstack.getItem().j().id;
				}
				else {
					itemstack.count -= baseseed.stackSize;
					if (itemstack.count <= 0) {
						entityhuman.inventory.items[entityhuman.inventory.itemInHandIndex] = null;
					}
				}

				return true;
			}
		}

		return false;
	}

	public boolean tryPlantIn(int i, int j, int k, int l, int i1, int j1) {
		if (this.id <= -1 && i > 0 && !this.upgraded) {
			if (!CropCard.getCrop(i).canGrow(this)) {
				return false;
			}
			else {
				this.reset();
				this.id = (short) i;
				this.size = (byte) j;
				this.statGrowth = (byte) k;
				this.statGain = (byte) l;
				this.statResistance = (byte) i1;
				this.scanLevel = (byte) j1;
				return true;
			}
		}
		else {
			return false;
		}
	}

	public boolean applyFertilizer(boolean flag) {
		if (this.nutrientStorage >= 100) {
			return false;
		}
		else {
			this.nutrientStorage += flag ? 100 : 90;
			return true;
		}
	}

	public boolean applyHydration(boolean flag, ItemStack itemstack) {
		if ((flag || this.waterStorage < 180) && this.waterStorage < 200) {
			int i = flag ? 200 - this.waterStorage : 180 - this.waterStorage;
			if (i + itemstack.getData() > itemstack.i()) {
				i = itemstack.i() - itemstack.getData();
			}

			itemstack.damage(i, null);
			if (itemstack.getData() >= itemstack.i()) {
				--itemstack.count;
			}

			this.waterStorage += i;
			return true;
		}
		else {
			return false;
		}
	}

	public boolean applyWeedEx(boolean flag) {
		if ((this.exStorage < 100 || !flag) && this.exStorage < 150) {
			this.exStorage += 50;
			boolean flag1 = this.world.random.nextInt(3) == 0;
			if (flag) {
				flag1 = this.world.random.nextInt(5) == 0;
			}

			if (this.id > 0 && this.exStorage >= 75 && flag1) {
				switch (this.world.random.nextInt(5)) {
					case 0:
						if (this.statGrowth > 0) {
							--this.statGrowth;
						}
					case 1:
						if (this.statGain > 0) {
							--this.statGain;
						}
					default:
						if (this.statResistance > 0) {
							--this.statResistance;
						}
				}
			}

			return true;
		}
		else {
			return false;
		}
	}

	public boolean harvest(boolean flag) {
		if (this.id >= 0 && this.crop().canBeHarvested(this)) {
			float f = this.crop().dropGainChance();

			int j;
			for (j = 0; j < this.statGain; ++j) {
				f *= 1.03F;
			}

			f -= mod_IC2.random.nextFloat();

			for (j = 0; f > 0.0F; f -= mod_IC2.random.nextFloat()) {
				++j;
			}

			ItemStack[] aitemstack = new ItemStack[j];

			int l;
			for (l = 0; l < j; ++l) {
				aitemstack[l] = this.crop().getGain(this);
				if (aitemstack[l] != null && mod_IC2.random.nextInt(100) <= this.statGain) {
					++aitemstack[l].count;
				}
			}

			this.size = this.crop().getSizeAfterHarvest(this);
			this.dirty = true;
			if (Platform.isSimulating() && aitemstack != null && aitemstack.length > 0) {
				for (l = 0; l < aitemstack.length; ++l) {
					StackUtil.dropAsEntity(this.world, this.x, this.y, this.z, aitemstack[l]);
				}
			}

			return true;
		}
		else {
			return false;
		}
	}

	public void onNeighbourChange() {
		if (this.id >= 0) {
			this.crop().onNeighbourChange(this);
		}
	}

	public boolean emitRedstone() {
		return this.id >= 0 && this.crop().emitRedstone(this);
	}

	public void onBlockDestroyed() {
		if (this.id >= 0) {
			this.crop().onBlockDestroyed(this);
		}
	}

	public int getEmittedLight() {
		return this.id < 0 ? 0 : this.crop().getEmittedLight(this);
	}

	public byte getHumidity() {
		if (this.humidity == -1) {
			this.humidity = this.updateHumidity();
		}

		return this.humidity;
	}

	public byte getNutrients() {
		if (this.nutrients == -1) {
			this.nutrients = this.updateNutrients();
		}

		return this.nutrients;
	}

	public byte getAirQuality() {
		if (this.airQuality == -1) {
			this.airQuality = this.updateAirQuality();
		}

		return this.airQuality;
	}

	public byte updateHumidity() {
		int i = 0;
		BiomeBase biomebase = this.world.getWorldChunkManager().getBiome(this.x, this.z);
		if (biomebase instanceof BiomeRiver || biomebase instanceof BiomeSwamp) {
			i += 2;
		}

		if (biomebase instanceof BiomeForest || biomebase instanceof BiomeJungle) {
			++i;
		}

		if (biomebase instanceof BiomeDesert) {
			--i;
		}

		if (this.world.getData(this.x, this.y - 1, this.z) >= 7) {
			i += 2;
		}

		if (this.waterStorage >= 5) {
			i += 2;
		}

		i += (this.waterStorage + 24) / 25;
		return (byte) i;
	}

	public byte updateNutrients() {
		int i = 0;
		BiomeBase biomebase = this.world.getWorldChunkManager().getBiome(this.x, this.z);
		if (biomebase instanceof BiomeSwamp || biomebase instanceof BiomeMushrooms || biomebase instanceof BiomeJungle) {
			i += 2;
		}

		if (biomebase instanceof BiomeForest) {
			++i;
		}

		for (int j = 2; j < 5 && this.world.getTypeId(this.x, this.y - j, this.z) == Block.DIRT.id; ++j) {
			++i;
		}

		i += (this.nutrientStorage + 19) / 20;
		return (byte) i;
	}

	public byte updateAirQuality() {
		int i = 0;
		int j = (this.y - 64) / 15;
		if (j > 4) {
			j = 4;
		}

		if (j < 0) {
			j = 0;
		}

		i = i + j; //Removed int declaration: Already declared.
		int k = 9;

		for (int l = this.x - 1; l < this.x + 1 && k > 0; ++l) {
			for (int i1 = this.z - 1; i1 < this.z + 1 && k > 0; ++i1) {
				if (this.world.r(l, this.y, i1) || this.world.getTileEntity(l, this.y, i1) instanceof TileEntityCrop) {
					--k;
				}
			}
		}

		i += k / 2;
		if (this.world.isChunkLoaded(this.x, this.y + 1, this.z)) {
			i += 2;
		}

		return (byte) i;
	}

	public byte updateMultiCulture() {
		LinkedList linkedlist = new LinkedList();

		for (int i = -1; i < 1; ++i) {
			for (int j = -1; j < 1; ++j) {
				if (this.world.getTileEntity(i + this.x, this.y, j + this.z) instanceof TileEntityCrop) {
					this.addIfNotPresent(((TileEntityCrop) this.world.getTileEntity(i + this.x, this.y, j + this.z)).crop(), linkedlist);
				}
			}
		}

		return (byte) (linkedlist.size() - 1);
	}

	public void addIfNotPresent(CropCard cropcard, LinkedList linkedlist) {
		for (int i = 0; i < linkedlist.size(); ++i) {
			if (cropcard == linkedlist.get(i)) {
				return;
			}
		}

		linkedlist.add(cropcard);
	}

	public int calcGrowthRate() {
		int i = 3 + mod_IC2.random.nextInt(7) + this.statGrowth;
		int j = (this.crop().tier() - 1) * 4 + this.statGrowth + this.statGain + this.statResistance;
		if (j < 0) {
			j = 0;
		}

		int k = this.crop().weightInfluences(this, (float) this.getHumidity(), (float) this.getNutrients(), (float) this.getAirQuality()) * 5;
		if (k >= j) {
			i = i * (100 + (k - j)) / 100;
		}
		else {
			int l = (j - k) * 4;
			if (l > 100 && mod_IC2.random.nextInt(32) > this.statResistance) {
				this.reset();
				i = 0;
			}
			else {
				i = i * (100 - l) / 100;
				if (i < 0) {
					i = 0;
				}
			}
		}

		return i;
	}

	public void calcTrampling() {
		if (Platform.isSimulating()) {
			if (mod_IC2.random.nextInt(100) == 0 && mod_IC2.random.nextInt(40) > this.statResistance) {
				this.reset();
				this.world.setTypeIdAndData(this.x, this.y - 1, this.z, Block.DIRT.id, 0);
			}

		}
	}

	public CropCard crop() {
		return CropCard.getCrop(this.id);
	}

	public int getSprite() {
		if (this.id < 0) {
			return this.upgraded ? 1 : 0;
		}
		else {
			return this.crop().getSpriteIndex(this);
		}
	}

	public void onEntityCollision(net.minecraft.server.Entity entity) {
		if (this.id >= 0) {
			if (this.crop().onEntityCollision(this, entity)) {
				this.calcTrampling();
			}

		}
	}

	public void reset() {
		this.id = -1;
		this.size = 0;
		this.custumData = new short[16];
		this.dirty = true;
		this.statGain = 0;
		this.statResistance = 0;
		this.statGrowth = 0;
		this.nutrients = -1;
		this.airQuality = -1;
		this.humidity = -1;
		this.growthPoints = 0;
		this.upgraded = false;
		this.scanLevel = 0;
	}

	public void updateState() {
		this.dirty = true;
	}

	public String getScanned() {
		if (this.scanLevel > 0 && this.id >= 0) {
			return this.scanLevel >= 4 ? this.crop().name() + " - Gr: " + this.statGrowth + " Ga: " + this.statGain + " Re: " + this.statResistance : this.crop().name();
		}
		else {
			return null;
		}
	}

	public boolean isBlockBelow(Block block) {
		for (int i = 1; i < 4; ++i) {
			int j = this.world.getTypeId(this.x, this.y - i, this.z);
			if (j == 0) {
				return false;
			}

			if (Block.byId[j] == block) {
				return true;
			}
		}

		return false;
	}

	public ItemStack generateSeeds(short word0, byte byte0, byte byte1, byte byte2, byte byte3) {
		return ItemCropSeed.generateItemStackFromValues(word0, byte0, byte1, byte2, byte3);
	}

	public void addLocal(String s, String s1) {
		Platform.AddLocalization(s, s1);
	}

	public void onNetworkUpdate(String s) {
		this.dirty = true;
	}
}
