/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.operations.bpo;


import jorus.array.Array2d;


public abstract class BpoToHist<T>
{
	protected int	w       = 0;
	protected int	h       = 0;
	protected int	off1    = 0;
	protected int	off2    = 0;
	protected int	stride1 = 0;
	protected int	stride2 = 0;
	

	public void init(Array2d<T> s1, Array2d<T> s2, boolean parallel)
	{
		int w1  = parallel ? s1.getPartialWidth() : s1.getWidth();
		int e1  = s1.getExtent();
		int bw1 = s1.getBorderWidth();
		int bw2 = s2.getBorderWidth();

		w       = w1 * e1;
		h       = parallel ? s1.getPartialHeight() : s1.getHeight();
		off1    = ((w1 + 2*bw1) * s1.getBorderHeight() + bw1) * e1;
		off2    = ((w1 + 2*bw2) * s2.getBorderHeight() + bw2) * e1;
		stride1 = bw1 * e1*2;
		stride2 = bw2 * e1*2;
	}


	public abstract void doIt(double[] dst, T s1, T s2,
							  int nBins, double minVal, double maxVal);
}
