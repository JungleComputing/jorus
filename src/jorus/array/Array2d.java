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

public abstract class Array2d<T, U extends Array2d<T, U>> implements Serializable {
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
			// state = GLOBAL_CREATED; //FIXME leads to glitches
			state = GLOBAL_VALID;
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
	protected Array2d(Array2d<T, U> original, boolean copyData) {
		setDimensions(original.width, original.height, original.borderWidth,
				original.borderHeight, original.extent);

		state = original.state;
		partialWidth = original.partialWidth;
		partialHeight = original.partialHeight;
		requiredReduceOp = original.requiredReduceOp;

		if (copyData && original.data != null) {
			data = original.copyArray();
		} else {
			data = createDataArray(original.getDataLength());
		}
	}

	// Copy constructor which copies an existing array, but changes the
	// dimension of the borders, partial data is also copied (with adjusted
	// border dimensions). Other state is unchanged -- J
	// Timo: Note that the border data that is _NOT_ copied
	protected Array2d(Array2d<T, U> original, int newBorderWidth,
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
	
	protected abstract int getDataLength();

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
		int requiredLength = (partialWidth + 2 * borderWidth)
				* (partialHeight + 2 * borderHeight) * extent;
		if (requiredLength > getDataArraySize()) {
			throw new Error("Array too small. current #elements: "
					+ getDataArraySize() + ", required #elements: "
					+ requiredLength);
		}
	}

	public boolean equalExtent(Pixel<?> p) {
		return (extent == p.getExtent());
	}

	public boolean equalSignature(Array2d<T, ?> a) {
		// NOTE: The array borders do not need to be equal in size!!!
		return (width == a.getWidth() && height == a.getHeight() && extent == a
				.getExtent());
	}

