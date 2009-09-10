package jorus.pixel;

import java.util.Arrays;

public class CxPixelInt extends CxPixel<int[]> {

	public CxPixelInt(int[] array)
	{
		this(0, 0, 1, 1, 0, 0, array.length, array);
	}
	
	public CxPixelInt(int xidx, int yidx, int w, int h, int bw, int bh,
			int ext, int[] array) {
		super(xidx, yidx, w, h, bw, bh, ext, array);
	}

	@Override
	public int[] getValue() {
		return Arrays.copyOfRange(data, index, index + extent);
	}

}
