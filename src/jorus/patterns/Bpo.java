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

public abstract class Bpo<T> {

	private final CxArray2d<T> image1;
	private final CxArray2d<T> image2;
	private final boolean inplace;

	@SuppressWarnings("unused")
	private Bpo() throws Exception { // prevent the use of the default
		// constructor
		throw new Exception("default constructor not allowed");
	}

	public Bpo(final CxArray2d<T> image1, final CxArray2d<T> image2,
			final boolean inplace) {
		this.image1 = image1;
		this.image2 = image2;
		this.inplace = inplace;
	}

	public CxArray2d<T> dispatch() {
		CxArray2d<T> dst = inplace ? image1.clone() : image1;

		if (PxSystem.initialized()) { // run parallel
			try {

				if (image1.getLocalState() != CxArray2d.VALID
						|| image1.getDistributionType() != CxArray2d.PARTIAL) {
					if (PxSystem.myCPU() == 0)
						System.out.println("BPO SCATTER 1...");
					PxSystem.scatterOFT(image1);
				}
				if (image2.getLocalState() != CxArray2d.VALID
						|| image2.getDistributionType() != CxArray2d.PARTIAL) {
					if (PxSystem.myCPU() == 0)
						System.out.println("BPO SCATTER 2...");
					PxSystem.scatterOFT(image2);
				}
				if (dst.getLocalState() != CxArray2d.VALID
						|| dst.getDistributionType() != CxArray2d.PARTIAL) {
					if (PxSystem.myCPU() == 0)
						System.out.println("BPO SCATTER 3...");
					PxSystem.scatterOFT(dst);
				}

				init(image1, image2, true);
				doIt(dst.getPartialData(), image2.getPartialData());
				dst.setGlobalState(CxArray2d.INVALID);

				// if (PxSystem.myCPU() == 0)
				// System.out.println("BPO GATHER...");
				// PxSystem.gatherOFT(dst);

			} catch (Exception e) {
				//
			}

		} else { // run sequential
			init(image1, image2, false);
			doIt(dst.getData(), image2.getData());
		}

		return dst;
	}

	protected abstract void init(CxArray2d<T> s1, CxArray2d<T> s2,
			boolean parallel);

	protected abstract void doIt(T dst, T src);
}
