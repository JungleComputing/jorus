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

//	public void doIt(double[] dst) {
//		for (int j = 0; j < height; j++) {
//			for (int i = 0; i < width; i++) {
//				dst[offset + j * (width + stride) + i] *= value[i
//						% value.length];
//			}
//		}
//	}
	
	
	
	@Override
	public void doRow(float[] dst, int index) {
//		final int index = offset + row * (width + stride);
		for (int i = 0; i < width; i++) {
			dst[index + i] *= value[i % value.length];
		}
	}
}
