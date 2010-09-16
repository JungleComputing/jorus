/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

import jorus.operations.bpo.BpoAbsDivFloat;
import jorus.operations.bpo.BpoAddFloat;
import jorus.operations.bpo.BpoDivFloat;
import jorus.operations.bpo.BpoMaxFloat;
import jorus.operations.bpo.BpoMinFloat;
import jorus.operations.bpo.BpoMulFloat;
import jorus.operations.bpo.BpoNegDivFloat;
import jorus.operations.bpo.BpoPosDivFloat;
import jorus.operations.bpo.BpoSubFloat;
import jorus.operations.bpoval.BpoAbsDivValFloat;
import jorus.operations.bpoval.BpoAddValFloat;
import jorus.operations.bpoval.BpoDivValFloat;
import jorus.operations.bpoval.BpoMaxValFloat;
import jorus.operations.bpoval.BpoMinValFloat;
import jorus.operations.bpoval.BpoMulValFloat;
import jorus.operations.bpoval.BpoNegDivValFloat;
import jorus.operations.bpoval.BpoPosDivValFloat;
import jorus.operations.bpoval.BpoSetValFloat;
import jorus.operations.bpoval.BpoSubValFloat;
import jorus.operations.communication.SetBorderMirrorFloat;
import jorus.operations.generalizedconvolution.Convolution1dFloat;
import jorus.operations.generalizedconvolution.Convolution2dFloat;
import jorus.operations.generalizedconvolution.ConvolutionRotated1dFloat;
import jorus.operations.geometric.Geometric2dFloat;
import jorus.operations.geometric.GeometricROIFloat;
import jorus.operations.reduce.ReduceInfFloat;
import jorus.operations.reduce.ReduceMaxFloat;
import jorus.operations.reduce.ReduceMinFloat;
import jorus.operations.reduce.ReduceProductFloat;
import jorus.operations.reduce.ReduceSumFloat;
import jorus.operations.reduce.ReduceSupFloat;
import jorus.operations.svo.SvoAddFloat;
import jorus.operations.svo.SvoSetFloat;
import jorus.patterns.PatBpo;
import jorus.patterns.PatBpoVal;
import jorus.patterns.PatGeneralizedConvolution1d;
import jorus.patterns.PatGeneralizedConvolution1dRotated;
import jorus.patterns.PatGeneralizedConvolution2d;
import jorus.patterns.PatGeneralizedConvolution2dRotatedSeparated;
import jorus.patterns.PatGeneralizedConvolution2dSeparated;
import jorus.patterns.PatGeometric2d;
import jorus.patterns.PatGeometricROI;
import jorus.patterns.PatReduce;
import jorus.patterns.PatSvo;
import jorus.pixel.Pixel;

public abstract class Array2dFloat<U extends Array2dFloat<U>> extends Array2d<float[], U> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6859296329319418412L;
	
//	private static long createCounter = 0; // TODO debug counter

