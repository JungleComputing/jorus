/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.bpoval;

import jorus.array.Array2d;

public abstract class BpoVal<T> {
	protected int width = 0;
	protected int height = 0;
	protected int offset = 0;
	protected int stride = 0;

	public void init(Array2d<T> s1, boolean parallel) {
		int w1 = parallel ? s1.getPartialWidth() : s1.getWidth();
		int e1 = s1.getExtent();
		int bw1 = s1.getBorderWidth();

		width = w1 * e1;
		height = parallel ? s1.getPartialHeight() : s1.getHeight();
		offset = ((w1 + 2 * bw1) * s1.getBorderHeight() + bw1) * e1;
		stride = bw1 * e1 * 2;
	}

	public abstract void doIt(T dst);
}
