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
import jorus.operations.geometric.GeometricROI;
import jorus.parallel.PxSystem;

public class PatGeometricROI {

	public static <T,U extends Array2d<T,U>> U dispatch(Array2d<T,U> source, int newImWidth, int newImHeight, int beginX, int beginY,
			GeometricROI<T> geometricROI) {
		U destination = source.createCompatibleArray(newImWidth, newImHeight, 0, 0);
		
		if (PxSystem.initialized()) { // run parallel

			final PxSystem px = PxSystem.get();

			try {
				source.changeStateTo(Array2d.LOCAL_FULL);

				final int pHeight = px.getPartHeight(destination.getHeight(),
						px.myCPU());

				final int length = (destination.getWidth() + destination
						.getBorderWidth() * 2)
						* (pHeight + destination.getBorderHeight() * 2)
						* destination.getExtent();

				destination.setPartialData(destination.getWidth(), pHeight,
						destination.createDataArray(length),
						Array2d.LOCAL_PARTIAL);
				beginY -= px.getLclStartY(destination.getHeight(), px.myCPU());


				geometricROI.init(destination, source, true);
				geometricROI.doIt(destination.getData(), source.getData(), beginX, beginY);
			} catch (Exception e) {
				System.err.println("Failed to perform operation!");
				e.printStackTrace(System.err);
			}

		} else { // run sequential
			if (destination.getState() == Array2d.NONE) {
				final int length = destination.getWidth() * destination.getHeight() * destination.getExtent();

				destination.setData(destination.getWidth(), destination.getHeight(),
						destination.createDataArray(length), Array2d.GLOBAL_VALID);
			}

			geometricROI.init(destination, source, false);
			geometricROI.doIt(destination.getData(), source.getData(), beginX, beginY);
		}
		return destination;
	}

	
}
