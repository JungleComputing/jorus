/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpoval;

public class BpoNegDivValInt extends BpoVal<int[]> {
	protected int[] value;

	public BpoNegDivValInt(int[] p) {
		value = p;
	}

	@Override
	public void doIt(int[] dst) {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				dst[offset + j * (width + stride) + i] = dst[offset + j
						* (width + stride) + i] < 0 ? -dst[offset + j
						* (width + stride) + i]
						/ value[(j * i) % value.length] : 0;
			}
		}
	}
}
