/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

import jorus.operations.bpo.BpoAbsDivDouble;
import jorus.operations.bpo.BpoAddDouble;
import jorus.operations.bpo.BpoDivDouble;
import jorus.operations.bpo.BpoMaxDouble;
import jorus.operations.bpo.BpoMinDouble;
import jorus.operations.bpo.BpoMulDouble;
import jorus.operations.bpo.BpoNegDivDouble;
import jorus.operations.bpo.BpoPosDivDouble;
import jorus.operations.bpo.BpoSubDouble;
import jorus.operations.bpoval.BpoAbsDivValDouble;
import jorus.operations.bpoval.BpoAddValDouble;
import jorus.operations.bpoval.BpoDivValDouble;
import jorus.operations.bpoval.BpoMaxValDouble;
import jorus.operations.bpoval.BpoMinValDouble;
import jorus.operations.bpoval.BpoMulValDouble;
import jorus.operations.bpoval.BpoNegDivValDouble;
import jorus.operations.bpoval.BpoSetValDouble;
import jorus.operations.bpoval.BpoSubValDouble;
import jorus.operations.communication.SetBorderMirrorDouble;
import jorus.operations.generalizedconvolution.Convolution1dDouble;
import jorus.operations.generalizedconvolution.Convolution2dDouble;
import jorus.operations.generalizedconvolution.ConvolutionRotated1dDouble;
import jorus.operations.geometric.Geometric2dDouble;
import jorus.operations.geometric.GeometricROIDouble;
import jorus.operations.reduce.ReduceInfDouble;
import jorus.operations.reduce.ReduceMaxDouble;
import jorus.operations.reduce.ReduceMinDouble;
import jorus.operations.reduce.ReduceProductDouble;
import jorus.operations.reduce.ReduceSumDouble;
import jorus.operations.reduce.ReduceSupDouble;
import jorus.operations.svo.SvoAddDouble;
import jorus.operations.svo.SvoSetDouble;
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

