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
import jorus.operations.bpo.BpoToHist;
import jorus.parallel.PxSystem;
import jorus.parallel.ReduceOp;

public class PatBpoToHist {
	public static <T,U extends Array2d<T,U>> double[] dispatch(U s1, U s2,
			int nBins, double minVal, double maxVal, BpoToHist<T> bpo) {
		double[] dst = new double[nBins];

		if (PxSystem.initialized()) {

			final PxSystem px = PxSystem.get();
			final boolean root = px.isRoot();

			try {
				s1.changeStateTo(Array2d.LOCAL_PARTIAL);
//				if (s1.getState() != Array2d.LOCAL_PARTIAL) {
//					if (root)
//						System.out.println("BPO2HIST SCATTER 1...");
//					px.scatter(s1);
//				}
				s2.changeStateTo(Array2d.LOCAL_PARTIAL);
//				if (s2.getState() != Array2d.LOCAL_PARTIAL) {
//					if (root)
//						System.out.println("BPO2HIST SCATTER 2...");
//					px.scatter(s2);
//				}

				bpo.init(s1, s2, true);

				bpo.doIt(dst, s1.getData(), s2.getData(), nBins, minVal,
								maxVal);

				// if (PxSystem.myCPU() == 0)
				// System.out.println("BPO2HIST ALLREDUCE..");
				px.reduceToAll(dst, 1, ReduceOp.SUM);

			} catch (Exception e) {
				System.err.println("Failed to perform operation!");
				e.printStackTrace(System.err);
			}

		} else { // run sequential
			bpo.init(s1, s2, false);
			bpo.doIt(dst, s1.getData(), s2.getData(), nBins, minVal, maxVal);
		}
		return dst;
	}
}
