/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpoval;



public class BpoMulValFloat extends BpoVal<float[]> {
	protected float[] value;

	public BpoMulValFloat(float[] p) {
		value = p;
	}
	
	@Override
	public void doRow(float[] dst, int index) {
		for (int i = 0; i < width; i++) {
			dst[index + i] *= value[i % value.length];
		}
	}
}
