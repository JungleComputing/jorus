/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *  Timo van Kessel (tpkessel@cs.vu.nl)
 *
 */

package jorus.operations;

import jorus.array.CxArray2d;

public abstract class CxConvolution2d<T> extends jorus.patterns.GenConv2d<T> {
	
	public CxConvolution2d(CxArray2d<T> source, CxArray2d<T> kernel,
			CxSetBorder<T> setBorderOperation) {
		super(source, kernel, setBorderOperation);
	}

	// Beware: my variables, not Frank's
	protected int width = 0;
	protected int height = 0;
	protected int extent = 0;
	protected int totalWidth = 0;
	protected int kernelWidth = 0;
	protected int kernelHeight = 0;
	protected int borderWidth = 0;
	protected int borderHeight = 0;

	protected int indexOf(int x, int y) {
		return (totalWidth * (borderHeight + y) + borderWidth + x) * extent;
	}

	@Override
	protected void init(CxArray2d<T> image, CxArray2d<T> kernel, boolean parallel) {
		width = parallel ? image.getPartialWidth() : image.getWidth();
		height = parallel ? image.getPartialHeight() : image.getHeight();
		extent = image.getExtent();
		borderWidth = image.getBorderWidth();
		borderHeight = image.getBorderWidth();
		totalWidth = width + 2 * borderWidth;

		kernelWidth = kernel.getWidth();
		kernelHeight = kernel.getHeight();
	}

}
