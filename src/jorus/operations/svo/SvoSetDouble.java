/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.svo;

public class SvoSetDouble extends Svo<double[]> {
	protected double[] value;

	public SvoSetDouble(double[] p) {
		value = p;
	}

	public void doIt(double[] dst, int x, int y) {
		final int index = off + y * (w + stride) + x * e;
		for (int i = 0; i < e; i++) {
			dst[index + i] = value[i];
		}
	}
}
