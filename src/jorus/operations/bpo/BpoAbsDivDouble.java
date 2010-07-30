/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpo;

public class BpoAbsDivDouble extends Bpo<double[]> {

	@Override
	public void doRow(double[] dst, double[] s2, int row) {
		for (int i = 0; i < width; i++) {
			final int dstIndex = offset1 + row * (width + stride1) + i;
			final int s2Index = offset2 + row * (width + stride2) + i;
			dst[dstIndex] = dst[dstIndex] / s2[s2Index];
			if (dst[dstIndex] < 0) {
				dst[dstIndex] *= -1;
				// dst[dstIndex] = 0;
			}
		}
	}
}
