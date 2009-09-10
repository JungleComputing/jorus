/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.patterns;

import jorus.array.CxArray2d;
import jorus.parallel.PxSystem;

public abstract class Svo<T> {

	final private CxArray2d<T> s1;
	final private int x;
	final private int y;
	final private boolean inplace;

	@SuppressWarnings("unused")
	private Svo() throws Exception { // prevent the use of the default
		// constructor
		throw new Exception("default constructor not allowed");
	}

	public Svo(final CxArray2d<T> s1, final int x, final int y,
			final boolean inplace) {
		this.s1 = s1;
		this.x = x;
		this.y = y;
		this.inplace = inplace;
	}

	public CxArray2d<T> dispatch() {
		CxArray2d<T> dst = s1;
		if (!inplace)
			dst = s1.clone();

		if (PxSystem.initialized()) { // run parallel
			try {

				if (s1.getLocalState() != CxArray2d.VALID
						|| s1.getDistributionType() != CxArray2d.PARTIAL) {
					if (PxSystem.myCPU() == 0)
						System.out.println("SVO SCATTER 1...");
					PxSystem.scatterOFT(s1);
				}
				if (dst.getLocalState() != CxArray2d.VALID
						|| dst.getDistributionType() != CxArray2d.PARTIAL) {
					if (PxSystem.myCPU() == 0)
						System.out.println("SVO SCATTER 2...");
					PxSystem.scatterOFT(dst);
				}

				init(s1, true);
				int start = PxSystem.getLclStartY(s1.getHeight(), PxSystem
						.myCPU());
				if ((y >= start) && (y < start + s1.getPartialHeight())) {
					doIt(dst.getPartialData(), x, y - start);
				}
				dst.setGlobalState(CxArray2d.INVALID);

				// if (PxSystem.myCPU() == 0)
				// System.out.println("SVO GATHER...");
				// PxSystem.gatherOFT(dst);

			} catch (Exception e) {
				//
			}

		} else { // run sequential
			init(s1, false);
			doIt(dst.getData(), x, y);
		}

		return dst;
	}

	protected abstract void init(CxArray2d<T> s1, boolean parallel);

	protected abstract void doIt(T dst, int x, int y);
}
