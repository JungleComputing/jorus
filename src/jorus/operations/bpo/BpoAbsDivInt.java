/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpo;

public class BpoAbsDivInt extends Bpo<int[]> {

	@Override
	public void doIt(int[] dst, int[] s2) {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				final int dstIndex = offset1 + j * (width + stride1) + i;
				final int s2Index = offset2 + j * (width + stride2) + i;
				dst[dstIndex] = dst[dstIndex] / s2[s2Index];
				if (dst[dstIndex] < 0) {
					// dst[dstIndex] *= -1;
					dst[dstIndex] = 0;
				}
			}
		}
	}
}
