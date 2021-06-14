package ic2.common;

public class TileEntityTransformerLV extends TileEntityTransformer {
//  public TileEntityTransformerLV() {
//    super(32, 128, 256);
//  }
// 2020-08-25 - maxStorage increased 128x to allow for greater throughput
public TileEntityTransformerLV() {
  super(32, 128, 256 * 128);
}
}
