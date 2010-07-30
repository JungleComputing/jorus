package jorus.operations.reduce;

import jorus.parallel.ReduceOp;
import jorus.pixel.PixelFloat;

public class ReduceMinFloat extends Reduce<float[]> {

	@Override
	public void doIt(float[] dst, float[] src) {
		// first set initial values in destination structure
		for (int k = 0; k < extent; k++) {
			dst[k] = src[offset + k];
		}
		
		for (int j = 0; j < height; j++) {
			doRow(dst, src, offset + j * rowWidth);
		}
	}
	
	private void doRow(float[] dst, float[] src, final int index) {
		for (int i = 0; i < width; i++) {
			doPixel(dst, src, index + i * extent);
		}
	}
	
	private void doPixel(float[] dst, float[] src, final int index) {
		if(PixelFloat.norm1(src, index, extent) <= PixelFloat.norm1(dst, 0, extent)) {
			for (int k = 0; k < extent; k++) {
				dst[k] = src[index + k];
			}			
		}
	}
	
	@Override
	public ReduceOp getOpcode() {
		return ReduceOp.MINIMUM;
	}
}
