package ic2.common;

public class TileEntityTransformerHV extends TileEntityTransformer {
//  public TileEntityTransformerHV() {
//    super(512, 2048, 4096);
//  }
// 2020-08-25 - maxStorage increased 128x to allow for greater throughput
public TileEntityTransformerHV() {
  super(512, 2048, 4096 * 128);
}
}
