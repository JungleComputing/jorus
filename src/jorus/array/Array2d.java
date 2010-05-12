/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

import java.io.IOException;
import java.io.Serializable;

import jorus.parallel.PxSystem;
import jorus.parallel.ReduceOp;
import jorus.pixel.Pixel;

public abstract class Array2d<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -378351580205311141L;

	public static final int GLOBAL_NONE = 0;
	public static final int GLOBAL_CREATED = 1;
	public static final int GLOBAL_VALID = 2;
	public static final int GLOBAL_INVALID = 3;
	
	public static final int LOCAL_NONE = 0;
	public static final int LOCAL_PARTIAL = 1;
	public static final int LOCAL_FULL = 2;
	public static final int LOCAL_NOT_REDUCED = 3;

	/** * Private Properties ****************************************** */

	protected int width = 0; // array width
	protected int height = 0; // array height
	protected int bwidth = 0; // array border width
	protected int bheight = 0; // array border height
	protected int extent = 0; // pixel extent

	// protected Class<?> type = null; // element type

	protected T data = null; // array of elements (not pixels)

	// protected GlobalState globalState;
	protected int globalState;

	protected transient int pwidth = 0; // partial array width
	protected transient int pheight = 0; // partial array height
	protected transient T pdata = null; // partial array of elements
	protected transient int localState = LOCAL_NONE;
	protected transient ReduceOp requiredReduceOp = null;

	// protected int gstate = NONE; // NONE, CREATED, VALID or INVALID
	// protected int pstate = NONE; // NONE, VALID or INVALID
	// protected int ptype = NONE; // NONE, PARTIAL, FULL or NOT-REDUCED

	/** * Distribution States ***************************************** */

	// public static final int NONE = 0; // empty state
	// public static final int CREATED = 1; // global created
	// public static final int VALID = 2; // global/local valid
	// public static final int INVALID = 3; // global/local invalid
	// public static final int PARTIAL = 4; // scattered structure
	// public static final int FULL = 5; // replicated structure
	// public static final int NOT_REDUCED = 6; // not reduced

	/** * Public Methods ********************************************** */

	// Private constructor which only sets the dimensions and retrieves the
	// desired element type from the subclass.
	private Array2d(int w, int h, int bw, int bh, int e) {
		// NOTE: here we assume array to be of length (w+2*bw)*(h+2*bh)
		// Subclasses should make sure that this is indeed the case!!!!
		setDimensions(w, h, bw, bh, e);
		// type = getDataType();
	}

	// Constructor which takes the desired dimensions of the Cx2dArray, and
	// optionally creates the data.
	public Array2d(int w, int h, int bw, int bh, int e, boolean create) {

		this(w, h, bw, bh, e);

		if (create) {
			// Create new array and copy values, ignoring border values
			data = createDataArray((w + 2 * bw) * (h + 2 * bh) * e);
			// globalState = GlobalState.VALID;
			globalState = GLOBAL_VALID;
			localState = LOCAL_NONE;
		} else {
			// globalState = GlobalState.NONE;
			globalState = GLOBAL_NONE;
			localState = LOCAL_NONE;
		}
	}

	// Constructor which accepts an existing primitive array containing the
	// data. This array may be wrapped (thereby becoming part of the Cx2dArray,
	// or copied. In both cases it is assumed that the array has the correct
	// length for the given dimensions.
	public Array2d(int w, int h, int bw, int bh, int e, T array, boolean copy) {

		this(w, h, bw, bh, e);

		if (copy) {
			final int length = (w + 2 * bw) * (h + 2 * bh) * e;
			data = createDataArray(length);
			System.arraycopy(array, 0, data, 0, length);
		} else {
			data = array;
		}

		// globalState = GlobalState.VALID;
		globalState = GLOBAL_VALID;
		localState = LOCAL_NONE;

		// type = getDataType();
		// type = byte.class;
	}

	// Copy constructor which copies an existing array, dimension of the
	// borders, state, etc. is unchanged. Partial data is also copied -- J.
	public Array2d(Array2d<T> orig) {

		this(orig.width, orig.height, orig.bwidth, orig.bheight, orig.extent);

		if (orig.data != null) {
			final int fullw = width + 2 * bwidth;
			final int fullh = height + 2 * bheight;

			data = createDataArray(fullw * fullh * extent);

			if (orig.globalState == GLOBAL_VALID) {
				//Timo: do not copy invalid data
				System.arraycopy(orig.data, 0, data, 0, fullw * fullh * extent);
			}
		}

		globalState = orig.globalState;

		if (orig.pdata != null) {

			pwidth = orig.pwidth;
			pheight = orig.pheight;

			// Special case: the 'pdata' may be an alias of 'data' when running
			// on a single CPU.
			if (orig.pdata == orig.data) {
				pdata = data;
			} else {
				final int fullpw = pwidth + 2 * bwidth;
				final int fullph = pheight + 2 * bheight;

				pdata = createDataArray(fullpw * fullph * extent);
				System.arraycopy(orig.pdata, 0, pdata, 0, fullpw * fullph
						* extent);
			}
		}
		localState = orig.localState;
	}

	// Copy constructor which copies an existing array, but changes the
	// dimension of the borders, partial data is also copied (with adjusted
	// border dimensions). Other state is unchanged -- J
	public Array2d(Array2d<T> orig, int newBW, int newBH) {

		this(orig.width, orig.height, newBW, newBH, orig.extent);

		if (orig.data != null) {
			final int fullw = width + 2 * bwidth;
			final int fullh = height + 2 * bheight;

			data = createDataArray(fullw * fullh * extent);

			if (orig.globalState == GLOBAL_VALID) {
				// Timo: only copy the data if it is valid :-)				
				final int off = ((orig.width + 2 * orig.bwidth) * orig.bheight + orig.bwidth)
						* extent;
				final int stride = orig.bwidth * extent * 2;

				final int newOff = ((width + 2 * bwidth) * bheight + bwidth)
						* extent;
				final int newStride = bwidth * extent * 2;

				for (int j = 0; j < orig.height; j++) {

					final int srcPtr = off + j * (orig.width * extent + stride);

					// This is not correct ?
					// final int dstPtr = j * (width * extent);

					// FIXME Timo:This is also not correct ?
					// final int dstPtr = j * (pwidth + 2 * newBW) * extent;
					final int dstPtr = newOff + j
							* (width * extent + newStride);

					System.arraycopy(orig.data, srcPtr, data, dstPtr, width
							* extent);
				}
			}
		}

		globalState = orig.globalState;

		if (orig.pdata != null) {

			// Special case: the 'pdata' may be an alias of 'data' when running
			// on a single CPU.
			if (orig.pdata == orig.data) {
				pdata = data;
			} else {
				pwidth = orig.pwidth;
				pheight = orig.pheight;

				pdata = createDataArray((orig.pwidth + 2 * newBW)
						* (orig.pheight + 2 * newBH) * extent);

				final int srcOff = ((orig.pwidth + 2 * orig.bwidth)
						* orig.bheight + orig.bwidth)
						* extent;
				final int dstOff = ((orig.pwidth + 2 * newBW) * newBH + newBW)
						* extent;

				for (int j = 0; j < orig.pheight; j++) {
					final int srcPtr = srcOff + j
							* (orig.pwidth + 2 * orig.bwidth) * extent;
					final int dstPtr = dstOff + j * (orig.pwidth + 2 * newBW)
							* extent;

					System.arraycopy(orig.pdata, srcPtr, pdata, dstPtr,
							orig.pwidth * extent);
				}
			}
		}

		localState = orig.localState;
	}

	protected void setDimensions(int w, int h, int bw, int bh, int e) {
		width = w;
		height = h;
		bwidth = bw;
		bheight = bh;
		extent = e;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getBorderWidth() {
		return bwidth;
	}

	public int getBorderHeight() {
		return bheight;
	}

	public int getExtent() {
		return extent;
	}

	// public Class<?> getElementType() {
	// return type;
	// }

	public T getDataReadOnly() {
		if (data == null) {
			throw new RuntimeException("Cannot read data: no data available");
		}

		// if (globalState != GlobalState.VALID) {
		if (globalState != GLOBAL_VALID) {
			throw new RuntimeException("Cannot read data: state != VALID ("
					+ globalState + ")");
		}

		return data;
	}

	public T getDataWriteOnly() {
		if (data == null) {
			throw new RuntimeException("Cannot read data: no data available");
		}

		return data;
	}

	public T getDataReadWrite() {
		if (data == null) {
			throw new RuntimeException("Cannot read data: no data available");
		}

		// if (globalState != GlobalState.VALID) {
		if (globalState != GLOBAL_VALID) {
			throw new RuntimeException("Cannot read data: state != VALID ("
					+ globalState + ")");
		}

		return data;
	}

	public int getPartialWidth() {
		return pwidth;
	}

	public int getPartialHeight() {
		return pheight;
	}

	public boolean hasData() {
		return (data != null);
	}

	public boolean hasPartialData() {
		return (pdata != null);
	}

	public T getPartialDataReadOnly() {
		if (pdata == null) {
			throw new RuntimeException("Cannot read partial data: "
					+ "no data available");
		}

		if (!(localState == LOCAL_FULL || localState == LOCAL_PARTIAL)) {
			// TODO Timo: what about NOT_REDUCED?
			throw new RuntimeException("Cannot read partial data: "
					+ "state != VALID (" + localState + ")");
		}

		return pdata;
	}

	public T getPartialDataWriteOnly() {
		if (pdata == null) {
			throw new RuntimeException("Cannot write partial data: "
					+ "no data available");
		}
		return pdata;
	}

	public T getPartialDataReadWrite() {
		if (pdata == null) {
			throw new RuntimeException("Cannot read/write partial data: "
					+ "no data available");
		}

		if (!(localState == LOCAL_FULL || localState == LOCAL_PARTIAL)) {
			throw new RuntimeException("Cannot read/write partial data: "
					+ "pstate != VALID (" + localState + ")");
		}

		return pdata;
	}

	// public void setData(int width, int height, T data, GlobalState state) {
	public void setData(int width, int height, T data, int state) {
		// This assumes data size (pwidth+2*bwidth)*(pheight+2*bheight)

		this.width = width;
		this.height = height;
		this.data = data;
		this.globalState = state;
		// ptype = type;
	}

	public void setPartialData(int width, int height, T data, int state) {
		// This assumes data size (pwidth+2*bwidth)*(pheight+2*bheight)

		pwidth = width;
		pheight = height;
		pdata = data;
		localState = state;
	}

	public boolean equalExtent(Pixel<?> p) {
		return (extent == p.getExtent());
	}

	public boolean equalSignature(Array2d<T> a) {
		// NOTE: The array borders do not need to be equal in size!!!

		// return (width == a.getWidth() && height == a.getHeight()
		// && extent == a.getExtent() && type == a.getElementType());

		return (width == a.getWidth() && height == a.getHeight() && extent == a
				.getExtent());
	}

	// public GlobalState getGlobalState() {
	public int getGlobalState() {
		return globalState;
	}

	// public void setGlobalState(GlobalState state) {
	public void setGlobalState(int state) {
		globalState = state;
	}

	public int getLocalState() {
		return localState;
	}

	public void setLocalState(int state) {
		localState = state;
	}

	public void setReduceOperation(ReduceOp opcode) {
		this.requiredReduceOp = opcode;
	}

	public ReduceOp getReduceOperation() {
		return requiredReduceOp;
	}

	public void bringToRoot() throws IOException {
		// if (globalState == GlobalState.VALID) {
		if (globalState == GLOBAL_VALID) {
			return;
		} else if (localState == LOCAL_NOT_REDUCED) {
			PxSystem.get().reduceToRoot(this);
		} else if (localState == LOCAL_PARTIAL) {
			PxSystem.get().gather(this);
		} else if (localState == LOCAL_FULL) {
			PxSystem.get().gather(this);
		}
	}

	/** * Clones ***************************************** */

	public abstract Array2d<T> clone();

	public abstract Array2d<T> clone(int newBorderWidth, int newBorderHeight);

	/** * Array creation and type information ************************** */

	public abstract T createDataArray(int size);

//	protected abstract Class<?> getDataType();

	/** * Creator from byte[] ***************************************** */

	// NOTE: THIS SOLUTION REQUIRES ALL SUBCLASSES TO HAVE A CONSTRUCTOR
	// WITH A byte[] AS ONE OF ITS PARAMETERS...
	/*
	 * public static <U> U makeFromData(int w, int h, int bw, int bh, byte[]
	 * array, Class<U> clazz) { try { Class[] args = new Class[5]; args[0] =
	 * int.class; args[1] = int.class; args[2] = int.class; args[3] = int.class;
	 * args[4] = byte[].class; Constructor<U> c = clazz.getConstructor(args);
	 * return c.newInstance(w, h, bw, bh, array); } catch (Throwable e) {
	 * e.printStackTrace(); return null; } }
	 */

	/** * Single Pixel (Value) Operations ***************************** */

	public abstract Array2d<T> setSingleValue(Pixel<T> p, int xidx, int yidx,
			boolean inpl);

	public abstract Array2d<T> addSingleValue(Pixel<T> p, int xidx, int yidx,
			boolean inpl);

	/**
	 * * Binary Pixel Single Value Operations
	 * **************************************
	 */

	public abstract Array2d<T> setVal(Pixel<T> p, boolean inpl);

	public abstract Array2d<T> addVal(Pixel<T> p, boolean inpl);

	public abstract Array2d<T> subVal(Pixel<T> p, boolean inpl);

	public abstract Array2d<T> mulVal(Pixel<T> p, boolean inpl);

	public abstract Array2d<T> divVal(Pixel<T> p, boolean inpl);

	public abstract Array2d<T> minVal(Pixel<T> p, boolean inpl);

	public abstract Array2d<T> maxVal(Pixel<T> p, boolean inpl);

	public abstract Array2d<T> negDivVal(Pixel<T> p, boolean inpl);

	public abstract Array2d<T> absDivVal(Pixel<T> p, boolean inpl);

	/** * Binary Pixel Operations ************************************* */

	public abstract Array2d<T> add(Array2d<T> a, boolean inpl);

	public abstract Array2d<T> sub(Array2d<T> a, boolean inpl);

	public abstract Array2d<T> mul(Array2d<T> a, boolean inpl);

	public abstract Array2d<T> div(Array2d<T> a, boolean inpl);

	public abstract Array2d<T> min(Array2d<T> a, boolean inpl);

	public abstract Array2d<T> max(Array2d<T> a, boolean inpl);

	public abstract Array2d<T> negDiv(Array2d<T> a, boolean inpl);

	public abstract Array2d<T> absDiv(Array2d<T> a, boolean inpl);

	/** * Reduction Operations *************************************** */

	public abstract Array2d<T> pixMin();

	public abstract Array2d<T> pixMax();

	public abstract Array2d<T> pixSum();

	public abstract Array2d<T> pixProduct();

	public abstract Array2d<T> pixSup(); // supremum

	public abstract Array2d<T> pixInf(); // infimum

	/** * Convolution Operations ************************************* */

	public abstract Array2d<T> convKernelSeparated2d(Array2d<T> kernelX,
			Array2d<T> kernelY, boolean inplace);

	public final Array2d<T> convKernelSeparated(Array2d<T> kernel, boolean inplace) {
		return convKernelSeparated2d(kernel, kernel, inplace);
	}

	public abstract Array2d<T> convolution(Array2d<T> kernel);

	public abstract Array2d<T> convolution1d(Array2d<T> kernel, int dimension);

	public abstract Array2d<T> convolutionRotated1d(Array2d<T> kernel,
			double phirad);

	public abstract Array2d<T> convGauss2d(double sigmaX, int orderDerivX,
			double truncationX, double sigmaY, int orderDerivY,
			double truncationY, boolean inplace);

	public final Array2d<T> gauss(double sigma, double truncation, boolean inplace) {
		return gaussDerivative2d(sigma, 0, 0, truncation, inplace);

	}

	public final Array2d<T> gaussDerivative2d(double sigma, int orderDerivX,
			int orderDerivY, double truncation, boolean inplace) {
		return convGauss2d(sigma, orderDerivX, truncation, sigma, orderDerivY,
				truncation, inplace);
	}

	/** Anisotropic Convolution **/

	public abstract Array2d<T> convGaussAnisotropic2d(double sigmaU,
			int orderDerivU, double truncationU, double sigmaV,
			int orderDerivV, double truncationV, double phiRad, boolean inplace);

	/** * Pixel Manipulation (NOT PARALLEL) *************************** */

	// Timo: deprecated
	// public abstract CxPixel<T> getPixel(int xindex, int yindex);

	// public abstract void setPixel(CxPixel<T> p, int xindex, int yindex);

}
