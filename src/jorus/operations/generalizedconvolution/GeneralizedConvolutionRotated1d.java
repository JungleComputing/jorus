/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.generalizedconvolution;

import jorus.array.Array2d;

public abstract class GeneralizedConvolutionRotated1d<T> {
	private enum Signature {
		VECTOR_VECTOR, VECTOR_SCALAR, SCALAR_SCALAR, INVALID;
	}

	protected Signature sig;
	
	protected int width = 0;
	protected int height = 0;
	protected int extent = 0;
	protected int offset = 0;
	protected int stride = 0;
	protected int rowSize = 0;
	protected int totalHeight = 0;
	protected int totalWidth = 0;
	protected int kernelWidth = 0; // kernel size for filtering
	protected int halfKerSize = 0;
	
	protected double phiRad;
	protected double sinPhi, cosPhi;		

	/**
	 * 
	 * @param s1
	 * @param ker1
	 * @param phiRad the angle of rotation in radians
	 * @param parallel
	 */
	public void init(Array2d<T> s1, Array2d<T> ker1, double phiRad,
			boolean parallel) {

		extent = s1.getExtent();
		this.phiRad = phiRad;

		int bw1 = s1.getBorderWidth();

		width = parallel ? s1.getPartialWidth() : s1.getWidth();
		height = parallel ? s1.getPartialHeight() : s1.getHeight();
		stride = bw1 * extent * 2;
		rowSize = width * extent + stride;
		totalHeight = height + 2 * s1.getBorderHeight();
		totalWidth = width + 2 * s1.getBorderWidth();
		offset = rowSize * s1.getBorderHeight() + (bw1 * extent);
		

		kernelWidth = ker1.getWidth();
		halfKerSize = kernelWidth / 2;

		if (extent == 1) {
			if (ker1.getExtent() == 1) {
				sig = Signature.SCALAR_SCALAR;
			} else {
				sig = Signature.INVALID;
			}
		} else {
			if (ker1.getExtent() == 1) {
				sig = Signature.VECTOR_SCALAR;
			} else if (extent == ker1.getExtent()) {
				sig = Signature.VECTOR_VECTOR;
			} else {
				sig = Signature.INVALID;
			}
		}
		cosPhi = Math.cos(phiRad);
		sinPhi = Math.sin(phiRad);		
	}

	public void doIt(T dst, T src, T ker) {
		switch (sig) {
		case SCALAR_SCALAR:
			doItSS(dst, src, ker);
			break;
		case VECTOR_SCALAR:
			doItVS(dst, src, ker);
			break;
		case VECTOR_VECTOR:
			doItVV(dst, src, ker);
			break;
		case INVALID:
		default:
			// TODO exception??
		}
	}

	protected abstract void doItSS(T dst, T src, T ker);// SCALAR_SCALAR

	protected abstract void doItVS(T dst, T src, T ker); // VECTOR_SCALAR

	protected abstract void doItVV(T dst, T src, T ker); // VECTOR_VECTOR
}
