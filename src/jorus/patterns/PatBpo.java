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
import jorus.operations.bpo.Bpo;
import jorus.parallel.PxSystem;

public class PatBpo {

	public static <T> Array2d<T> dispatch(Array2d<T> s1, Array2d<T> s2,
			boolean inplace, Bpo<T> bpo) {
		Array2d<T> dst = s1;

		if (PxSystem.initialized()) { // run parallel

			final PxSystem px = PxSystem.get();

			try {

				if (s1.getState() != Array2d.LOCAL_PARTIAL) {
					px.scatter(s1);
				}

				if (s2.getState() != Array2d.LOCAL_PARTIAL) {
					px.scatter(s2);
				}

				if (!inplace) {
					dst = s1.clone();
				}

				bpo.init(s1, s2, true);
				bpo.doIt(dst.getData(), s2.getData());
			} catch (Exception e) {
				System.err.println("Failed to perform operation!");
				e.printStackTrace(System.err);
			}

		} else {
			if (s1.getState() == Array2d.NONE) {
				// Added -- J
				//
				// A hack that assumes dst is a target data structure which we
				// do not need to
				// scatter. We only initialize the local partitions.

				final int size = (s1.getWidth() + s1.getBorderWidth() * 2)
						* (s1.getHeight() + s1.getBorderHeight() * 2)
						* s1.getExtent();

				s1.setData(s1.getWidth(), s1.getHeight(), s1.createDataArray(size), Array2d.GLOBAL_VALID);
			}

			if (!inplace)
				dst = s1.clone();

			// run sequential
			bpo.init(s1, s2, false);
			bpo.doIt(dst.getData(), s2.getData());
		}
		return dst;
	}
}
