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

public class Array2dScalarShort extends Array2dShorts {
	/*** Public Methods ***********************************************/

	public Array2dScalarShort(Array2dScalarShort orig, int newBW, int newBH) {
		super(orig, newBW, newBH);
	}

	public Array2dScalarShort(Array2dScalarShort orig) {
		super(orig);
	}

	public Array2dScalarShort(int w, int h, int bw, int bh, boolean create) {
		super(w, h, bw, bh, 1, create);
	}

	public Array2dScalarShort(int w, int h, int bw, int bh, short[] array,
			boolean copy) {
		super(w, h, bw, bh, 1, array, copy);
	}

	/*** Pixel Manipulation (NOT PARALLEL) ****************************/
	/*@Override
	public CxPixelShort getPixel(int xidx, int yidx) {
		return new CxPixelShort(xidx, yidx, width, height, bwidth, bheight,
				extent, data);
	}*/

	/*@Override
	public void setPixel(CxPixel<short[]> p, int xidx, int yidx) {
		short[] values = p.getValue();

		int off = ((width + 2 * bwidth) * bheight + bwidth) * extent;
		int stride = bwidth * extent * 2;
		int pos = off + yidx * (width * extent + stride) + xidx * extent;

		for (int i = 0; i < extent; i++) {
			data[pos + i] = values[i];
		}
		return;
	}*/

	@Override
	public Array2dScalarShort clone() {
		return new Array2dScalarShort(this);
	}

	@Override
	public Array2dScalarShort clone(int newBorderWidth, int newBorderHeight) {
		return new Array2dScalarShort(this, newBorderWidth, newBorderHeight);
	}
}
