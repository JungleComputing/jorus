/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;


public class Array2dScalarByte extends Array2dBytes {

	/*** Public Methods ***********************************************/

	public Array2dScalarByte(Array2dScalarByte orig, int newBW, int newBH) {
		super(orig, newBW, newBH);
	}

	public Array2dScalarByte(Array2dScalarByte orig) {
		super(orig);
	}

	public Array2dScalarByte(int w, int h, int bw, int bh, boolean create) {
		super(w, h, bw, bh, 1, create);
	}

	public Array2dScalarByte(int w, int h, int bw, int bh, byte[] array,
			boolean copy) {
		super(w, h, bw, bh, 1, array, copy);
	}

	/*** Pixel Manipulation (NOT PARALLEL) ****************************/

/*	public CxPixelByte getPixel(int xidx, int yidx) {
		return new CxPixelByte(xidx, yidx, width, height, bwidth, bheight,
				extent, data);
	}
*/
	/*@Override
	public void setPixel(CxPixel<byte[]> p, int xidx, int yidx) {
		byte[] values = p.getValue();

		int off = ((width + 2 * bwidth) * bheight + bwidth) * extent;
		int stride = bwidth * extent * 2;
		int pos = off + yidx * (width * extent + stride) + xidx * extent;

		for (int i = 0; i < extent; i++) {
			data[pos + i] = values[i];
		}
		return;
	}*/

	@Override
	public Array2dScalarByte clone() {
		return new Array2dScalarByte(this);
	}

	@Override
	public Array2dScalarByte clone(int newBorderWidth, int newBorderHeight) {
		return new Array2dScalarByte(this, newBorderWidth, newBorderHeight);
	}
}
