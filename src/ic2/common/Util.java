package ic2.common;

public final class Util {
  public static int roundToNegInf(float f) {
    int i = (int) f;
    if ((float) i > f) {
      --i;
    }

    return i;
  }

  public static int roundToNegInf(double d) {
    int i = (int) d;
    if ((double) i > d) {
      --i;
    }

    return i;
  }

  public static int countInArray(Object[] aobj, Class class1) {
    int i = 0;
    Object[] aobj1 = aobj;
    int j = aobj.length;

    for (int k = 0; k < j; ++k) {
      Object obj = aobj1[k];
      if (class1.isAssignableFrom(obj.getClass())) {
        ++i;
      }
    }

    return i;
  }
}
