/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

import jorus.operations.bpo.CxBpoAddShort;
import jorus.operations.bpo.CxBpoMulShort;
import jorus.operations.bpo.CxBpoSubShort;
import jorus.operations.svo.CxSvoSetShort;
import jorus.operations.upo.CxUpoMulValShort;
import jorus.operations.upo.CxUpoSetValShort;
import jorus.pixel.*;

public class CxArray2dShorts extends CxArray2d<short[]> {
	/*** Public Methods ***********************************************/

	public CxArray2dShorts(int width, int height, int extent) {
		this(width, height, extent, null);
	}

	public CxArray2dShorts(int width, int height, int extent, short[] array) {
		this(width, height, 0, 0, extent, array);
	}

	public CxArray2dShorts(int w, int h, int bw, int bh, int e, short[] array) {
		// Initialize

		super(w, h, bw, bh, e, array);

		// Create new array and copy values, ignoring border values

		int fullw = w + 2 * bw;
		int fullh = h + 2 * bh;
		int start = (fullw * bh + bw) * e;

		short[] newarray = new short[fullw * fullh * e];

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
	public CxArray2d<short[]> setSingleValue(CxPixel<short[]> p, int xidx,
			int yidx, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return new CxSvoSetShort(this, xidx, yidx, inpl, p.getValue())
				.dispatch();
	}

	/*** Unary Pixel Operations ***************************************/

	@Override
	public CxArray2d<short[]> setVal(CxPixel<short[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return new CxUpoSetValShort(this, inpl, p.getValue()).dispatch();
	}

	@Override
	public CxArray2d<short[]> mulVal(CxPixel<short[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return new CxUpoMulValShort(this, inpl, p.getValue()).dispatch();
	}

	/*** Binary Pixel Operations **************************************/

	@Override
	public CxArray2d<short[]> add(CxArray2d<short[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return new CxBpoAddShort(this, a, inpl).dispatch();
	}

	@Override
	public CxArray2d<short[]> sub(CxArray2d<short[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return new CxBpoSubShort(this, a, inpl).dispatch();
	}

	@Override
	public CxArray2d<short[]> mul(CxArray2d<short[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return new CxBpoMulShort(this, a, inpl).dispatch();
	}

	@Override
	public CxArray2d<short[]> div(CxArray2d<short[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return new CxBpoMulShort(this, a, inpl).dispatch();
	}

	/*** Pixel Manipulation (NOT PARALLEL) ****************************/

	@Override
	public CxPixelShort getPixel(int xidx, int yidx) {
		return new CxPixelShort(xidx, yidx, width, height, borderWidth,
				borderHeight, extent, data);
	}

	@Override
	public void setPixel(CxPixel<short[]> p, int xidx, int yidx) {
		short[] values = p.getValue();

		int off = ((width + 2 * borderWidth) * borderHeight + borderWidth)
				* extent;
		int stride = borderWidth * extent * 2;
		int pos = off + yidx * (width * extent + stride) + xidx * extent;

		for (int i = 0; i < extent; i++) {
			data[pos + i] = values[i];
		}
		return;
	}

	/*** Clone ********************************************************/

	@Override
	public CxArray2dShorts clone() {
		CxArray2dShorts c = new CxArray2dShorts(width + 2 * borderWidth, height
				+ 2 * borderHeight, extent, data.clone());
		c.setDimensions(width, height, borderWidth, borderHeight, extent);
		c.setGlobalState(globalState);
		if (partialData != null) {
			c.setPartialData(partialDataWidth, partialDataHeight, partialData
					.clone(), partialState, distributionType);
		}
		return c;
	}

	@Override
	public CxArray2dShorts clone(int newBorderWidth, int newBorderHeight) {
		short[] newdata = new short[width * height * extent];

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
		CxArray2dShorts c = new CxArray2dShorts(width, height, newBorderWidth,
				newBorderHeight, extent, newdata);
		c.setGlobalState(globalState);
		if (partialData != null) {
			short[] newpdata = new short[(partialDataWidth + 2 * newBorderWidth)
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
