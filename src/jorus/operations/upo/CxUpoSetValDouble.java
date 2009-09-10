/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.upo;

import jorus.array.CxArray2d;
import jorus.operations.CxUpo;

public class CxUpoSetValDouble extends CxUpo<double[]> {
	protected double[] value;

	public CxUpoSetValDouble(CxArray2d<double[]> s1, boolean inplace, double[] p) {
		super(s1, inplace);
		value = p;
	}

	@Override
	public void doIt(double[] dst) {
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				dst[off + j * (w + stride) + i] = value[i % value.length];
			}
		}
	}
}
