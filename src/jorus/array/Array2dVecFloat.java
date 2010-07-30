/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

import jorus.operations.upo.UpoRGB2OOOFloat;
import jorus.patterns.PatUpo;

public class Array2dVecFloat extends Array2dFloat<Array2dVecFloat> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3630440685388095401L;

	/*** Public Methods ***********************************************/

	public Array2dVecFloat(Array2dVecFloat orig, int newBW, int newBH) {
		super(orig, newBW, newBH);
	}

	public Array2dVecFloat(Array2dVecFloat orig, boolean copyData) {
		super(orig, copyData);
	}

	public Array2dVecFloat(int width, int height, int borderWidth,
			int borderHeight, int extent, boolean create) {
		super(width, height, borderWidth, borderHeight, extent, create);
	}

	public Array2dVecFloat(int width, int height, int borderWidth,
			int borderHeight, int extent, float[] array, boolean copy) {
		super(width, height, borderWidth, borderHeight, extent, array, copy);
	}

	public Array2dVecFloat(int width, int height, int extent, float[] array,
			boolean copy) {
		super(width, height, 0, 0, extent, array, copy);
	}

	/*** Unary Pixel Operations ***************************************/

	public Array2dVecFloat convertRGB2OOO(boolean inpl)
			throws UnsupportedOperationException {
		if (getExtent() != 3) {
			throw new UnsupportedOperationException("extent != 3");
		}
		return (Array2dVecFloat) PatUpo.dispatch(this, inpl, new UpoRGB2OOOFloat());
	}

	/*** Binary Pixel Operations **************************************/

	public Array2dScalarFloat getPlane(int idx) {
		//FIXME fix this
		throw new UnsupportedOperationException("Need fixing");
		/*
		// Skip this new, since the constructor will do it for us -- J
		// double[] a = new double[(width+2*bwidth)*(height+2*bheight)];

		Array2dScalarFloat dst = new Array2dScalarFloat(getWidth(), getHeight(),
				getBorderWidth(), getBorderHeight(), false);

		return (Array2dScalarFloat) PatBpo.dispatch(dst, this, true,
				new BpoGetPixEltFloat(idx));
		*/
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
	public Array2dVecFloat shallowClone() {
		return new Array2dVecFloat(this, false);
	}
	
	@Override
	public Array2dVecFloat clone() {
		return new Array2dVecFloat(this, true);
	}

	@Override
	public Array2dVecFloat clone(int newBorderWidth, int newBorderHeight) {
		if(newBorderWidth == getBorderWidth() && newBorderHeight == getBorderHeight()) {
			return clone();
		}
		return new Array2dVecFloat(this, newBorderWidth, newBorderHeight);
	}

	
	
	@Override
	public Array2dVecFloat prepareForSideChannel() {
		Array2dVecFloat result = new Array2dVecFloat(getWidth(), getHeight(), getBorderWidth(), getBorderHeight(), getExtent(), false);
		result.setState(GLOBAL_VALID);
		setState(GLOBAL_VALID);
		return result;
	}

	@Override
	public Array2dVecFloat createCompatibleArray(int width, int height, int borderWidth,
			int borderHeight) {
		Array2dVecFloat result = new Array2dVecFloat(width, height, borderWidth, borderHeight, getExtent(), true);
		return result;
	}
}
