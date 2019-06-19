package ic2.common;

import ic2.platform.Platform;
import net.minecraft.server.*;

import java.lang.reflect.Field;

public class ItemDebug extends ItemIC2 {
  public ItemDebug(int i) {
    super(i, 47);
    this.a(false);
    Ic2Items.debug = new ItemStack(this, 1, 0);
  }

  private static void dumpObjectFields(Object obj) {
    Class class1 = obj.getClass();

    do {
      Field[] afield = class1.getDeclaredFields();
      Field[] afield1 = afield;
      int i = afield.length;

      for (int j = 0; j < i; ++j) {
        Field field = afield1[j];
        boolean flag = field.isAccessible();
        field.setAccessible(true);

        try {
          System.out.println("name: " + class1.getName() + "." + field.getName() + " type: " + field.getType() + " value: " + field.get(obj));
        } catch (IllegalAccessException var9) {
          System.out.println("name: " + class1.getName() + "." + field.getName() + " type: " + field.getType() + " value: <can't access>");
        }

        field.setAccessible(flag);
      }

      class1 = class1.getSuperclass();
    } while (class1 != null);

  }

  public String a(ItemStack itemstack) {
    switch (itemstack.getData()) {
      case 0:
        return "debugItem";
      default:
        return null;
    }
  }

  public boolean onItemUseFirst(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l) {
    int i1 = world.getTypeId(i, j, k);
    TileEntity tileentity = world.getTileEntity(i, j, k);
    System.out.println("[IC2] Debug item output:");
    String s2 = Platform.isRendering() ? (Platform.isSimulating() ? "sp" : "client") : "server";
    String s;
    if (i1 < Block.byId.length && Block.byId[i1] != null) {
      s = "[" + s2 + "] id: " + i1 + " name: " + Block.byId[i1].q() + " te: " + tileentity;
    }
    else {
      s = "[" + s2 + "] id: " + i1 + " name: null te: " + tileentity;
    }

    Platform.messagePlayer(entityhuman, s);
    System.out.println(s);
    if (tileentity != null) {
      String s1 = "[" + s2 + "] interfaces:";
      Class class1 = tileentity.getClass();

      do {
        Class[] aclass = class1.getInterfaces();
        int j1 = aclass.length;

        for (int k1 = 0; k1 < j1; ++k1) {
          Class class2 = aclass[k1];
          s1 = s1 + " " + class2.getName();
        }

        class1 = class1.getSuperclass();
      } while (class1 != null);

      Platform.messagePlayer(entityhuman, s1);
      System.out.println(s1);
    }

    if (i1 < Block.byId.length && Block.byId[i1] != null) {
      System.out.println("block fields:");
      dumpObjectFields(Block.byId[i1]);
    }

    if (tileentity != null) {
      System.out.println("tile entity fields:");
      dumpObjectFields(tileentity);
    }

    return true;
  }
}
