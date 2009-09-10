/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

import jorus.operations.bpo.CxBpoAddInt;
import jorus.operations.bpo.CxBpoDivInt;
import jorus.operations.bpo.CxBpoMulInt;
import jorus.operations.bpo.CxBpoSubInt;
import jorus.operations.svo.CxSvoSetInt;
import jorus.operations.upo.CxUpoMulValInt;
import jorus.operations.upo.CxUpoSetValInt;
import jorus.pixel.*;

public class CxArray2dInts extends CxArray2d<int[]> {
	/*** Public Methods ***********************************************/
	
	public CxArray2dInts(int width, int height, int extent) {
		this(width, height, extent, null);
	}

	public CxArray2dInts(int width, int height, int extent, int[] array) {
		this(width, height, 0, 0, extent, array);
	}
	
	public CxArray2dInts(int w, int h, int bw, int bh, int e, int[] array) {
		// Initialize

		super(w, h, bw, bh, e, array);

		// Create new array and copy values, ignoring border values

		int fullw = w + 2 * bw;
		int fullh = h + 2 * bh;
		int start = (fullw * bh + bw) * e;

		int[] newarray = new int[fullw * fullh * e];

		if (data != null) {
			for (int j = 0; j < h; j++) {
				for (int i = 0; i < w * e; i++) {
					newarray[start + j * fullw * e + i] = data[j * w * e + i];
				}
			}
		}
		data = newarray;
		globalState = VALID;
	}

	/*** Single Pixel (Value) Operations ******************************/

	@Override
	public CxArray2d<int[]> setSingleValue(CxPixel<int[]> p, int xidx,
			int yidx, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return new CxSvoSetInt(this, xidx, yidx, inpl, p.getValue()).dispatch();
	}

	/*** Unary Pixel Operations ***************************************/

	@Override
	public CxArray2d<int[]> setVal(CxPixel<int[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return new CxUpoSetValInt(this, inpl, p.getValue()).dispatch();
	}

	@Override
	public CxArray2d<int[]> mulVal(CxPixel<int[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return new CxUpoMulValInt(this, inpl, p.getValue()).dispatch();
	}

	/*** Binary Pixel Operations **************************************/

	@Override
	public CxArray2d<int[]> add(CxArray2d<int[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return new CxBpoAddInt(this, a, inpl).dispatch();
	}

	@Override
	public CxArray2d<int[]> sub(CxArray2d<int[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return new CxBpoSubInt(this, a, inpl).dispatch();
	}

	@Override
	public CxArray2d<int[]> mul(CxArray2d<int[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return new CxBpoMulInt(this, a, inpl).dispatch();
	}

	@Override
	public CxArray2d<int[]> div(CxArray2d<int[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return new CxBpoDivInt(this, a, inpl).dispatch();
	}

	/*** Pixel Manipulation (NOT PARALLEL) ****************************/

	@Override
	public CxPixelInt getPixel(int xidx, int yidx) {
		return new CxPixelInt(xidx, yidx, width, height, borderWidth, borderHeight,
				extent, data);
	}

	@Override
	public void setPixel(CxPixel<int[]> p, int xidx, int yidx) {
		int[] values = p.getValue();

		int off = ((width + 2 * borderWidth) * borderHeight + borderWidth) * extent;
		int stride = borderWidth * extent * 2;
		int pos = off + yidx * (width * extent + stride) + xidx * extent;

		for (int i = 0; i < extent; i++) {
			data[pos + i] = values[i];
		}
		return;
	}
	
	/*** Clone ********************************************************/

	@Override
	public CxArray2dInts clone() {
		CxArray2dInts c = new CxArray2dInts(width + 2 * borderWidth,
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
	public CxArray2dInts clone(int newBorderWidth, int newBorderHeight) {
		int[] newdata = new int[width * height * extent];

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
		CxArray2dInts c = new CxArray2dInts(width, height,
				newBorderWidth, newBorderHeight, extent, newdata);
		c.setGlobalState(globalState);
		if (partialData != null) {
			int[] newpdata = new int[(partialDataWidth + 2 * newBorderWidth)
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
