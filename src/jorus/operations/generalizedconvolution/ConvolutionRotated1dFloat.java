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

public class ConvolutionRotated1dFloat extends
		GeneralizedConvolutionRotated1d<float[]> {

	@Override
	protected void doItSS(float[] dst, float[] src, float[] ker) {
		// FIXME only works for symmetric filters!!
		// doItSSTimo(dst, src, ker);
		for (int i = 0; i < height; i++) {
			doRowSS(dst, src, ker, offset + i * rowSize);
		}
	}

	private final void doRowSS(float[] dst, float[] src, float[] ker, int index) {
		// FIXME only works for symmetric filters!!

		/*** start at center of filter ***/
		final float kernelCenter = ker[halfKerSize];
		for (int j = 0; j < width; j++) {
			dst[index + j] = kernelCenter * src[index + j];
		}

		/*** do rest of filter ***/

		if (sinPhi / cosPhi <= 0.0) {
			for (int distanceToCenter = 1; distanceToCenter <= halfKerSize; distanceToCenter++) {
				doOuterFilterEven(dst, index, src, index, ker, distanceToCenter);
			}
		} else {
			for (int distanceToCenter = 1; distanceToCenter <= halfKerSize; distanceToCenter++) {
				doOuterFilterOdd(dst, index, src, index, ker, distanceToCenter);
			}
		}
	}

	private final void doOuterFilterEven(float[] dst, int dstIndex,
			float[] src, int srcIndex, float[] ker, int distanceToCenter) {
		// 2nd and 4th quadrant

		final float xf = absCosPhi * distanceToCenter;
		final float yf = absSinPhi * distanceToCenter;
		final int x = (int) xf;
		final int y = (int) yf;

		// TODO Still need to decypher this:
		final float xDiff = xf - x; // distance to previous scalar on x
		// axis
		final float xDiff2 = 1 - xDiff; // distance to next scalar on x axis
		final float yDiff = yf - y; // distance to previous scalar on
		// y-axis
		final float yDiff2 = 1 - yDiff; // distance to next scalar on y-axis

//		final float kerValue = ker[halfKerSize + distanceToCenter];
//		final int kerIndex = halfKerSize + distanceToCenter;
		final float d = ker[halfKerSize + distanceToCenter] * xDiff2 * yDiff; // 'top-right'
		final float c = ker[halfKerSize + distanceToCenter] * xDiff * yDiff; // 'top-left'
		final float a = ker[halfKerSize + distanceToCenter] * xDiff * yDiff2; // 'bottom-left'
		final float b = ker[halfKerSize + distanceToCenter] * xDiff2 * yDiff2; // 'bottom-right'
		// ---

		final int pup = srcIndex + y * rowSize + x;
		final int ppup = srcIndex + (y + 1) * rowSize + x;
		// ppup = pup + rowSize;

		final int pdown = srcIndex - y * rowSize - x - 1;
		final int ppdown = srcIndex - (y + 1) * rowSize - x - 1;
		// ppdown = pdown - rowSize;


		// final int kerIndex = halfKerSize + distanceToCenter;
		// a *= ker[kerIndex];
		// b *= ker[kerIndex];
		// c *= ker[kerIndex];
		// d *= ker[kerIndex];

		// a *= ker[halfKerSize + distanceToCenter];
		// b *= ker[halfKerSize + distanceToCenter];
		// c *= ker[halfKerSize + distanceToCenter];
		// d *= ker[halfKerSize + distanceToCenter];

		// slower in Sun-Java
		// final float kernelValue = ker[kerPtr + distanceToCenter];
		// a *= kernelValue;
		// b *= kernelValue;
		// c *= kernelValue;
		// d *= kernelValue;

		/*** do the real filtering ***/

		for (int j = 0; j < width; j++) {
			final float aDing = a * (src[pup + 1 + j] + src[pdown + j]);
			final float bDing = b * (src[pup + j] + src[pdown + 1 + j]);
			dst[dstIndex + j] += aDing + bDing;
			final float cDing = c * (src[ppup + 1 + j] + src[ppdown + j]);
			final float dDing = d * (src[ppup + j] + src[ppdown + 1 + j]);
			dst[dstIndex + j] += cDing + dDing;
		}		
		// Separate function for this loop is slower

	}

	private final void doOuterFilterOdd(float[] dst, int dstIndex, float[] src,
			int srcIndex, float[] ker, int distanceToCenter) {
		// 1st and 3rd quadrant

		final float xf = absCosPhi * distanceToCenter;
		final float yf = absSinPhi * distanceToCenter;
		final int x = (int) xf;
		final int y = (int) yf;

		// TODO Still need to decypher this:
		final float xDiff = xf - x; // distance to previous scalar on x
		// axis
		final float xDiff2 = 1 - xDiff; // distance to next scalar on x axis
		final float yDiff = yf - y; // distance to previous scalar on
		// y-axis
		final float yDiff2 = 1 - yDiff; // distance to next scalar on y-axis

//		final float kerValue = ker[halfKerSize + distanceToCenter];
//		final int kerIndex = halfKerSize + distanceToCenter;
		final float d = ker[halfKerSize + distanceToCenter] * xDiff2 * yDiff; // 'top-right'
		final float c = ker[halfKerSize + distanceToCenter] * xDiff * yDiff; // 'top-left'
		final float a = ker[halfKerSize + distanceToCenter] * xDiff * yDiff2; // 'bottom-left'
		final float b = ker[halfKerSize + distanceToCenter] * xDiff2 * yDiff2; // 'bottom-right'
		// ---

		final int pup = srcIndex - y * rowSize + x;
		final int ppup = srcIndex - (y + 1) * rowSize + x;
		// ppup = pup - rowSize;

		final int pdown = srcIndex + y * rowSize - (x + 1);
		final int ppdown = srcIndex + (y + 1) * rowSize - (x + 1);
		// ppdown = pdown + rowSize;


		// final int kerIndex = halfKerSize + distanceToCenter;
		// a *= ker[kerIndex];
		// b *= ker[kerIndex];
		// c *= ker[kerIndex];
		// d *= ker[kerIndex];

		// a *= ker[halfKerSize + distanceToCenter];
		// b *= ker[halfKerSize + distanceToCenter];
		// c *= ker[halfKerSize + distanceToCenter];
		// d *= ker[halfKerSize + distanceToCenter];

		// slower in Sun-Java
		// final float kernelValue = ker[kerPtr + distanceToCenter];
		// a *= kernelValue;
		// b *= kernelValue;
		// c *= kernelValue;
		// d *= kernelValue;

		/*** do the real filtering ***/

		// Separate function for this loop is slower

		for (int j = 0; j < width; j++) {
			final float aDing = a * (src[pup + 1 + j] + src[pdown + j]);
			final float bDing = b * (src[pup + j] + src[pdown + 1 + j]);
			dst[dstIndex + j] += aDing + bDing;
			final float cDing = c * (src[ppup + 1 + j] + src[ppdown + j]);
			final float dDing = d * (src[ppup + j] + src[ppdown + 1 + j]);
			dst[dstIndex + j] += cDing + dDing;
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

				for (int i = 0; i < height; i++) { // one extra due to linear
													// interpolation
					doRowSSTimo(dst, dstPointer + i * rowSize, src, srcPointer
							+ i * rowSize, weights);
					// dstPointer += rowSize;
					// srcPointer += rowSize;
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

		dstPointer += stride;
		srcPointer += stride;
		for (int j = 0; j < width; j++) {
			dst[dstPointer] += src[srcPointer] * kerValues[2];
			srcPointer++;
			dst[dstPointer] += src[srcPointer] * kerValues[3];
			dstPointer++;
		}

		// for (int j = 0; j < width; j++) {
		// dst[dstPointer] += src[srcPointer] * kerValues[0];
		// dst[dstPointer + rowSize] += src[srcPointer + rowSize] *
		// kerValues[2];
		// srcPointer++;
		// dst[dstPointer] += src[srcPointer] * kerValues[1];
		// dst[dstPointer + rowSize] += src[srcPointer + rowSize] *
		// kerValues[3];
		// dstPointer++;
		// }
	}

	private final void doCenterRowSSTimo(float[] dst, float[] src,
			int rowIndex, float kernelValue) {
		final int end = rowIndex + width;
		for (int j = rowIndex; j < end; j++) {
			dst[j] = src[j] * kernelValue;
		}
	}
	
	
	
	
	
	
	/*** 33.030 functions ***/
	
	/*private final void doOuterFilterEven(float[] dst, int dstIndex,
			float[] src, int srcIndex, float[] ker, int distanceToCenter) {
		// 2nd and 4th quadrant

		final float xf = absCosPhi * distanceToCenter;
		final float yf = absSinPhi * distanceToCenter;
		final int x = (int) xf;
		final int y = (int) yf;

		// TODO Still need to decypher this:
		final float xDiff = xf - x; // distance to previous scalar on x
		// axis
		final float xDiff2 = 1 - xDiff; // distance to next scalar on x axis
		final float yDiff = yf - y; // distance to previous scalar on
		// y-axis
		final float yDiff2 = 1 - yDiff; // distance to next scalar on y-axis

//		final float kerValue = ker[halfKerSize + distanceToCenter];
//		final int kerIndex = halfKerSize + distanceToCenter;
		final float d = ker[halfKerSize + distanceToCenter] * xDiff2 * yDiff; // 'top-right'
		final float c = ker[halfKerSize + distanceToCenter] * xDiff * yDiff; // 'top-left'
		final float a = ker[halfKerSize + distanceToCenter] * xDiff * yDiff2; // 'bottom-left'
		final float b = ker[halfKerSize + distanceToCenter] * xDiff2 * yDiff2; // 'bottom-right'
		// ---

		final int pup = srcIndex + y * rowSize + x;
		final int ppup = srcIndex + (y + 1) * rowSize + x;
		// ppup = pup + rowSize;

		final int pdown = srcIndex - y * rowSize - x - 1;
		final int ppdown = srcIndex - (y + 1) * rowSize - x - 1;
		// ppdown = pdown - rowSize;


		// final int kerIndex = halfKerSize + distanceToCenter;
		// a *= ker[kerIndex];
		// b *= ker[kerIndex];
		// c *= ker[kerIndex];
		// d *= ker[kerIndex];

		// a *= ker[halfKerSize + distanceToCenter];
		// b *= ker[halfKerSize + distanceToCenter];
		// c *= ker[halfKerSize + distanceToCenter];
		// d *= ker[halfKerSize + distanceToCenter];

		// slower in Sun-Java
		// final float kernelValue = ker[kerPtr + distanceToCenter];
		// a *= kernelValue;
		// b *= kernelValue;
		// c *= kernelValue;
		// d *= kernelValue;

		*//*** do the real filtering ***//*

		// Separate function for this loop is slower
		for (int j = 0; j < width; j++) {
			dst[dstIndex + j] += a * (src[pup + 1 + j] + src[pdown + j]) + b
					* (src[pup + j] + src[pdown + 1 + j]) + c
					* (src[ppup + 1 + j] + src[ppdown + j]) + d
					* (src[ppup + j] + src[ppdown + 1 + j]);
		}
	}

	private final void doOuterFilterOdd(float[] dst, int dstIndex, float[] src,
			int srcIndex, float[] ker, int distanceToCenter) {
		// 1st and 3rd quadrant

		final float xf = absCosPhi * distanceToCenter;
		final float yf = absSinPhi * distanceToCenter;
		final int x = (int) xf;
		final int y = (int) yf;

		// TODO Still need to decypher this:
		final float xDiff = xf - x; // distance to previous scalar on x
		// axis
		final float xDiff2 = 1 - xDiff; // distance to next scalar on x axis
		final float yDiff = yf - y; // distance to previous scalar on
		// y-axis
		final float yDiff2 = 1 - yDiff; // distance to next scalar on y-axis

//		final float kerValue = ker[halfKerSize + distanceToCenter];
//		final int kerIndex = halfKerSize + distanceToCenter;
		final float d = ker[halfKerSize + distanceToCenter] * xDiff2 * yDiff; // 'top-right'
		final float c = ker[halfKerSize + distanceToCenter] * xDiff * yDiff; // 'top-left'
		final float a = ker[halfKerSize + distanceToCenter] * xDiff * yDiff2; // 'bottom-left'
		final float b = ker[halfKerSize + distanceToCenter] * xDiff2 * yDiff2; // 'bottom-right'
		// ---

		final int pup = srcIndex - y * rowSize + x;
		final int ppup = srcIndex - (y + 1) * rowSize + x;
		// ppup = pup - rowSize;

		final int pdown = srcIndex + y * rowSize - (x + 1);
		final int ppdown = srcIndex + (y + 1) * rowSize - (x + 1);
		// ppdown = pdown + rowSize;


		// final int kerIndex = halfKerSize + distanceToCenter;
		// a *= ker[kerIndex];
		// b *= ker[kerIndex];
		// c *= ker[kerIndex];
		// d *= ker[kerIndex];

		// a *= ker[halfKerSize + distanceToCenter];
		// b *= ker[halfKerSize + distanceToCenter];
		// c *= ker[halfKerSize + distanceToCenter];
		// d *= ker[halfKerSize + distanceToCenter];

		// slower in Sun-Java
		// final float kernelValue = ker[kerPtr + distanceToCenter];
		// a *= kernelValue;
		// b *= kernelValue;
		// c *= kernelValue;
		// d *= kernelValue;

		*//*** do the real filtering ***//*

		// Separate function for this loop is slower
		for (int j = 0; j < width; j++) {
			dst[dstIndex + j] += a * (src[pup + 1 + j] + src[pdown + j]) + b
					* (src[pup + j] + src[pdown + 1 + j]) + c
					* (src[ppup + 1 + j] + src[ppdown + j]) + d
					* (src[ppup + j] + src[ppdown + 1 + j]);
		}
	}
	*/

	
	/*** 27.5 functions ***/
	
	/*private final void doOuterFilterEven(float[] dst, int dstIndex,
			float[] src, int srcIndex, float[] ker, int distanceToCenter) {
		// 2nd and 4th quadrant

		final float xf = absCosPhi * distanceToCenter;
		final float yf = absSinPhi * distanceToCenter;
		final int x = (int) xf;
		final int y = (int) yf;

		// TODO Still need to decypher this:
		final float xDiff = xf - x; // distance to previous scalar on x
		// axis
		final float xDiff2 = 1 - xDiff; // distance to next scalar on x axis
		final float yDiff = yf - y; // distance to previous scalar on
		// y-axis
		final float yDiff2 = 1 - yDiff; // distance to next scalar on y-axis

//		final float kerValue = ker[halfKerSize + distanceToCenter];
//		final int kerIndex = halfKerSize + distanceToCenter;
		final float d = ker[halfKerSize + distanceToCenter] * xDiff2 * yDiff; // 'top-right'
		final float c = ker[halfKerSize + distanceToCenter] * xDiff * yDiff; // 'top-left'
		final float a = ker[halfKerSize + distanceToCenter] * xDiff * yDiff2; // 'bottom-left'
		final float b = ker[halfKerSize + distanceToCenter] * xDiff2 * yDiff2; // 'bottom-right'
		// ---

		final int pup = srcIndex + y * rowSize + x;
		final int ppup = srcIndex + (y + 1) * rowSize + x;
		// ppup = pup + rowSize;

		final int pdown = srcIndex - y * rowSize - x - 1;
		final int ppdown = srcIndex - (y + 1) * rowSize - x - 1;
		// ppdown = pdown - rowSize;


		// final int kerIndex = halfKerSize + distanceToCenter;
		// a *= ker[kerIndex];
		// b *= ker[kerIndex];
		// c *= ker[kerIndex];
		// d *= ker[kerIndex];

		// a *= ker[halfKerSize + distanceToCenter];
		// b *= ker[halfKerSize + distanceToCenter];
		// c *= ker[halfKerSize + distanceToCenter];
		// d *= ker[halfKerSize + distanceToCenter];

		// slower in Sun-Java
		// final float kernelValue = ker[kerPtr + distanceToCenter];
		// a *= kernelValue;
		// b *= kernelValue;
		// c *= kernelValue;
		// d *= kernelValue;

		*//*** do the real filtering ***//*

		for (int j = 0; j < width; j++) {
			
			final float aDing = a * (src[pup + 1 + j] + src[pdown + j]);
			final float bDing = b * (src[pup + j] + src[pdown + 1 + j]);
			dst[dstIndex + j] += aDing + bDing;
			final float cDing = c * (src[ppup + 1 + j] + src[ppdown + j]);
			final float dDing = d * (src[ppup + j] + src[ppdown + 1 + j]);
			dst[dstIndex + j] += cDing + dDing;
		}		
		// Separate function for this loop is slower

	}

	private final void doOuterFilterOdd(float[] dst, int dstIndex, float[] src,
			int srcIndex, float[] ker, int distanceToCenter) {
		// 1st and 3rd quadrant

		final float xf = absCosPhi * distanceToCenter;
		final float yf = absSinPhi * distanceToCenter;
		final int x = (int) xf;
		final int y = (int) yf;

		// TODO Still need to decypher this:
		final float xDiff = xf - x; // distance to previous scalar on x
		// axis
		final float xDiff2 = 1 - xDiff; // distance to next scalar on x axis
		final float yDiff = yf - y; // distance to previous scalar on
		// y-axis
		final float yDiff2 = 1 - yDiff; // distance to next scalar on y-axis

//		final float kerValue = ker[halfKerSize + distanceToCenter];
//		final int kerIndex = halfKerSize + distanceToCenter;
		final float d = ker[halfKerSize + distanceToCenter] * xDiff2 * yDiff; // 'top-right'
		final float c = ker[halfKerSize + distanceToCenter] * xDiff * yDiff; // 'top-left'
		final float a = ker[halfKerSize + distanceToCenter] * xDiff * yDiff2; // 'bottom-left'
		final float b = ker[halfKerSize + distanceToCenter] * xDiff2 * yDiff2; // 'bottom-right'
		// ---

		final int pup = srcIndex - y * rowSize + x;
		final int ppup = srcIndex - (y + 1) * rowSize + x;
		// ppup = pup - rowSize;

		final int pdown = srcIndex + y * rowSize - (x + 1);
		final int ppdown = srcIndex + (y + 1) * rowSize - (x + 1);
		// ppdown = pdown + rowSize;


		// final int kerIndex = halfKerSize + distanceToCenter;
		// a *= ker[kerIndex];
		// b *= ker[kerIndex];
		// c *= ker[kerIndex];
		// d *= ker[kerIndex];

		// a *= ker[halfKerSize + distanceToCenter];
		// b *= ker[halfKerSize + distanceToCenter];
		// c *= ker[halfKerSize + distanceToCenter];
		// d *= ker[halfKerSize + distanceToCenter];

		// slower in Sun-Java
		// final float kernelValue = ker[kerPtr + distanceToCenter];
		// a *= kernelValue;
		// b *= kernelValue;
		// c *= kernelValue;
		// d *= kernelValue;

		*//*** do the real filtering ***//*

		// Separate function for this loop is slower

		for (int j = 0; j < width; j++) {
			final float aDing = a * (src[pup + 1 + j] + src[pdown + j]);
			final float bDing = b * (src[pup + j] + src[pdown + 1 + j]);
			dst[dstIndex + j] += aDing + bDing;
			final float cDing = c * (src[ppup + 1 + j] + src[ppdown + j]);
			final float dDing = d * (src[ppup + j] + src[ppdown + 1 + j]);
			dst[dstIndex + j] += cDing + dDing;

		}
	}
*/	
}
