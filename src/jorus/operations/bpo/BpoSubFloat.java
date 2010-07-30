/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpo;

public class BpoSubFloat extends Bpo<float[]> {

	public void doRow(float[] dst, float[] src, int row) {
		final int dstIndex = offset1 + row * (width + stride1);
		final int srcIndex = offset2 + row * (width + stride2);
		for (int i = 0; i < width; i++) {
			dst[dstIndex + i] -= src[srcIndex + i];
		}
	}
}
