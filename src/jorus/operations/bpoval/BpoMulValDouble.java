/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpoval;


public class BpoMulValDouble extends BpoVal<double[]> {
	protected double[] value;

	public BpoMulValDouble(double[] p) {
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
	public void doIt(double[] dst) {
		int index = offset;
		for (int j = 0; j < height; j++) {
			doRow(dst, index);
			index += rowWidth;
		}
	}
	
	private void doRow(double[] dst, final int index) {
		for (int i = 0; i < rowWidth; i++) {
			dst[index + i] *= value[i % value.length];
		}
	}
}
