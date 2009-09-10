/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.operations;


import jorus.array.CxArray2d;
import jorus.patterns.Upo;


public abstract class CxUpo<T> extends Upo<T>
{
	protected int	w      = 0;
	protected int	h      = 0;
	protected int	off    = 0;
	protected int	stride = 0;

	public CxUpo(CxArray2d<T> s1, boolean inplace) {
		super(s1, inplace);
	}

	@Override
	public void init(CxArray2d<T> s1, boolean parallel)
	{
		int w1  = parallel ? s1.getPartialWidth() : s1.getWidth();
		int e1  = s1.getExtent();
		int bw1 = s1.getBorderWidth();

		w      = w1 * e1;
		h      = parallel ? s1.getPartialHeight() : s1.getHeight();
		off    = ((w1 + 2*bw1) * s1.getBorderHeight() + bw1) * e1;
		stride = bw1 * e1*2;
	}
}
