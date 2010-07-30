/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.svo;

public class SvoSetFloat extends Svo<float[]> {
	protected float[] value;

	public SvoSetFloat(float[] p) {
		value = p;
	}

	public void doIt(float[] dst, int x, int y) {
		final int index = off + y * (w + stride) + x * e;
		for (int i = 0; i < e; i++) {
			dst[index + i] = value[i];
		}
	}
}
