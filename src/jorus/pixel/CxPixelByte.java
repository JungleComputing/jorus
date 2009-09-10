package jorus.pixel;

import java.util.Arrays;

public class CxPixelByte extends CxPixel<byte[]> {

	public CxPixelByte(byte[] array)
	{
		this(0, 0, 1, 1, 0, 0, array.length, array);
	}
	
	public CxPixelByte(int xidx, int yidx, int w, int h, int bw, int bh,
			int ext, byte[] array) {
		super(xidx, yidx, w, h, bw, bh, ext, array);
	}

	@Override
	public byte[] getValue() {
		return Arrays.copyOfRange(data, index, index + extent);
	}

}
