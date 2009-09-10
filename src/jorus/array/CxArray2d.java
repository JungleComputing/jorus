/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

import jorus.pixel.CxPixel;
//import java.lang.reflect.Constructor;

public abstract class CxArray2d<T> implements Cloneable {
	/*** Private Properties *******************************************/

	protected int width = 0; // array width
	protected int height = 0; // array height
	protected int borderWidth = 0; // array border width
	protected int borderHeight = 0; // array border height
	protected int extent = 0; // pixel extent
	protected T data = null; // array of elements (not pixels)

	protected int partialDataWidth = 0; // partial array width
	protected int partialDataHeight = 0; // partial array height
	protected T partialData = null; // partial array of elements

	protected int globalState = NONE;
	protected int partialState = NONE;
	protected int distributionType = NONE;

	/*** Distribution States ******************************************/

	public static final int NONE = 0; // empty state
	public static final int CREATED = 1; // global created
	public static final int VALID = 2; // global/local valid
	public static final int INVALID = 3; // global/local invalid
	public static final int PARTIAL = 4; // scattered structure
	public static final int FULL = 5; // replicated structure
	public static final int NOT_RED = 6; // not reduced

	/*** Public Methods ***********************************************/

	public CxArray2d(int width, int height, int borderWidth, int borderHeight,
			int extent, T data) {
		// NOTE: here we assume array to be of length (w+2*bw)*(h+2*bh)
		// Subclasses should make sure that this is indeed the case!!!!

		setDimensions(width, height, borderWidth, borderHeight, extent);
		this.data = data;
		if (this.data != null) {
			globalState = VALID;
		}
	}

	protected void setDimensions(int width, int height, int borderWidth,
			int borderHeight, int extent) {
		this.width = width;
		this.height = height;
		this.borderWidth = borderWidth;
		this.borderHeight = borderHeight;
		this.extent = extent;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getBorderWidth() {
		return borderWidth;
	}

	public int getBorderHeight() {
		return borderHeight;
	}

	public int getExtent() {
		return extent;
	}

	public T getData() {
		return data;
	}

	public int getPartialWidth() {
		return partialDataWidth;
	}

	public int getPartialHeight() {
		return partialDataHeight;
	}

	public T getPartialData() {
		return partialData;
	}

	public void setPartialData(int width, int height, T data, int state,
			int distributionType) {
		// This assumes data size (pwidth+2*bwidth)*(pheight+2*bheight)

		partialDataWidth = width;
		partialDataHeight = height;
		partialData = data;
		partialState = state;
		this.distributionType = distributionType;
	}

	public boolean equalExtent(CxPixel<T> pixel) {
		return (extent == pixel.getExtent());
	}

	public boolean equalSignature(CxArray2d<T> array) {
		// NOTE: The array borders do not need to be equal in size!!!
		return (width == array.getWidth() && height == array.getHeight() && extent == array
				.getExtent());
	}

	public int getGlobalState() {
		return globalState;
	}

	public void setGlobalState(int state) {
		globalState = state;
	}

	public int getLocalState() {
		return partialState;
	}

	public void setLocalState(int state) {
		partialState = state;
	}

	public int getDistributionType() {
		return distributionType;
	}

	public void setDistributionType(int type) {
		distributionType = type;
	}

	/*** Clone ********************************************************/

	public abstract CxArray2d<T> clone();

	public abstract CxArray2d<T> clone(int newBorderWidth, int newBorderHeight);

	/*** Creator from byte[] ******************************************/

//	// NOTE: THIS SOLUTION REQUIRES ALL SUBCLASSES TO HAVE A CONSTRUCTOR
//	// WITH A byte[] AS ONE OF ITS PARAMETERS...
//	// TODO Help! what is this ??
//	public static <U> U makeFromData(int w, int h, int bw, int bh,
//			byte[] array, Class<U> clazz) {
//		try {
//			Class<?>[] args = new Class[5];
//			args[0] = int.class;
//			args[1] = int.class;
//			args[2] = int.class;
//			args[3] = int.class;
//			args[4] = byte[].class;
//			Constructor<U> c = clazz.getConstructor(args);
//			return c.newInstance(w, h, bw, bh, array);
//		} catch (Throwable e) {
//			e.printStackTrace();
//			return null;
//		}
//	}

	/*** Single Pixel (Value) Operations ******************************/

	public abstract CxArray2d<T> setSingleValue(CxPixel<T> pixel, int xIndex,
			int yIndex, boolean inplace);

	/*** Unary Pixel Operations ***************************************/

	public abstract CxArray2d<T> setVal(CxPixel<T> value, boolean inplace);

	public abstract CxArray2d<T> mulVal(CxPixel<T> value, boolean inplace);

	/*** Binary Pixel Operations **************************************/

	public abstract CxArray2d<T> add(CxArray2d<T> array, boolean inplace);

	public abstract CxArray2d<T> sub(CxArray2d<T> array, boolean inplace);

	public abstract CxArray2d<T> mul(CxArray2d<T> array, boolean inplace);

	public abstract CxArray2d<T> div(CxArray2d<T> array, boolean inplace);

	/*** Pixel Manipulation (NOT PARALLEL) ****************************/

	public abstract CxPixel<T> getPixel(int xIndex, int yIndex);

	public abstract void setPixel(CxPixel<T> pixel, int xIndex, int yIndex);
}
