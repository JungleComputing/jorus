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

public class CxUpoMulValByte extends CxUpo<byte[]> {
	protected byte[] value;

	public CxUpoMulValByte(CxArray2d<byte[]> s1, boolean inplace, byte[] p) {
		super(s1, inplace);
		value = p;
	}

	@Override
	public void doIt(byte[] dst) {
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				dst[off + j * (w + stride) + i] *= value[i % value.length];
			}
		}
	}
}
