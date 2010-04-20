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


public class Array2dVecLong extends Array2dLongs
{
    /*** Public Methods ***********************************************/
    public Array2dVecLong(Array2dVecLong orig, int newBW, int newBH) {
        super(orig, newBW, newBH);
    }

    public Array2dVecLong(Array2dVecLong orig) {
        super(orig);
    }

    public Array2dVecLong(int w, int h, int bw, int bh, int extent, boolean create) {
        super(w, h, bw, bh, extent, create);
    }

    public Array2dVecLong(int w, int h, int bw, int bh, int extent, long[] array, 
            boolean copy) {
        super(w, h, bw, bh, extent, array, copy);
    }

    /*** Pixel Manipulation (NOT PARALLEL) ****************************/

    /*@Override
    public CxPixelLong getPixel(int xidx, int yidx)
    {
        return new CxPixelLong(xidx, yidx, width, height,
                bwidth, bheight, extent, data);
    }*/

	/*@Override
	public void setPixel(CxPixel<long[]> p, int xidx, int yidx) {
		long[] values = p.getValue();

		int off = ((width + 2 * bwidth) * bheight + bwidth) * extent;
		int stride = bwidth * extent * 2;
		int pos = off + yidx * (width * extent + stride) + xidx * extent;

		for (int i = 0; i < extent; i++) {
			data[pos + i] = values[i];
		}
		return;
    }*/
    
    @Override
    public Array2dVecLong clone() {
        return new Array2dVecLong(this);
    }

    @Override
    public Array2dVecLong clone(int newBorderWidth, int newBorderHeight) {
        return new Array2dVecLong(this, newBorderWidth, newBorderHeight);
    }
}
