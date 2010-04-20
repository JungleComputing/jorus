/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.pixel;

public class PixelByte extends Pixel<byte[]> {
	public PixelByte(byte[] array) {
		super(array.length, array);
	}

	public PixelByte(int extent) {
		super(extent);
	}
	
	@Override
	protected byte[] createDataArray(int size) {
		return new byte[size];
	}
}
