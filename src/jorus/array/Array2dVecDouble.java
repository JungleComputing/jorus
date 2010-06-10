/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

import jorus.operations.bpo.BpoGetPixEltDouble;
import jorus.operations.upo.UpoRGB2OOO;
import jorus.patterns.PatBpo;
import jorus.patterns.PatUpo;

public class Array2dVecDouble extends Array2dDoubles {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3630440685388095401L;

	/*** Public Methods ***********************************************/

	public Array2dVecDouble(Array2dVecDouble orig, int newBW, int newBH) {
		super(orig, newBW, newBH);
	}

	public Array2dVecDouble(Array2dVecDouble orig) {
		super(orig);
	}

	public Array2dVecDouble(int width, int height, int borderWidth,
			int borderHeight, int extent, boolean create) {
		super(width, height, borderWidth, borderHeight, extent, create);
	}

	public Array2dVecDouble(int width, int height, int borderWidth,
			int borderHeight, int extent, double[] array, boolean copy) {
		super(width, height, borderWidth, borderHeight, extent, array, copy);
	}

	public Array2dVecDouble(int width, int height, int extent, double[] array,
			boolean copy) {
		super(width, height, 0, 0, extent, array, copy);
	}

	/*** Unary Pixel Operations ***************************************/

	public Array2dVecDouble convertRGB2OOO(boolean inpl)
			throws UnsupportedOperationException {
		if (getExtent() != 3) {
			throw new UnsupportedOperationException("extent != 3");
		}
		return (Array2dVecDouble) PatUpo.dispatch(this, inpl, new UpoRGB2OOO());
	}

	/*** Binary Pixel Operations **************************************/

	public Array2dScalarDouble getPlane(int idx) {
		// Skip this new, since the constructor will do it for us -- J
		// double[] a = new double[(width+2*bwidth)*(height+2*bheight)];

		Array2dScalarDouble dst = new Array2dScalarDouble(getWidth(), getHeight(),
				getBorderWidth(), getBorderHeight(), false);

		return (Array2dScalarDouble) PatBpo.dispatch(dst, this, true,
				new BpoGetPixEltDouble(idx));
	}

	/*** Pixel Manipulation (NOT PARALLEL) ****************************/
	/*@Override
	public CxPixelDouble getPixel(int xidx, int yidx) {
		return new CxPixelDouble(xidx, yidx, width, height, bwidth, bheight,
				extent, data);
	}*/

	/*@Override
	public void setPixel(CxPixel<double[]> p, int xidx, int yidx) {
		double[] values = p.getValue();

		int off = ((width + 2 * bwidth) * bheight + bwidth) * extent;
		int stride = bwidth * extent * 2;
		int pos = off + yidx * (width * extent + stride) + xidx * extent;

		for (int i = 0; i < extent; i++) {
			data[pos + i] = values[i];
		}
		return;
	}*/

	@Override
	public Array2dVecDouble clone() {
		return new Array2dVecDouble(this);
	}

	@Override
	public Array2dVecDouble clone(int newBorderWidth, int newBorderHeight) {
		return new Array2dVecDouble(this, newBorderWidth, newBorderHeight);
	}

	
	
	@Override
	public Array2dVecDouble prepareForSideChannel() {
		Array2dVecDouble result = new Array2dVecDouble(getWidth(), getHeight(), getBorderWidth(), getBorderHeight(), getExtent(), false);
		result.setState(GLOBAL_VALID);
		setState(GLOBAL_VALID);
		return result;
	}
}
