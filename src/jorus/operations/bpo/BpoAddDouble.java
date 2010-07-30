/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpo;

public class BpoAddDouble extends Bpo<double[]> {
	public void doRow(double[] dst, double[] src, int row) {
		final int dstIndex = offset1 + row * (width + stride1);
		final int srcIndex = offset2 + row * (width + stride2);
		for (int i = 0; i < width; i++) {
			dst[dstIndex + i] += src[srcIndex + i];
		}
	}
}
