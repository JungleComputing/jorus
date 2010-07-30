/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.communication;

import jorus.array.Array2d;

public abstract class SetBorder<T> {
	protected int width;
	protected int height;
	protected int extent;
	protected int offset;
	protected int stride;
	protected int rowSize;
	protected boolean doParallel = false;

	public void init(Array2d<T,?> source, boolean parallel) {	
		doParallel = parallel;
		
		extent = source.getExtent();
		width = parallel ? source.getPartialWidth() : source.getWidth();
		height = parallel ? source.getPartialHeight() : source.getHeight();
		
		int borderWidth = source.getBorderWidth();
		int borderHeight = source.getBorderHeight();
		
		stride = borderWidth * extent * 2;
		rowSize = width * extent + stride;
		offset = rowSize * borderHeight + (borderWidth * extent);
		
	}

	public abstract void doIt(T dst, int numX, int numY);
}
