package jorus.parallel.array;

import jorus.parallel.ArrayUtil;
import jorus.parallel.ReduceOp;
import jorus.pixel.PixelDouble;

public final class DoubleArrayUtil extends ArrayUtil<double[]> {

	private double[] cache;

	@Override
	public double[] clone(double[] array) {
		return array.clone();
	}

	@Override
	public double[] create(int length) {

		double[] tmp = cache;

		if (tmp == null || tmp.length != length) {
			tmp = new double[length];
		}

		return tmp;
	}

	@Override
	public int getLength(double[] array) {
		return array.length;
	}

	@Override
	public int typeSize() {
		return 8;
	}

	@Override
	public void release(double[] array) {
		cache = array;
	}

	@Override
	public void reduce(double[] target, double[] source, int extent,
			int offset, int pixels, ReduceOp reduceOp) {
		int startIndex = offset * extent;
		int endIndex = startIndex + pixels * extent;
		switch (reduceOp) {
		case SUM:
			for (int i = startIndex; i < endIndex; i++) {
				target[i] += source[i];
			}
			break;
		case PRODUCT:
			for (int i = startIndex; i < endIndex; i++) {
				target[i] *= source[i];
			}
			break;
		case MINIMUM:
			for (int i = startIndex; i < endIndex; i += extent) {
				if (PixelDouble.norm1(source, i, extent) <= PixelDouble.norm1(
						target, i, extent)) {
					for (int j = 0; j < extent; j++) {
						target[i + extent] = source[i + extent];
					}
				}
			}
			break;
		case MAXIMUM:
			for (int i = startIndex; i < endIndex; i += extent) {
				if (PixelDouble.norm1(source, i, extent) >= PixelDouble.norm1(
						target, i, extent)) {
					for (int j = 0; j < extent; j++) {
						target[i + extent] = source[i + extent];
					}
				}
			}
			break;
		case SUPREMUM:
			for (int i = startIndex; i < endIndex; i++) {
				if (source[i] > target[i]) {
					target[i] = source[i];
				}
			}
			break;
		case INFIMUM:
			for (int i = startIndex; i < endIndex; i++) {
				if (source[i] < target[i]) {
					target[i] = source[i];
				}
			}
			break;
		}
	}

	public void reduce(double[] target, double[] source, int extent,
			ReduceOp reduceOp) {
		reduce(target, source, extent, 0, target.length / extent, reduceOp);
	}
}
