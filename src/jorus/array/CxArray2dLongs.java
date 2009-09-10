/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

import jorus.operations.bpo.CxBpoAddLong;
import jorus.operations.bpo.CxBpoDivLong;
import jorus.operations.bpo.CxBpoMulLong;
import jorus.operations.bpo.CxBpoSubLong;
import jorus.operations.svo.CxSvoSetLong;
import jorus.operations.upo.CxUpoMulValLong;
import jorus.operations.upo.CxUpoSetValLong;
import jorus.pixel.*;

public class CxArray2dLongs extends CxArray2d<long[]> {
	/*** Public Methods ***********************************************/
	
	public CxArray2dLongs(int width, int height, int extent) {
		this(width, height, extent, null);
	}

	public CxArray2dLongs(int width, int height, int extent, long[] array) {
		this(width, height, 0, 0, extent, array);
	}
	
	public CxArray2dLongs(int w, int h, int bw, int bh, int e, long[] array) {
		// Initialize

		super(w, h, bw, bh, e, array);

		// Create new array and copy values, ignoring border values

		int fullw = w + 2 * bw;
		int fullh = h + 2 * bh;
		int start = (fullw * bh + bw) * e;

		long[] newarray = new long[fullw * fullh * e];

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
	public CxArray2d<long[]> setSingleValue(CxPixel<long[]> p, int xidx,
			int yidx, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return new CxSvoSetLong(this, xidx, yidx, inpl, p.getValue())
				.dispatch();
	}

	/*** Unary Pixel Operations ***************************************/

	@Override
	public CxArray2d<long[]> setVal(CxPixel<long[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return new CxUpoSetValLong(this, inpl, p.getValue()).dispatch();
	}

	@Override
	public CxArray2d<long[]> mulVal(CxPixel<long[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return new CxUpoMulValLong(this, inpl, p.getValue()).dispatch();
	}

	/*** Binary Pixel Operations **************************************/

	@Override
	public CxArray2d<long[]> add(CxArray2d<long[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return new CxBpoAddLong(this, a, inpl).dispatch();
	}

	@Override
	public CxArray2d<long[]> sub(CxArray2d<long[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return new CxBpoSubLong(this, a, inpl).dispatch();
	}

	@Override
	public CxArray2d<long[]> mul(CxArray2d<long[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return new CxBpoMulLong(this, a, inpl).dispatch();
	}

	@Override
	public CxArray2d<long[]> div(CxArray2d<long[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return new CxBpoDivLong(this, a, inpl).dispatch();
	}

	/*** Pixel Manipulation (NOT PARALLEL) ****************************/

	@Override
	public CxPixelLong getPixel(int xidx, int yidx) {
		return new CxPixelLong(xidx, yidx, width, height, borderWidth, borderHeight,
				extent, data);
	}

	@Override
	public void setPixel(CxPixel<long[]> p, int xidx, int yidx) {
		long[] values = p.getValue();

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
	public CxArray2dLongs clone() {
		CxArray2dLongs c = new CxArray2dLongs(width + 2 * borderWidth,
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
	public CxArray2dLongs clone(int newBorderWidth, int newBorderHeight) {
		long[] newdata = new long[width * height * extent];

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
		CxArray2dLongs c = new CxArray2dLongs(width, height,
				newBorderWidth, newBorderHeight, extent, newdata);
		c.setGlobalState(globalState);
		if (partialData != null) {
			long[] newpdata = new long[(partialDataWidth + 2 * newBorderWidth)
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
