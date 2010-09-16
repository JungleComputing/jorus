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
import jorus.operations.reduce.Reduce;
import jorus.parallel.PxSystem;

public class PatReduce {

	public static <T,U extends Array2d<T,U>> U dispatch(Array2d<T,U> destination,
			Array2d<T,U> source, Reduce<T> reduceOperation) {
		// FIXME check data formats of destination structure. Those are not
		// consistent for sequential and parallel algorithm

		if (PxSystem.initialized()) { // run parallel

			final PxSystem px = PxSystem.get();

			try {		
				if (destination.getState() != Array2d.LOCAL_FULL) {
					destination.setPartialData(destination.getWidth(),
							destination.getHeight(), destination
									.createDataArray(destination.getWidth()
											* destination.getHeight()
											* destination.getExtent()),
							Array2d.LOCAL_NOT_REDUCED);

				} else {
					destination.setPartialData(destination.getWidth(),
							destination.getHeight(), destination.getData(),
							Array2d.LOCAL_NOT_REDUCED);
				}

				source.changeStateTo(Array2d.LOCAL_PARTIAL);
//				if (source.getState() != Array2d.LOCAL_PARTIAL) {
//					px.scatter(source);
//				}

				reduceOperation.init(source, true);
				reduceOperation.doIt(destination.getData(), source.getData());

				destination.setReduceOperation(reduceOperation.getOpcode());
				// localState already set
			} catch (Exception e) {
				System.err.println("Failed to perform operation!");
				e.printStackTrace(System.err);
			}

		} else {
			// if (destination.getGlobalState() == GlobalState.NONE) {
			if (destination.getState() == Array2d.NONE) {
				// Added -- J
				//
				// A hack that assumes dst is a target data structure which we
				// do not need to
				// scatter. We only initialize the local partitions.
				destination.setData(1, 1, destination
						.createDataArray(destination.getExtent()),
						Array2d.GLOBAL_VALID);
			}

			// run sequential
			reduceOperation.init(source, false);
			reduceOperation.doIt(destination.getData(), source.getData());
		}

		return (U) destination;
	}
}
