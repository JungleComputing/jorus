/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

import jorus.operations.bpo.CxBpoAddByte;
import jorus.operations.bpo.CxBpoDivByte;
import jorus.operations.bpo.CxBpoMulByte;
import jorus.operations.bpo.CxBpoSubByte;
import jorus.operations.svo.CxSvoSetByte;
import jorus.operations.upo.CxUpoMulValByte;
import jorus.operations.upo.CxUpoSetValByte;
import jorus.pixel.*;

public class CxArray2dBytes extends CxArray2d<byte[]> {
	/*** Public Methods ***********************************************/
	public CxArray2dBytes(int width, int height, int extent) {
		this(width, height, extent, null);
	}

	public CxArray2dBytes(int width, int height, int extent, byte[] array) {
		this(width, height, 0, 0, extent, array);
	}

	public CxArray2dBytes(int width, int height, int borderWidth,
			int borderHeight, int extent, byte[] data) {
		// Initialize

		super(width, height, borderWidth, borderHeight, extent, data);

		// Create new array and copy values, ignoring border values

		int fullWidth = width + 2 * borderWidth;
		int fullHeight = height + 2 * borderHeight;
		int start = (fullWidth * borderHeight + borderWidth) * extent;

		byte[] newArray = new byte[fullWidth * fullHeight * extent];

		if (this.data != null) {
			for (int j = 0; j < height; j++) {
				for (int i = 0; i < width * extent; i++) {
					newArray[start + j * fullWidth * extent + i] = this.data[j
							* width * extent + i];
				}
			}
		}
		this.data = newArray;
		globalState = VALID;
	}

	/*** Single Pixel (Value) Operations ******************************/
	@Override
	public CxArray2d<byte[]> setSingleValue(CxPixel<byte[]> pixel, int xIndex,
			int yIndex, boolean inplace) {
		if (!equalExtent(pixel))
			return null;
		return new CxSvoSetByte(this, xIndex, yIndex, inplace, pixel.getValue())
				.dispatch();
	}

	/*** Unary Pixel Operations ***************************************/
	@Override
	public CxArray2d<byte[]> setVal(CxPixel<byte[]> pixel, boolean inplace) {
		if (!equalExtent(pixel))
			return null;
		return new CxUpoSetValByte(this, inplace, pixel.getValue()).dispatch();
	}

	@Override
	public CxArray2d<byte[]> mulVal(CxPixel<byte[]> pixel, boolean inplace) {
		if (!equalExtent(pixel))
			return null;
		return new CxUpoMulValByte(this, inplace, pixel.getValue()).dispatch();
	}

	/*** Binary Pixel Operations **************************************/

	@Override
	public CxArray2d<byte[]> add(CxArray2d<byte[]> array, boolean inplace) {
		if (!equalSignature(array))
			return null;
		return new CxBpoAddByte(this, array, inplace).dispatch();
	}

	@Override
	public CxArray2d<byte[]> sub(CxArray2d<byte[]> array, boolean inplace) {
		if (!equalSignature(array))
			return null;
		return new CxBpoSubByte(this, array, inplace).dispatch();
	}

	@Override
	public CxArray2d<byte[]> mul(CxArray2d<byte[]> array, boolean inplace) {
		if (!equalSignature(array))
			return null;
		return new CxBpoMulByte(this, array, inplace).dispatch();
	}

	@Override
	public CxArray2d<byte[]> div(CxArray2d<byte[]> array, boolean inplace) {
		if (!equalSignature(array))
			return null;
		return new CxBpoDivByte(this, array, inplace).dispatch();
	}

	/*** Pixel Manipulation (NOT PARALLEL) ****************************/

	@Override
	public CxPixelByte getPixel(int xIndex, int yIndex) {
		return new CxPixelByte(xIndex, yIndex, width, height, borderWidth,
				borderHeight, extent, data);
	}

	@Override
	public void setPixel(CxPixel<byte[]> pixel, int xIndex, int yIndex) {
		byte[] values = pixel.getValue();

		int offset = ((width + 2 * borderWidth) * borderHeight + borderWidth)
				* extent;
		int stride = borderWidth * extent * 2;
		int pos = offset + yIndex * (width * extent + stride) + xIndex * extent;

		for (int i = 0; i < extent; i++) {
			data[pos + i] = values[i];
		}
		return;
	}

	/*** Clone ********************************************************/

	@Override
	public CxArray2dBytes clone() {
		CxArray2dBytes c = new CxArray2dBytes(width + 2 * borderWidth,
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
	public CxArray2dBytes clone(int newBorderWidth, int newBorderHeight) {
		byte[] newdata = new byte[width * height * extent];

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
		CxArray2dBytes c = new CxArray2dBytes(width, height,
				newBorderWidth, newBorderHeight, extent, newdata);
		c.setGlobalState(globalState);
		if (partialData != null) {
			byte[] newpdata = new byte[(partialDataWidth + 2 * newBorderWidth)
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
