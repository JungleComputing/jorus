package jorus.operations.reduce;

import jorus.parallel.ReduceOp;


public class ReduceSumDouble extends Reduce<double[]> {

	@Override
	public void doIt(double[] dst, double[] src) {
		// first set initial values in destination structure
		for (int k = 0; k < extent; k++) {
			dst[k] = src[offset + k];
		}
		
		for (int j = 0; j < height; j++) {
			doRow(dst, src, offset + j * rowWidth);
		}
	}

	private void doRow(double[] dst, double[] src, final int index) {
		for (int i = 0; i < width; i++) {
			doPixel(dst, src, index + i * extent);
		}
	}

	private void doPixel(double[] dst, double[] src, final int index) {
		for (int k = 0; k < extent; k++) {
			dst[k] += src[index + k];
		}
	}

	@Override
	public ReduceOp getOpcode() {
		return ReduceOp.SUM;
	}
}
