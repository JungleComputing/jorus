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

public class CxSvoSetInt extends CxSvo<int[]> {
	protected int[] value;

	public CxSvoSetInt(CxArray2d<int[]> s1, int x, int y, boolean inplace,
			int[] p) {
		super(s1, x, y, inplace);
		value = p;
	}

	@Override
	public void doIt(int[] dst, int x, int y) {
		for (int i = 0; i < e; i++) {
			dst[off + y * (w + stride) + x * e + i] = value[i];
		}
	}
}
