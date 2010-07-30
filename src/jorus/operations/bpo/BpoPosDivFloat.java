/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpo;

public class BpoPosDivFloat extends Bpo<float[]> {

	public void doRow(float[] dst, float[] src, int row) {
		int dstIndex = offset1 + row * (width + stride1);
		int srcIndex = offset1 + row * (width + stride1);
		for (int i = 0; i < width; i++) {
			final float dingest = dst[dstIndex + i]; 
			if(dingest > 0) {
				dst[dstIndex + i] = dingest / src[srcIndex + i];
			} else {
				dst[dstIndex + i] = 0;
			}
		}
	}
}
