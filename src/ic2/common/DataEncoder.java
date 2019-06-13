package ic2.common;

import forge.DimensionManager;
import ic2.platform.Platform;
import net.minecraft.server.*;

import java.io.*;

public class DataEncoder {
	public static Object decode(DataInputStream datainputstream) throws IOException {
		byte byte0 = datainputstream.readByte();
		switch (byte0) {
			case 0:
				return datainputstream.readInt();
			case 1:
				short word0 = datainputstream.readShort();
				int[] ai = new int[word0];

				for (int j1 = 0; j1 < word0; ++j1) {
					ai[j1] = datainputstream.readInt();
				}

				return ai;
			case 2:
				return datainputstream.readShort();
			case 3:
				short word1 = datainputstream.readShort();
				short[] aword0 = new short[word1];

				for (int k1 = 0; k1 < word1; ++k1) {
					aword0[k1] = datainputstream.readShort();
				}

				return aword0;
			case 4:
				return datainputstream.readByte();
			case 5:
				short word2 = datainputstream.readShort();
				byte[] abyte0 = new byte[word2];

				for (int l1 = 0; l1 < word2; ++l1) {
					abyte0[l1] = datainputstream.readByte();
				}

				return abyte0;
			case 6:
				return datainputstream.readLong();
			case 7:
				short word3 = datainputstream.readShort();
				long[] al = new long[word3];

				for (int i2 = 0; i2 < word3; ++i2) {
					al[i2] = datainputstream.readLong();
				}

				return al;
			case 8:
				return datainputstream.readBoolean();
			case 9:
				short word4 = datainputstream.readShort();
				boolean[] aflag = new boolean[word4];
				byte byte4 = 0;

				for (int j3 = 0; j3 < word4; ++j3) {
					if (j3 % 8 == 0) {
						byte4 = datainputstream.readByte();
					}

					aflag[j3] = (byte4 & 1 << j3 % 8) != 0;
				}

				return aflag;
			case 10:
				short word5 = datainputstream.readShort();
				char[] ac = new char[word5];

				for (int j2 = 0; j2 < word5; ++j2) {
					ac[j2] = datainputstream.readChar();
				}

				return new String(ac);
			case 11:
				short word6 = datainputstream.readShort();
				String[] as = new String[word6];

				for (int k2 = 0; k2 < word6; ++k2) {
					short word9 = datainputstream.readShort();
					char[] ac1 = new char[word9];

					for (int i4 = 0; i4 < word9; ++i4) {
						ac1[i4] = datainputstream.readChar();
					}

					as[k2] = new String(ac1);
				}

				return as;
			case 12:
				short word7 = datainputstream.readShort();
				if (word7 == 0) {
					return null;
				}

				byte byte3 = datainputstream.readByte();
				short word8 = datainputstream.readShort();
				ItemStack itemstack = new ItemStack(word7, byte3, word8);
				if (Item.byId[word7].g() || Platform.unknown1(Item.byId[word7])) {
					itemstack.setTag(NBTCompressedStreamTools.a((DataInput)datainputstream)); //Could be DataInput or InputStream
				}

				return itemstack;
			case 13:
				return NBTBase.b(datainputstream);
			case 14:
				byte byte1 = datainputstream.readByte();
				int k = datainputstream.readInt();
				switch (byte1) {
					case 0:
						return Block.byId[k];
					case 1:
						return Item.byId[k];
					case 2:
						return AchievementList.e.get(k);
					case 3:
						return MobEffectList.byId[k];
					case 4:
						return Enchantment.byId[k];
				}
			case 15:
				byte byte2 = datainputstream.readByte();
				int l = datainputstream.readInt();
				int l2 = 0;
				if (byte2 == 1) {
					l2 = datainputstream.readInt();
				}

				int k3 = datainputstream.readInt();
				switch (byte2) {
					case 0:
						return new ChunkCoordIntPair(l, k3);
					case 1:
						return new ChunkCoordinates(l, l2, k3);
				}
			case 16:
				int i = datainputstream.readInt();
				int i1 = datainputstream.readInt();
				int i3 = datainputstream.readInt();
				int l3 = datainputstream.readInt();
				return DimensionManager.getWorld(i).getTileEntity(i1, i3, l3);
			case 17:
				int j = datainputstream.readInt();
				return DimensionManager.getWorld(j);
			case 127:
				return null;
			default:
				Platform.displayError("An unknown data type was received over multiplayer to be decoded.\nThis could happen due to corrupted data or a bug.\n\n(Technical information: type ID " + byte0 + ")");
				return null;
		}
	}

