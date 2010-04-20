/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

import jorus.pixel.*;

public class Array2dVecByte extends Array2dBytes {
	/*** Public Methods ***********************************************/

	public Array2dVecByte(Array2dVecByte orig, int newBW, int newBH) {
		super(orig, newBW, newBH);
	}

	public Array2dVecByte(Array2dVecByte orig) {
		super(orig);
	}

	public Array2dVecByte(int w, int h, int bw, int bh, int extent,
			boolean create) {
		super(w, h, bw, bh, extent, create);
	}

	public Array2dVecByte(int w, int h, int bw, int bh, int extent,
			byte[] array, boolean copy) {
		super(w, h, bw, bh, extent, array, copy);
	}

	/*** Pixel Manipulation (NOT PARALLEL) ****************************/

	/*@Override
	public CxPixelByte getPixel(int xidx, int yidx) {
		return new CxPixelByte(xidx, yidx, width, height, bwidth, bheight,
				extent, data);
	}*/

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
	public Array2dVecByte clone() {
		return new Array2dVecByte(this);
	}

	@Override
	public Array2dVecByte clone(int newBorderWidth, int newBorderHeight) {
		return new Array2dVecByte(this, newBorderWidth, newBorderHeight);
	}
}
