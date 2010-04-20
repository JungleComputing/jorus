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

public class BpoMaxValLong extends BpoVal<long[]> {
	protected long[] value;
	protected int extent;
	protected int pixelWidth;

	public BpoMaxValLong(long[] p) {
		value = p;
	}

	@Override
	public void init(Array2d<long[]> s1, boolean parallel) {
		pixelWidth = parallel ? s1.getPartialWidth() : s1.getWidth();
		extent = s1.getExtent();
		super.init(s1, parallel);
	}

	@Override
	public void doIt(long[] dst) {
		if (extent == 1) {
			doItSimple(dst); // TODO Test whether this is really faster
			return;
		}
		// According to Horus docs: use the L1 norm;
		final long valueL1 = normL1(value, 0, value.length);

		for (int j = 0; j < height; j++) {
			for (int i = 0; i < pixelWidth; i += extent) {
				if (valueL1 > normL1(dst, offset + j * (width + stride) + i,
						extent)) {
					for (int k = 0; k < extent; k++) {
						dst[offset + j * (width + stride) + i + k] = value[k];
					}
				}
			}
		}
	}

	public void doItSimple(long[] dst) {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if (value[0] > dst[offset + j * (width + stride) + i]) {
					dst[offset + j * (width + stride) + i] = value[0];
				}
			}
		}
	}

	private long normL1(final long[] data, final int index, final int length) {
		long result = 0;
		for (int i = index; i < index + length; i++) {
			result += data[i];
		}
		return result;
	}
}
