package ic2.api;

import net.minecraft.server.TileEntity;

public enum Direction {
  XN(0),
  XP(1),
  YN(2),
  YP(3),
  ZN(4),
  ZP(5);

  private int dir;

  Direction(int j) {
    this.dir = j;
  }

  public TileEntity applyToTileEntity(TileEntity tileentity) {
    int[] ai = new int[]{tileentity.x, tileentity.y, tileentity.z};
    int var10001 = this.dir / 2;
    ai[var10001] += this.getSign();
    return tileentity.world != null && tileentity.world.isLoaded(ai[0], ai[1], ai[2]) ? tileentity.world.getTileEntity(ai[0], ai[1], ai[2]) : null;
  }

  public Direction getInverse() {
    int i = this.dir - this.getSign();
    Direction[] adirection = values();
    int j = adirection.length;

    for (int k = 0; k < j; ++k) {
      Direction direction = adirection[k];
      if (direction.dir == i) {
        return direction;
      }
    }

    return this;
  }

  public int toSideValue() {
    return (this.dir + 4) % 6;
  }

  private int getSign() {
    return this.dir % 2 * 2 - 1;
  }
}
