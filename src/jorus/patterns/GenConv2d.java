/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *  Timo van Kessel (tpkessel@cs.vu.nl)
 *
 */

package jorus.patterns;

import jorus.array.CxArray2d;
import jorus.operations.CxSetBorder;
import jorus.parallel.PxSystem;

public abstract class GenConv2d<T> {
	private final CxArray2d<T> source;
	private final CxArray2d<T> kernel;
	private final CxSetBorder<T> setBorderOperation;

	
	@SuppressWarnings("unused")
	private GenConv2d() throws Exception { // prevent the use of the default
										// constructor
		throw new Exception("default constructor not allowed");
	}

	public GenConv2d(final CxArray2d<T> source, final CxArray2d<T> kernel, final CxSetBorder<T> setBorderOperation) {
		this.source = source;
		this.kernel = kernel;	
		this.setBorderOperation = setBorderOperation;
	}
	
	public CxArray2d<T> dispatch() {
		int numX = kernel.getWidth() / 2;
		int numY = kernel.getHeight() / 2;

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
				init(dst, kernel, true);
				doIt(tmp.getPartialData(), dst.getPartialData(), kernel.getData());
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
			init(dst, kernel, false);
			doIt(tmp.getData(), dst.getData(), kernel.getData());
		}

		return dst;
	}
	
	protected abstract void init(CxArray2d<T> image, CxArray2d<T> kernel,
			boolean parallel);

	protected abstract void doIt(T destination, T source, T kernel);
	

}
