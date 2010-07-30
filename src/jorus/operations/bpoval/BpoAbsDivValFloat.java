/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpoval;


public class BpoAbsDivValFloat extends BpoVal<float[]> {
	protected float[] value;

	public BpoAbsDivValFloat(float[] p) {
		value = p;
	}

	@Override
	public void doRow(float[] dst, int row) {
			for (int i = 0; i < width; i++) {
				dst[offset + row * (width + stride) + i] /= value[i % value.length];
				if (dst[offset + row * (width + stride) + i] < 0) {
					dst[offset + row * (width + stride) + i] *= -1;
					// dst[offset + j * (width + stride) + i] = 0;
				}
		}
	}
}
