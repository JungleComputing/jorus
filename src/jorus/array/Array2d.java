/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

import java.io.Serializable;

import jorus.parallel.PxSystem;
import jorus.parallel.ReduceOp;
import jorus.pixel.Pixel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Array2d<T> implements Serializable {
	private static final long serialVersionUID = -378351580205311141L;

	private static final Logger logger = LoggerFactory.getLogger(Array2d.class);

	public static final int NONE = 0;
	public static final int GLOBAL_CREATED = 1;
	public static final int GLOBAL_VALID = 2;
	public static final int LOCAL_PARTIAL = 3;
	public static final int LOCAL_FULL = 4;
	public static final int LOCAL_NOT_REDUCED = 5;

	/** * Private Properties ****************************************** */

	/**
	 * image width in pixels
	 */
	private int width;

	/**
	 * array height in pixels
	 */
	private int height;

	/**
	 * pixel extent
	 */
	private int extent;

	/**
	 * array border width in pixels
	 */
	private int borderWidth;

	/**
	 * array border height in pixels
	 */
	private int borderHeight;

	/**
	 * partial array width
	 */
	private int partialWidth = 0;

	/**
	 * partial array height
	 */
	private int partialHeight = 0;

	/**
	 * The state of the data structure
	 */
	private int state = NONE;

	/**
	 * The required Reduction operation for NOT_REDUCED local data structure to
	 * get a valid image
	 */
	private transient ReduceOp requiredReduceOp = null;

	/**
	 * Array of elements (not pixels)
	 */
	private T data = null;

	/** * Public Methods ********************************************** */

	// Constructor which takes the desired dimensions of the 2dArray, and
	// optionally creates the data.
	protected Array2d(int width, int height, int borderWidth, int borderHeight,
			int extent, boolean create) {
		setDimensions(width, height, borderWidth, borderHeight, extent);
		if (create) {
			// Create new array and copy values, ignoring border values
			data = createDataArray((width + 2 * borderWidth)
					* (height + 2 * borderHeight) * extent);
			state = GLOBAL_CREATED;
		}
	}

	// Constructor which accepts an existing primitive array containing the
	// data. This array may be wrapped (thereby becoming part of the Array2d,
	// or copied. In both cases it is assumed that the array has the correct
	// length for the given dimensions.
	protected Array2d(int width, int height, int borderWidth, int borderHeight,
			int extent, T array, boolean copy) {

		setDimensions(width, height, borderWidth, borderHeight, extent);

		if (copy) {
			final int length = (width + 2 * borderWidth)
					* (height + 2 * borderHeight) * extent;
			data = createDataArray(length);
			System.arraycopy(array, 0, data, 0, length);
		} else {
			data = array;
		}
		state = GLOBAL_VALID;
	}

	// Copy constructor which copies an existing array, dimension of the
	// borders, state, etc. is unchanged. Partial data is also copied -- J.
	protected Array2d(Array2d<T> original) {
		setDimensions(original.width, original.height, original.borderWidth,
				original.borderHeight, original.extent);

		state = original.state;
		partialWidth = original.partialWidth;
		partialHeight = original.partialHeight;
		requiredReduceOp = original.requiredReduceOp;

		if (original.data != null) {
			data = original.copyArray();
		}
	}

	// Copy constructor which copies an existing array, but changes the
	// dimension of the borders, partial data is also copied (with adjusted
	// border dimensions). Other state is unchanged -- J
	// Timo: Note that the border data that is _NOT_ copied
	protected Array2d(Array2d<T> original, int newBorderWidth,
			int newBorderHeight) {
		setDimensions(original.width, original.height, newBorderWidth,
				newBorderHeight, original.extent);
		state = original.state;
		partialWidth = original.partialWidth;
		partialHeight = original.partialHeight;
		requiredReduceOp = original.requiredReduceOp;

		if (original.data != null) {
			if (state == GLOBAL_CREATED) {
				// no real data in array, just create a new one
				final int fullWidth = width + 2 * borderWidth;
				final int fullHeight = height + 2 * borderHeight;
				data = createDataArray(fullWidth * fullHeight * extent);
			} else if (state == GLOBAL_VALID || state == LOCAL_FULL
					|| state == LOCAL_NOT_REDUCED) {
				// copy the entire data of the old array
				final int fullWidth = width + 2 * borderWidth;
				final int fullHeight = height + 2 * borderHeight;
				data = createDataArray(fullWidth * fullHeight * extent);

				final int oldStride = original.borderWidth * extent * 2;
				final int oldRowSize = oldStride + original.width * extent;
				final int oldOffset = oldRowSize * original.borderHeight
						+ original.borderWidth * extent;

				final int newStride = borderWidth * extent * 2;
				final int newRowSize = newStride + original.width * extent;
				final int newOffset = newRowSize * borderHeight + borderWidth
						* extent;

				for (int j = 0; j < original.height; j++) {
					final int srcPtr = oldOffset + j * oldRowSize;
					final int dstPtr = newOffset + j * newRowSize;
					System.arraycopy(original.data, srcPtr, data, dstPtr, width
							* extent);
				}
			} else if (state == LOCAL_PARTIAL) {
				// only copy the partial data of the old array
				final int fullWidth = partialWidth + 2 * borderWidth;
				final int fullHeight = partialHeight + 2 * borderHeight;
				data = createDataArray(fullWidth * fullHeight * extent);

				final int oldStride = original.borderWidth * extent * 2;
				final int oldRowSize = oldStride + original.partialWidth
						* extent;
				final int oldOffset = oldRowSize * original.borderHeight
						+ original.borderWidth * extent;

				final int newStride = borderWidth * extent * 2;
				final int newRowSize = newStride + original.partialWidth
						* extent;
				final int newOffset = newRowSize * borderHeight + borderWidth
						* extent;

				for (int j = 0; j < original.partialHeight; j++) {
					final int srcPtr = oldOffset + j * oldRowSize;
					final int dstPtr = newOffset + j * newRowSize;
					System.arraycopy(original.data, srcPtr, data, dstPtr, width
							* extent);
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Distribution is wrong in copy constructor");
				}
			}
		}

	}

	public String stateString() {
		return stateString(state);
	}

	public static String stateString(int state) {
		switch (state) {
		case NONE:
			return "NONE";
		case GLOBAL_CREATED:
			return "GLOBAL_CREATED";
		case GLOBAL_VALID:
			return "GLOBAL_VALID";
		case LOCAL_PARTIAL:
			return "LOCAL_PARTIAL";
		case LOCAL_FULL:
			return "LOCAL_FULL";
		case LOCAL_NOT_REDUCED:
			return "LOCAL_NOT_REDUCED";
		default:
			return "NO_VALID_STATE";
		}
	}

	private void setDimensions(int width, int height, int borderWidth,
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

	// public T getDataReadOnly() {
	// if (data == null) {
	// throw new RuntimeException("Cannot read data: no data available");
	// }
	//
	// if (state != GLOBAL_VALID) {
	// throw new RuntimeException("Cannot read data: state != VALID ("
	// + stateString(state) + ")");
	// }
	//
	// return data;
	// }
	//
	// public T getDataWriteOnly() {
	// if (data == null) {
	// throw new RuntimeException("Cannot read data: no data available");
	// }
	// if (state != GLOBAL_VALID && state != GLOBAL_CREATED) {
	// throw new RuntimeException("Cannot write data: invalid state ("
	// + stateString(state) + ")");
	// }
	//
	// return data;
	// }
	//
	// public T getDataReadWrite() {
	// if (data == null) {
	// throw new RuntimeException("Cannot read data: no data available");
	// }
	//
	// if (state != GLOBAL_VALID) {
	// throw new RuntimeException("Cannot read/write data: invalid state ("
	// + stateString(state) + ")");
	// }
	//
	// return data;
	// }

	public int getPartialWidth() {
		return partialWidth;
	}

	public int getPartialHeight() {
		return partialHeight;
	}

	// public boolean hasData() {
	// return ((state == GLOBAL_CREATED || state == GLOBAL_VALID) && data !=
	// null);
	// }
	//
	// public boolean hasPartialData() {
	// return ((state == LOCAL_FULL || state == LOCAL_PARTIAL || state ==
	// LOCAL_NOT_REDUCED) && data != null);
	// }

	// public T getPartialDataReadOnly() {
	// if (data == null) {
	// throw new RuntimeException("Cannot read partial data: "
	// + "no data available");
	// }
	//
	// if (!(state == LOCAL_FULL || state == LOCAL_PARTIAL)) {
	// // TODO Timo: what about NOT_REDUCED?
	// throw new RuntimeException("Cannot read partial data: "
	// + "state != VALID (" + stateString(state) + ")");
	// }
	//
	// return data;
	// }
	//
	// public T getPartialDataWriteOnly() {
	// if (!hasPartialData()) {
	// throw new RuntimeException("Cannot write partial data: "
	// + "no data available");
	// }
	// return data;
	// }
	//
	// public T getPartialDataReadWrite() {
	// if (!hasPartialData()) {
	// throw new RuntimeException("Cannot read/write partial data: "
	// + "no data available");
	// }
	//
	// if (!(state == LOCAL_FULL || state == LOCAL_PARTIAL)) {
	// throw new RuntimeException("Cannot read/write partial data: "
	// + "invalid state (" + stateString(state) + ")");
	// }
	// return data;
	// }

	public void setData(int width, int height, T data, int state) {
		// This assumes data size (width + 2 * borderWidth) * (height + 2 *
		// borderHeight) * extent
		this.width = width;
		this.height = height;
		this.data = data;
		this.state = state;
	}

	public void setPartialData(int width, int height, T data, int state) {
		// This assumes data size (partialWidth + 2 * borderWidth) *
		// (partialHeight + 2 * borderHeight) * extent
		this.partialWidth = width;
		this.partialHeight = height;
		this.data = data;
		this.state = state;
		int requiredLength = (partialWidth + 2 * borderWidth) * (partialHeight + 2 * borderHeight) * extent;
		if (requiredLength > getDataArraySize()) {
			throw new Error("Array too small. current #elements: " + getDataArraySize() + ", required #elements: " + requiredLength);
		}
	}

	public boolean equalExtent(Pixel<?> p) {
		return (extent == p.getExtent());
	}

	public boolean equalSignature(Array2d<T> a) {
		// NOTE: The array borders do not need to be equal in size!!!
		return (width == a.getWidth() && height == a.getHeight() && extent == a
				.getExtent());
	}

	/**
	 * Change the state to @param newState using communication
	 * 
	 * @param newState the desired state for this array
	 */
	public void changeStateTo(int newState) throws Exception {
		if(!PxSystem.initialized()) {
			// no state changes are needed
			return;
		}
		switch (newState) {
		case GLOBAL_VALID:
			switch (state) {
			case GLOBAL_VALID:
				return;
			case GLOBAL_CREATED:
				state = GLOBAL_VALID;
				return;
			case LOCAL_FULL:
				// a NOOP, the data at root node is exactly the same
				state = GLOBAL_VALID;
				return;
			case LOCAL_PARTIAL:
				PxSystem.get().gather(this);
				return;
			case LOCAL_NOT_REDUCED:
				PxSystem.get().reduceToRoot(this);
				return;
			}			
			break;
		case LOCAL_FULL:
			switch (state) {
			case GLOBAL_VALID:
				PxSystem.get().broadcast(this);
				return;
			case GLOBAL_CREATED:
				//FIXME just create a new data structure??
				PxSystem.get().broadcast(this);
				return;
			case LOCAL_FULL:
				return;
			case LOCAL_PARTIAL: //FIXME implement a gatherAll in PxSystem
//				PxSystem.get().gatherAll(this);
				PxSystem.get().gather(this);
				PxSystem.get().broadcast(this);
				return;
			case LOCAL_NOT_REDUCED:
				PxSystem.get().reduceToAll(this);
				return;
			}
			break;
		case LOCAL_PARTIAL:
			switch (state) {
			case GLOBAL_VALID:
				PxSystem.get().scatter(this);
				return;
			case GLOBAL_CREATED:
				//FIXME just create a new data structure??
				PxSystem.get().scatter(this);
				return;
			case LOCAL_FULL:
				PxSystem.get().scatter(this);
				return;
			case LOCAL_PARTIAL:
				// NOOP
				return;
			case LOCAL_NOT_REDUCED:
				PxSystem.get().reduceToAll(this); 
				// or do the normal reduce??
				PxSystem.get().scatter(this);
				return;
			}
			break;
		}
		throw new Exception("Illegal State Transistion: " + stateString() + " --> "+ stateString(newState));
	}

	public int getState() {
		return state;
	}
	
	public void setState(int state) {
		this.state = state;
	}

	public void setReduceOperation(ReduceOp opcode) {
		this.requiredReduceOp = opcode;
	}

	public ReduceOp getReduceOperation() {
		return requiredReduceOp;
	}

	public void createGlobalImage() throws Exception {
		// for both global states, the image is already correct
		if (state == NONE) {
			throw new Exception("Image not initialized");
		} else if (state == LOCAL_NOT_REDUCED) {
			PxSystem.get().reduceToRoot(this);
		} else if (state == LOCAL_PARTIAL) {
			PxSystem.get().gather(this);
		} else if (state == LOCAL_FULL) {
			PxSystem.get().gather(this);
		}
	}

	/** * Clones ***************************************** */

	public abstract Array2d<T> clone();

	public abstract Array2d<T> clone(int newBorderWidth, int newBorderHeight);

	public abstract Array2d<T> prepareForSideChannel(); //clone, except the data, state --> GLOBAL_VALID

	/** * Array creation and type information ************************** */

	public abstract int getDataArraySize();
	public abstract T createDataArray(int size);

	protected abstract T copyArray();

	/** * Creator from byte[] ***************************************** */

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

	public final Array2d<T> convKernelSeparated(Array2d<T> kernel,
			boolean inplace) {
		return convKernelSeparated2d(kernel, kernel, inplace);
	}

	public abstract Array2d<T> convolution(Array2d<T> kernel);

	public abstract Array2d<T> convolution1d(Array2d<T> kernel, int dimension);

	public abstract Array2d<T> convolutionRotated1d(Array2d<T> kernel,
			double phirad);

	public abstract Array2d<T> convGauss2d(double sigmaX, int orderDerivX,
			double truncationX, double sigmaY, int orderDerivY,
			double truncationY, boolean inplace);

	public final Array2d<T> gauss(double sigma, double truncation,
			boolean inplace) {
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
}
