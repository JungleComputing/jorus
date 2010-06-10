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
import jorus.operations.generalizedconvolution.ConvolutionRotated1dDouble;
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
import jorus.patterns.PatGeneralizedConvolution2dRotatedSeparated;
import jorus.patterns.PatGeneralizedConvolution2dSeparated;
import jorus.patterns.PatReduce;
import jorus.patterns.PatSvo;
import jorus.pixel.Pixel;

public abstract class Array2dDoubles extends Array2d<double[]> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6859296329319418412L;

	/*** Public Methods ***********************************************/

	public Array2dDoubles(Array2dDoubles orig, int newBW, int newBH) {
		super(orig, newBW, newBH);
	}

	public Array2dDoubles(Array2dDoubles orig) {
		super(orig);
	}

	public Array2dDoubles(int w, int h, int bw, int bh, int e, boolean create) {
		super(w, h, bw, bh, e, create);
	}

	public Array2dDoubles(int w, int h, int bw, int bh, int e, double[] array,
			boolean copy) {
		super(w, h, bw, bh, e, array, copy);
	}

	/*** Single Pixel (Value) Operations ******************************/

	@Override
	public Array2d<double[]> setSingleValue(Pixel<double[]> p, int xidx,
			int yidx, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatSvo.dispatch(this, xidx, yidx, inpl, new SvoSetDouble(p
				.getValue()));
	}

	@Override
	public Array2d<double[]> addSingleValue(Pixel<double[]> p, int xidx,
			int yidx, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatSvo.dispatch(this, xidx, yidx, inpl, new SvoAddDouble(p
				.getValue()));
	}

	/*** Unary Pixel Operations ***************************************/

	/** Binary Pixel Single Value Operations **************************/

	@Override
	public Array2d<double[]> setVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal
				.dispatch(this, inpl, new BpoSetValDouble(p.getValue()));
	}

	@Override
	public Array2d<double[]> addVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal
				.dispatch(this, inpl, new BpoAddValDouble(p.getValue()));
	}

	@Override
	public Array2d<double[]> subVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal
				.dispatch(this, inpl, new BpoSubValDouble(p.getValue()));
	}

	@Override
	public Array2d<double[]> mulVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal
				.dispatch(this, inpl, new BpoMulValDouble(p.getValue()));
	}

	@Override
	public Array2d<double[]> divVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal
				.dispatch(this, inpl, new BpoDivValDouble(p.getValue()));
	}

	@Override
	public Array2d<double[]> minVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal
				.dispatch(this, inpl, new BpoMinValDouble(p.getValue()));
	}

	@Override
	public Array2d<double[]> maxVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal
				.dispatch(this, inpl, new BpoMaxValDouble(p.getValue()));
	}

	@Override
	public Array2d<double[]> negDivVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoNegDivValDouble(p
				.getValue()));
	}

	@Override
	public Array2d<double[]> absDivVal(Pixel<double[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoAbsDivValDouble(p
				.getValue()));
	}

	/*** Binary Pixel Operations **************************************/

	@Override
	public Array2d<double[]> add(Array2d<double[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoAddDouble());
	}

	@Override
	public Array2d<double[]> sub(Array2d<double[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoSubDouble());
	}

	@Override
	public Array2d<double[]> mul(Array2d<double[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMulDouble());
	}

	@Override
	public Array2d<double[]> div(Array2d<double[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoDivDouble());
	}

	@Override
	public Array2d<double[]> min(Array2d<double[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMinDouble());
	}

	@Override
	public Array2d<double[]> max(Array2d<double[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMaxDouble());
	}

	@Override
	public Array2d<double[]> negDiv(Array2d<double[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoNegDivDouble());
	}

	@Override
	public Array2d<double[]> absDiv(Array2d<double[]> a, boolean inpl) {
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
	public Array2d<double[]> pixInf() {
		Array2dDoubles result;
		if (this instanceof Array2dScalarDouble) {
			result = new Array2dScalarDouble(1, 1, 0, 0, false);
		} else {
			result = new Array2dVecDouble(1, 1, 0, 0, getExtent(), false);
		}
		return PatReduce.dispatch(result, this, new ReduceInfDouble());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#pixMax()
	 */
	@Override
	public Array2d<double[]> pixMax() {
		Array2dDoubles result;
		if (this instanceof Array2dScalarDouble) {
			result = new Array2dScalarDouble(1, 1, 0, 0, false);
		} else {
			result = new Array2dVecDouble(1, 1, 0, 0, getExtent(), false);
		}
		return PatReduce.dispatch(result, this, new ReduceMaxDouble());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#pixMin()
	 */
	@Override
	public Array2d<double[]> pixMin() {
		Array2dDoubles result;
		if (this instanceof Array2dScalarDouble) {
			result = new Array2dScalarDouble(1, 1, 0, 0, false);
		} else {
			result = new Array2dVecDouble(1, 1, 0, 0, getExtent(), false);
		}
		return PatReduce.dispatch(result, this, new ReduceMinDouble());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#pixProduct()
	 */
	@Override
	public Array2d<double[]> pixProduct() {
		Array2dDoubles result;
		if (this instanceof Array2dScalarDouble) {
			result = new Array2dScalarDouble(1, 1, 0, 0, false);
		} else {
			result = new Array2dVecDouble(1, 1, 0, 0, getExtent(), false);
		}
		return PatReduce.dispatch(result, this, new ReduceProductDouble());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#pixSum()
	 */
	@Override
	public Array2d<double[]> pixSum() {
		Array2dDoubles result;
		if (this instanceof Array2dScalarDouble) {
			result = new Array2dScalarDouble(1, 1, 0, 0, false);
		} else {
			result = new Array2dVecDouble(1, 1, 0, 0, getExtent(), false);
		}
		return PatReduce.dispatch(result, this, new ReduceSumDouble());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jorus.array.Array2d#pixSup()
	 */
	@Override
	public Array2d<double[]> pixSup() {
		Array2dDoubles result;
		if (this instanceof Array2dScalarDouble) {
			result = new Array2dScalarDouble(1, 1, 0, 0, false);
		} else {
			result = new Array2dVecDouble(1, 1, 0, 0, getExtent(), false);
		}
		return PatReduce.dispatch(result, this, new ReduceSupDouble());
	}

	/*** Convolution Operations ***************************************/

	@Override
	public Array2d<double[]> convGauss2d(double sigmaX, int orderDerivX,
			double truncationX, double sigmaY, int orderDerivY,
			double truncationY, boolean inplace) {
		if (truncationX < 1) {
			truncationX = 3;
		}
		if (truncationY < 1) {
			truncationY = 3;
		}
		// TODO what about the accuracy??
		Array2dScalarDouble gx = Gaussian1d.create(sigmaX, orderDerivX, 0.995,
				(int) (truncationX * sigmaX * 2 + 1), getWidth());
		Array2dScalarDouble gy = Gaussian1d.create(sigmaY, orderDerivY, 0.995,
				(int) (truncationY * sigmaY * 2 + 1), getHeight());

		return PatGeneralizedConvolution2dSeparated
				.dispatch(this, gx, gy, new Convolution1dDouble(),
						new SetBorderMirrorDouble(), inplace);
	}

	@Override
	public final Array2d<double[]> convGaussAnisotropic2d(double sigmaU,
			int orderDerivU, double truncationU, double sigmaV,
			int orderDerivV, double truncationV, double phiRad, boolean inplace) {
		if (truncationU < 1) {
			truncationU = 3;
		}
		if (truncationV < 1) {
			truncationV = 3;
		}
		// TODO what about the accuracy??
		Array2dScalarDouble gx = Gaussian1d.create(sigmaU, orderDerivU, 0.995,
				(int) (truncationU * sigmaU * 2 + 1), getWidth());
		Array2dScalarDouble gy = Gaussian1d.create(sigmaV, orderDerivV, 0.995,
				(int) (truncationV * sigmaV * 2 + 1), getHeight());
		return PatGeneralizedConvolution2dRotatedSeparated.dispatch(this, gx,
				gy, phiRad, new ConvolutionRotated1dDouble(),
				new SetBorderMirrorDouble(), inplace);
	}

	@Override
	public Array2d<double[]> convKernelSeparated2d(Array2d<double[]> kernelX,
			Array2d<double[]> kernelY, boolean inplace) {
		return PatGeneralizedConvolution2dSeparated.dispatch(this, kernelX,
				kernelY, new Convolution1dDouble(),
				new SetBorderMirrorDouble(), inplace);

	}

	@Override
	public Array2d<double[]> convolution(Array2d<double[]> kernel) {
		// FIXME implement
		throw new UnsupportedOperationException();

	}

	@Override
	public Array2d<double[]> convolution1d(Array2d<double[]> kernel,
			int dimension) {
		return PatGeneralizedConvolution1d.dispatch(this, kernel,
				new Convolution1dDouble(), dimension,
				new SetBorderMirrorDouble());

	}

	@Override
	public Array2d<double[]> convolutionRotated1d(Array2d<double[]> kernel,
			double phirad) {
		return PatGeneralizedConvolution1dRotated.dispatch(this, kernel,
				phirad, new ConvolutionRotated1dDouble(),
				new SetBorderMirrorDouble());
	}

	// @Override
	// protected Class<?> getDataType() {
	// return double.class;
	// }

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
