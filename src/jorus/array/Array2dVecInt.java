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

public class Array2dVecInt extends Array2dInts {
	/*** Public Methods ***********************************************/
	public Array2dVecInt(Array2dVecInt orig, int newBW, int newBH) {
		super(orig, newBW, newBH);
	}

	public Array2dVecInt(Array2dVecInt orig) {
		super(orig);
	}

	public Array2dVecInt(int w, int h, int bw, int bh, int extent,
			boolean create) {
		super(w, h, bw, bh, extent, create);
	}

	public Array2dVecInt(int w, int h, int bw, int bh, int extent, int[] array,
			boolean copy) {
		super(w, h, bw, bh, extent, array, copy);
	}

	/*** Pixel Manipulation (NOT PARALLEL) ****************************/

	/*@Override
	public CxPixelInt getPixel(int xidx, int yidx) {
		return new CxPixelInt(xidx, yidx, width, height, bwidth, bheight,
				extent, data);
	}*/

	/*@Override
	public void setPixel(CxPixel<int[]> p, int xidx, int yidx) {
		int[] values = p.getValue();

		int off = ((width + 2 * bwidth) * bheight + bwidth) * extent;
		int stride = bwidth * extent * 2;
		int pos = off + yidx * (width * extent + stride) + xidx * extent;

		for (int i = 0; i < extent; i++) {
			data[pos + i] = values[i];
		}
		return;
	}*/

	@Override
	public Array2dVecInt clone() {
		return new Array2dVecInt(this);
	}

	@Override
	public Array2dVecInt clone(int newBorderWidth, int newBorderHeight) {
		return new Array2dVecInt(this, newBorderWidth, newBorderHeight);
	}

}
