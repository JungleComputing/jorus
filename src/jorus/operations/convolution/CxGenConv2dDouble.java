/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Timo van Kessel (tpkessel@cs.vu.nl)
 *
 */

package jorus.operations.convolution;

import jorus.array.CxArray2d;
import jorus.operations.CxConvolution2d;
import jorus.operations.CxSetBorder;

public class CxGenConv2dDouble extends CxConvolution2d<double[]> {

	public CxGenConv2dDouble(CxArray2d<double[]> s1, CxArray2d<double[]> ker,
			CxSetBorder<double[]> sbo) {
		super(s1, ker, sbo);
	}

	@Override
	public void doIt(double[] dst, double[] src, double[] ker) {
		int srcPtr = 0;
		int dstPtr = 0;
		int kerPtr = 0;

		int dstToSrc = ((kernelHeight / 2) * totalWidth + kernelWidth / 2)
				* extent;

		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				dstPtr = indexOf(i, j);
				srcPtr = dstPtr - dstToSrc;
				kerPtr = 0;
				dst[dstPtr] = 0.; // neutral element of addition
				for (int u = 0; u < kernelHeight; u++) {
					for (int v = 0; v < kernelWidth; v++) {
						for (int k = 0; k < extent; k++) {
							dst[dstPtr] += src[srcPtr + (u * totalWidth + v)
									* extent + k]
									* ker[kerPtr];
							kerPtr++;
						}
					}
				}
			}
		}
	}
}
