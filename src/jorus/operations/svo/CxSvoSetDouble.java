/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.svo;

import jorus.array.CxArray2d;
import jorus.operations.CxSvo;

public class CxSvoSetDouble extends CxSvo<double[]> {
	protected double[] value;

	public CxSvoSetDouble(CxArray2d<double[]> s1, int x, int y,
			boolean inplace, double[] p) {
		super(s1, x, y, inplace);
		value = p;
	}

	@Override
	public void doIt(double[] dst, int x, int y) {
		for (int i = 0; i < e; i++) {
			dst[off + y * (w + stride) + x * e + i] = value[i];
		}
	}
}
