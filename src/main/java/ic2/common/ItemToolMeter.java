package ic2.common;

import ic2.api.IEnergyConductor;
import ic2.api.IEnergySink;
import ic2.api.IEnergySource;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.text.DecimalFormat;

public class ItemToolMeter extends ItemIC2 {

  /* NBT keys for packet‑statistics measurement. */
  private static final String KEY_POWER_TIME          = "lastPowerMeasureTime";
  private static final String KEY_PACKET_TIME         = "lastPacketMeasureTime";
  private static final String KEY_PACKET_COUNT        = "lastPacketCount";
  private static final String KEY_PACKET_SIZE         = "lastTotalPacketSize";
  private static final String KEY_MIN_PACKET_SIZE     = "lastMinPacketSize";
  private static final String KEY_MAX_PACKET_SIZE     = "lastMaxPacketSize";
  private static final String KEY_TOTAL_CONDUCTED     = "lastTotalConducted";

  /* Voltage tier boundaries – the value is *inclusive* at the lower bound
   * and *exclusive* at the upper bound. */
  private static final long ULV_MAX = 6;
  private static final long LV_MAX  = 33;
  private static final long MV_MAX  = 129;
  private static final long HV_MAX = 513;
  private static final long EV_MAX  = 2049;
  private static final long UEV_MAX = 9001;

  public ItemToolMeter(int i, int j) {
    super(i, j);
    this.maxStackSize = 1;
    this.setMaxDurability(0);
  }

