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

public class CopyOfConvolutionRotated1dFloat extends
		GeneralizedConvolutionRotated1d<float[]> {

	@Override
	protected void doItSS(float[] dst, float[] src, float[] ker) {
		// FIXME only works for symmetric filters!!
//		doItSSTimo(dst, src, ker);
		 for (int i = 0; i < height; i++) {
			 doRowSS(dst, src, ker, offset + i * rowSize);
		 }
	}

	private final void doRowSS(float[] dst, float[] src, float[] ker,
			int index) {
		// FIXME only works for symmetric filters!!
		final int srcPtr = index;
		final int dstPtr = srcPtr;
		int kerPtr = halfKerSize; // only half of kernel used

		/*** start at center of filter ***/

		for (int j = 0; j < width; j++) {
			dst[dstPtr + j] = ker[kerPtr] * src[srcPtr + j];
		}

		/*** do rest of filter ***/

		for (int distanceToCenter = 1; distanceToCenter <= halfKerSize; distanceToCenter++) {
			float xf = (float) (Math.abs(cosPhi) * distanceToCenter);
			float yf = (float) (Math.abs(sinPhi) * distanceToCenter);
			int x = (int) xf;
			int y = (int) yf;

			// TODO Still need to decypher this:
			float xDiff = xf - (float) x; // distance to previous scalar on x
			// axis
			float xDiff2 = 1 - xDiff; // distance to next scalar on x axis
			float yDiff = yf - (float) y; // distance to previous scalar on
			// y-axis
			float yDiff2 = 1 - yDiff; // distance to next scalar on y-axis

			float d = xDiff2 * yDiff; // 'top-right'
			float c = xDiff * yDiff; // 'top-left'
			float a = xDiff * yDiff2; // 'bottom-left'
			float b = xDiff2 * yDiff2; // 'bottom-right'
			// ---

			int pup, ppup, pdown, ppdown;
			if (sinPhi / cosPhi <= 0.0) { // 2nd and 4th quadrant
				pup = srcPtr + y * rowSize;
				ppup = srcPtr + (y + 1) * rowSize;

				pdown = srcPtr - y * rowSize;
				ppdown = srcPtr - (y + 1) * rowSize;
			} else { // 1st and 3rd quadrant
				pup = srcPtr - y * rowSize;
				ppup = srcPtr - (y + 1) * rowSize;

				pdown = srcPtr + y * rowSize;
				ppdown = srcPtr + (y + 1) * rowSize;
			}

			int kernelIndex = kerPtr + distanceToCenter;
			a *= ker[kernelIndex];
			b *= ker[kernelIndex];
			c *= ker[kernelIndex];
			d *= ker[kernelIndex];

			/*** do the real filtering ***/

			pup += x;
			ppup += x;
			pdown -= (x + 1);
			ppdown -= (x + 1);

			for (int j = 0; j < width; j++) {
				dst[dstPtr + j] += a * (src[pup + 1] + src[pdown]) + b
						* (src[pup] + src[pdown + 1]) + c
						* (src[ppup + 1] + src[ppdown]) + d
						* (src[ppup] + src[ppdown + 1]);
				pup++;
				ppup++;
				pdown++;
				ppdown++;
			}
		}
	}

	// @Override
	// protected void doItSS(double[] dst, double[] src, double[] ker) {
	// //FIXME only works for symmetric filters!!
	//		
	// final int halfKerSize = kernelWidth / 2;
	// final int rowSize = width + stride;
	//
	// for (int i = 0; i < height; i++) {
	// final int srcPtr = offset + i * rowSize;
	// final int dstPtr = srcPtr;
	// int kerPtr = halfKerSize; // only half of kernel used
	//
	// /*** start at center of filter ***/
	//
	// for (int j = 0; j < width; j++) {
	// dst[dstPtr + j] = ker[kerPtr] * src[srcPtr + j];
	// }
	//
	// /*** do rest of filter ***/
	//
	// for (int distanceToCenter = 1; distanceToCenter <= halfKerSize;
	// distanceToCenter++) {
	// double xf = Math.abs(cosPhi) * distanceToCenter;
	// double yf = Math.abs(sinPhi) * distanceToCenter;
	// int x = (int) xf;
	// int y = (int) yf;
	//
	// // TODO Still need to decypher this:
	// double xDiff = xf - (double) x; // distance to previous scalar on x axis
	// double xDiff2 = 1.0 - xDiff; // distance to next scalar on x axis
	// double yDiff = yf - (double) y; // distance to previous scalar on y-axis
	// double yDiff2 = 1.0 - yDiff; // distance to next scalar on y-axis
	//				
	// double d = xDiff2 * yDiff; // 'top-right'
	// double c = xDiff * yDiff; // 'top-left'
	// double a = xDiff * yDiff2; // 'bottom-left'
	// double b = xDiff2 * yDiff2; // 'bottom-right'
	// // ---
	//
	// int pup, ppup, pdown, ppdown;
	// if (sinPhi / cosPhi <= 0.0) { // 2nd and 4th quadrant
	// pup = srcPtr + y * rowSize;
	// ppup = srcPtr + (y + 1) * rowSize;
	//
	// pdown = srcPtr - y * rowSize;
	// ppdown = srcPtr - (y + 1) * rowSize;
	// } else { // 1st and 3rd quadrant
	// pup = srcPtr - y * rowSize;
	// ppup = srcPtr - (y + 1) * rowSize;
	//					
	// pdown = srcPtr + y * rowSize;
	// ppdown = srcPtr + (y + 1) * rowSize;
	// }
	//
	// int kernelIndex = kerPtr + distanceToCenter;
	// a *= ker[kernelIndex];
	// b *= ker[kernelIndex];
	// c *= ker[kernelIndex];
	// d *= ker[kernelIndex];
	//
	// /*** do the real filtering ***/
	//
	// pup += x;
	// ppup += x;
	// pdown -= (x + 1);
	// ppdown -= (x + 1);
	//
	// for (int j = 0; j < width; j++) {
	// dst[dstPtr + j] += a * (src[pup + 1] + src[pdown]) + b
	// * (src[pup] + src[pdown + 1]) + c
	// * (src[ppup + 1] + src[ppdown]) + d
	// * (src[ppup] + src[ppdown + 1]);
	// pup++;
	// ppup++;
	// pdown++;
	// ppdown++;
	// }
	// }
	// }
	// }

	@Override
	protected void doItVS(float[] dst, float[] src, float[] ker) {
		throw new UnsupportedOperationException();
		// TODO implement

	}

	@Override
	protected void doItVV(float[] dst, float[] src, float[] ker) {
		throw new UnsupportedOperationException();
		// TODO implement
	}

	protected void doItSSTimo(float[] dst, float[] src, float[] ker) {
		// TODO is slow
	
		// do center pixel first
		for (int i = 0; i < height; i++) {
			doCenterRowSSTimo(dst, src, offset + i * rowSize, ker[halfKerSize]);
		}
	
		for (int kerPointer = -halfKerSize; kerPointer < halfKerSize; kerPointer++) {
			if (kerPointer != 0) {
				int yOffset, xOffset;
				float xPos, yPos;
				float[] weights = new float[4];
				
				xPos = (float) (kerPointer * cosPhi);
				yPos = (float) (kerPointer * -sinPhi);
				
				xOffset = (int) Math.floor(xPos);
				yOffset = (int) Math.floor(yPos);
				weights[0] = (1 - (xPos - xOffset)) * (1 - (yPos - yOffset))
						* ker[kerPointer + halfKerSize]; // 'top
				// left'
				weights[1] = (xPos - xOffset) * (1 - (yPos - yOffset))
						* ker[kerPointer + halfKerSize]; // 'top
				// right'
				weights[2] = (1 - (xPos - xOffset)) * (yPos - yOffset)
						* ker[kerPointer + halfKerSize]; // 'bottom
				// left'
				weights[3] = (xPos - xOffset) * (yPos - yOffset)
						* ker[kerPointer + halfKerSize]; // 'bottom right'
	
				
				int dstPointer = offset;
				int srcPointer = dstPointer + yOffset * rowSize + xOffset
						* extent;
	
				for (int i = 0; i < height; i++) { //one extra due to linear interpolation
					doRowSSTimo(dst, dstPointer, src, srcPointer, weights);
					dstPointer += rowSize;
					srcPointer += rowSize;
				}
			}
		}
	}

		// FIXME finish this
		private final void doRowSSTimo(float[] dst, int dstPointer, float[] src,
				int srcPointer, float[] kerValues) {
			for (int j = 0; j < width; j++) {
				dst[dstPointer] += src[srcPointer] * kerValues[0];
				srcPointer++;
				dst[dstPointer] += src[srcPointer] * kerValues[1];
				dstPointer++;
			}
			
			dstPointer+=stride;
			srcPointer+=stride;
			for (int j = 0; j < width; j++) {
				dst[dstPointer] += src[srcPointer] * kerValues[2];
				srcPointer++;
				dst[dstPointer] += src[srcPointer] * kerValues[3];
				dstPointer++;
			}
			
	//		for (int j = 0; j < width; j++) {
	//			dst[dstPointer] += src[srcPointer] * kerValues[0];
	//			dst[dstPointer + rowSize] += src[srcPointer + rowSize] * kerValues[2];
	//			srcPointer++;
	//			dst[dstPointer] += src[srcPointer] * kerValues[1];
	//			dst[dstPointer + rowSize] += src[srcPointer + rowSize] * kerValues[3];
	//			dstPointer++;
	//		}
		}

	private final void doCenterRowSSTimo(float[] dst, float[] src,
			int rowIndex, float kernelValue) {
		final int end = rowIndex + width;
		for (int j = rowIndex; j < end; j++) {
			dst[j] = src[j] * kernelValue;
		}
	}

}
