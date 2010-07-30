/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpoval;


public class BpoNegDivValDouble extends BpoVal<double[]> {
	protected double[] value;

	public BpoNegDivValDouble(double[] p) {
		value = p;
	}

	@Override
	public void doRow(double[] dst, int row) {
		for (int i = 0; i < width; i++) {
			dst[offset + row * (width + stride) + i] = dst[offset + row
					* (width + stride) + i] < 0 ? -dst[offset + row
					* (width + stride) + i]
					/ value[(row * i) % value.length] : 0;
		}
	}
}
