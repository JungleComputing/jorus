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

public class CxUpoMulValLong extends CxUpo<long[]> {
	protected long[] value;

	public CxUpoMulValLong(CxArray2d<long[]> s1, boolean inplace, long[] p) {
		super(s1, inplace);
		value = p;
	}

	@Override
	public void doIt(long[] dst) {
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				dst[off + j * (w + stride) + i] *= value[i % value.length];
			}
		}
	}
}
