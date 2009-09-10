/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.weibull;

public class CxWeibullFit extends jorus.patterns.WeibullFitTask {
	// A bit of a hack; need to generalize this (much) more!

	public CxWeibullFit(int max, int max2) {
		super(max, max2);
	}

	FitWeibull fitW;
	double[][][] histoArray;
	double[][] betaArray;
	double[][] gammaArray;
	int nrBins;

	@Override
	public void init(FitWeibull fw, double[][][] histos, double[][] betas,
			double[][] gammas, int bins) {
		fitW = fw;
		histoArray = histos;
		betaArray = betas;
		gammaArray = gammas;
		nrBins = bins;
	}

	@Override
	public void doIt(int i, int j) {
		if (j == 0) {
			fitW.doFit(histoArray[j][i], nrBins, 1.);
		} else {
			fitW.doFitMarginal(histoArray[j][i], nrBins, -1., 1.);
		}
		betaArray[j][i] = fitW.beta();
		gammaArray[j][i] = fitW.gamma();
	}

	@Override
	public double[] getBetas(int idx) {
		return betaArray[idx];
	}

	@Override
	public double[] getGammas(int idx) {
		return gammaArray[idx];
	}
}
