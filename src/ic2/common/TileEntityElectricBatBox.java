package ic2.common;

public class TileEntityElectricBatBox extends TileEntityElectricBlock {
  public TileEntityElectricBatBox() {
    super(1, 32, 40000);
  }
  
  public String getName() {
    return "BatBox";
  }
}
