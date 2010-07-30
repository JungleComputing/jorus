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

public class Convolution2dDouble extends GeneralizedConvolution2d<double[]> {

	@Override
	protected void doItSS(double[] destination, double[] source, double[] kernel) {
		for (int y = 0; y < height; y++) {
			int destinationIndex = y * width;
			int sourceIndex = y * rowSize;
			for (int x = 0; x < width; x++) {
				doKernelSS(destination, destinationIndex, source, sourceIndex, kernel);
				destinationIndex++;
				sourceIndex++;
			}
		}
	}
	
	private void doKernelSS(double[] destination, int destinationIndex, double[] source, int sourceIndex, double[] kernel) {
		double sum = 0;
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
//		sum /= (kernel.length);
		
		
		
		destination[destinationIndex] = sum;
	}

	@Override
	protected void doItVS(double[] destination, double[] source, double[] kernel) {
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
	
	private void doKernelVS(double[] destination, int destinationIndex, double[] source, int sourceIndex, double[] kernel) {
		double sum[] = new double[extent];
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
	protected void doItVV(double[] destination, double[] source, double[] kernel) {
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
	
	private void doKernelVV(double[] destination, int destinationIndex, double[] source, int sourceIndex, double[] kernel) {
		double sum[] = new double[extent];
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
