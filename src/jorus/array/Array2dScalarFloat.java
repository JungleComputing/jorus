/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

import jorus.operations.bpo.BpoToHistFloat;
import jorus.patterns.PatBpoArrayToHistArray;
import jorus.patterns.PatBpoToHist;

public class Array2dScalarFloat extends Array2dFloat<Array2dScalarFloat> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5366288690791184319L;

	/** * Public Methods ********************************************** */

	public Array2dScalarFloat(Array2dScalarFloat orig, int newBW, int newBH, boolean copyData) {
		
		super(orig, newBW, newBH, copyData);
	}

	public Array2dScalarFloat(Array2dScalarFloat orig, boolean copyData) {
		super(orig, copyData);
	}

	public Array2dScalarFloat(int w, int h, int bw, int bh, boolean create) {
		super(w, h, bw, bh, 1, create);
	}

	public Array2dScalarFloat(int w, int h, int bw, int bh, float[] array,
			boolean copy) {
		super(w, h, bw, bh, 1, array, copy);
	}

	public Array2dScalarFloat(int w, int h, float[] array, boolean copy) {
		super(w, h, 0, 0, 1, array, copy);
	}

	/** * Separated Gaussian Filter Operations ************************ */

//	public Array2dScalarDouble gaussDerivative(double sigma, int orderX,
//			int orderY, double truncation) {
//		return gaussDerivative(sigma, sigma, orderX, orderY, truncation);
//	}
//
//	private Array2dScalarDouble gaussDerivative(double sigmaX, double sigmaY,
//			int orderX, int orderY, double truncation) {
//		if(truncation < 1) {
//			truncation = 3; //default truncation
//		}
//		
//		Array2dScalarDouble gx = Gaussian1d.create(sigmaX, orderX, 0.995,
//				(int) (truncation * sigmaX * 2 + 1), width);
//		Array2dScalarDouble gy = Gaussian1d.create(sigmaY, orderY, 0.995,
//				(int) (truncation * sigmaY * 2 + 1), height);
//
//		return (Array2dScalarDouble) PatGenConv2dSep.dispatch(this, gx, gy,
//				new GenConv2dSepGauss(), new SetBorderMirrorDouble());
//	}
//
//	// theta in radians
//	public Array2dScalarDouble gaussDerivativeRot(double theta, double su,
//			double sv, int orderU, int orderV) {
//		Array2dScalarDouble gu = Gaussian1d.create(su, orderU, 0.995,
//				(int) (3 * su * 2 + 1), width);
//		Array2dScalarDouble gv = Gaussian1d.create(sv, orderV, 0.995,
//				(int) (3 * sv * 2 + 1), height);
//		return (Array2dScalarDouble) PatGenConvRot2dSep.dispatch(this, gu, gv,
//				theta, new GenConvRot2dSepGauss(), new SetBorderMirrorDouble());
//	}

	/** * Histogram Operations **************************************** */

	public double[] impreciseHistogram(Array2dScalarFloat a, int nBins,
			double minVal, double maxVal) {
		if (!equalSignature(a))
			return null;
		return PatBpoToHist.dispatch(this, a, nBins, minVal, maxVal,
				new BpoToHistFloat());
	}

	public double[][] impreciseHistograms(Array2dScalarFloat[] a, int nBins,
			double minVal, double maxVal) {

		if (a == null || a.length == 0 || !equalSignature(a[0])) {
			return null;
		}

		return PatBpoArrayToHistArray.dispatch(this, a, nBins, minVal, maxVal,
				new BpoToHistFloat());
	}

	/** * Pixel Manipulation (NOT PARALLEL) *************************** */

	/*@Override
	public CxPixelDouble getPixel(int xidx, int yidx) {
		return new CxPixelDouble(xidx, yidx, width, height, bwidth,
				bheight, extent, data);
	}*/

/*	@Override
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
	public Array2dScalarFloat clone() {
		return new Array2dScalarFloat(this, true);
	}

	@Override
	public Array2dScalarFloat clone(int newBorderWidth, int newBorderHeight) {
		if(newBorderWidth == getBorderWidth() && newBorderHeight == getBorderHeight()) {
			return clone();
		}
		return new Array2dScalarFloat(this, newBorderWidth, newBorderHeight, true);
	}
	
	@Override
	public Array2dScalarFloat shallowClone() {
		return new Array2dScalarFloat(this, false);
	}
	
	@Override
	public Array2dScalarFloat shallowClone(int newBorderWidth, int newBorderHeight) {
		if(newBorderWidth == getBorderWidth() && newBorderHeight == getBorderHeight()) {
			return shallowClone();
		}
		return new Array2dScalarFloat(this, newBorderWidth, newBorderHeight, false);
	}

	@Override
	public Array2dScalarFloat prepareForSideChannel() {
		Array2dScalarFloat result = new Array2dScalarFloat(getWidth(), getHeight(), getBorderWidth(), getBorderHeight(), false);
		result.setState(GLOBAL_VALID);
		setState(GLOBAL_VALID);
		return result;
	}
	
	@Override
	public Array2dScalarFloat createCompatibleArray(int width, int height, int borderWidth,
			int borderHeight) {
		Array2dScalarFloat result = new Array2dScalarFloat(width, height, borderWidth, borderHeight, true);
		return result;
	}
}
