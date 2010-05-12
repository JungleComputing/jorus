/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.pixel;

public class PixelLong extends Pixel<long[]> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4915684078179679390L;

	public PixelLong(long[] array) {
		super(array.length, array);
	}
	
	public PixelLong(int extent) {
		super(extent);
	}

	@Override
	protected long[] createDataArray(int size) {
		return new long[size];
	}
}
