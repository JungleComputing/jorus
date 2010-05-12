/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.pixel;


public class PixelDouble extends Pixel<double[]> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5831860220231270494L;

	public PixelDouble(double... array) {
		super(array.length, array);
	}
	
//	public PixelDouble(double[] array) {
//		super(array.length, array);
//	}

	public PixelDouble(int extent) {
		super(extent);
	}

	@Override
	protected double[] createDataArray(int size) {
		return new double[size];
	}
	
	public double norm1() {
		return norm1(data, 0, extent);
	}
	
	public double norm2() {
		return norm2(data, 0, extent);
	}
	
	public double normInfinity() {
		return normInfinity(data, 0, extent);
	}
	
	public static double norm1(double[] pixelArray, int offset, int extent) {
		double result = Math.abs(pixelArray[offset]);
		for (int i = 1; i < extent; i++) {
			result += Math.abs(pixelArray[offset + i]);
		}
		return result;
	}
	
	public static double norm2(double[] pixelArray, int offset, int extent) {
		double result = pixelArray[offset] * pixelArray[offset];
		for (int i = 1; i < extent; i++) {
			result += pixelArray[offset + i] * pixelArray[offset + i];
		}
		return Math.sqrt(result);
	}
	
	public static double normInfinity(double[] pixelArray, int offset, int extent) {
		double result = Math.abs(pixelArray[offset]);
		for (int i = 1; i < extent; i++) {
			final double absVal = Math.abs(pixelArray[offset + i]);
			if(absVal > result) {
				result = absVal;
			}
		}
		return result;
	}
}
