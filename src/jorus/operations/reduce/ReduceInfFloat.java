package jorus.operations.reduce;

import jorus.parallel.ReduceOp;

public class ReduceInfFloat extends Reduce<float[]> {

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
		for (int k = 0; k < extent; k++) {
			if (dst[k] > src[index + k]) {
				dst[k] = src[index + k];
			}
		}
	}

	@Override
	public ReduceOp getOpcode() {
		return ReduceOp.INFIMUM;
	}
}
