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

public abstract class Bpo<T> {
	protected int width = 0;
	protected int height = 0;
	protected int offset1 = 0;
	protected int offset2 = 0;
	protected int stride1 = 0;
	protected int stride2 = 0;

	public void init(Array2d<T,?> s1, Array2d<T,?> s2, boolean parallel) {
		int w1 = parallel ? s1.getPartialWidth() : s1.getWidth();
		int e1 = s1.getExtent();
		int w2 = parallel ? s2.getPartialWidth() : s2.getWidth();
		int e2 = s2.getExtent();
		int bw1 = s1.getBorderWidth();
		int bw2 = s2.getBorderWidth();

		width = w1 * e1;
		height = parallel ? s1.getPartialHeight() : s1.getHeight();
		offset1 = ((w1 + 2 * bw1) * s1.getBorderHeight() + bw1) * e1;
		offset2 = ((w2 + 2 * bw2) * s2.getBorderHeight() + bw2) * e2;
		stride1 = bw1 * e1 * 2;
		stride2 = bw2 * e2 * 2;
	}

	public final void doIt(T dst, T src) {
		for (int j = 0; j < height; j++) {
			doRow(dst, src, j);
		}
	}
	
	
	public abstract void doRow(T dst, T src, int row);
}
