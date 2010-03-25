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

import jorus.array.CxArray2d;
import jorus.operations.CxGenConvRot2dSep;
import jorus.operations.CxSetBorder;
import jorus.parallel.PxSystem;

//import array.CxArray2dScalarDouble;

public class CxPatGenConvRot2dSep {
	private static final Logger logger = LoggerFactory
			.getLogger(CxPatGenConvRot2dSep.class);
	
	public static CxArray2d dispatch(CxArray2d s1, CxArray2d ker1,
			CxArray2d ker2, double theta, CxGenConvRot2dSep gco, CxSetBorder sbo) {
		double theta2 = theta + Math.PI/2;
		
		int borWidth1  = ((int)(((ker1.getWidth() - 1) / 2) * Math.abs(Math.cos(theta)))) + 1;
		int borHeight1 = ((int)(((ker1.getWidth() - 1) / 2) * Math.abs(Math.sin(theta)))) + 1;
		int borWidth2  = ((int)(((ker2.getWidth() - 1) / 2) * Math.abs(Math.cos(theta2)))) + 1;
		int borHeight2 = ((int)(((ker2.getWidth() - 1) / 2) * Math.abs(Math.sin(theta2)))) + 1;
		
		int numX = borWidth1 > borWidth2 ? borWidth1: borWidth2;
		int numY = borHeight1 > borHeight2 ? borHeight1 : borHeight2; 
		CxArray2d dst = null;
	
		if (numX > s1.getBorderWidth() || numY > s1.getBorderHeight()) {
			dst = s1.clone(numX, numY);
		} else {
			dst = s1.clone();
		}
	
		if (PxSystem.initialized()) { // run parallel
			final PxSystem px = PxSystem.get();
            final int rank = px.myCPU();
            
			try {	
				if (dst.getLocalState() != CxArray2d.VALID
						|| dst.getDistType() != CxArray2d.PARTIAL) {
					if (rank == 0)
						logger.debug("GENCONV SCATTER 1...");
					px.scatter(dst);
				}
				CxArray2d tmp = dst.clone();
				CxPatSetBorder.dispatch(dst, borWidth1, borHeight1, sbo);
				
				gco.init(dst, ker1, theta, true);
				gco.doIt(tmp.getPartialDataWriteOnly(), dst.getPartialDataReadOnly(), ker1
						.getDataReadOnly());
				
				CxPatSetBorder.dispatch(tmp, borWidth2, borHeight2, sbo);
//				dst = dst.clone(0,0); //remove the borders //FIXME WRONG
				gco.init(tmp, ker2, theta2, true);
				gco.doIt(dst.getPartialDataWriteOnly(), tmp.getPartialDataReadOnly(), ker2
						.getDataReadOnly());
				dst.setGlobalState(CxArray2d.INVALID);
	
				// if (PxSystem.myCPU() == 0)
				// System.out.println("GENCONV GATHER...");
				// PxSystem.gatherOFT(dst);
	
			} catch (Exception e) {
				//
			}
	
		} else { // run sequential
			CxArray2d tmp = dst.clone();
			CxPatSetBorder.dispatch(dst, borWidth1, borHeight1, sbo);
			
			gco.init(dst, ker1, theta, false);
			gco.doIt(tmp.getDataWriteOnly(), dst.getDataReadOnly(), ker1
					.getDataReadOnly());
			CxPatSetBorder.dispatch(tmp, borWidth2, borHeight2, sbo);
//			dst = dst.clone(0,0); //remove the borders //FIXME WRONG!
			gco.init(tmp, ker2, theta2, false);
			gco.doIt(dst.getDataWriteOnly(), tmp.getDataReadOnly(), ker2
					.getDataReadOnly());
		}
	
		return dst;
	}
}
