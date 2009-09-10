package jorus.pixel;

import java.util.Arrays;

public class CxPixelShort extends CxPixel<short[]> {

	public CxPixelShort(short[] array)
	{
		this(0, 0, 1, 1, 0, 0, array.length, array);
	}
	
	public CxPixelShort(int xidx, int yidx, int w, int h, int bw, int bh,
			int ext, short[] array) {
		super(xidx, yidx, w, h, bw, bh, ext, array);
	}

	@Override
	public short[] getValue() {
		return Arrays.copyOfRange(data, index, index + extent);
	}

}
