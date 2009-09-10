/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpo;

import jorus.array.CxArray2d;
import jorus.operations.CxBpo;

public class CxBpoGetPixEltDouble extends CxBpo<double[]> {

	protected int index;

	public CxBpoGetPixEltDouble(CxArray2d<double[]> s1, CxArray2d<double[]> s2,
			final boolean inplace, final int index) {
		super(s1, s2, inplace);
		this.index = index;
		if (this.index < 0) {
			this.index = 1;
		}
		this.index %= 3;
	}

	@Override
	public void doIt(double[] dst, double[] src) {
		int e2 = src.length / dst.length;
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				dst[off1 + j * (w + stride1) + i] = src[off2 + j
						* (w * e2 + stride2) + i * e2 + index];
			}
		}
	}
}
