/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpo;

public class BpoAddFloat extends Bpo<float[]> {

	public void doRow(float[] dst, float[] src, int row) {
		for (int i = 0; i < width; i++) {
			dst[offset1 + row * (width + stride1) + i] += src[offset2 + row
					* (width + stride2) + i];
		}
	}
}