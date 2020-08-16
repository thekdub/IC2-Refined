package ic2.common;

public class TileEntityElectricMFSU extends TileEntityElectricBlock {
  public TileEntityElectricMFSU() {
    super(3, 512, 10000000);
  }
  
  public String getName() {
    return "MFSU";
  }
}
