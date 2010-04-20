/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpoval;

public class BpoAbsDivValByte extends BpoVal<byte[]> {
	protected byte[] value;

	public BpoAbsDivValByte(byte[] p) {
		value = p;
	}

	@Override
	public void doIt(byte[] dst) {
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				dst[offset + j * (width + stride) + i] /= value[i % value.length];
				if (dst[offset + j * (width + stride) + i] < 0) {
					// dst[offset + j * (width + stride) + i] *= -1;
					dst[offset + j * (width + stride) + i] = 0;
				}
			}
		}
	}
}