	/**
	 * Change the state to @param newState using communication
	 * 
	 * @param newState
	 *            the desired state for this array
	 */
	public void changeStateTo(int newState) throws Exception {
		if (!PxSystem.initialized()) {
			// no state changes are needed
			return;
		}
		if (newState == state) {
			// we don't have to do anything
			return;
		}

		switch (newState) {
		case GLOBAL_VALID:
			switch (state) {
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
				// FIXME just create a new data structure??
				PxSystem.get().broadcast(this);
				return;
			case LOCAL_PARTIAL: // FIXME implement a gatherAll in PxSystem
				// PxSystem.get().gatherAll(this);
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
				// FIXME just create a new data structure??
				PxSystem.get().scatter(this);
				return;
			case LOCAL_FULL:
				PxSystem.get().scatter(this);
				return;
			case LOCAL_NOT_REDUCED:
				PxSystem.get().reduceToAll(this);
				// or do the normal reduce??
				PxSystem.get().scatter(this);
				return;
			}
			break;
		}
		throw new Exception("Illegal State Transistion: " + stateString()
				+ " --> " + stateString(newState));
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

	public abstract U clone();

	public abstract U clone(int newBorderWidth, int newBorderHeight);
	
	public abstract U shallowClone();

	public abstract U createCompatibleArray(int width, int height,
			int borderWidth, int borderHeight);

	public abstract Array2d<T, U> prepareForSideChannel(); // clone, except the

	// data, state -->
	// GLOBAL_VALID

	/** * Array creation and type information ************************** */

	public abstract int getDataArraySize();

	public abstract T createDataArray(int size);

	protected abstract T copyArray();

	/** * Creator from byte[] ***************************************** */

	/** * Single Pixel (Value) Operations ***************************** */

	public abstract U setSingleValue(Pixel<T> p, int xidx, int yidx,
			boolean inpl);

	public abstract U addSingleValue(Pixel<T> p, int xidx, int yidx,
			boolean inpl);

	/**
	 * * Binary Pixel Single Value Operations
	 * **************************************
	 */

	public abstract U setVal(Pixel<T> p, boolean inpl);

	public abstract U addVal(Pixel<T> p, boolean inpl);

	public abstract U subVal(Pixel<T> p, boolean inpl);

	public abstract U mulVal(Pixel<T> p, boolean inpl);

	public abstract U divVal(Pixel<T> p, boolean inpl);

	public abstract U minVal(Pixel<T> p, boolean inpl);

	public abstract U maxVal(Pixel<T> p, boolean inpl);

	public abstract U negDivVal(Pixel<T> p, boolean inpl);

	public abstract U absDivVal(Pixel<T> p, boolean inpl);

	/** * Binary Pixel Operations ************************************* */

	public abstract U add(Array2d<T, ?> a, boolean inpl);

	public abstract U sub(Array2d<T, ?> a, boolean inpl);

	public abstract U mul(Array2d<T, ?> a, boolean inpl);

	public abstract U div(Array2d<T, ?> a, boolean inpl);

	public abstract U min(Array2d<T, ?> a, boolean inpl);

	public abstract U max(Array2d<T, ?> a, boolean inpl);

	public abstract U negDiv(Array2d<T, ?> a, boolean inpl);

	public abstract U posDiv(Array2d<T, ?> a, boolean inpl);

	public abstract U absDiv(Array2d<T, ?> a, boolean inpl);

	/** * Reduction Operations *************************************** */

	public abstract U pixMin();

	public abstract U pixMax();

	public abstract U pixSum();

	public abstract U pixProduct();

	public abstract U pixSup(); // supremum

	public abstract U pixInf(); // infimum

	/** * Convolution Operations ************************************* */

	public abstract U convKernelSeparated2d(Array2d<T, ?> kernelX,
			Array2d<T, ?> kernelY, boolean inplace);

	public final U convKernelSeparated(Array2d<T, ?> kernel,
			boolean inplace) {
		return convKernelSeparated2d(kernel, kernel, inplace);
	}

	public abstract U convolution(Array2d<T, ?> kernel);

	public abstract U convolution1d(Array2d<T, ?> kernel, int dimension);

	public abstract U convolutionRotated1d(Array2d<T, ?> kernel,
			double phirad);

	public abstract U convGauss2d(double sigmaX, int orderDerivX,
			double truncationX, double sigmaY, int orderDerivY,
			double truncationY, boolean inplace);

	/**
	 * Deprecated: for use in Conv2D benchmark only.
	 * 
	 * @param sigmaR
	 * @param sigmaT
	 * @param phiDegrees
	 * @param derivativeT
	 * @param n
	 * @return
	 */
	public abstract U convGauss1x2d(double sigmaR, double sigmaT,
			double phiDegrees, int derivativeT, double n);

	public final U gauss(double sigma, double truncation,
			boolean inplace) {
		return gaussDerivative2d(sigma, 0, 0, truncation, inplace);

	}

	public final U gaussDerivative2d(double sigma, int orderDerivX,
			int orderDerivY, double truncation, boolean inplace) {
		return convGauss2d(sigma, orderDerivX, truncation, sigma, orderDerivY,
				truncation, inplace);
	}

	/** Anisotropic Convolution **/

	public abstract U convGaussAnisotropic2d(double sigmaU,
			int orderDerivU, double truncationU, double sigmaV,
			int orderDerivV, double truncationV, double phiRad, boolean inplace);

	/** Geometric Operations **/

	protected double[][] calculateDimensionsandTranslationVector(
			Matrix forwardsTransformationMatrix) {

		double[] ll, lr, ur, ul;
		try {
			double[] vector = new double[] { 0, 0, 1 };
			ul = Matrix.multMV(forwardsTransformationMatrix, vector);
			vector[0] = width; // { width, 0, 1 }
			ur = Matrix.multMV(forwardsTransformationMatrix, vector);
			vector[1] = height; // { width, height, 1 }
			lr = Matrix.multMV(forwardsTransformationMatrix, vector);
			vector[0] = 0; // { 0, height, 1 }
			ll = Matrix.multMV(forwardsTransformationMatrix, vector);
		} catch (Exception e) {
			// TODO Will never happen
			throw new Error(e);
		}

		// homogeneous coordinates:
		ul[0] /= ul[2];
		ul[1] /= ul[2];
		ur[0] /= ur[2];
		ur[1] /= ur[2];
		lr[0] /= lr[2];
		lr[1] /= lr[2];
		ll[0] /= ll[2];
		ll[1] /= ll[2];

		double right = Math.max(Math.max(ur[0], lr[0]), Math.max(ul[0], ll[0]));
		double left = Math.min(Math.min(ur[0], lr[0]), Math.min(ul[0], ll[0]));

		double bottom = Math
				.max(Math.max(ul[1], ur[1]), Math.max(ll[1], lr[1]));
		double top = Math.min(Math.min(ul[1], ur[1]), Math.min(ll[1], lr[1]));

		int newHeight = (int) (bottom - top + 0.5);
		int newWidth = (int) (right - left + 0.5);

		double[] newDimensions = new double[] { newWidth, newHeight };
		double[] tData = new double[3];
		tData[0] = left;
		tData[1] = top;

		return new double[][] { tData, newDimensions };
	}

	protected abstract U geometricOp2d(Matrix transformationMatrix,
			boolean forwardMatrix, boolean linearInterpolation,
			boolean adjustSize, Pixel<T> background);

	/**
	 * Translates the image with tx and ty pixels in the x- and y- direction
	 * respectively. Background pixels will be set to '0'
	 * 
	 * @param tx
	 *            The translation in the x direction
	 * @param ty
	 *            The translation in the y direction
	 * @param adjustSize
	 *            when false, corners may be chopped off the image
	 * @return the translated image
	 */
	public final U translate(int tx, int ty) {
		Matrix m = Matrix.translate2d(tx, ty);
		return geometricOp2d(m, true, false, false, null);
	}

	/**
	 * Rotates the image around the z-axis. The center of rotation is the center
	 * of the image
	 * 
	 * @param alpha
	 *            The rotation angle in degrees (positive alpha leads to
	 *            rotation in counterclockwise direction)
	 * @param linearInterpolation
	 *            true: use linear interpolation false: use nearest neighbour
	 *            fit
	 * @param adjustSize
	 *            when false, corners may be chopped off the image
	 * @param background
	 *            the value of the background pixels
	 * @return the rotated image
	 */
	public final U rotate(double alpha, boolean linearInterpolation,
			boolean adjustSize, Pixel<T> background) {
		/*
		 * The image coordinate system has the origin at the top left instead of
		 * the bottom left, so rotation is in the opposite direction
		 */
		try {
			Matrix mt1 = Matrix.translate2d((double) (getWidth()) / 2.,
					(double) (getHeight()) / 2.);
			Matrix mr1 = Matrix.rotate2dDeg(-alpha);
			Matrix mt2 = Matrix.translate2d(-(double) (getWidth()) / 2.,
					-(double) (getHeight()) / 2.);
			Matrix m = Matrix.multMM(Matrix.multMM(mt1, mr1), mt2);

			return geometricOp2d(m, true, linearInterpolation, adjustSize,
					background);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new Error(e);
		}
	}

	/**
	 * Scales the image with scaling factors sx and sy in the x- and y-
	 * direction respectively. Background pixels will be set to '0'
	 * 
	 * @param sx
	 *            The scale factor in the x direction
	 * @param sy
	 *            The scale factor in the y direction
	 * @param linearInterpolation
	 *            true: use linear interpolation false: use nearest neighbour
	 *            fit
	 * @param adjustSize
	 *            when false, corners may be chopped off the image
	 * @return the scaled image
	 */
	public final U scale(double sx, double sy,
			boolean linearInterpolation, boolean adjustSize, Pixel<T> background) {
		Matrix m = Matrix.scale2d(sx, sy);
		return geometricOp2d(m, true, linearInterpolation, adjustSize, null);
	}

	/** Geometric ROI operations **/

	public abstract U geometricOpROI(int newImWidth,
			int newImHeight, Pixel<T> background, int beginX, int beginY);

	public final U extend(int newImWidth, int newImHeight,
			Pixel<T> background, int beginX, int beginY) {
		return geometricOpROI(newImWidth, newImHeight, background, -beginX,
				-beginY);
	}

	public final U restrict(int beginX, int beginY, int endX, int endY) {
		int newImWidth = endX - beginX + 1;
		int newImHeight = endY - beginY + 1;
		// Pixel<T> background;
		// return geometricOpROI(newImWidth, newImHeight, background, -beginX,
		// -beginY);
		return geometricOpROI(newImWidth, newImHeight, null, beginX, beginY);
	}

	/** * Pixel Manipulation (NOT PARALLEL) *************************** */
}
