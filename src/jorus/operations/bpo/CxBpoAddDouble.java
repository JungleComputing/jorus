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

public class CxBpoAddDouble extends CxBpo<double[]> {
	public CxBpoAddDouble(CxArray2d<double[]> s1, CxArray2d<double[]> s2,
			boolean inplace) {
		super(s1, s2, inplace);
	}

	@Override
	protected void doIt(double[] destination, double[] source) {
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				destination[off1 + j * (w + stride1) + i] += source[off2 + j
						* (w + stride2) + i];
			}
		}
	}
}
