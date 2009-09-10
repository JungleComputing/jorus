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

public class CxBpoMulByte extends CxBpo<byte[]> {
	public CxBpoMulByte(CxArray2d<byte[]> s1, CxArray2d<byte[]> s2,
			boolean inplace) {
		super(s1, s2, inplace);
	}

	@Override
	public void doIt(byte[] dst, byte[] s2) {
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				dst[off1 + j * (w + stride1) + i] *= s2[off2 + j
						* (w + stride2) + i];
			}
		}
	}
}
