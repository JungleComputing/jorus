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

public class BpoMaxValFloat extends BpoVal<float[]> {
	protected float[] value;
	protected int extent;
	protected int pixelWidth;

	// According to Horus docs: use the L1 norm;
	protected float valueL1;

	public BpoMaxValFloat(float[] p) {
		value = p;
	}

	@Override
	public void init(Array2d<float[],?> s1, boolean parallel) {
		pixelWidth = parallel ? s1.getPartialWidth() : s1.getWidth();
		extent = s1.getExtent();
		valueL1 = normL1(value, 0, value.length);
		super.init(s1, parallel);
	}

	@Override
	public void doRow(float[] dst, int row) {
		if (extent == 1) {
			doItSimple(dst, row); // TODO Test whether this is really faster
			return;
		}

		for (int i = 0; i < pixelWidth; i += extent) {
			if (valueL1 > normL1(dst, offset + row * (width + stride) + i,
					extent)) {
				for (int k = 0; k < extent; k++) {
					dst[offset + row * (width + stride) + i + k] = value[k];
				}
			}
		}
	}

	public void doItSimple(float[] dst, int row) {
		for (int i = 0; i < width; i++) {
			if (value[0] > dst[offset + row * (width + stride) + i]) {
				dst[offset + row * (width + stride) + i] = value[0];
			}
		}
	}

	private float normL1(final float[] data, final int index, final int length) {
		float result = 0;
		for (int i = index; i < index + length; i++) {
			result += data[i];
		}
		return result;
	}
}
