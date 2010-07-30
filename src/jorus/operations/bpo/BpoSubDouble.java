/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpo;

public class BpoSubDouble extends Bpo<double[]> {

	public void doRow(double[] dst, double[] src, int row) {
		for (int i = 0; i < width; i++) {
			dst[offset1 + row * (width + stride1) + i] -= src[offset2 + row
					* (width + stride2) + i];
		}
	}
}
