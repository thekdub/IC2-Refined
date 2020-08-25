package ic2.common;

public class TileEntityTransformerMV extends TileEntityTransformer {
//  public TileEntityTransformerMV() {
//    super(128, 512, 1024);
//  }
// 2020-08-25 - maxStorage increased 128x to allow for greater throughput
public TileEntityTransformerMV() {
  super(128, 512, 1024 * 128);
}
}
