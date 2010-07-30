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

public class Convolution1dFloat extends GeneralizedConvolution1d<float[]> {

	@Override
	protected void doItSS(float[] dst, float[] src, float[] kernel) { // SCALAR_SCALAR
		final int kerBW = kernelWidth / 2;
		final int destinationIndex = offset;
		switch (dimension) {
		case 0: // horizontal kernel
			
			final int sourceIndexH = destinationIndex - kerBW;
			for (int y = 0; y < height; y++) {
				doRowSSH(dst, destinationIndex + y * rowSize, src, sourceIndexH + y * rowSize, kernel);
			}
			break;
		case 1: // vertical kernel
			final int sourceIndexV = destinationIndex - kerBW * rowSize;
			for (int y = 0; y < height; y++) {
				doColSSH(dst, destinationIndex + y * rowSize, src, sourceIndexV + y * rowSize, kernel);
			}
			
			break;
		default:
			// FIXME error
		}
	}

	private void doRowSSH(float[] dst, int dstIndex, float[] src, int srcIndex, float[] ker) {
		for (int x = 0; x < width; x++) {
			dst[dstIndex + x] = 0; // neutral element of addition
			for (int k = 0; k < kernelWidth; k++) {
				dst[dstIndex + x] += src[srcIndex + x + k] * ker[k];				
			}
		}
	}
	
	private void doColSSH(float[] dst, int dstIndex, float[] src, int srcIndex, float[] ker) {
		for (int x = 0; x < width; x++) {
			dst[dstIndex + x] = 0; // neutral element of addition
			for (int k = 0; k < kernelWidth; k++) {
				dst[dstIndex + x] += src[srcIndex + x + k * rowSize] * ker[k];	
			}
		}
	}
	

	
//	@Override
//	protected void doItSS(float[] dst, float[] src, float[] ker) { // SCALAR_SCALAR
//		final int kerBW = kernelWidth / 2;
//		switch (dimension) {
//		case 0: // horizontal kernel
//			for (int y = 0; y < height; y++) {
//				doRowSSH(dst, src, ker, y, kerBW);
//			}
//			break;
//		case 1: // vertical kernel
////			for (int x = 0; x < width; x++) {
////				doColSSHOld(dst, src, ker, x, kerBW);
////			}
//			for (int y = 0; y < height; y++) {
//				doColSSH(dst, src, ker, y, kerBW);
//			}
//			break;
//		default:
//			// FIXME error
//		}
//	}
//
//	private void doRowSSH(float[] dst, float[] src, float[] ker, int row,
//			int kerBW) {
//		for (int i = 0; i < width; i++) {
//			final int dstPtr = offset + row * rowSize + i;
//			final int srcPtr = dstPtr - kerBW;
//			dst[dstPtr] = 0; // neutral element of addition
//
//			for (int k = 0; k < kernelWidth; k++) {
//				dst[dstPtr] += src[srcPtr + k] * ker[k];
//			}
//		}
//	}
//	
//	private void doColSSH(float[] dst, float[] src, float[] ker, int y,
//			int kerBW) {
//		for (int x = 0; x < width; x++) {
//			final int dstPtr = offset + y * rowSize + x;
//			final int srcPtr = dstPtr - kerBW * rowSize;
//			dst[dstPtr] = 0; // neutral element of addition
//
//			for (int k = 0; k < kernelWidth; k++) {
//				dst[dstPtr] += src[srcPtr + k * rowSize] * ker[k];
//			}
//		}
//	}

//	private void doColSSHOld(float[] dst, float[] src, float[] ker, int col,
//			int kerBW) {
//		for (int j = 0; j < height; j++) {
//			final int dstPtr = offset + j * rowSize + col;
//			final int srcPtr = dstPtr - kerBW * rowSize;
//			dst[dstPtr] = 0; // neutral element of addition
//
//			for (int k = 0; k < kernelWidth; k++) {
//				dst[dstPtr] += src[srcPtr + k * rowSize] * ker[k];
//			}
//		}
//	}
	


	@Override
	protected void doItVS(float[] dst, float[] src, float[] ker) { // VECTOR_SCALAR
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
					dst[dstPtr] = 0; // neutral element of addition

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
					dst[dstPtr] = 0; // neutral element of addition

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
	protected void doItVV(float[] dst, float[] src, float[] ker) { // VECTOR_VECTOR
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
					dst[dstPtr] = 0; // neutral element of addition

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
					dst[dstPtr] = 0; // neutral element of addition

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
