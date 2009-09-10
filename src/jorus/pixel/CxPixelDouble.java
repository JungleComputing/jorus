package jorus.pixel;

import java.util.Arrays;

public class CxPixelDouble extends CxPixel<double[]> {

	public CxPixelDouble(double[] array)
	{
		this(0, 0, 1, 1, 0, 0, array.length, array);
	}
	
	public CxPixelDouble(int xidx, int yidx, int w, int h, int bw, int bh,
			int ext, double[] array) {
		super(xidx, yidx, w, h, bw, bh, ext, array);
	}

	@Override
	public double[] getValue() {
		return Arrays.copyOfRange(data, index, index + extent);
	}

}
