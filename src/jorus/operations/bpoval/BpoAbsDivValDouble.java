/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpoval;

public class BpoAbsDivValDouble extends BpoVal<double[]> {
	protected double[] value;

	public BpoAbsDivValDouble(double[] p) {
		value = p;
	}

	@Override
	public void doRow(double[] dst, int index) {
		for (int i = 0; i < width; i++) {
			dst[index + i] /= value[i % value.length];
			if (dst[index + i] < 0) {
				dst[index + i] *= -1;
			}
		}
	}
}
