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

public class BpoMinValByte extends BpoVal<byte[]> {
	protected byte[] value;
	protected int extent;
	protected int pixelWidth;

	public BpoMinValByte(byte[] p) {
		value = p;
	}

	@Override
	public void init(Array2d<byte[]> s1, boolean parallel) {
		pixelWidth = parallel ? s1.getPartialWidth() : s1.getWidth();
		extent = s1.getExtent();
		super.init(s1, parallel);
	}

	@Override
	public void doIt(byte[] dst) {
		if (extent == 1) {
			doItSimple(dst); //TODO Test whether this is really faster
			return;
		}
		// According to Horus docs: use the L1 norm;
		final int valueL1 = normL1(value, 0, value.length);

		for (int j = 0; j < height; j++) {
			for (int i = 0; i < pixelWidth; i += extent) {
				if (valueL1 < normL1(dst, offset + j * (width + stride) + i,
						extent)) {
					for (int k = 0; k < extent; k++) {
						dst[offset + j * (width + stride) + i + k] = value[k];
					}
				}
			}
		}
	}

	public void doItSimple(byte[] dst) {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if(value[0] < dst[offset + j * (width + stride) + i]) {
					dst[offset + j * (width + stride) + i] = value[0];
				}
			}
		}
	}

	private int normL1(final byte[] data, final int index, final int length) {
		int result = 0;
		for (int i = index; i < index + length; i++) {
			result += data[i];
		}
		return result;
	}
}
