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

	public static <T,U extends Array2d<T,U>> U dispatch(Array2d<T,U> destination, Array2d<T,U> source,
			Geometric2d<T> geometric2d) {

		if (PxSystem.initialized()) { // run parallel

			final PxSystem px = PxSystem.get();

			try {
				source.changeStateTo(Array2d.LOCAL_FULL);
				destination.changeStateTo(Array2d.LOCAL_PARTIAL);

				if (destination.getState() != Array2d.NONE) {

					px.scatter(destination);

				} else {
					// Added -- J
					//
					// A hack that assumes dst is a target data structure
					// which we do not need to
					// scatter. We only initialize the local partitions.

					final int pHeight = px.getPartHeight(source.getHeight(),
							px.myCPU());

					final int length = (source.getWidth() + source
							.getBorderWidth() * 2)
							* (pHeight + source.getBorderHeight() * 2)
							* source.getExtent();

					source.setPartialData(source.getWidth(), pHeight,
							source.createDataArray(length),
							Array2d.LOCAL_PARTIAL);
				}

				geometric2d.init(destination, source, true);
				geometric2d.doIt(destination.getData(), source.getData());
			} catch (Exception e) {
				System.err.println("Failed to perform operation!");
				e.printStackTrace(System.err);
			}

		} else { // run sequential
			if (source.getState() == Array2d.NONE) {
				final int length = (source.getWidth() + source.getBorderWidth() * 2)
						* (source.getHeight() + source.getBorderHeight() * 2)
						* source.getExtent();

				source.setData(source.getWidth(), source.getHeight(),
						source.createDataArray(length), Array2d.GLOBAL_VALID);
			}

			geometric2d.init(destination, source, false);
			geometric2d.doIt(destination.getData(), source.getData());
		}
		return (U) destination;
	}

	
}
