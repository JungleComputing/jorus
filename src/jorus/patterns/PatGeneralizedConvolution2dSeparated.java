/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.patterns;

import jorus.array.Array2d;
import jorus.operations.communication.SetBorder;
import jorus.operations.generalizedconvolution.GeneralizedConvolution1d;
import jorus.parallel.PxSystem;

//import array.CxArray2dScalarDouble;

public class PatGeneralizedConvolution2dSeparated {
	public static <T> Array2d<T> dispatch(Array2d<T> source,
			Array2d<T> kernelX, Array2d<T> kernelY,
			GeneralizedConvolution1d<T> gco, SetBorder<T> sbo, boolean inplace) {
		int numX = kernelX.getWidth() / 2;
		int numY = kernelY.getWidth() / 2;

		Array2d<T> dst = null;

		if (numX > source.getBorderWidth() || numY > source.getBorderHeight()) {
			dst = source.clone(numX, numY);
		} else if(inplace) {
			dst = source;
		} else {
			dst = source.clone();
		}

		if (PxSystem.initialized()) {

			final PxSystem px = PxSystem.get();
			final boolean root = px.isRoot();

			// run parallel
			try {
				if (dst.getLocalState() != Array2d.LOCAL_PARTIAL) {
					if (root)
						System.out.println("GENCONV SCATTER 1...");
					px.scatter(dst);
				}

				PatSetBorder.dispatch(dst, numX, 0, sbo);
				Array2d<T> tmp = dst.clone();
				gco.init(dst, kernelX, 0, true);
				gco.doIt(tmp.getPartialDataWriteOnly(), dst
						.getPartialDataReadOnly(), kernelX.getDataReadOnly());

				PatSetBorder.dispatch(tmp, 0, numY, sbo);
				gco.init(dst, kernelY, 1, true);
				gco.doIt(dst.getPartialDataWriteOnly(), tmp
						.getPartialDataReadOnly(), kernelY.getDataReadOnly());

//				dst.setGlobalState(GlobalState.INVALID);
				dst.setGlobalState(Array2d.GLOBAL_INVALID);
			} catch (Exception e) {
				System.err.println("Failed to perform operation!");
				e.printStackTrace(System.err);

			}

		} else { // run sequential
			PatSetBorder.dispatch(dst, numX, 0, sbo);
			Array2d<T> tmp = dst.clone();
			gco.init(dst, kernelX, 0, false);
			gco.doIt(tmp.getDataWriteOnly(), dst.getDataReadOnly(), kernelX
					.getDataReadOnly());

			PatSetBorder.dispatch(tmp, 0, numY, sbo);
			gco.init(dst, kernelY, 1, false);
			gco.doIt(dst.getDataWriteOnly(), tmp.getDataReadOnly(), kernelY
					.getDataReadOnly());
		}

		return dst;
	}
}