	public static void encode(DataOutputStream dataoutputstream, Object obj) throws IOException {
		if (obj instanceof Integer) {
			dataoutputstream.writeByte(0);
			dataoutputstream.writeInt((Integer) obj);
		}
		else {
			int i1;
			if (obj instanceof int[]) {
				dataoutputstream.writeByte(1);
				int[] ai = (int[]) obj;
				dataoutputstream.writeShort(ai.length);

				for (i1 = 0; i1 < ai.length; ++i1) {
					dataoutputstream.writeInt(ai[i1]);
				}
			}
			else if (obj instanceof Short) {
				dataoutputstream.writeByte(2);
				dataoutputstream.writeShort((Short) obj);
			}
			else if (obj instanceof short[]) {
				dataoutputstream.writeByte(3);
				short[] aword0 = (short[]) obj;
				dataoutputstream.writeShort(aword0.length);

				for (i1 = 0; i1 < aword0.length; ++i1) {
					dataoutputstream.writeShort(aword0[i1]);
				}
			}
			else if (obj instanceof Byte) {
				dataoutputstream.writeByte(4);
				dataoutputstream.writeByte((Byte) obj);
			}
			else if (obj instanceof byte[]) {
				dataoutputstream.writeByte(5);
				byte[] abyte0 = (byte[]) obj;
				dataoutputstream.writeShort(abyte0.length);

				for (i1 = 0; i1 < abyte0.length; ++i1) {
					dataoutputstream.writeByte(abyte0[i1]);
				}
			}
			else if (obj instanceof Long) {
				dataoutputstream.writeByte(6);
				dataoutputstream.writeLong((Long) obj);
			}
			else if (obj instanceof long[]) {
				dataoutputstream.writeByte(7);
				long[] al = (long[]) obj;
				dataoutputstream.writeShort(al.length);

				for (i1 = 0; i1 < al.length; ++i1) {
					dataoutputstream.writeLong(al[i1]);
				}
			}
			else if (obj instanceof Boolean) {
				dataoutputstream.writeByte(8);
				dataoutputstream.writeBoolean((Boolean) obj);
			}
			else if (obj instanceof boolean[]) {
				dataoutputstream.writeByte(9);
				boolean[] aflag = (boolean[]) obj;
				dataoutputstream.writeShort(aflag.length);
				byte byte0 = 0;

				for (int j1 = 0; j1 < aflag.length; ++j1) {
					if (j1 % 8 == 0 && j1 > 0) {
						dataoutputstream.writeByte(byte0);
						byte0 = 0;
					}

					byte0 = (byte) (byte0 | (aflag[j1] ? 1 : 0) << j1 % 8);
				}

				dataoutputstream.writeByte(byte0);
			}
			else if (obj instanceof String) {
				dataoutputstream.writeByte(10);
				String s = (String) obj;
				dataoutputstream.writeShort(s.length());
				dataoutputstream.writeChars(s);
			}
			else if (obj instanceof String[]) {
				dataoutputstream.writeByte(11);
				String[] as = (String[]) obj;
				dataoutputstream.writeShort(as.length);

				for (i1 = 0; i1 < as.length; ++i1) {
					dataoutputstream.writeShort(as[i1].length());
					dataoutputstream.writeChars(as[i1]);
				}
			}
			else if (obj instanceof ItemStack) {
				dataoutputstream.writeByte(12);
				ItemStack itemstack = (ItemStack) obj;
				dataoutputstream.writeShort(itemstack.id);
				if (itemstack.id == 0) {
					return;
				}

				dataoutputstream.writeByte(itemstack.count);
				dataoutputstream.writeShort(itemstack.getData());
				if (Item.byId[itemstack.id].g() || Platform.unknown1(Item.byId[itemstack.id])) {
					NBTTagCompound nbttagcompound = itemstack.getTag();
					if (nbttagcompound != null) {
						NBTCompressedStreamTools.a(nbttagcompound, (DataOutput) dataoutputstream); //Could be DataOutput or OutputStream
					}
				}
			}
			else if (obj instanceof NBTBase) {
				dataoutputstream.writeByte(13);
				NBTBase.a((NBTBase) obj, dataoutputstream);
			}
			else if (obj instanceof Block) {
				dataoutputstream.writeByte(14);
				dataoutputstream.writeByte(0);
				dataoutputstream.writeInt(((Block) obj).id);
			}
			else if (obj instanceof Item) {
				dataoutputstream.writeByte(14);
				dataoutputstream.writeByte(1);
				dataoutputstream.writeInt(((Item) obj).id);
			}
			else if (obj instanceof Achievement) {
				dataoutputstream.writeByte(14);
				dataoutputstream.writeByte(2);
				dataoutputstream.writeInt(((Achievement) obj).e);
			}
			else if (obj instanceof MobEffectList) {
				dataoutputstream.writeByte(14);
				dataoutputstream.writeByte(3);
				dataoutputstream.writeInt(((MobEffectList) obj).id);
			}
			else if (obj instanceof Enchantment) {
				dataoutputstream.writeByte(14);
				dataoutputstream.writeByte(4);
				dataoutputstream.writeInt(((Enchantment) obj).id);
			}
			else if (obj instanceof ChunkCoordinates) {
				dataoutputstream.writeByte(15);
				dataoutputstream.writeByte(0);
				ChunkCoordinates chunkcoordinates = (ChunkCoordinates) obj;
				dataoutputstream.writeInt(chunkcoordinates.x);
				dataoutputstream.writeInt(chunkcoordinates.y);
				dataoutputstream.writeInt(chunkcoordinates.z);
			}
			else if (obj instanceof ChunkCoordIntPair) {
				dataoutputstream.writeByte(15);
				dataoutputstream.writeByte(1);
				ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) obj;
				dataoutputstream.writeInt(chunkcoordintpair.x);
				dataoutputstream.writeInt(chunkcoordintpair.b() - 8 >> 4);
			}
			else if (obj instanceof TileEntity) {
				dataoutputstream.writeByte(16);
				TileEntity tileentity = (TileEntity) obj;
				dataoutputstream.writeInt(tileentity.world.worldProvider.dimension);
				dataoutputstream.writeInt(tileentity.x);
				dataoutputstream.writeInt(tileentity.y);
				dataoutputstream.writeInt(tileentity.z);
			}
			else if (obj instanceof World) {
				dataoutputstream.writeByte(17);
				dataoutputstream.writeInt(((World) obj).worldProvider.dimension);
			}
			else if (obj == null) {
				dataoutputstream.writeByte(127);
			}
			else {
				Platform.displayError("An unknown data type was attempted to be encoded for sending through\nmultiplayer.\nThis could happen due to a bug.\n\n(Technical information: " + obj.getClass().getName() + ")");
			}
		}

	}
}
