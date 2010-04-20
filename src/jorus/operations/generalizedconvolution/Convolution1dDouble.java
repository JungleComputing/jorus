/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *  Timo van Kessel (timo@cs.vu.nl)
 *
 */

package jorus.operations.generalizedconvolution;

public class Convolution1dDouble extends GeneralizedConvolution1d<double[]> {

	@Override
	protected void doItSS(double[] dst, double[] src, double[] ker) { // SCALAR_SCALAR
		final int kerBW = kernelWidth / 2;
		switch (dimension) {
		case 0: // x
			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width; i++) {
					final int dstPtr = offset + j * rowSize + i;
					final int srcPtr = dstPtr - kerBW;
					// if(src[dstPtr + kerBW] != 0) {
					// Thread.yield(); //FIXME debug
					// }
					dst[dstPtr] = 0.; // neutral element of addition

					for (int k = 0; k < kernelWidth; k++) {
						dst[dstPtr] += src[srcPtr + k] * ker[k];
					}
				}
			}
			break;
		case 1: // y
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					final int dstPtr = offset + j * rowSize + i;
					final int srcPtr = dstPtr - kerBW * rowSize;
					dst[dstPtr] = 0.; // neutral element of addition

					for (int k = 0; k < kernelWidth; k++) {
						dst[dstPtr] += src[srcPtr + k * rowSize] * ker[k];
					}
				}
			}
			break;
		default:
			// FIXME error
		}
	}

	@Override
	protected void doItVS(double[] dst, double[] src, double[] ker) { // VECTOR_SCALAR
		final int kerBW = kernelWidth / 2;
		switch (dimension) {
		case 0: // x
			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width; i++) {
					final int dstPtr = offset + j * rowSize + i * extent;
					final int srcPtr = dstPtr - kerBW * extent;
					// if(src[dstPtr + kerBW] != 0) {
					// Thread.yield(); //FIXME debug
					// }
					dst[dstPtr] = 0.; // neutral element of addition

					for (int k = 0; k < kernelWidth; k++) {
						for (int l = 0; l < extent; l++) {
							dst[dstPtr + l] += src[srcPtr + k * extent + l]
									* ker[k];
						}
					}
				}
			}
			break;
		case 1: // y
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					final int dstPtr = offset + j * rowSize + i * extent;
					final int srcPtr = dstPtr - kerBW * rowSize;
					dst[dstPtr] = 0.; // neutral element of addition

					for (int k = 0; k < kernelWidth; k++) {
						for (int l = 0; l < extent; l++) {
							dst[dstPtr + l] += src[srcPtr + k * rowSize + l]
									* ker[k];
						}
					}
				}
			}
			break;
		default:
			// FIXME error
		}
	}

	@Override
	protected void doItVV(double[] dst, double[] src, double[] ker) { // VECTOR_VECTOR
		final int kerBW = kernelWidth / 2;
		switch (dimension) {
		case 0: // x
			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width; i++) {
					final int dstPtr = offset + j * rowSize + i * extent;
					final int srcPtr = dstPtr - kerBW * extent;
					// if(src[dstPtr + kerBW] != 0) {
					// Thread.yield(); //FIXME debug
					// }
					dst[dstPtr] = 0.; // neutral element of addition

					for (int k = 0; k < kernelWidth; k++) {
						for (int l = 0; l < extent; l++) {
							dst[dstPtr + l] += src[srcPtr + k * extent + l]
									* ker[k + l];
						}
					}
				}
			}
			break;
		case 1: // y
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					final int dstPtr = offset + j * rowSize + i * extent;
					final int srcPtr = dstPtr - kerBW * rowSize;
					dst[dstPtr] = 0.; // neutral element of addition

					for (int k = 0; k < kernelWidth; k++) {
						for (int l = 0; l < extent; l++) {
							dst[dstPtr + l] += src[srcPtr + k * rowSize + l]
									* ker[k + l];
						}
					}
				}
			}
			break;
		default:
			// FIXME error
		}
	}
}
