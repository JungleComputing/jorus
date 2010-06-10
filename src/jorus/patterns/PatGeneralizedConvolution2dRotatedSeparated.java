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

public class PatGeneralizedConvolution2dRotatedSeparated {
	private static final Logger logger = LoggerFactory
			.getLogger(PatGeneralizedConvolution2dRotatedSeparated.class);

	public static <T> Array2d<T> dispatch(Array2d<T> sourceImage,
			Array2d<T> kernelU, Array2d<T> kernelV, double phiRad,
			GeneralizedConvolutionRotated1d<T> convolutionOperation,
			SetBorder<T> borderOperation, boolean inplace) {

		int borderWidthU = ((int) (((kernelU.getWidth() - 1) / 2) * Math
				.abs(Math.cos(phiRad)))) + 1;
		int borderHeightU = ((int) (((kernelU.getWidth() - 1) / 2) * Math
				.abs(Math.sin(phiRad)))) + 1;
		int borderWidthV = ((int) (((kernelV.getWidth() - 1) / 2) * Math
				.abs(Math.sin(phiRad)))) + 1;
		int borderHeightV = ((int) (((kernelV.getWidth() - 1) / 2) * Math
				.abs(Math.cos(phiRad)))) + 1;

		int borderWidth = borderWidthU > borderWidthV ? borderWidthU
				: borderWidthV;
		int borderHeight = borderHeightU > borderHeightV ? borderHeightU
				: borderHeightV;
		Array2d<T> result = null;

		if (borderWidth > sourceImage.getBorderWidth()
				|| borderHeight > sourceImage.getBorderHeight()) {
			result = sourceImage.clone(borderWidth, borderHeight);
		} else if(inplace) {
			result = sourceImage;
		} else {
			result = sourceImage.clone();
		}

		if (PxSystem.initialized()) { // run parallel
			final PxSystem px = PxSystem.get();
			final boolean root = px.isRoot();

			try {
				if (result.getState() != Array2d.LOCAL_PARTIAL) {
					if (root)
						logger.debug("GENCONV SCATTER 1...");
					px.scatter(result);
				}
				if (kernelU.getState() != Array2d.LOCAL_FULL) {
					px.broadcast(kernelU);
				}
				if (kernelV.getState() != Array2d.LOCAL_FULL) {
					px.broadcast(kernelV);
				}
				PatSetBorder.dispatch(result, borderWidthU, borderHeightU, borderOperation);
				Array2d<T> tmp = result.clone();
				convolutionOperation.init(result, kernelU, phiRad, true);
				convolutionOperation.doIt(tmp.getData(), result.getData(), kernelU
						.getData());

				PatSetBorder.dispatch(tmp, borderWidthV, borderHeightV, borderOperation);
				convolutionOperation.init(tmp, kernelV, phiRad + 0.5 * Math.PI, true); //FIXME + or - 0.5 * PI ????
				convolutionOperation.doIt(result.getData(), tmp.getData(), kernelV
						.getData());
			} catch (Exception e) {
				//
			}

		} else { // run sequential			
			PatSetBorder.dispatch(result, borderWidthU, borderHeightU, borderOperation);
			Array2d<T> tmp = result.clone();
			convolutionOperation.init(result, kernelU, phiRad, false);
			convolutionOperation.doIt(tmp.getData(), result.getData(), kernelU
					.getData());

			PatSetBorder.dispatch(tmp, borderWidthV, borderHeightV, borderOperation);
			convolutionOperation.init(tmp, kernelV, phiRad + 0.5 * Math.PI, false); //FIXME + or - 0.5 * PI ????
			convolutionOperation.doIt(result.getData(), tmp.getData(), kernelV
					.getData());
		}

		return result;
	}
}
