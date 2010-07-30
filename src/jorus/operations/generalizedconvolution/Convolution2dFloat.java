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


public final class Convolution2dFloat extends GeneralizedConvolution2d<float[]> {


	
	@Override
	protected void doItSS(float[] destination, float[] source, float[] kernel) {
		for (int y = 0; y < height; y++) {
			doRowSS(destination, y * width, source, y * rowSize, kernel);
//			final int destinationIndex = y * width;
//			final int sourceIndex = y * rowSize;
//			for (int x = 0; x < width; x++) {
//				doKernelSS(destination,destinationIndex + x, source, sourceIndex + x, kernel);
//			}
		}
	}
	
	private void doRowSS(float[] destination, int destinationIndex, float[] source, int sourceIndex, float[] kernel) {
		for (int x = 0; x < width; x++) {
			doKernelSS(destination, destinationIndex + x, source, sourceIndex + x, kernel);
		}
	}
		
	private void doKernelSS(float[] destination, int destinationIndex, float[] source, int sourceIndex, float[] kernel) {
	float sum = 0;
	int kernelIndex = 0;
	for (int j = 0; j < kernelHeight; j++) {
		for (int i = 0; i < kernelWidth; i++) {
			sum += source[sourceIndex] * kernel[kernelIndex];
			sourceIndex++;
			kernelIndex++;
		}
		sourceIndex -= kernelWidth;
		sourceIndex += rowSize;
	}
//sum /= kernel.length;
	destination[destinationIndex] = sum;
}
	
	
	
//	private void doKernelSS(float[] destination, int destinationIndex, float[] source, int sourceIndex, float[] kernel) {
//		destination[destinationIndex] = 0;
//		for (int j = 0; j < kernelHeight; j++) {
//			doKernelRowSS(destination, destinationIndex, source, sourceIndex + j * rowSize, kernel, j * kernelWidth);
//		}
////		destination[destinationIndex] /= (kernel.length);
//
//	}
//	
//	private void doKernelRowSS(float[] destination, int destinationIndex, float[] source, int sourceIndex, float[] kernel, int kernelIndex) {
//		for (int i = 0; i < kernelWidth; i++) {
//			destination[destinationIndex] += source[sourceIndex + i] * kernel[kernelIndex + i];
//		}
//	}
	
	
//	@Override
//	protected void doItSS(float[] destination, float[] source, float[] kernel) {
//		for (int y = 0; y < height; y++) {
//			int destinationIndex = y * width;
//			int sourceIndex = y * rowSize;
//			for (int x = 0; x < width; x++) {
//				doKernelSS(destination, destinationIndex, source, sourceIndex, kernel);
//				destinationIndex++;
//				sourceIndex++;
//			}
//		}
//	}
	
//	private void doKernelSS(float[] destination, int destinationIndex, float[] source, int sourceIndex, float[] kernel) {
//		float sum = 0;
//		int kernelIndex = 0;
//		for (int j = 0; j < kernelHeight; j++) {
//			for (int i = 0; i < kernelWidth; i++) {
//				sum += source[sourceIndex + i] * kernel[kernelIndex];
//				kernelIndex++;
//			}
//			sourceIndex += rowSize;
//		}
//		
//		
////		sum /= kernel.length;
//		destination[destinationIndex] = sum;
//	}
	
	
//	private void doKernelSS(float[] destination, int destinationIndex, float[] source, int sourceIndex, float[] kernel) {
//		float sum = 0;
//		int kernelIndex = 0;
//		for (int j = 0; j < kernelHeight; j++) {
//			for (int i = 0; i < kernelWidth; i++) {
//				sum += source[sourceIndex] * kernel[kernelIndex];
//				sourceIndex++;
//				kernelIndex++;
//			}
//			sourceIndex -= kernelWidth;
//			sourceIndex += rowSize;
//		}
////	sum /= kernel.length;
//		destination[destinationIndex] = sum;
//	}

	@Override
	protected void doItVS(float[] destination, float[] source, float[] kernel) {
		for (int y = 0; y < height; y++) {
			int destinationIndex = y * width * extent;
			int sourceIndex = y * rowSize;
			for (int x = 0; x < width; x++) {
				doKernelVS(destination, destinationIndex, source, sourceIndex, kernel);
				destinationIndex += extent;
				sourceIndex += extent;
			}
		}
	}
	
	private void doKernelVS(float[] destination, int destinationIndex, float[] source, int sourceIndex, float[] kernel) {
		float sum[] = new float[extent];
		int kernelIndex = 0;
		for (int j = 0; j < kernelHeight; j++) {
			for (int i = 0; i < kernelWidth; i++) {
				for(int k = 0; k < extent; k++) {
					sum[k] += source[sourceIndex] * kernel[kernelIndex];
					sourceIndex++;	
				}
				kernelIndex++;
			}
			sourceIndex -= kernelWidth * extent;
			sourceIndex += rowSize;
		}
		for(int k = 0; k < extent; k++) {
			sum[k] /= (kernelWidth * kernelHeight);
			destination[destinationIndex+k] = sum[k];
		}
	}

	@Override
	protected void doItVV(float[] destination, float[] source, float[] kernel) {
		for (int y = 0; y < height; y++) {
			int destinationIndex = y * width * extent;
			int sourceIndex = y * rowSize;
			for (int x = 0; x < width; x++) {
				doKernelVV(destination, destinationIndex, source, sourceIndex, kernel);
				destinationIndex += extent;
				sourceIndex += extent;
			}
		}
	}
	
	private void doKernelVV(float[] destination, int destinationIndex, float[] source, int sourceIndex, float[] kernel) {
		float sum[] = new float[extent];
		int kernelIndex = 0;
		for (int j = 0; j < kernelHeight; j++) {
			for (int i = 0; i < kernelWidth; i++) {
				for(int k = 0; k < extent; k++) {
					sum[k] += source[sourceIndex] * kernel[kernelIndex];
					sourceIndex++;
					kernelIndex++;
				}
			}
			sourceIndex -= kernelWidth * extent;
			sourceIndex += rowSize;
		}
		for(int k = 0; k < extent; k++) {
			sum[k] /= (kernelWidth * kernelHeight);
			destination[destinationIndex+k] = sum[k];
		}
	}
}
