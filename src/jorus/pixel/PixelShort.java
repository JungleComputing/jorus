/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.pixel;

public class PixelShort extends Pixel<short[]> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5775146302306556841L;

	public PixelShort(short[] array) {
		super(array.length, array);
	}

	public PixelShort(int extent) {
		super(extent);
	}
	
	@Override
	protected short[] createDataArray(int size) {
		return new short[size];
	}
}
