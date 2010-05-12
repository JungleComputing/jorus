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

				if (s1.getLocalState() != Array2d.LOCAL_PARTIAL) {

					// if (s1.getGlobalState() != GlobalState.NONE) {

					// if (px.isRoot())
					// System.out.println("BPO SCATTER 1...");
					px.scatter(s1);

					// } else {
					// Added -- J
					//
					// A hack that assumes dst is a target data structure
					// which we do not need to
					// scatter. We only initialize the local partitions.

					// final int pHeight = px.getPartHeight(s1.getHeight(), px
					// .myCPU());

					// final int size = (s1.getWidth() + s1.getBorderWidth() *
					// 2)
					// * (pHeight + s1.getBorderHeight() * 2)
					// * s1.getExtent();
					//
					// s1.setPartialData(s1.getWidth(), pHeight, s1
					// .createDataArray(size), PartialState.PARTIAL);
					// }
				}

				if (s2.getLocalState() != Array2d.LOCAL_PARTIAL) {
					// if (px.isRoot())
					// System.out.println("BPO SCATTER 2...");
					px.scatter(s2);
				}

				if (!inplace) {
					dst = s1.clone();
				}

				bpo.init(s1, s2, true);
				bpo.doIt(dst.getPartialDataReadWrite(), s2
						.getPartialDataReadOnly());

//				dst.setGlobalState(GlobalState.INVALID);
				dst.setGlobalState(Array2d.GLOBAL_INVALID);

				// if (PxSystem.myCPU() == 0)
				// System.out.println("BPO GATHER...");
				// PxSystem.gatherOFT(dst);

			} catch (Exception e) {
				System.err.println("Failed to perform operation!");
				e.printStackTrace(System.err);
			}

		} else {
//			if (s1.getGlobalState() == GlobalState.NONE) {
			if (s1.getGlobalState() == Array2d.GLOBAL_NONE) {
				// Added -- J
				//
				// A hack that assumes dst is a target data structure which we
				// do not need to
				// scatter. We only initialize the local partitions.

				final int size = (s1.getWidth() + s1.getBorderWidth() * 2)
						* (s1.getHeight() + s1.getBorderHeight() * 2)
						* s1.getExtent();

//				s1.setData(s1.getWidth(), s1.getHeight(), s1
//						.createDataArray(size), GlobalState.VALID);
				s1.setData(s1.getWidth(), s1.getHeight(), s1
						.createDataArray(size), Array2d.GLOBAL_VALID);
			}

			if (!inplace)
				dst = s1.clone();

			// run sequential
			bpo.init(s1, s2, false);
			bpo.doIt(dst.getDataReadWrite(), s2.getDataReadOnly());
		}

		return dst;
	}
}
