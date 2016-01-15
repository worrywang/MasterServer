package com.mars.masterserver.net.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Administrator on 2015/12/10.
 */
public class StreamUtils {
	volatile int size;
	volatile int readpos;
	volatile int writepos;
	volatile byte[] buffer;

	public StreamUtils(){
		buffer =  new byte[10];
		size = 0;
		readpos = 0;
		writepos = 0;
	}

	public StreamUtils(byte[] in){
		buffer = new byte[in.length];
		System.arraycopy(in,0,buffer,0,in.length);
		size = in.length;
		readpos = 0;
		writepos = 0;
	}

	public int waitData(int size){
		return 0;
	}

	public void resetRead(){ readpos = 0;}

	public void resetWrite(){writepos = 0;}

	/**
	 * 跳过
	 * @param _length
	 */
	public void skip(int _length){
		if(readpos+_length>size){
			_length = size-readpos;
		}
		readpos+=_length;
		if(readpos<0){
			readpos = 0;
		}
	}

	/**
	 * 读数据
	 * @return
	 */
	public int read(){
		if(readpos>=size){
			if(waitData(1)==0)
				return -1;
		}
		if(readpos<size){
			return buffer[readpos++];
		}
		return -1;
	}

	public int readInt(){
		return (int)_readData(4);
	}

	public short readShort() {
		return (short) _readData(2);
	}

	public long readLong() {
		return (long) _readData(8);
	}

	public long _readData(int aLength){
		int av = available();
		if(aLength>av){
			if(waitData(aLength-av)==0) return -1;
		}
		if(aLength>8) return -1;
		long ret = 0;
		int temp =0;
		while(temp<aLength*8){
			ret|=((read()&0xFF)<<temp);
			temp+=8;
		}
		return ret;
	}

	public byte[] getBytesM(){
		byte[] data = new byte[size];
		System.arraycopy(buffer,0,data,0,size);
		return data;
	}

	public byte readByte() {
		return (byte) _readData(1);
	}

	public int readBytes(byte[] data) {
		return readBytes(data, 0, data.length);
	}

	public int readBytes(byte[] data, int offset, int length) {
		if (data != null) {
			if (readpos >= buffer.length) {
				if (waitData(1) == -1)
					return -1;
				return 0;
			}
			int l = length;
			int av = available();
			if (l > av) {
				l = av;
			}
			System.arraycopy(buffer, readpos, data, offset, l);
			readpos += l;
			return l;
		}
		return 0;
	}

	/**
	 * 写数据
	 * @param aValue
	 */
	public void writeInt(int aValue){
		_writeData(aValue,4);
	}

	public void writeShort(short aValue) {
		_writeData(aValue, 2);
	}

	public void writeLong(long aValue) {
		_writeData(aValue, 8);
	}
	public void _writeData(long aValue,int aLength){
		while (writepos+aLength>buffer.length){
			_expand(writepos+aLength+100);
		}
		int temp = 0;
		while(temp<aLength*8){
			buffer[writepos++]=(byte)((aValue>>temp)&0xFF);
			temp+=8;
		}
		size+=aLength;
	}

	public void write(byte aValue){
		if (writepos + 1 > buffer.length) {
			_expand(buffer.length + 100);
		}
		buffer[writepos] = aValue;
		writepos++;
		size++;
	}

	public void write(byte[] aValue){
		if(aValue==null) return;
		write(aValue,0,aValue.length);
	}

	public void write(byte[] aValue,int aFrom,int aLength){
		if(aValue==null) return;
		if(aLength+aFrom>aValue.length){
			aLength = aValue.length-aFrom;
		}
		while (writepos+aLength>buffer.length){
			_expand(writepos+aLength+100);
		}
		int temp = 0;
		while(temp<aLength){
			buffer[writepos++] = aValue[aFrom+temp];
			temp++;
		}
		size+=aLength;
	}

	public void writeObject(Object obj) {
		if (obj == null)
			return;
		if (obj instanceof Object[]) {
			Object[] tmp = (Object[]) obj;
			writeInt(tmp.length);
			for (int i = 0; i < tmp.length; i++) {
				writeObject(tmp[i]);
			}
			return;
		} else if (obj instanceof byte[]) {
			byte[] tmp = (byte[]) obj;
			writeInt(tmp.length);
			write(tmp);
			return;
		} else if (obj instanceof long[]) {
			long[] tmp = (long[]) obj;
			writeLong(tmp.length);
			for (int i = 0; i < tmp.length; i++) {
				writeLong(tmp[i]);
			}
			return;
		} else if (obj instanceof String) {
			String tmp = (String) obj;
			char[] data = tmp.toCharArray();
			writeInt(data.length);
			for (int i = 0; i < data.length; i++) {
				writeShort((short) data[i]);
			}
			return;
		} else if (obj instanceof char[]) {
			char[] data = (char[]) obj;
			writeInt(data.length);
			for (int i = 0; i < data.length; i++) {
				writeShort((short) data[i]);
			}
			return;
		} else if (obj instanceof int[]) {
			int[] data = (int[]) obj;
			writeInt(data.length);
			for (int i = 0; i < data.length; i++) {
				writeInt(data[i]);
			}
			return;
		} else if (obj instanceof short[]) {
			short[] data = (short[]) obj;
			writeInt(data.length);
			for (int i = 0; i < data.length; i++) {
				writeShort((short) data[i]);
			}
			return;
		}
	}

