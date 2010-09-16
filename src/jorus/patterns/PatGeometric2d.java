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
import jorus.operations.geometric.Geometric2d;
import jorus.parallel.PxSystem;

public class PatGeometric2d {

	public static <T, U extends Array2d<T, U>> U dispatch(Array2d<T, U> source,
			Geometric2d<T> geometric2d, int newWidth, int newHeight) {

		U destination;
		if (PxSystem.initialized()) { // run parallel

			final PxSystem px = PxSystem.get();
			try {
				source.changeStateTo(Array2d.LOCAL_FULL);
				int partHeight = px.getPartHeight(newHeight, px.myCPU());
				destination = source.createCompatibleArray(newWidth, newHeight,
						0, 0);
				destination.setPartialData(newWidth, partHeight, destination.getData(),
						Array2d.LOCAL_PARTIAL);

				geometric2d.init(destination, source, true);
				geometric2d.doIt(destination.getData(), source.getData());
			} catch (Exception e) {
				System.err.println("Failed to perform operation!");
				e.printStackTrace(System.err);
				return null;
			}
		} else { // run sequential
			if (source.getState() == Array2d.NONE) {
				// FIXME huh? source == NONE is not valid
				final int length = (source.getWidth() + source.getBorderWidth() * 2)
						* (source.getHeight() + source.getBorderHeight() * 2)
						* source.getExtent();

				source.setData(source.getWidth(), source.getHeight(), source
						.createDataArray(length), Array2d.GLOBAL_VALID);
			}
			destination = source.createCompatibleArray(newWidth, newHeight, 0,
					0);
			geometric2d.init(destination, source, false);
			geometric2d.doIt(destination.getData(), source.getData());
		}
		return destination;
	}

}
