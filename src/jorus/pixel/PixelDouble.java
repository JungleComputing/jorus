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
}
