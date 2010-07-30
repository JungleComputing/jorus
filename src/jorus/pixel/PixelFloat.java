/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.pixel;


public class PixelFloat extends Pixel<float[]> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5831860220231270494L;

	public PixelFloat(float... array) {
		super(array.length, array);
	}
	
//	public PixelDouble(double[] array) {
//		super(array.length, array);
//	}

	public PixelFloat(int extent) {
		super(extent);
	}

	@Override
	protected float[] createDataArray(int size) {
		return new float[size];
	}
	
	public float norm1() {
		return norm1(data, 0, extent);
	}
	
	public float norm2() {
		return norm2(data, 0, extent);
	}
	
	public float normInfinity() {
		return normInfinity(data, 0, extent);
	}
	
	public static float norm1(float[] pixelArray, int offset, int extent) {
		float result = Math.abs(pixelArray[offset]);
		for (int i = 1; i < extent; i++) {
			result += Math.abs(pixelArray[offset + i]);
		}
		return result;
	}
	
	public static float norm2(float[] pixelArray, int offset, int extent) {
		float result = pixelArray[offset] * pixelArray[offset];
		for (int i = 1; i < extent; i++) {
			result += pixelArray[offset + i] * pixelArray[offset + i];
		}
		return (float) Math.sqrt(result);
	}
	
	public static float normInfinity(float[] pixelArray, int offset, int extent) {
		float result = Math.abs(pixelArray[offset]);
		for (int i = 1; i < extent; i++) {
			final float absVal = Math.abs(pixelArray[offset + i]);
			if(absVal > result) {
				result = absVal;
			}
		}
		return result;
	}
}
