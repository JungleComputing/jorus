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

public class PatGeneralizedConvolution1d {
	public static <T> Array2d<T> dispatch(Array2d<T> sourceImage,
			Array2d<T> kernel,
			GeneralizedConvolution1d<T> convolutionOperation, int dimension,
			SetBorder<T> borderOperation) {
		int requiredBorderSize = kernel.getWidth() / 2;

		if (dimension == 0 && requiredBorderSize > sourceImage.getBorderWidth()) {
			sourceImage = sourceImage.clone(requiredBorderSize, 0);
		} else if (dimension == 1
				&& requiredBorderSize > sourceImage.getBorderHeight()) {
			sourceImage = sourceImage.clone(0, requiredBorderSize);
		}
		Array2d<T> resultImage = null;
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

				PatSetBorder.dispatch(sourceImage, requiredBorderSize, 0,
						borderOperation);
				resultImage = sourceImage.clone();
				convolutionOperation.init(sourceImage, kernel, dimension, true);
				convolutionOperation.doIt(resultImage.getData(), sourceImage
						.getData(), kernel.getData());
			} catch (Exception e) {
				System.err.println("Failed to perform operation!");
				e.printStackTrace(System.err);
			}
		} else { // run sequential
			PatSetBorder.dispatch(sourceImage, requiredBorderSize, 0,
					borderOperation);
			resultImage = sourceImage.clone();
			convolutionOperation.init(sourceImage, kernel, dimension, true);
			convolutionOperation.doIt(resultImage.getData(), sourceImage
					.getData(), kernel.getData());
		}
		return resultImage;
	}
}