  @Override
  public boolean onItemUseFirst(ItemStack itemstack, EntityHuman entityhuman,
                                World world, int i, int j, int k, int l) {

    TileEntity tileentity = world.getTileEntity(i, j, k);
    if (!(tileentity instanceof IEnergySource) &&
        !(tileentity instanceof IEnergyConductor) &&
        !(tileentity instanceof IEnergySink)) {
      return false;   // Not an energy‑related block – nothing to measure
    }

    /* No action in client world – only server side produces messages. */
    if (!Platform.isSimulating()) {
      return true;
    }

    NBTTagCompound nb = StackUtil.getOrCreateNbtData(itemstack);
    long worldTime   = world.getTime();

    /* ==================== POWER MEASUREMENT ==================== */
    long totalConducted = EnergyNet.getForWorld(world)
        .getTotalEnergyConducted(tileentity);

    boolean sameTileForPower = nb.getInt("lastMeasuredTileEntityX") == i &&
        nb.getInt("lastMeasuredTileEntityY") == j &&
        nb.getInt("lastMeasuredTileEntityZ") == k;

    if (sameTileForPower) {
      long deltaTicks = worldTime - nb.getLong(KEY_POWER_TIME);
      if (deltaTicks < 1L) deltaTicks = 1L;

      double powerRate = (double) (totalConducted - nb.getLong(KEY_TOTAL_CONDUCTED))
          / (double) deltaTicks;

      DecimalFormat dfPower = new DecimalFormat("0.##");
      Platform.messagePlayer(entityhuman,
          "§aMeasured power: §f" + dfPower.format(powerRate) +
              " EU/t §8(§7avg. over " + deltaTicks + " ticks§8)");
    } else {
      nb.setInt("lastMeasuredTileEntityX", i);
      nb.setInt("lastMeasuredTileEntityY", j);
      nb.setInt("lastMeasuredTileEntityZ", k);
      Platform.messagePlayer(entityhuman, " ! Starting new measurements !");
    }

    nb.setLong(KEY_TOTAL_CONDUCTED, totalConducted);
    nb.setLong(KEY_POWER_TIME, worldTime);

    /* ==================== PACKET MEASUREMENT ==================== */
    EnergyNet.EnergyPacketStats stats =
        EnergyNet.getForWorld(world).getEnergyPacketStats(tileentity);

		if (!sameTileForPower) {
      // First interaction with this block – reset packet timers
      nb.setLong(KEY_PACKET_TIME, worldTime);
      nb.setLong(KEY_PACKET_COUNT, 0L);
      nb.setLong(KEY_PACKET_SIZE, 0L);
      nb.setInt(KEY_MIN_PACKET_SIZE, Integer.MAX_VALUE);
      nb.setInt(KEY_MAX_PACKET_SIZE, Integer.MIN_VALUE);
    }

    long packetDeltaT = worldTime - nb.getLong(KEY_PACKET_TIME);
    if (packetDeltaT < 1L) packetDeltaT = 1L;

    long prevPacketCnt   = nb.getLong(KEY_PACKET_COUNT);
    long prevPacketSize  = nb.getLong(KEY_PACKET_SIZE);
    int  prevMinPktSize  = nb.getInt(KEY_MIN_PACKET_SIZE);
    int  prevMaxPktSize  = nb.getInt(KEY_MAX_PACKET_SIZE);

    long deltaPackets = stats.totalPackets - prevPacketCnt;
    long deltaSize    = stats.totalPacketSize - prevPacketSize;
    int  deltaMin     = stats.minPacketSize == Integer.MAX_VALUE ? 0 : stats.minPacketSize;
    int  deltaMax     = stats.maxPacketSize == Integer.MIN_VALUE ? 0 : stats.maxPacketSize;

    if (deltaPackets < 0) deltaPackets = 0;
    if (deltaSize    < 0) deltaSize    = 0;

    double avgPktPerTick = (double) deltaPackets / (double) packetDeltaT;
    double avgPktSize    = deltaPackets == 0 ? 0.0
        : (double) deltaSize / (double) deltaPackets;

    /* Helper: get tier string for a numeric EU size. */
    String avgSizeTier   = getTierFromEU((long) avgPktSize);
    String minSizeTier   = getTierFromEU(deltaMin);
    String maxSizeTier   = getTierFromEU(deltaMax);

    if (avgPktSize == 0.0) {
      /* Reset the stored packet statistics – there is nothing to accumulate. */
      nb.setLong(KEY_PACKET_SIZE,      0L);
      nb.setLong(KEY_PACKET_COUNT,     0L);
      nb.setInt(KEY_MIN_PACKET_SIZE,   Integer.MAX_VALUE);
      nb.setInt(KEY_MAX_PACKET_SIZE,   Integer.MIN_VALUE);
      deltaMin = 0;
      deltaMax = 0;
      avgSizeTier = "N/A";
    }

    DecimalFormat dfPkt = new DecimalFormat("0.##");
    if (sameTileForPower) {          // send only when already measuring
      String packetMsg = String.format(
          "§bAvg pkt/t: §f%.2f §7| §bAvg EU: §f%.2f §8[%s%s§8] §7(§bMin: §f%d §8[%s%s§8], §bMax: §f%d §8[%s%s§8]§7)",
          avgPktPerTick,
          avgPktSize,
          tierColor(avgSizeTier), avgSizeTier,
          deltaMin,
          tierColor(minSizeTier), minSizeTier,
          deltaMax,
          tierColor(maxSizeTier), maxSizeTier);

      Platform.messagePlayer(entityhuman, packetMsg);
    }

    /* Persist latest packet stats for the next cycle */
    nb.setLong(KEY_PACKET_TIME, worldTime);
    nb.setLong(KEY_PACKET_COUNT, stats.totalPackets);
    nb.setLong(KEY_PACKET_SIZE,  stats.totalPacketSize);
    nb.setInt(KEY_MIN_PACKET_SIZE, stats.minPacketSize);
    nb.setInt(KEY_MAX_PACKET_SIZE, stats.maxPacketSize);

    return true;
  }

  private static String getTierFromEU(long eu) {
    if (eu == 0L) return "N/A";
    if (eu < ULV_MAX) return "ULV";
    if (eu < LV_MAX)  return "LV";
    if (eu < MV_MAX)  return "MV";
    if (eu < HV_MAX)  return "HV";
    if (eu < EV_MAX)  return "EV";
    if (eu < UEV_MAX) return "UEV";
    return "UNKNOWN";
  }

  /* Update the color‑mapping to handle the new tier */
  private static String tierColor(String tier) {
    switch (tier) {
      case "ULV": return "§f";
      case "LV":  return "§a";
      case "MV":  return "§e";
      case "HV":  return "§c";
      case "EV":  return "§d";
      case "UEV": return "§5";
      case "N/A": return "§8";
      default:    return "§f";
    }
  }
}