/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpo;

public class BpoToHistDouble extends BpoToHist<double[]> {
	public void doIt(double[] dst, double[] s1, double[] s2, int nBins,
			double minVal, double maxVal) {
		final double range = maxVal - minVal;

		for (int j = 0; j < h; j++) {

			int s1Ptr = off1 + j * (w + stride1);
			int s2Ptr = off2 + j * (w + stride2);

			for (int i = 0; i < w; i++) {

				final int index = (int) (nBins * (s1[s1Ptr] - minVal) / range);

				if (index >= 0 && index < nBins) {
					dst[index] += s2[s2Ptr];
				}

				s1Ptr++;
				s2Ptr++;
			}
		}
	}
	/*
	 * private final void eep(double[] dst, double[] s1, double[] s2, int nBins,
	 * final double minVal, final double range, final int s1Ptr, final int
	 * s2Ptr) {
	 * 
	 * for (int i=0; i<w; i++) {
	 * 
	 * //final int index = (int) (nBins*(s1[s1Ptr+1]-minVal)/range);
	 * 
	 * final double tmp = (s1[s1Ptr+i]-minVal)/range;
	 * 
	 * final int index = (int)(nBins * tmp);
	 * 
	 * if (index >= 0 && index < nBins) { dst[index] += s2[s2Ptr+i]; } } }
	 */
	/*
	 * int width = CxArrayCW(src1); int height = CxArrayCH(src1); double range =
	 * maxVal - minVal; for (int y=0 ; y<height ; y++) { Src1StorT* s1Ptr =
	 * CxArrayCPB(src1, 0, y); Src2StorT* s2Ptr = CxArrayCPB(src2, 0, y); for
	 * (int x=0 ; x<width ; x++) { int index = nrBins * (CxPtrRead(s1Ptr,
	 * Src1ArithT()) - minVal) / range; if (index >= 0 && index < nrBins) {
	 * dst[index] += CxPtrRead(s2Ptr, Src2ArithT()); } s1Ptr +=
	 * Src1ArrayT::ElemSize(); s2Ptr += Src2ArrayT::ElemSize(); } }
	 */
}
