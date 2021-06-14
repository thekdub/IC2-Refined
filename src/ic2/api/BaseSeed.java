package ic2.api;

public class BaseSeed {
  public int id;
  public int size;
  public int statGrowth;
  public int statGain;
  public int statResistance;
  public int stackSize;
  
  public BaseSeed(int i, int j, int k, int l, int i1, int j1) {
    this.id = i;
    this.size = j;
    this.statGrowth = k;
    this.statGain = l;
    this.statResistance = i1;
    this.stackSize = j1;
  }
}
