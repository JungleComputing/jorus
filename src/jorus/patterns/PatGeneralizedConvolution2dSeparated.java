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
	public static <T,U extends Array2d<T,U>> U dispatch(Array2d<T,U> source,
			Array2d<T,?> kernelX, Array2d<T,?> kernelY,
			GeneralizedConvolution1d<T> gco, SetBorder<T> sbo, boolean inplace) {
		int numX = kernelX.getWidth() / 2;
		int numY = kernelY.getWidth() / 2;

		U dst = null;

		if (numX > source.getBorderWidth() || numY > source.getBorderHeight()) {
			dst = source.clone(numX, numY);
		} else if(inplace) {
			dst = (U) source;
		} else {
			dst = source.clone();
		}

		if (PxSystem.initialized()) {

			final PxSystem px = PxSystem.get();
			final boolean root = px.isRoot();

			// run parallel
			try {
				if (dst.getState() != Array2d.LOCAL_PARTIAL) {
					if (root)
						System.out.println("GENCONV SCATTER 1...");
					px.scatter(dst);
				}
				if (kernelX.getState() != Array2d.LOCAL_FULL) {
					px.broadcast(kernelX);
				}
				if (kernelY.getState() != Array2d.LOCAL_FULL) {
					px.broadcast(kernelY);
				}

				PatSetBorder.dispatch(dst, numX, 0, sbo);
				U tmp = dst.clone();
				gco.init(tmp, kernelX, 0, true);
				gco.doIt(tmp.getData(), dst
						.getData(), kernelX.getData());

				PatSetBorder.dispatch(tmp, 0, numY, sbo);
				gco.init(dst, kernelY, 1, true);
				gco.doIt(dst.getData(), tmp
						.getData(), kernelY.getData());
			} catch (Exception e) {
				System.err.println("Failed to perform operation!");
				e.printStackTrace(System.err);

			}

		} else { // run sequential
			PatSetBorder.dispatch(dst, numX, 0, sbo);
			U tmp = dst.clone();
			gco.init(dst, kernelX, 0, false);
			gco.doIt(tmp.getData(), dst.getData(), kernelX
					.getData());

			PatSetBorder.dispatch(tmp, 0, numY, sbo);
			gco.init(dst, kernelY, 1, false);
			gco.doIt(dst.getData(), tmp.getData(), kernelY
					.getData());
		}
		return dst;
	}
}
