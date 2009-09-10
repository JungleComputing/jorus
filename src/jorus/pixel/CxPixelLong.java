package jorus.pixel;

import java.util.Arrays;

public class CxPixelLong extends CxPixel<long[]> {

	public CxPixelLong(long[] array)
	{
		this(0, 0, 1, 1, 0, 0, array.length, array);
	}
	
	public CxPixelLong(int xidx, int yidx, int w, int h, int bw, int bh,
			int ext, long[] array) {
		super(xidx, yidx, w, h, bw, bh, ext, array);
	}

	@Override
	public long[] getValue() {
		return Arrays.copyOfRange(data, index, index + extent);
	}

}
