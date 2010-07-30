/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpo;

public class BpoGetPixEltFloat extends Bpo<float[]> {
	protected int index;

	public BpoGetPixEltFloat(int idx) {
		index = idx;
		if (index < 0) {
			index = 1;
		}
		index %= 3;
	}

	public void doRow(float[] dst, float[] src, int row) {
		final int e2 = src.length / dst.length;
		final int dstIndex = offset1 + row * (width + stride1);
		final int srcIndex = offset2 + row * (width * e2 + stride2) + index;
		
		for (int i = 0; i < width; i++) {
			dst[dstIndex + i] = src[srcIndex + i * e2];
		}
	}
}
