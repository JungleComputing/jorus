/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.pixel;

public class PixelInt extends Pixel<int[]> {
	public PixelInt(int[] array) {
		super(array.length, array);
	}

	public PixelInt(int extent) {
		super(extent);
	}
	
	@Override
	protected int[] createDataArray(int size) {
		return new int[size];
	}
}