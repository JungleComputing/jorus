/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpoval;

import jorus.array.Array2d;


public class BpoMaxValDouble extends BpoVal<double[]> {
	protected double[] value;
	protected int extent;
	protected int pixelWidth;
	// According to Horus docs: use the L1 norm;
	protected double valueL1;

	public BpoMaxValDouble(double[] p) {
		value = p;
	}

	@Override
	public void init(Array2d<double[],?> s1, boolean parallel) {
		pixelWidth = parallel ? s1.getPartialWidth() : s1.getWidth();
		extent = s1.getExtent();
		// According to Horus docs: use the L1 norm;
		valueL1 = normL1(value, 0, value.length);
		super.init(s1, parallel);
	}

	@Override
	public void doRow(double[] dst, int index) {
		if (extent == 1) {
			doItSimple(dst, index); // TODO Test whether this is really faster
			return;
		}

		for (int i = 0; i < pixelWidth; i += extent) {
			if (valueL1 > normL1(dst, index + i,
					extent)) {
				for (int k = 0; k < extent; k++) {
					dst[index + i + k] = value[k];
				}
			}
		}
	}

	public void doItSimple(double[] dst, int index) {
		for (int i = 0; i < width; i++) {
			if (value[0] > dst[index + i]) {
				dst[index + i] = value[0];
			}
		}
	}

	private double normL1(final double[] data, final int index, final int length) {
		double result = 0;
		for (int i = index; i < index + length; i++) {
			result += data[i];
		}
		return result;
	}
}
