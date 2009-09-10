/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

import jorus.operations.bpo.CxBpoAddDouble;
import jorus.operations.bpo.CxBpoDivDouble;
import jorus.operations.bpo.CxBpoGetPixEltDouble;
import jorus.operations.bpo.CxBpoMulDouble;
import jorus.operations.bpo.CxBpoSubDouble;
import jorus.operations.bpo.CxBpoToHistDouble;
import jorus.operations.convolution.CxGenConv2dDouble;
import jorus.operations.convolution.CxGenConv2dSepGauss;
import jorus.operations.setborder.CxSetBorderMirrorDouble;
import jorus.operations.svo.CxSvoSetDouble;
import jorus.operations.upo.CxUpoMulValDouble;
import jorus.operations.upo.CxUpoRGB2OOO;
import jorus.operations.upo.CxUpoSetValDouble;
import jorus.pixel.*;

/**
 * @author Timo van Kessel
 *
 */
public class CxArray2dDoubles extends CxArray2d<double[]> {
	/*** Public Methods ***********************************************/
	public CxArray2dDoubles(int width, int height, int extent) {
		this(width, height, extent, null);
	}

	public CxArray2dDoubles(int width, int height, int extent, double[] array) {
		this(width, height, 0, 0, extent, array);
	}

	public CxArray2dDoubles(int width, int height, int borderWidth,
			int borderHeight, int extent, double[] data) {
		// Initialize

		super(width, height, borderWidth, borderHeight, extent, data);

		// Create new array and copy values, ignoring border values

		int fullw = width + 2 * borderWidth;
		int fullh = height + 2 * borderHeight;
		int start = (fullw * borderHeight + borderWidth) * extent;

		double[] newarray = new double[fullw * fullh * extent];

		if (this.data != null) {
			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width * extent; i++) {
					newarray[start + j * fullw * extent + i] = this.data[j
							* width * extent + i];
				}
			}
		}
		this.data = newarray;
		globalState = VALID;
	}

	/*** Single Pixel (Value) Operations ******************************/

	@Override
	public CxArray2d<double[]> setSingleValue(CxPixel<double[]> pixel,
			int xIndex, int yIndex, boolean inplace) {
		if (!equalExtent(pixel))
			return null;
		return new CxSvoSetDouble(this, xIndex, yIndex, inplace, pixel
				.getValue()).dispatch();
	}

	/*** Unary Pixel Operations ***************************************/

	@Override
	public CxArray2d<double[]> setVal(CxPixel<double[]> pixel, boolean inplace) {
		if (!equalExtent(pixel))
			return null;
		return new CxUpoSetValDouble(this, inplace, pixel.getValue())
				.dispatch();
	}

	@Override
	public CxArray2d<double[]> mulVal(CxPixel<double[]> pixel, boolean inplace) {
		if (!equalExtent(pixel))
			return null;
		return new CxUpoMulValDouble(this, inplace, pixel.getValue())
				.dispatch();
	}

	/*** Binary Pixel Operations **************************************/

	@Override
	public CxArray2d<double[]> add(CxArray2d<double[]> array, boolean inplace) {
		if (!equalSignature(array))
			return null;
		return new CxBpoAddDouble(this, array, inplace).dispatch();
	}

	@Override
	public CxArray2d<double[]> sub(CxArray2d<double[]> array, boolean inplace) {
		if (!equalSignature(array))
			return null;
		return new CxBpoSubDouble(this, array, inplace).dispatch();
	}

	@Override
	public CxArray2d<double[]> mul(CxArray2d<double[]> array, boolean inplace) {
		if (!equalSignature(array))
			return null;
		return new CxBpoMulDouble(this, array, inplace).dispatch();
	}

	@Override
	public CxArray2d<double[]> div(CxArray2d<double[]> array, boolean inplace) {
		if (!equalSignature(array))
			return null;
		return new CxBpoDivDouble(this, array, inplace).dispatch();
	}

	/*** Generalized Convolution Operations ***************************/

	public CxArray2d<double[]> convolution(CxArray2d<double[]> kernel) {
		if (extent != kernel.extent)
			return null;
		return new CxGenConv2dDouble(this, kernel,
				new CxSetBorderMirrorDouble()).dispatch();
	}

	/*** Pixel Manipulation (NOT PARALLEL) ****************************/

	@Override
	public CxPixelDouble getPixel(int xIndex, int yIndex) {
		return new CxPixelDouble(xIndex, yIndex, width, height, borderWidth,
				borderHeight, extent, data);
	}

	@Override
	public void setPixel(CxPixel<double[]> pixel, int xIndex, int yIndex) {
		double[] values = pixel.getValue();

		int offset = ((width + 2 * borderWidth) * borderHeight + borderWidth)
				* extent;
		int stride = borderWidth * extent * 2;
		int pos = offset + yIndex * (width * extent + stride) + xIndex * extent;

		for (int i = 0; i < extent; i++) {
			data[pos + i] = values[i];
		}
		return;
	}

	/*** Separated Gaussian Filter Operations *************************/

	
	/**
	 * Requires an extent of 1
	 * @param sigma
	 * @param orderX
	 * @param orderY
	 * @param truncation
	 * @return
	 */
	public CxArray2dDoubles gaussDerivative(double sigma, int orderX,
			int orderY, double truncation) {
		return gaussDerivative(sigma, sigma, orderX, orderY, truncation);
	}
	
	

	/**
	 * Requires an extent of 1
	 * @param sx
	 * @param sy
	 * @param orderX
	 * @param orderY
	 * @param truncation
	 * @return
	 */
	public CxArray2dDoubles gaussDerivative(double sx, double sy, int orderX,
			int orderY, double truncation) {
		if (extent != 1) {
			return null;
		}
		CxArray2dDoubles gx = CxGaussian1d.create(sx, orderX, 3 * sx * 2 + 1,
				width, 0);
		CxArray2dDoubles gy = CxGaussian1d.create(sy, orderY, 3 * sy * 2 + 1,
				height, 0);
		return (CxArray2dDoubles) new CxGenConv2dSepGauss(this, gx, gy,
				new CxSetBorderMirrorDouble()).dispatch();
	}

	/*** Histogram Operations *****************************************/

	/**
	 * Requires an extent of 1
	 * @param a
	 * @param nBins
	 * @param minVal
	 * @param maxVal
	 * @return
	 */
	public double[] impreciseHistogram(CxArray2dDoubles a, int nBins,
			double minVal, double maxVal) {
		if (extent != 1 || !equalSignature(a)) {
			return null;
		}
		return new CxBpoToHistDouble(this, a, nBins, minVal, maxVal).dispatch();
	}

	/*** Unary Pixel Operations ***************************************/

	/**
	 * Requires an extent of 3
	 * @param inpl
	 * @return
	 */
	public CxArray2dDoubles convertRGB2OOO(boolean inpl) {
		return (CxArray2dDoubles) new CxUpoRGB2OOO(this, inpl).dispatch();
	}

	/*** Binary Pixel Operations **************************************/

	// FIXME do we need to correct the return type??
	/**
	 * Requires an extent of 3
	 * @param idx
	 * @return A plane as an image with an extent of 1
	 */
	public CxArray2dDoubles getPlane(int idx) {
		double[] a = new double[(width + 2 * borderWidth) * (height + 2 * borderHeight)];
		CxArray2dDoubles dst = new CxArray2dDoubles(width, height,
				borderWidth, borderHeight, 1, a);
		return (CxArray2dDoubles) new CxBpoGetPixEltDouble(dst, this,
				true, idx).dispatch();
	}
	
	/*** Clone ********************************************************/

	@Override
	public CxArray2dDoubles clone() {
		CxArray2dDoubles c = new CxArray2dDoubles(width + 2 * borderWidth,
				height + 2 * borderHeight, extent, data.clone());
		c.setDimensions(width, height, borderWidth, borderHeight, extent);
		c.setGlobalState(globalState);
		if (partialData != null) {
			c.setPartialData(partialDataWidth, partialDataHeight, partialData
					.clone(), partialState, distributionType);
		}
		return c;
	}

	@Override
	public CxArray2dDoubles clone(int newBorderWidth, int newBorderHeight) {
		double[] newdata = new double[width * height * extent];

		int off = ((width + 2 * borderWidth) * borderHeight + borderWidth)
				* extent;
		int stride = borderWidth * extent * 2;
		int srcPtr = 0;
		int dstPtr = 0;

		for (int j = 0; j < height; j++) {
			srcPtr = off + j * (width * extent + stride);
			dstPtr = j * (width * extent);
			for (int i = 0; i < width * extent; i++) {
				newdata[dstPtr + i] = data[srcPtr + i];
			}
		}
		CxArray2dDoubles c = new CxArray2dDoubles(width, height,
				newBorderWidth, newBorderHeight, extent, newdata);
		c.setGlobalState(globalState);
		if (partialData != null) {
			double[] newpdata = new double[(partialDataWidth + 2 * newBorderWidth)
					* (partialDataHeight + 2 * newBorderHeight) * extent];

			int srcOff = ((partialDataWidth + 2 * borderWidth) * borderHeight + borderWidth)
					* extent;
			int dstOff = ((partialDataWidth + 2 * newBorderWidth)
					* newBorderHeight + newBorderWidth)
					* extent;

			for (int j = 0; j < partialDataHeight; j++) {
				srcPtr = srcOff + j * (partialDataWidth + 2 * borderWidth)
						* extent;
				dstPtr = dstOff + j * (partialDataWidth + 2 * newBorderWidth)
						* extent;
				for (int i = 0; i < partialDataWidth * extent; i++) {
					newpdata[dstPtr + i] = partialData[srcPtr + i];
				}
			}
			c.setPartialData(partialDataWidth, partialDataHeight, newpdata,
					partialState, distributionType);
		}
		return c;
	}

}
