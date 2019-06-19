package ic2.common;

public class TileEntityElectricMFE extends TileEntityElectricBlock {
  public TileEntityElectricMFE() {
    super(2, 128, 600000);
  }

  public String getName() {
    return "MFE";
  }
}
