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

public abstract class Upo<T> {

	private final CxArray2d<T> s1;
	private final boolean inplace;

	@SuppressWarnings("unused")
	private Upo() throws Exception { // prevent the use of the default
		// constructor
		throw new Exception("default constructor not allowed");
	}

	public Upo(final CxArray2d<T> s1, final boolean inplace) {
		this.s1 = s1;
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
						System.out.println("UPO SCATTER 1...");
					PxSystem.scatterOFT(s1);
				}
				if (dst.getLocalState() != CxArray2d.VALID
						|| dst.getDistributionType() != CxArray2d.PARTIAL) {
					if (PxSystem.myCPU() == 0)
						System.out.println("UPO SCATTER 2...");
					PxSystem.scatterOFT(dst);
				}

				init(s1, true);
				doIt(dst.getPartialData());
				dst.setGlobalState(CxArray2d.INVALID);

				// if (PxSystem.myCPU() == 0)
				// System.out.println("UPO GATHER...");
				// PxSystem.gatherOFT(dst);

			} catch (Exception e) {
				//
			}

		} else { // run sequential
			init(s1, false);
			doIt(dst.getData());
		}

		return dst;
	}
	
	protected abstract void init(CxArray2d<T> s1, boolean parallel);
	protected abstract void doIt(T dst);
}
