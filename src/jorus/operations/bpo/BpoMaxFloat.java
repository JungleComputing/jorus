/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpo;

public class BpoMaxFloat extends Bpo<float[]> {
	public void doRow(float[] dst, float[] src, int row) {
		final int srcIndex = offset2 + row * (width + stride2);
		final int dstIndex = offset1 + row * (width + stride1);
		
		for (int i = 0; i < width; i++) {
			if (src[srcIndex + i] > dst[dstIndex + i]) {
				dst[dstIndex + i] = src[srcIndex + i];
			}
		}
	}
}
