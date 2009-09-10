/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.patterns;

import jorus.weibull.FitWeibull;
import jorus.parallel.PxSystem;
import jorus.operations.red.CxRedOpAddDoubleArray;

public abstract class WeibullFitTask {

	private final int iMax;
	private final int jMax;

	@SuppressWarnings("unused")
	private WeibullFitTask() throws Exception { // prevent the use of the default
		// constructor
		throw new Exception("default constructor not allowed");
	}

	public WeibullFitTask(int iMax, int jMax) {
		this.iMax = iMax;
		this.jMax = jMax;
	}

	public void dispatch() {
		if (PxSystem.initialized()) { // run parallel
			try {
				int taskNr = 0;
				for (int j = 0; j < jMax; j++) {
					for (int i = 0; i < iMax; i++) {
						if (taskNr % PxSystem.nrCPUs() == PxSystem.myCPU()) {
							doIt(i, j);
						}
					}
				}
				for (int j = 0; j < jMax; j++) {
					PxSystem.reduceArrayToRootOFT(getBetas(j),
							new CxRedOpAddDoubleArray());
					PxSystem.reduceArrayToRootOFT(getGammas(j),
							new CxRedOpAddDoubleArray());
				}
			} catch (Exception e) {
				//
			}
		} else { // run sequential
			for (int j = 0; j < jMax; j++) {
				for (int i = 0; i < iMax; i++) {
					doIt(i, j);
				}
			}
		}
	}

	protected abstract void init(FitWeibull fw, double[][][] histos,
			double[][] betas, double[][] gammas, int bins);

	protected abstract void doIt(int i, int j);

	protected abstract double[] getBetas(int idx);

	protected abstract double[] getGammas(int idx);

}
