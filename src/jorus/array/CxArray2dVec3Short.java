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


public class CxArray2dVec3Short extends CxArray2dShorts
{
	/*** Public Methods ***********************************************/

	public CxArray2dVec3Short(int w, int h)
	{
		this(w, h, 0, 0, null);
	}


	public CxArray2dVec3Short(int w, int h, short[] array)
	{
		this(w, h, 0, 0, array);
	}


	public CxArray2dVec3Short(int w, int h,
							  int bw, int bh, short[] array)
	{
		super(w, h, bw, bh, 3, array);
	}


	/*** Clone ********************************************************/

	public CxArray2dVec3Short clone()
	{
		CxArray2dVec3Short c = new CxArray2dVec3Short(width+2*bwidth,
										height+2*bheight, data.clone());
		c.setDimensions(width, height, bwidth, bheight, extent);
		c.setGlobalState(gstate);
		if (pdata != null) {
			c.setPartialData(pwidth, pheight,
							 pdata.clone(), pstate, ptype);
		}
		return c;
	}


	public CxArray2dVec3Short clone(int newBW, int newBH)
	{
		short[] newdata = new short[width*height*extent];

		int off    = ((width+2*bwidth) * bheight + bwidth) * extent;
		int stride = bwidth * extent * 2;
		int srcPtr = 0;
		int dstPtr = 0;

		for (int j=0; j<height; j++) {
			srcPtr = off + j*(width*extent+stride);
			dstPtr = j*(width*extent);
			for (int i=0; i<width*extent; i++) {
				newdata[dstPtr + i] = data[srcPtr + i];
			}
		}
		CxArray2dVec3Short c = new CxArray2dVec3Short(width, height,
												newBW, newBH, newdata);
		c.setGlobalState(gstate);
		if (pdata != null) {
			short[] newpdata = new
					short[(pwidth+2*newBW)*(pheight+2*newBH)*extent];

			int srcOff = ((pwidth+2*bwidth)*bheight+bwidth)*extent;
			int dstOff = ((pwidth+2*newBW)*newBH+newBW)*extent;

			for (int j=0; j<pheight; j++) {
				srcPtr = srcOff + j*(pwidth+2*bwidth)*extent;
				dstPtr = dstOff + j*(pwidth+2*newBW)*extent;
				for (int i=0; i<pwidth*extent; i++) {
					newpdata[dstPtr + i] = pdata[srcPtr + i];
				}
			}
			c.setPartialData(pwidth, pheight, newpdata, pstate, ptype);
		}
		return c;
	}


	/*** Pixel Manipulation (NOT PARALLEL) ****************************/

	public CxPixelVec3Short getPixel(int xidx, int yidx)
	{
		return new CxPixelVec3Short(xidx, yidx, width, height,
												bwidth, bheight, data);
	}


	public void setPixel(CxPixel p, int xidx, int yidx)
	{
		short[] values = ((CxPixelVec3Short)p).getValue();

		int off = ((width + 2*bwidth) * bheight + bwidth) * extent;
		int stride = bwidth * extent * 2;
		int pos = off + yidx*(width*extent+stride) + xidx*extent;

		for (int i=0; i<extent; i++) {
			data[pos+i] = values[i];
		}
		return;
	}
}
