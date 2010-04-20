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

public class ConvolutionRotated1dDouble extends GeneralizedConvolutionRotated1d<double[]> {

//	@Override
//	protected void doItSS(double[] dst, double[] src, double[] ker) {
//		
//		// we will do this filtering in source-oriented way.		
//		
//		final int halfKerSize = kernelWidth / 2;
//		final int rowSize = width + stride;
//
//		for (int i = 0; i < height; i++) {
//			final int srcPtr = offset + i * rowSize;
//			final int dstPtr = srcPtr;
//
//
//			/*** start at the center of the filter. it always lies at the center value of this loop ***/
//			
//			for (int j = 0; j < width; j++) {
//				dst[dstPtr + j] = src[srcPtr + j] * ker[halfKerSize]; 
//			}
//
//			
//			/*** Now we will do the rest of the filter by moving away from the center ***/
//
//			for (int distanceToCenter = 1; distanceToCenter <= halfKerSize; distanceToCenter++) {
//				double FilterXdeviation = cosPhi * distanceToCenter;
//				double FilterYdeviation = sinPhi * distanceToCenter;
//								
//
//				//start with the 'left side of the filter:
//				// calculate the weights for the linear interpolation
//				double xDiff = FilterXdeviation - Math.floor(FilterXdeviation); // distance to previous scalar on x axis
//				double xDiff2 = 1.0 - xDiff; // distance to next scalar on x axis
//				double yDiff = FilterYdeviation - Math.floor(FilterYdeviation); // distance to previous scalar on y-axis
//				double yDiff2 = 1.0 - yDiff; // distance to next scalar on y-axis
//				
//				double bottomLeft = xDiff * yDiff2; // 'bottom-left' pixel-weight
//				double bottomRight = xDiff2 * yDiff2; // 'bottom-right' pixel-weight
//				double topLeft = xDiff2 * yDiff; // 'top-right' pixel-weight
//				double topRight = xDiff * yDiff; // 'top-left' pixel-weight
//				
//				// calculate the location of the top-left source pixel
//				double topLeftPixel = srcPtr + yDiff * rowSize + xDiff; 
//				
//				
//				
//
//				
//				
//				int pup, ppup, pdown, ppdown;
//				if (sinPhi / cosPhi <= 0.0) { // 2nd and 4th quadrant
//					pup = srcPtr + (y) * rowSize;
//					ppup = srcPtr + (y + 1) * rowSize;
//
//					pdown = srcPtr - y * rowSize;
//					ppdown = srcPtr + (i - y - 1) * rowSize;
//				} else { // 1st and 3rd quadrant
//					pup = srcPtr + (i - y) * rowSize;
//					ppup = srcPtr + (i - y - 1) * rowSize;
//					
//					pdown = srcPtr + (i + y) * rowSize;
//					ppdown = srcPtr + (i + y + 1) * rowSize;
//				}
//
//				int kernelIndex = halfKerSize + distanceToCenter;
//				a *= ker[kernelIndex];
//				b *= ker[kernelIndex];
//				c *= ker[kernelIndex];
//				d *= ker[kernelIndex];
//
//				/*** do the real filtering ***/
//
//				pup += x;
//				ppup += x;
//				pdown -= (x + 1);
//				ppdown -= (x + 1);
//
//				for (int j = 0; j < width; j++) {
//					dst[dstPtr + j] += a * (src[pup + 1] + src[pdown]) + b
//							* (src[pup] + src[pdown + 1]) + c
//							* (src[ppup + 1] + src[ppdown]) + d
//							* (src[ppup] + src[ppdown + 1]);
//					pup++;
//					ppup++;
//					pdown++;
//					ppdown++;
//				}
//			}
//		}
//	}
	
	
	
	@Override
	protected void doItSS(double[] dst, double[] src, double[] ker) {
		//FIXME only works for symmetric filters!!
		
		final int halfKerSize = kernelWidth / 2;
		final int rowSize = width + stride;

		for (int i = 0; i < height; i++) {
			final int srcPtr = offset + i * rowSize;
			final int dstPtr = srcPtr;
			int kerPtr = halfKerSize; // only half of kernel used

			/*** start at center of filter ***/

			for (int j = 0; j < width; j++) {
				dst[dstPtr + j] = ker[kerPtr] * src[srcPtr + j];
			}

			/*** do rest of filter ***/

			for (int distanceToCenter = 1; distanceToCenter <= halfKerSize; distanceToCenter++) {
				double xf = Math.abs(cosPhi) * distanceToCenter;
				double yf = Math.abs(sinPhi) * distanceToCenter;
				int x = (int) xf;
				int y = (int) yf;

				// TODO Still need to decypher this:
				double xDiff = xf - (double) x; // distance to previous scalar on x axis
				double xDiff2 = 1.0 - xDiff; // distance to next scalar on x axis
				double yDiff = yf - (double) y; // distance to previous scalar on y-axis
				double yDiff2 = 1.0 - yDiff; // distance to next scalar on y-axis
				
				double d = xDiff2 * yDiff; // 'top-right'
				double c = xDiff * yDiff; // 'top-left'
				double a = xDiff * yDiff2; // 'bottom-left'
				double b = xDiff2 * yDiff2; // 'bottom-right'
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
	}

	@Override
	protected void doItVS(double[] dst, double[] src, double[] ker) {
		throw new UnsupportedOperationException();
		// TODO implement

	}

	@Override
	protected void doItVV(double[] dst, double[] src, double[] ker) {
		throw new UnsupportedOperationException();
		// TODO implement
	}

}
