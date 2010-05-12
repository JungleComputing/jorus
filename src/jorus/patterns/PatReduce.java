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

	public static <T> Array2d<T> dispatch(Array2d<T> destination,
			Array2d<T> source, Reduce<T> reduceOperation) {

		if (PxSystem.initialized()) { // run parallel

			final PxSystem px = PxSystem.get();

			try {

				if (destination.getLocalState() != Array2d.LOCAL_FULL) {
					destination.setPartialData(destination.getWidth(),
							destination.getHeight(), destination
									.createDataArray(destination.getWidth()
											* destination.getHeight()
											* destination.getExtent()),
											Array2d.LOCAL_NOT_REDUCED);

				} else {
					destination.setLocalState(Array2d.LOCAL_NOT_REDUCED);
				}

				if (source.getLocalState() != Array2d.LOCAL_PARTIAL) {
					px.scatter(source);
				}

				reduceOperation.init(source, true);
				reduceOperation.doIt(destination.getPartialDataReadWrite(),
						source.getPartialDataReadOnly());

//				destination.setGlobalState(GlobalState.INVALID);
				destination.setGlobalState(Array2d.GLOBAL_INVALID);
				destination.setReduceOperation(reduceOperation.getOpcode());
				// localState already set
			} catch (Exception e) {
				System.err.println("Failed to perform operation!");
				e.printStackTrace(System.err);
			}

		} else {
			// if (destination.getGlobalState() == GlobalState.NONE) {
			if (destination.getGlobalState() == Array2d.GLOBAL_NONE) {
				// Added -- J
				//
				// A hack that assumes dst is a target data structure which we
				// do not need to
				// scatter. We only initialize the local partitions.

//				destination.setData(1, 1, destination
//						.createDataArray(destination.getExtent()),
//						GlobalState.VALID);
				destination.setData(1, 1, destination
						.createDataArray(destination.getExtent()),
						Array2d.GLOBAL_VALID);
			}

			// run sequential
			reduceOperation.init(source, false);
			reduceOperation.doIt(destination.getDataReadWrite(), source
					.getDataReadOnly());
		}

		return destination;
	}
}
