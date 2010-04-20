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
			final int rank = px.myCPU();

			// run parallel
			try {
				if (sourceImage.getLocalState() != Array2d.VALID
						|| sourceImage.getDistType() != Array2d.PARTIAL) {
					if (rank == 0)
						System.out.println("GENCONV SCATTER 1...");
					px.scatter(sourceImage);
				}

				PatSetBorder.dispatch(sourceImage, requiredBorderSize, 0,
						borderOperation);
				resultImage = sourceImage.clone();
				convolutionOperation.init(sourceImage, kernel, dimension, true);
				convolutionOperation.doIt(
						resultImage.getPartialDataWriteOnly(), sourceImage
								.getPartialDataReadOnly(), kernel
								.getDataReadOnly());

				resultImage.setGlobalState(Array2d.INVALID);
			} catch (Exception e) {
				System.err.println("Failed to perform operation!");
				e.printStackTrace(System.err);
			}
		} else { // run sequential
			PatSetBorder.dispatch(sourceImage, requiredBorderSize, 0,
					borderOperation);
			resultImage = sourceImage.clone();
			convolutionOperation.init(sourceImage, kernel, dimension, true);
			convolutionOperation.doIt(resultImage.getPartialDataWriteOnly(),
					sourceImage.getPartialDataReadOnly(), kernel
							.getDataReadOnly());
		}
		return resultImage;
	}
}
