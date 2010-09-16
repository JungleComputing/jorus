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
import jorus.operations.generalizedconvolution.GeneralizedConvolutionRotated1d;
import jorus.parallel.PxSystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import array.CxArray2dScalarDouble;

public class PatGeneralizedConvolution1dRotated {
	private static final Logger logger = LoggerFactory
			.getLogger(PatGeneralizedConvolution1dRotated.class);

	public static <T, U extends Array2d<T, U>> U dispatch(
			Array2d<T, U> sourceImage, Array2d<T, ?> kernel, double theta,
			GeneralizedConvolutionRotated1d<T> convolutionOperation,
			SetBorder<T> borderOperation) {

		int borderWidth = ((int) (((kernel.getWidth() - 1) / 2) * Math.abs(Math
				.cos(theta)))) + 1;
		int borderHeight = ((int) (((kernel.getWidth() - 1) / 2) * Math
				.abs(Math.sin(theta)))) + 1;

		U result = null;

		if (borderWidth > sourceImage.getBorderWidth()
				|| borderHeight > sourceImage.getBorderHeight()) {
			result = sourceImage.clone(borderWidth, borderHeight);
		} else {
			result = sourceImage.clone();
		}

		if (PxSystem.initialized()) { // run parallel
			final PxSystem px = PxSystem.get();

			try {
				result.changeStateTo(Array2d.LOCAL_PARTIAL);
//				if (result.getState() != Array2d.LOCAL_PARTIAL) {
//					if (px.isRoot()) {
//						if (logger.isDebugEnabled()) {
//							logger.debug("GENCONV SCATTER 1...");
//						}
//					}
//					px.scatter(result);
//				}
//				if (kernel.getState() != Array2d.LOCAL_FULL) {
//					px.broadcast(kernel);
//				}
				kernel.changeStateTo(Array2d.LOCAL_FULL);
				
				U temp = result.clone();
				PatSetBorder.dispatch(temp, borderWidth, borderHeight,
						borderOperation);

				convolutionOperation.init(temp, kernel, theta, true);
				convolutionOperation.doIt(result.getData(), temp.getData(),
						kernel.getData());
			} catch (Exception e) {
				//
			}

		} else { // run sequential
			U temp = result.clone();
			PatSetBorder.dispatch(temp, borderWidth, borderHeight,
					borderOperation);

			convolutionOperation.init(temp, kernel, theta, false);
			convolutionOperation.doIt(result.getData(), temp.getData(),
					kernel.getData());
		}

		return result;
	}
}
