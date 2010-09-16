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
import jorus.operations.bpoval.BpoVal;
import jorus.parallel.PxSystem;

public class PatBpoVal {
	public static <T, U extends Array2d<T, U>> U dispatch(Array2d<T, U> s1,
			boolean inplace, BpoVal<T> bpoVal) {
		U dst;

		if (PxSystem.initialized()) { // run parallel

			final PxSystem px = PxSystem.get();

			if (s1.getState() != Array2d.NONE) {

				// if (PxSystem.myCPU() == 0) System.out.println("UPO
				// SCATTER 1...");
				try {
					s1.changeStateTo(Array2d.LOCAL_PARTIAL);
					// px.scatter(s1);
				} catch (Exception e) {
					System.err.println("Failed to perform operation!");
					e.printStackTrace(System.err);
				}
			} else {
				// Added -- J
				//
				// A hack that assumes dst is a target data structure
				// which we do not need to
				// scatter. We only initialize the local partitions.

				final int pHeight = px
						.getPartHeight(s1.getHeight(), px.myCPU());

				final int length = (s1.getWidth() + s1.getBorderWidth() * 2)
						* (pHeight + s1.getBorderHeight() * 2) * s1.getExtent();

				s1.setPartialData(s1.getWidth(), pHeight, s1
						.createDataArray(length), Array2d.LOCAL_PARTIAL);
			}

			if (inplace) {
				dst = (U) s1;
			} else {
				dst = s1.clone();
			}

			bpoVal.init(s1, true);
			bpoVal.doIt(dst.getData());

		} else { // run sequential
			if (s1.getState() == Array2d.NONE) {
				final int length = (s1.getWidth() + s1.getBorderWidth() * 2)
						* (s1.getHeight() + s1.getBorderHeight() * 2)
						* s1.getExtent();
				s1.setData(s1.getWidth(), s1.getHeight(), s1
						.createDataArray(length), Array2d.GLOBAL_VALID);
			}
			if (inplace) {
				dst = (U) s1;
			} else {
				dst = s1.clone();
			}

			bpoVal.init(s1, false);
			bpoVal.doIt(dst.getData());

		}

		return dst;
	}
}
