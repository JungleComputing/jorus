/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.patterns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jorus.array.Array2d;
import jorus.operations.communication.SetBorder;
import jorus.operations.generalizedconvolution.GeneralizedConvolution1d;
import jorus.parallel.PxSystem;

//import array.CxArray2dScalarDouble;

public class PatGeneralizedConvolution2dSeparated {

	private static final Logger logger = LoggerFactory
			.getLogger(PatGeneralizedConvolution2dSeparated.class);

	static Object cache;

	public static <T, U extends Array2d<T, U>> U dispatch(Array2d<T, U> source,
			Array2d<T, ?> kernelX, Array2d<T, ?> kernelY,
			GeneralizedConvolution1d<T> gco, SetBorder<T> sbo, boolean inplace) {
		int numX = kernelX.getWidth() / 2;
		int numY = kernelY.getWidth() / 2;

		U dst = null;
		if (PxSystem.initialized()) {

			final PxSystem px = PxSystem.get();
			final boolean root = px.isRoot();

			// run parallel
			try {
				if (source.getState() != Array2d.LOCAL_PARTIAL) {
					if (root) {
						if (logger.isDebugEnabled()) {
							System.out.println("GENCONV SCATTER 1...");
						}
					}
					px.scatter(source);
				}
				if (numX > source.getBorderWidth()
						|| numY > source.getBorderHeight()) {
					dst = source.clone(numX, numY);
				} else if (inplace) {
					dst = (U) source;
				} else {
					dst = source.clone();
				}

				if (kernelX.getState() != Array2d.LOCAL_FULL) {
					px.broadcast(kernelX);
				}
				if (kernelY.getState() != Array2d.LOCAL_FULL) {
					px.broadcast(kernelY);
				}

				PatSetBorder.dispatch(dst, numX, 0, sbo);
				U tmp = dst.shallowClone();
				// U tmp = dst.clone();
				gco.init(dst, kernelX, 0, true);
				gco.doIt(tmp.getData(), dst.getData(), kernelX.getData());

				PatSetBorder.dispatch(tmp, 0, numY, sbo);
				gco.init(tmp, kernelY, 1, true);
				gco.doIt(dst.getData(), tmp.getData(), kernelY.getData());
			} catch (Exception e) {
				System.err.println("Failed to perform operation!");
				e.printStackTrace(System.err);

			}

		} else { // run sequential
			if (numX > source.getBorderWidth()
					|| numY > source.getBorderHeight()) {
				dst = source.clone(numX, numY);
			} else if (inplace) {
				dst = (U) source;
			} else {
				dst = source.clone();
			}

			PatSetBorder.dispatch(dst, numX, 0, sbo);
			// U tmp = dst.createCompatibleArray(dst.getWidth(),
			// dst.getHeight(), dst.getBorderWidth(), dst.getBorderHeight());
			// U tmp = dst.clone();
			U tmp;
			if (cache != null && cache.getClass().equals(dst.getClass())
					&& dst.equalSignature((U) cache)
					&& dst.getBorderHeight() == ((U) cache).getBorderHeight()
					&& dst.getBorderWidth() == ((U) cache).getBorderWidth()) {
				tmp = (U) cache;
			} else {
				tmp = dst.shallowClone();
				cache = tmp;
				// System.err.println("miss");
			}
			gco.init(dst, kernelX, 0, false);
			gco.doIt(tmp.getData(), dst.getData(), kernelX.getData());

			PatSetBorder.dispatch(tmp, 0, numY, sbo);
			gco.init(dst, kernelY, 1, false);
			gco.doIt(dst.getData(), tmp.getData(), kernelY.getData());
		}
		return dst;
	}
}
