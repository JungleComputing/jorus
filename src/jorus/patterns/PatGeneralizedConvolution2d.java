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
import jorus.operations.generalizedconvolution.GeneralizedConvolution2d;
import jorus.parallel.PxSystem;

public class PatGeneralizedConvolution2d {
	public static <T,U extends Array2d<T,U>> U dispatch(Array2d<T,U> sourceImage,
			Array2d<T,?> kernel,
			GeneralizedConvolution2d<T> convolutionOperation, SetBorder<T> borderOperation) {
		int requiredBorderWidth = kernel.getWidth() / 2;
		int requiredBorderHeight = kernel.getHeight() / 2;

		if (requiredBorderWidth > sourceImage.getBorderWidth() || requiredBorderHeight > sourceImage.getBorderHeight()) {
			sourceImage = sourceImage.clone(requiredBorderWidth, requiredBorderHeight);
		}
		U resultImage = sourceImage.createCompatibleArray(sourceImage.getWidth(), sourceImage.getHeight(), 0, 0);
		if (PxSystem.initialized()) {

			final PxSystem px = PxSystem.get();

			// run parallel
			try {
				if (sourceImage.getState() != Array2d.LOCAL_PARTIAL) {
					px.scatter(sourceImage);
				}
				if (kernel.getState() != Array2d.LOCAL_FULL) {
					px.broadcast(kernel);
				}

				PatSetBorder.dispatch(sourceImage, requiredBorderWidth, requiredBorderHeight,
						borderOperation);
				convolutionOperation.init(sourceImage, kernel, true);
				convolutionOperation.doIt(resultImage.getData(), sourceImage
						.getData(), kernel.getData());
			} catch (Exception e) {
				System.err.println("Failed to perform operation!");
				e.printStackTrace(System.err);
			}
		} else { // run sequential
			PatSetBorder.dispatch(sourceImage, requiredBorderWidth, requiredBorderHeight,
					borderOperation);
			convolutionOperation.init(sourceImage, kernel, false);
			convolutionOperation.doIt(resultImage.getData(), sourceImage
					.getData(), kernel.getData());
		}
		return resultImage;
	}
}
