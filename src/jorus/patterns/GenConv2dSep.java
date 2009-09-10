/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.patterns;

import jorus.array.CxArray2d;
import jorus.operations.CxSetBorder;
import jorus.parallel.PxSystem;

//import array.CxArray2dScalarDouble;

public abstract class GenConv2dSep<T> {

	private final CxArray2d<T> source;
	private final CxArray2d<T> horizontalKernel;
	private final CxArray2d<T> verticalKernel;
	private final CxSetBorder<T> setBorderOperation;

	@SuppressWarnings("unused")
	private GenConv2dSep() throws Exception { // prevent the use of the default
		// constructor
		throw new Exception("default constructor not allowed");
	}

	public GenConv2dSep(final CxArray2d<T> solurec, final CxArray2d<T> ker1,
			final CxArray2d<T> ker2, final CxSetBorder<T> sbo) {
		this.source = solurec;
		this.horizontalKernel = ker1;
		this.verticalKernel = ker2;
		this.setBorderOperation = sbo;
	}

	public CxArray2d<T> dispatch() {
		int numX = horizontalKernel.getWidth() / 2;
		int numY = verticalKernel.getWidth() / 2;

		CxArray2d<T> dst = null;

		if (numX > source.getBorderWidth() || numY > source.getBorderHeight()) {
			dst = source.clone(numX, numY);
		} else {
			dst = source.clone();
		}

		if (PxSystem.initialized()) { // run parallel
			try {

				if (dst.getLocalState() != CxArray2d.VALID
						|| dst.getDistributionType() != CxArray2d.PARTIAL) {
					if (PxSystem.myCPU() == 0)
						System.out.println("GENCONV SCATTER 1...");
					PxSystem.scatterOFT(dst);
				}

				setBorderOperation.dispatch(dst, numX, 0);
				CxArray2d<T> tmp = dst.clone();
				init(dst, horizontalKernel, verticalKernel, true);
				doIt(tmp.getPartialData(), dst.getPartialData(),
						horizontalKernel.getData(), 0);
				setBorderOperation.dispatch(tmp, 0, numY);
				doIt(dst.getPartialData(), tmp.getPartialData(),
						verticalKernel.getData(), 1);
				dst.setGlobalState(CxArray2d.INVALID);

				// if (PxSystem.myCPU() == 0)
				// System.out.println("GENCONV GATHER...");
				// PxSystem.gatherOFT(dst);

			} catch (Exception e) {
				//
			}

		} else { // run sequential
			setBorderOperation.dispatch(dst, numX, 0);
			CxArray2d<T> tmp = dst.clone();
			init(dst, horizontalKernel, verticalKernel, false);
			doIt(tmp.getData(), dst.getData(), horizontalKernel.getData(), 0);
			setBorderOperation.dispatch(tmp, 0, numY);
			doIt(dst.getData(), tmp.getData(), verticalKernel.getData(), 1);
		}

		return dst;
	}

	protected abstract void doIt(T dst, T src, T ker1, int direction);

	protected abstract void init(CxArray2d<T> s1, CxArray2d<T> ker1,
			CxArray2d<T> ker2, boolean parallel);
}
