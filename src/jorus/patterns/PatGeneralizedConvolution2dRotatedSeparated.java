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
	
	static Object cache;
	
	public static <T,U extends Array2d<T,U>> U dispatch(Array2d<T,U> sourceImage,
			Array2d<T,?> kernelU, Array2d<T,?> kernelV, double phiRad,
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
		U dst = null;

		if (borderWidth > sourceImage.getBorderWidth()
				|| borderHeight > sourceImage.getBorderHeight()) {
			dst = sourceImage.clone(borderWidth, borderHeight);
		} else if(inplace) {
			dst = (U) sourceImage;
		} else {
			dst = sourceImage.clone();
		}

		if (PxSystem.initialized()) { // run parallel
			final PxSystem px = PxSystem.get();
			final boolean root = px.isRoot();

			try {
				if (dst.getState() != Array2d.LOCAL_PARTIAL) {
					if (root) {
						if(logger.isDebugEnabled()) {
							logger.debug("GENCONV SCATTER 1...");
						}
					}
					px.scatter(dst);
				}
				if (kernelU.getState() != Array2d.LOCAL_FULL) {
					px.broadcast(kernelU);
				}
				if (kernelV.getState() != Array2d.LOCAL_FULL) {
					px.broadcast(kernelV);
				}
				PatSetBorder.dispatch(dst, borderWidthU, borderHeightU, borderOperation);
				U tmp = dst.shallowClone();
				convolutionOperation.init(dst, kernelU, phiRad, true);
				convolutionOperation.doIt(tmp.getData(), dst.getData(), kernelU
						.getData());

				PatSetBorder.dispatch(tmp, borderWidthV, borderHeightV, borderOperation);
				convolutionOperation.init(tmp, kernelV, phiRad + 0.5 * Math.PI, true); //FIXME + or - 0.5 * PI ????
				convolutionOperation.doIt(dst.getData(), tmp.getData(), kernelV
						.getData());
			} catch (Exception e) {
				//
			}

		} else { // run sequential			
			PatSetBorder.dispatch(dst, borderWidthU, borderHeightU, borderOperation);
			U tmp;
			if(cache != null && cache.getClass().equals(dst.getClass()) &&
					dst.equalSignature((U)cache) && 
					dst.getBorderHeight() == ((U)cache).getBorderHeight() && 
					dst.getBorderWidth() == ((U)cache).getBorderWidth()) {
				tmp = (U)cache;
//				System.err.println("hit");
			} else {
				tmp = dst.shallowClone();
				cache = tmp;
//				System.err.println("miss");
			}
			
			convolutionOperation.init(dst, kernelU, phiRad, false);
			convolutionOperation.doIt(tmp.getData(), dst.getData(), kernelU
					.getData());

			PatSetBorder.dispatch(tmp, borderWidthV, borderHeightV, borderOperation);
			convolutionOperation.init(tmp, kernelV, phiRad + 0.5 * Math.PI, false); //FIXME + or - 0.5 * PI ????
			convolutionOperation.doIt(dst.getData(), tmp.getData(), kernelV
					.getData());
		}

		
		return dst;
	}
}
