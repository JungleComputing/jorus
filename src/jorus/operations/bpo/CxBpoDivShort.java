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

public class CxBpoDivShort extends CxBpo<short[]> {
	public CxBpoDivShort(CxArray2d<short[]> s1, CxArray2d<short[]> s2,
			boolean inplace) {
		super(s1, s2, inplace);
	}

	@Override
	public void doIt(short[] dst, short[] s2) {
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				dst[off1 + j * (w + stride1) + i] /= s2[off2 + j
						* (w + stride2) + i];
			}
		}
	}
}
