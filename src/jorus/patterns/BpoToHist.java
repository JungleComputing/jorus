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
import jorus.operations.red.CxRedOpAddDoubleArray;
import jorus.parallel.PxSystem;

public abstract class BpoToHist<T> {
	final CxArray2d<T> image1;
	final CxArray2d<T> image2;
	final int nBins;
	final double minValue;
	final double maxValue;
	
	@SuppressWarnings("unused")
	private BpoToHist() throws Exception { // prevent the use of the default
										// constructor
		throw new Exception("default constructor not allowed");
	}
	
	public BpoToHist(CxArray2d<T> image1, CxArray2d<T> image2, int nBins,
			double minValue, double maxValue) {
		this.image1 = image1;
		this.image2 = image2;
		this.nBins = nBins;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public double[] dispatch() {
		double[] destination = new double[nBins];
		for (int i = 0; i < nBins; i++) {
			destination[i] = 0.;
		}

		if (PxSystem.initialized()) { // run parallel
			try {

				if (image1.getLocalState() != CxArray2d.VALID
						|| image1.getDistributionType() != CxArray2d.PARTIAL) {
					if (PxSystem.myCPU() == 0)
						System.out.println("BPO2HIST SCATTER 1...");
					PxSystem.scatterOFT(image1);
				}
				if (image2.getLocalState() != CxArray2d.VALID
						|| image2.getDistributionType() != CxArray2d.PARTIAL) {
					if (PxSystem.myCPU() == 0)
						System.out.println("BPO2HIST SCATTER 2...");
					PxSystem.scatterOFT(image2);
				}

				init(image1, image2, true);
				doIt(destination, image1.getPartialData(), image2.getPartialData(), nBins,
						minValue, maxValue);

				// if (PxSystem.myCPU() == 0)
				// System.out.println("BPO2HIST ALLREDUCE..");
				PxSystem.reduceArrayToAllOFT(destination, new CxRedOpAddDoubleArray());

			} catch (Exception e) {
				//
			}

		} else { // run sequential
			init(image1, image2, false);
			doIt(destination, image1.getData(), image2.getData(), nBins, minValue, maxValue);
		}
		return destination;
	}

	protected abstract void init(CxArray2d<T> s1, CxArray2d<T> s2, boolean b);
	
	protected abstract void doIt(double[] dst, T data, T data2, int bins, double minVal2,
			double maxVal2);
}