//	public static long getAndResetcreateCounter() {
//		long tmp = createCounter;
//		createCounter = 0;
//		return tmp;
//	}

	/*** Public Methods ***********************************************/

	public Array2dFloat(Array2dFloat<U> orig, int newBW, int newBH, boolean copyData) {
		super(orig, newBW, newBH, copyData);
	}

	public Array2dFloat(Array2dFloat<U> orig, boolean copyData) {
		super(orig, copyData);
	}

	public Array2dFloat(int w, int h, int bw, int bh, int e, boolean create) {
		super(w, h, bw, bh, e, create);
	}

	public Array2dFloat(int w, int h, int bw, int bh, int e, float[] array,
			boolean copy) {
		super(w, h, bw, bh, e, array, copy);
	}

	/*** Single Pixel (Value) Operations ******************************/

	@Override
	public U setSingleValue(Pixel<float[]> p, int xidx,
			int yidx, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatSvo.dispatch(this, xidx, yidx, inpl,
				new SvoSetFloat(p.getValue()));
	}

	@Override
	public U addSingleValue(Pixel<float[]> p, int xidx,
			int yidx, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatSvo.dispatch(this, xidx, yidx, inpl,
				new SvoAddFloat(p.getValue()));
	}

	/*** Unary Pixel Operations ***************************************/

	/** Binary Pixel Single Value Operations **************************/

	@Override
	public U setVal(Pixel<float[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoSetValFloat(p.getValue()));
	}

	@Override
	public U addVal(Pixel<float[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoAddValFloat(p.getValue()));
	}

	@Override
	public U subVal(Pixel<float[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoSubValFloat(p.getValue()));
	}

	@Override
	public U mulVal(Pixel<float[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoMulValFloat(p.getValue()));
	}

	@Override
	public U divVal(Pixel<float[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoDivValFloat(p.getValue()));
	}

	@Override
	public U minVal(Pixel<float[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoMinValFloat(p.getValue()));
	}

	@Override
	public U maxVal(Pixel<float[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoMaxValFloat(p.getValue()));
	}

	@Override
	public U negDivVal(Pixel<float[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl,
				new BpoNegDivValFloat(p.getValue()));
	}
	
	@Override
	public U posDivVal(Pixel<float[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl,
				new BpoPosDivValFloat(p.getValue()));
	}

	@Override
	public U absDivVal(Pixel<float[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl,
				new BpoAbsDivValFloat(p.getValue()));
	}

	/*** Binary Pixel Operations **************************************/

	@Override
	public U add(Array2d<float[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoAddFloat());
	}

	@Override
	public U sub(Array2d<float[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoSubFloat());
	}

	@Override
	public U mul(Array2d<float[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMulFloat());
	}

	@Override
	public U div(Array2d<float[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoDivFloat());
	}

	@Override
	public U min(Array2d<float[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMinFloat());
	}

	@Override
	public U max(Array2d<float[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMaxFloat());
	}

	@Override
	public U negDiv(Array2d<float[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoNegDivFloat());
	}

	@Override
	public U posDiv(Array2d<float[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoPosDivFloat());
	}

	@Override
	public U absDiv(Array2d<float[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoAbsDivFloat());
	}

	/*** Reduction Operations *****************************************/

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#pixInf()
	 */
	@Override
	public U pixInf() {
		U result = createCompatibleArray(1, 1, 0, 0);
		
//		U result;	
//		if (this instanceof Array2dScalarFloat) {
//			result = (U) new Array2dScalarFloat(1, 1, 0, 0, false);
//		} else {
//			result = (U) new Array2dVecFloat(1, 1, 0, 0, getExtent(), false);
//		}
		return PatReduce.dispatch(result, this, new ReduceInfFloat());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#pixMax()
	 */
	@Override
	public U pixMax() {
		U result = createCompatibleArray(1, 1, 0, 0);
//		if (this instanceof Array2dScalarFloat) {
//			result = new Array2dScalarFloat(1, 1, 0, 0, false);
//		} else {
//			result = new Array2dVecFloat(1, 1, 0, 0, getExtent(), false);
//		}
		return PatReduce.dispatch(result, this, new ReduceMaxFloat());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#pixMin()
	 */
	@Override
	public U pixMin() {
		U result = createCompatibleArray(1, 1, 0, 0);
		
//		if (this instanceof Array2dScalarFloat) {
//			result = new Array2dScalarFloat(1, 1, 0, 0, false);
//		} else {
//			result = new Array2dVecFloat(1, 1, 0, 0, getExtent(), false);
//		}
		return PatReduce.dispatch(result, this, new ReduceMinFloat());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#pixProduct()
	 */
	@Override
	public U pixProduct() {
		U result = createCompatibleArray(1, 1, 0, 0);
		
//		if (this instanceof Array2dScalarFloat) {
//			result = new Array2dScalarFloat(1, 1, 0, 0, false);
//		} else {
//			result = new Array2dVecFloat(1, 1, 0, 0, getExtent(), false);
//		}
		return PatReduce.dispatch(result, this, new ReduceProductFloat());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#pixSum()
	 */
	@Override
	public U pixSum() {
		U result = createCompatibleArray(1, 1, 0, 0);
//		if (this instanceof Array2dScalarFloat) {
//			result = new Array2dScalarFloat(1, 1, 0, 0, false);
//		} else {
//			result = new Array2dVecFloat(1, 1, 0, 0, getExtent(), false);
//		}
		return PatReduce.dispatch(result, this, new ReduceSumFloat());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#pixSup()
	 */
	@Override
	public U pixSup() {
		U result = createCompatibleArray(1, 1, 0, 0);
//		if (this instanceof Array2dScalarFloat) {
//			result = new Array2dScalarFloat(1, 1, 0, 0, false);
//		} else {
//			result = new Array2dVecFloat(1, 1, 0, 0, getExtent(), false);
//		}
		return PatReduce.dispatch(result, this, new ReduceSupFloat());
	}

	/*** Convolution Operations ***************************************/

	@Override
	public U convGauss2d(double sigmaX, int orderDerivX,
			double truncationX, double sigmaY, int orderDerivY,
			double truncationY, boolean inplace) {
		if (truncationX < 1) {
			truncationX = 3;
		}
		if (truncationY < 1) {
			truncationY = 3;
		}
		// TODO what about the accuracy??
		Array2dScalarFloat gx = Gaussian1d.createFloat(sigmaX, orderDerivX,
				0.995, (int) (truncationX * sigmaX * 2 + 1), getWidth());
		Array2dScalarFloat gy = Gaussian1d.createFloat(sigmaY, orderDerivY,
				0.995, (int) (truncationY * sigmaY * 2 + 1), getHeight());

		return PatGeneralizedConvolution2dSeparated.dispatch(this, gx, gy,
				new Convolution1dFloat(), new SetBorderMirrorFloat(), inplace);
	}

	@Override
	public U convGauss1x2d(double sigmaT, double sigmaR,
			double phiDegrees, int derivativeT, double n) {
		Array2dScalarFloat g = Gaussian2d.createGaussianKernel2dFloat(sigmaT,
				sigmaR, phiDegrees, derivativeT, n);

		return PatGeneralizedConvolution2d.dispatch(this, g,
				new Convolution2dFloat(), new SetBorderMirrorFloat());
	}

	@Override
	public final U convGaussAnisotropic2d(double sigmaU,
			int orderDerivU, double truncationU, double sigmaV,
			int orderDerivV, double truncationV, double phiRad, boolean inplace) {
		if (truncationU < 1) {
			truncationU = 3;
		}
		if (truncationV < 1) {
			truncationV = 3;
		}
		// TODO what about the accuracy??
		Array2dScalarFloat gx = Gaussian1d.createFloat(sigmaU, orderDerivU,
				0.995, (int) (truncationU * sigmaU * 2 + 1), getWidth());
		Array2dScalarFloat gy = Gaussian1d.createFloat(sigmaV, orderDerivV,
				0.995, (int) (truncationV * sigmaV * 2 + 1), getHeight());
		return PatGeneralizedConvolution2dRotatedSeparated.dispatch(this, gx,
				gy, phiRad, new ConvolutionRotated1dFloat(),
				new SetBorderMirrorFloat(), inplace);
	}

	@Override
	public U convKernelSeparated2d(Array2d<float[],?> kernelX,
			Array2d<float[],?> kernelY, boolean inplace) {
		return PatGeneralizedConvolution2dSeparated.dispatch(this, kernelX,
				kernelY, new Convolution1dFloat(), new SetBorderMirrorFloat(),
				inplace);

	}

	@Override
	public U convolution(Array2d<float[], ?> kernel) {
		// FIXME implement
		throw new UnsupportedOperationException();

	}

	@Override
	public U convolution1d(Array2d<float[], ?> kernel, int dimension) {
		return PatGeneralizedConvolution1d
				.dispatch(this, kernel, new Convolution1dFloat(), dimension,
						new SetBorderMirrorFloat());

	}

	@Override
	public U convolutionRotated1d(Array2d<float[], ?> kernel,
			double phirad) {
		return PatGeneralizedConvolution1dRotated.dispatch(this, kernel,
				phirad, new ConvolutionRotated1dFloat(),
				new SetBorderMirrorFloat());
	}

	// @Override
	// protected Class<?> getDataType() {
	// return double.class;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#geometricOp2d(Jama.Matrix, boolean, boolean,
	 * boolean, jorus.pixel.Pixel)
	 */
	@Override
	protected U geometricOp2d(Matrix transformationMatrix,
			boolean forwardMatrix, boolean linearInterpolation,
			boolean adjustSize, Pixel<float[]> background) {

		double[] translationVector;

		Matrix forwardsTransformationMatrix = transformationMatrix.clone();
		Matrix backwardsTransformationMatrix = transformationMatrix.clone();
		try {
			if (forwardMatrix) {
				backwardsTransformationMatrix.inverse();
			} else {
				forwardsTransformationMatrix.inverse();
			}
		} catch (Exception e) {
			// TODO Will (probably?) never happen
			throw new Error(e);
		}

		/*** Create result image and translation vector ***/
//		U destination;
		int newWidth, newHeight;
		if (adjustSize) {
			double[][] specs = calculateDimensionsandTranslationVector(forwardsTransformationMatrix);
			newWidth = (int) specs[1][0];
			newHeight= (int) specs[1][1];
//			destination = createCompatibleArray((int) specs[1][0],
//					(int) specs[1][1], 0, 0);
			translationVector = specs[0];
		} else {
//			destination = shallowClone();
			newWidth = getWidth();
			newHeight= getHeight();
			translationVector = new double[3];
		}

		Geometric2dFloat geometricOperation = new Geometric2dFloat(
				backwardsTransformationMatrix, translationVector,
				background.getValue(), linearInterpolation);
		return PatGeometric2d.dispatch(this, geometricOperation, newWidth, newHeight);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#geometricROI(int, int, jorus.pixel.Pixel, int,
	 * int)
	 */
	@Override
	public final U geometricOpROI(int newImWidth, int newImHeight,
			Pixel<float[]> background, int beginX, int beginY) {
		GeometricROIFloat geometricOperation = new GeometricROIFloat(background);
		return PatGeometricROI.dispatch(this, newImWidth, newImHeight, beginX,
				beginY, geometricOperation);
	}

	public int getDataArraySize() {
		float[] data = getData();
		if (data == null) {
			return -1;
		} else {
			return data.length;
		}
	}

	@Override
	public float[] createDataArray(int size) {
		return new float[size];
	}
	
	@Override
	protected int getDataLength() {
		return getData().length;
	}

	@Override
	protected float[] copyArray() {
		if (getData() == null) {
			return null;
		}
		return getData().clone();
	}
}
