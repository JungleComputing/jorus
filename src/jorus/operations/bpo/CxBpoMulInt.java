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

public class CxBpoMulInt extends CxBpo<int[]> {

	public CxBpoMulInt(CxArray2d<int[]> s1, CxArray2d<int[]> s2, boolean inplace) {
		super(s1, s2, inplace);
	}

	@Override
	public void doIt(int[] dst, int[] s2) {
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				dst[off1 + j * (w + stride1) + i] *= s2[off2 + j
						* (w + stride2) + i];
			}
		}
	}
}