	public int available(){
		return size-readpos;
	}

	/**
	 * 字符串扩充
	 * @param aSize
	 */
	private void _expand(int aSize){
		byte[] newdata = new byte[aSize];
		if(buffer!=null){
			System.arraycopy(buffer,0,newdata,0,buffer.length);
		}
		buffer = null;
		buffer = newdata;
	}


	public int getSize() {
		return size;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void writeByte(int aValue) throws IOException{
		_writeData(aValue&0xFF,1);
	}

	public static void writeByte(int data,OutputStream out) throws IOException{
		out.write(data & 0xFF);
	}

	public static void writeBytes(byte[] data,OutputStream out) throws IOException{
		out.write(data);
	}

	//todo: 待看
	public void writeBytes(byte[] data) {
		if (data == null)
			return;
		_expand(writepos + data.length + 100);
		System.arraycopy(data, 0, buffer, writepos, data.length);
		writepos += data.length;
		size += data.length;
	}

	public void writeFloat(float data) {
		// byte[] b = new byte[4];
		int l = Float.floatToIntBits(data);
		for (int i = 0; i < 4; i++) {
			try {
				writeByte(l);
			} catch (IOException e) {
				e.printStackTrace();
			}
			l = l >> 8;
		}
	}

	public void replaceInt(int from, int newint) {
		byte[] intbyte = new byte[4];
		intbyte[3] = (byte) ((newint >> 24) & 0xFF);
		intbyte[2] = (byte) ((newint >> 16) & 0xFF);
		intbyte[1] = (byte) ((newint >> 8) & 0xFF);
		intbyte[0] = (byte) (newint & 0xFF);
		replace(from, 4, intbyte, 0, 4);
	}

	public void replaceShort(int from, short newshort) {
		byte[] intbyte = new byte[2];
		intbyte[1] = (byte) ((newshort >> 8) & 0xFF);
		intbyte[0] = (byte) ((newshort) & 0xFF);
		replace(from, 2, intbyte, 0, 2);
	}

	public void replaceByte(int from, byte newbyte) {
		buffer[from] = newbyte;
	}

	public void replace(int from, int length, byte[] data, int dataOffset,
	                    int dataLength) {
		if (data == null)
			return;
		if (dataOffset + dataLength > data.length)
			return;
		int sizeoffset = dataLength - length;
		if (sizeoffset > 0) {

			byte[] tmp = new byte[size + sizeoffset + 100];
			System.arraycopy(buffer, 0, tmp, 0, from);
			System.arraycopy(buffer, from + length, tmp, from + dataLength,
					size - (from + length));
			System.arraycopy(data, dataOffset, tmp, from, dataLength);
			buffer = null;
			buffer = tmp;
		} else {
			System.arraycopy(buffer, from + length, buffer, from + dataLength,
					size - (from + length));
			System.arraycopy(data, dataOffset, buffer, from, dataLength);
		}
		size += sizeoffset;
	}

	public static void writeInt(int data, OutputStream out) throws IOException {
		writeByte((data) & 0xFF, out);
		writeByte((data >> 8) & 0xFF, out);
		writeByte((data >> 16) & 0xFF, out);
		writeByte((data >> 24) & 0xFF, out);
	}

	public static void writeLong(long data, OutputStream out)
			throws IOException {
		writeByte((int) ((data) & 0xFF), out);
		writeByte((int) ((data >> 8) & 0xFF), out);
		writeByte((int) ((data >> 16) & 0xFF), out);
		writeByte((int) ((data >> 24) & 0xFF), out);
		writeByte((int) ((data >> 32) & 0xFF), out);
		writeByte((int) ((data >> 40) & 0xFF), out);
		writeByte((int) ((data >> 48) & 0xFF), out);
		writeByte((int) ((data >> 56) & 0xFF), out);
	}

	public static void writeShort(int data, OutputStream out)
			throws IOException {
		writeByte((data) & 0xFF, out);
		writeByte((data >> 8) & 0xFF, out);
	}

	public static byte readByte(InputStream in) throws IOException {
		return (byte) (in.read() & 0xFF);
	}

	public static void readBytes(byte[] data, InputStream in)
			throws IOException {
		int readed = 0;
		int pos = 0;
		if (data.length == 0)
			return;
		while ((readed = in.read(data, pos, data.length - pos)) != -1) {
			pos += readed;
			if (pos == data.length)
				break;
		}
	}

	public static int readInt(InputStream in) throws IOException {
		int b0 = readByte(in);
		int b1 = readByte(in);
		int b2 = readByte(in);
		int b3 = readByte(in);
		return ((b3 & 0xFF) << 24) | ((b2 & 0xFF) << 16) | ((b1 & 0xFF) << 8)
				| (b0 & 0xFF);
	}

	public static short readShort(InputStream in) throws IOException {
		int b0 = readByte(in);
		int b1 = readByte(in);
		return (short) (((b1 & 0xFF) << 8) | (b0 & 0xFF));
	}

	public static long readLong(InputStream in) throws IOException {
		long b0 = readByte(in) & 0xFF;
		long b1 = readByte(in) & 0xFF;
		long b2 = readByte(in) & 0xFF;
		long b3 = readByte(in) & 0xFF;
		long b4 = readByte(in) & 0xFF;
		long b5 = readByte(in) & 0xFF;
		long b6 = readByte(in) & 0xFF;
		long b7 = readByte(in) & 0xFF;
		// long result = ((b7 & 0xFF) << 56) | ((b6 & 0xFF) << 48) | ((b5 &
		// 0xFF) << 40)
		// | ((b4 & 0xFF) << 32) | ((b3 & 0xFF) << 24)
		// | ((b2 & 0xFF) << 16) | ((b1 & 0xFF) << 8) | (b0 & 0xFF);
		long result = (b7 << 56) | (b6 << 48) | (b5 << 40) | (b4 << 32)
				| (b3 << 24) | (b2 << 16) | (b1 << 8) | (b0);
		return result;
	}

	public static void writeObject(Object obj, OutputStream out)
			throws IOException {
		if (obj == null)
			return;
		if (obj instanceof Object[]) {
			Object[] tmp = (Object[]) obj;
			writeInt(tmp.length, out);
			for (int i = 0; i < tmp.length; i++) {
				writeObject(tmp[i], out);
			}
			return;
		} else if (obj instanceof byte[]) {
			byte[] tmp = (byte[]) obj;
			writeInt(tmp.length, out);
			writeBytes(tmp, out);
			return;
		} else if (obj instanceof String) {
			String tmp = (String) obj;
			char[] data = tmp.toCharArray();
			writeInt(data.length, out);
			for (int i = 0; i < data.length; i++) {
				writeShort((short) data[i], out);
			}
			return;
		} else if (obj instanceof char[]) {
			char[] data = (char[]) obj;
			writeInt(data.length, out);
			for (int i = 0; i < data.length; i++) {
				writeShort((short) data[i], out);
			}
			return;
		} else if (obj instanceof int[]) {
			int[] data = (int[]) obj;
			writeInt(data.length, out);
			for (int i = 0; i < data.length; i++) {
				writeInt(data[i], out);
			}
			return;
		} else if (obj instanceof short[]) {
			short[] data = (short[]) obj;
			writeInt(data.length, out);
			for (int i = 0; i < data.length; i++) {
				writeShort((short) data[i], out);
			}
			return;
		}
	}

	public static String readString(InputStream in) throws IOException {
		int length = readInt(in);
		char[] c = new char[length];
		for (int i = 0; i < length; i++) {
			c[i] = (char) readShort(in);
		}
		return new String(c);
	}

	public static String readChar(InputStream in, int length)
			throws IOException {

		char[] c = new char[length];
		for (int i = 0; i < length; i++) {
			c[i] = (char) readByte(in);
		}
		return new String(c);
	}

	public static float readFloat(InputStream in) throws IOException {
		byte[] b = new byte[4];
		in.read(b);
		int accum = 0;
		accum = accum | (b[0] & 0xff) << 0;
		accum = accum | (b[1] & 0xff) << 8;
		accum = accum | (b[2] & 0xff) << 16;
		accum = accum | (b[3] & 0xff) << 24;
		return Float.intBitsToFloat(accum);
	}

	public static int readIntByByte(byte[] b) throws IOException {
		int accum = 0;
		accum = accum | (b[0] & 0xff) << 0;
		accum = accum | (b[1] & 0xff) << 8;
		accum = accum | (b[2] & 0xff) << 16;
		accum = accum | (b[3] & 0xff) << 24;
		return accum;
	}

	public void writeFloa1t(float data) {
		// byte[] b = new byte[4];
		int l = Float.floatToIntBits(data);
		for (int i = 0; i < 4; i++) {
			try {
				writeByte(l);
			} catch (IOException e) {
				e.printStackTrace();
			}
			l = l >> 8;
		}
	}


}
