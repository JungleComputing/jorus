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

public abstract class CxGenConvRot2dSep<T> {
	protected int imWidth = 0;
	protected int imHeight = 0;
//	protected int extent = 0;
	protected int offset = 0;
	protected int stride = 0;

	protected int borWidth = 0;
	protected int borHeight = 0;
	protected int halfKerSize = 0;
	protected double cosTheta = 0;
	protected double sinTheta = 0;
	protected double theta = 0;
	
	protected int srcWidth = 0;
	protected int srcHeight = 0;

	public void init(CxArray2d src, CxArray2d ker, double theta, boolean parallel) {
		// FIXME base parameters on actual implementation
		imWidth = parallel ? src.getPartialWidth() : src.getWidth();
		imHeight = parallel ? src.getPartialHeight() : src.getHeight();
//		extent = src.getExtent();
		borWidth = src.getBorderWidth();
		borHeight = src.getBorderHeight();
		srcWidth = imWidth + 2 * borWidth;
		srcHeight = imHeight + 2 * borHeight;
		offset = ((imWidth + 2 * src.getBorderWidth()) * src.getBorderHeight() + src.getBorderWidth());
		stride = 2 * src.getBorderWidth();

		halfKerSize = (ker.getWidth() -1) / 2;
		this.theta = theta;
		cosTheta = Math.cos(theta);
		sinTheta = Math.sin(theta);		
	}

	public abstract void doIt(T dst, T src, T ker1);
}
