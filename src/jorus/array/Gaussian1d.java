/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

public class Gaussian1d {

	/*
	 * Replaced by an in-place filter generation below -- J
	 * 
	 * public static CxArray2dScalarDouble create(double sigma, int deri, double
	 * acc, int maxfsize, int fsize) { if (fsize < 1) { fsize =
	 * filterWidth(sigma, deri, acc, maxfsize); } double[] fData =
	 * makeFilter(sigma, deri, acc, fsize, maxfsize); return new
	 * CxArray2dScalarDouble(fsize, 1, 0, 0, fData); }
	 * 
	 * 
	 * private static double[] makeFilter(double sigma, int deri, double acc,
	 * int fsize, int maxfsize) { double[] filter = new double[fsize]; fsize =
	 * fsize/2; int centerIdx = fsize;
	 * 
	 * 
	 * // Calculate filter
	 * 
	 * double sum = CxGauss(0.0, sigma); filter[centerIdx] = sum;
	 * 
	 * for (int i=1; i<=fsize; i++) { double val = CxGauss(i, sigma); sum +=
	 * val+val; filter[centerIdx+i] = val; filter[centerIdx-i] = val; }
	 * 
	 * 
	 * // Normalize to sum=1.0
	 * 
	 * for (int i=0; i<filter.length; i++) { filter[i] /= sum; }
	 * 
	 * 
	 * // Replace by Hermite polinomial of order deri
	 * 
	 * if (deri > 0) { for (int i=0; i<filter.length; i++) { filter[i] =
	 * CxHermite(i-fsize, filter[i], sigma, deri); } } return filter; }
	 */

	public static Array2dScalarDouble create(double sigma, int deri,
			double acc, int fsize, int maxfsize) {

		if (fsize < 1) {
			fsize = filterWidth(sigma, deri, acc, maxfsize);
		} else if (fsize % 2 == 0) {
			fsize -= 1; // length of a filter must be odd
		}

		Array2dScalarDouble tmp = new Array2dScalarDouble(fsize, 1, 0, 0, true);

		makeFilter(tmp.getDataWriteOnly(), sigma, deri, fsize);
		
//		double[] filter = tmp.getDataWriteOnly();
//		double pos = 0;
//		double neg = 0;
//		for (int i = 0; i < filter.length; i++) {
//			if (filter[i] > 0) {
//				pos += filter[i];
//			} else {
//				neg += filter[i];
//			}
//		}

		return tmp;
	}

	private static void makeFilter(double[] filter, double sigma, int deri,
			int fsize) {

		fsize = fsize / 2;
		int centerIdx = fsize;

		// Calculate filter

		double sum = CxGauss(0.0, sigma);
		filter[centerIdx] = sum;

		for (int i = 1; i <= fsize; i++) {
			double val = CxGauss(i, sigma);
			sum += val + val;
			filter[centerIdx + i] = val;
			filter[centerIdx - i] = val;
		}

		// Normalize to sum=1.0
		for (int i = 0; i < filter.length; i++) {
			filter[i] /= sum;
		}

		// Replace by Hermite polinomial of order deri
		if (deri > 0) {
			for (int i = 0; i < filter.length; i++) {
				filter[i] = CxHermite(i - fsize, filter[i], sigma, deri);
			}

			// FIXME Timo: normalization added. Should we keep this?
			double pos = 0;
			double neg = 0;
			for (int i = 0; i < filter.length; i++) {
				if (filter[i] > 0) {
					pos += filter[i];
				} else {
					neg += filter[i];
				}
			}
			double normalizationFactor;
			if (deri % 2 == 0) { // even
				normalizationFactor = pos - neg;
			} else {// uneven
				normalizationFactor = pos;
			}
			for (int i = 0; i < filter.length; i++) {
				filter[i] /= normalizationFactor;
			}
		}
	}

	private static int filterWidth(double sigma, int deri, double acc,
			int maxlen) {

		// Unused ? -- J
		// double acc2 = 1.0 - (1.0 - acc)/2.0;

		int fsize = 2 * (int) (acc * sigma + 0.5) + 1;

		// Filter is always odd sized; so if maxlen is even subtract 1.
		if (maxlen % 2 == 0) {
			maxlen -= 1;
		}

		if (fsize > maxlen) {
			fsize = maxlen;
		}

		return fsize;
	}

	private static double CxGauss(double x, double sigma) {
		return Math.exp(-0.5 * x * x / (sigma * sigma));
	}

	private static double CxHermite(double x, double H0, double sigma, int order) {

		if (order == 0)
			return H0;

		double a = 1.0 / (sigma * sigma);
		double ax = a * x;
		double Hnmin1 = H0;
		double Hn = -ax * H0;

		for (int n = 2; n <= order; n++) {
			double Hnmin2 = Hnmin1;
			Hnmin1 = Hn;
			Hn = a * (1 - n) * Hnmin2 - ax * Hnmin1;
		}

		// FIXME Timo: remove this? I think it is wrong
		// if (order%2 == 1) {
		// return -Hn;
		// } else {
		return Hn;
		// }

	}
}
