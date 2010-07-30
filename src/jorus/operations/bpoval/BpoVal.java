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
	protected int rowWidth = 0;

	public void init(Array2d<T,?> s1, boolean parallel) {
		int w1 = parallel ? s1.getPartialWidth() : s1.getWidth();
		int extent = s1.getExtent();
		int borderWidth = s1.getBorderWidth();

		width = w1 * extent;
		height = parallel ? s1.getPartialHeight() : s1.getHeight();
		stride = 2 * borderWidth * extent;
		rowWidth = width + stride;
		offset = rowWidth * s1.getBorderHeight() + (borderWidth * extent);
		
	}

	public abstract void doRow(T dst, int row);
	
	public final void doIt(T dst) {
//		final int index = offset + j * rowWidth;
		for (int j = 0; j < height; j++) {
			doRow(dst, offset + j * rowWidth);
		}
	}
}