public abstract class Array2dDouble<U extends Array2d<double[], U>> extends Array2d<double[], U> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6859296329319418412L;

	/*** Public Methods ***********************************************/

	public Array2dDouble(Array2dDouble<U> orig, int newBW, int newBH) {
		super(orig, newBW, newBH);
	}

	public Array2dDouble(Array2dDouble<U> orig) {
		super(orig);
	}

	public Array2dDouble(int w, int h, int bw, int bh, int e, boolean create) {
		super(w, h, bw, bh, e, create);
	}

	public Array2dDouble(int w, int h, int bw, int bh, int e, double[] array,
			boolean copy) {
		super(w, h, bw, bh, e, array, copy);
	}

	/*** Single Pixel (Value) Operations ******************************/

	@Override
	public U setSingleValue(Pixel<double[]> p, int xidx,
			int yidx, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatSvo.dispatch(this, xidx, yidx, inpl,
				new SvoSetDouble(p.getValue()));
	}

	@Override
	public U addSingleValue(Pixel<double[]> p, int xidx,
			int yidx, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatSvo.dispatch(this, xidx, yidx, inpl,
				new SvoAddDouble(p.getValue()));
	}

	/*** Unary Pixel Operations ***************************************/

	/** Binary Pixel Single Value Operations **************************/

	@Override
	public U setVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal
				.dispatch(this, inpl, new BpoSetValDouble(p.getValue()));
	}

	@Override
	public U addVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal
				.dispatch(this, inpl, new BpoAddValDouble(p.getValue()));
	}

	@Override
	public U subVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal
				.dispatch(this, inpl, new BpoSubValDouble(p.getValue()));
	}

	@Override
	public U mulVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal
				.dispatch(this, inpl, new BpoMulValDouble(p.getValue()));
	}

	@Override
	public U divVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal
				.dispatch(this, inpl, new BpoDivValDouble(p.getValue()));
	}

	@Override
	public U minVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal
				.dispatch(this, inpl, new BpoMinValDouble(p.getValue()));
	}

	@Override
	public U maxVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal
				.dispatch(this, inpl, new BpoMaxValDouble(p.getValue()));
	}

	@Override
	public U negDivVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl,
				new BpoNegDivValDouble(p.getValue()));
	}

	@Override
	public U absDivVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl,
				new BpoAbsDivValDouble(p.getValue()));
	}

	/*** Binary Pixel Operations **************************************/

	@Override
	public U add(Array2d<double[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoAddDouble());
	}

	@Override
	public U sub(Array2d<double[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoSubDouble());
	}

	@Override
	public U mul(Array2d<double[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMulDouble());
	}

	@Override
	public U div(Array2d<double[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoDivDouble());
	}

	@Override
	public U min(Array2d<double[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMinDouble());
	}

	@Override
	public U max(Array2d<double[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMaxDouble());
	}

	@Override
	public U negDiv(Array2d<double[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoNegDivDouble());
	}

	@Override
	public U posDiv(Array2d<double[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoPosDivDouble());
	}

	@Override
	public U absDiv(Array2d<double[], ?> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoAbsDivDouble());
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
//		if (this instanceof Array2dScalarDouble) {
//			result = new Array2dScalarDouble(1, 1, 0, 0, false);
//		} else {
//			result = new Array2dVecDouble(1, 1, 0, 0, getExtent(), false);
//		}
		return PatReduce.dispatch(result, this, new ReduceInfDouble());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#pixMax()
	 */
	@Override
	public U pixMax() {
		U result = createCompatibleArray(1, 1, 0, 0);
//		if (this instanceof Array2dScalarDouble) {
//			result = new Array2dScalarDouble(1, 1, 0, 0, false);
//		} else {
//			result = new Array2dVecDouble(1, 1, 0, 0, getExtent(), false);
//		}
		return PatReduce.dispatch(result, this, new ReduceMaxDouble());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#pixMin()
	 */
	@Override
	public U pixMin() {
		U result = createCompatibleArray(1, 1, 0, 0);
//		if (this instanceof Array2dScalarDouble) {
//			result = new Array2dScalarDouble(1, 1, 0, 0, false);
//		} else {
//			result = new Array2dVecDouble(1, 1, 0, 0, getExtent(), false);
//		}
		return PatReduce.dispatch(result, this, new ReduceMinDouble());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#pixProduct()
	 */
	@Override
	public U pixProduct() {
		U result = createCompatibleArray(1, 1, 0, 0);
//		if (this instanceof Array2dScalarDouble) {
//			result = new Array2dScalarDouble(1, 1, 0, 0, false);
//		} else {
//			result = new Array2dVecDouble(1, 1, 0, 0, getExtent(), false);
//		}
		return PatReduce.dispatch(result, this, new ReduceProductDouble());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#pixSum()
	 */
	@Override
	public U pixSum() {
		U result = createCompatibleArray(1, 1, 0, 0);
//		if (this instanceof Array2dScalarDouble) {
//			result = new Array2dScalarDouble(1, 1, 0, 0, false);
//		} else {
//			result = new Array2dVecDouble(1, 1, 0, 0, getExtent(), false);
//		}
		return PatReduce.dispatch(result, this, new ReduceSumDouble());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#pixSup()
	 */
	@Override
	public U pixSup() {
		U result = createCompatibleArray(1, 1, 0, 0);
//		if (this instanceof Array2dScalarDouble) {
//			result = new Array2dScalarDouble(1, 1, 0, 0, false);
//		} else {
//			result = new Array2dVecDouble(1, 1, 0, 0, getExtent(), false);
//		}
		return PatReduce.dispatch(result, this, new ReduceSupDouble());
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
		Array2dScalarDouble gx = Gaussian1d.createDouble(sigmaX, orderDerivX,
				0.995, (int) (truncationX * sigmaX * 2 + 1), getWidth());
		Array2dScalarDouble gy = Gaussian1d.createDouble(sigmaY, orderDerivY,
				0.995, (int) (truncationY * sigmaY * 2 + 1), getHeight());

		return PatGeneralizedConvolution2dSeparated
				.dispatch(this, gx, gy, new Convolution1dDouble(),
						new SetBorderMirrorDouble(), inplace);
	}

	@Override
	public U convGauss1x2d(double sigmaT, double sigmaR,
			double phiDegrees, int derivativeT, double n) {
		Array2dScalarDouble g = Gaussian2d.createGaussianKernel2dDouble(sigmaT,
				sigmaR, phiDegrees, derivativeT, n);

		return PatGeneralizedConvolution2d.dispatch(this, g,
				new Convolution2dDouble(), new SetBorderMirrorDouble());
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
		Array2dScalarDouble gx = Gaussian1d.createDouble(sigmaU, orderDerivU,
				0.995, (int) (truncationU * sigmaU * 2 + 1), getWidth());
		Array2dScalarDouble gy = Gaussian1d.createDouble(sigmaV, orderDerivV,
				0.995, (int) (truncationV * sigmaV * 2 + 1), getHeight());
		return PatGeneralizedConvolution2dRotatedSeparated.dispatch(this, gx,
				gy, phiRad, new ConvolutionRotated1dDouble(),
				new SetBorderMirrorDouble(), inplace);
	}

	@Override
	public U convKernelSeparated2d(Array2d<double[], ?> kernelX,
			Array2d<double[], ?> kernelY, boolean inplace) {
		return PatGeneralizedConvolution2dSeparated.dispatch(this, kernelX,
				kernelY, new Convolution1dDouble(),
				new SetBorderMirrorDouble(), inplace);

	}

	@Override
	public U convolution(Array2d<double[], ?> kernel) {
		// FIXME implement
		throw new UnsupportedOperationException();

	}

	@Override
	public U convolution1d(Array2d<double[], ?> kernel,
			int dimension) {
		return PatGeneralizedConvolution1d.dispatch(this, kernel,
				new Convolution1dDouble(), dimension,
				new SetBorderMirrorDouble());

	}

	@Override
	public U convolutionRotated1d(Array2d<double[], ?> kernel,
			double phirad) {
		return PatGeneralizedConvolution1dRotated.dispatch(this, kernel,
				phirad, new ConvolutionRotated1dDouble(),
				new SetBorderMirrorDouble());
	}

	// @Override
	// protected Class<?> getDataType() {
	// return double.class;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#geometricOp2d(Matrix, boolean, boolean,
	 * boolean, jorus.pixel.Pixel)
	 */
	@Override
	protected U geometricOp2d(Matrix transformationMatrix,
			boolean forwardMatrix, boolean linearInterpolation,
			boolean adjustSize, Pixel<double[]> background) {
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
		U destination;
		if (adjustSize) {
			double[][] specs = calculateDimensionsandTranslationVector(forwardsTransformationMatrix);
			destination = createCompatibleArray((int) specs[1][0],
					(int) specs[1][1], 0, 0);
			translationVector = specs[0];
		} else {
			destination = clone();
			translationVector = new double[3];
		}

		Geometric2dDouble geometricOperation = new Geometric2dDouble(
				backwardsTransformationMatrix, translationVector,
				background.getValue(), linearInterpolation);
		return PatGeometric2d.dispatch(destination, this, geometricOperation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#geometricROI(int, int, jorus.pixel.Pixel, int, int)
	 */
	@Override
	public final U geometricOpROI(int newImWidth, int newImHeight,
			Pixel<double[]> background, int beginX, int beginY) {
		GeometricROIDouble geometricOperation = new GeometricROIDouble(background);
		return PatGeometricROI.dispatch(this, newImWidth, newImHeight, beginX, beginY, geometricOperation);
	}

	private static long createCounter = 0; // TODO debug counter

	public static long getAndResetcreateCounter() {
		long tmp = createCounter;
		createCounter = 0;
		return tmp;
	}

	public int getDataArraySize() {
		double[] data = getData();
		if (data == null) {
			return -1;
		} else {
			return data.length;
		}
	}

	@Override
	public double[] createDataArray(int size) {
		// System.err.println("double[] #" + ++createCounter);
		createCounter++;
		return new double[size];
	}

	@Override
	protected double[] copyArray() {
		if (getData() == null) {
			return null;
		}
		return getData().clone();
	}
}
