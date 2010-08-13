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
	private static final int VECTOR_VECTOR =1, VECTOR_SCALAR = 2, SCALAR_SCALAR = 3, INVALID= 0;
	
//	private enum Signature {
//		VECTOR_VECTOR, VECTOR_SCALAR, SCALAR_SCALAR, INVALID;
//	}

//	protected Signature sig;
	protected int signature;
	
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
	protected float sinPhi, cosPhi;		
	protected float absCosPhi;
	protected float absSinPhi;

	/**
	 * 
	 * @param source
	 * @param kernel
	 * @param phiRad the angle of rotation in radians
	 * @param parallel
	 */
	public void init(Array2d<T,?> source, Array2d<T,?> kernel, double phiRad,
			boolean parallel) {

		extent = source.getExtent();
		this.phiRad = phiRad;

		int bw1 = source.getBorderWidth();

		width = parallel ? source.getPartialWidth() : source.getWidth();
		height = parallel ? source.getPartialHeight() : source.getHeight();
		stride = bw1 * extent * 2;
		rowSize = width * extent + stride;
		totalHeight = height + 2 * source.getBorderHeight();
		totalWidth = width + 2 * source.getBorderWidth();
		offset = rowSize * source.getBorderHeight() + (bw1 * extent);
		

		kernelWidth = kernel.getWidth();
		halfKerSize = kernelWidth / 2;

		if (extent == 1) {
			if (kernel.getExtent() == 1) {
//				sig = Signature.SCALAR_SCALAR;
				signature = SCALAR_SCALAR;
			} else {
//				sig = Signature.INVALID;
				signature = INVALID;
			}
		} else {
			if (kernel.getExtent() == 1) {
//				sig = Signature.VECTOR_SCALAR;
				signature = VECTOR_SCALAR;
			} else if (extent == kernel.getExtent()) {
//				sig = Signature.VECTOR_VECTOR;
				signature = VECTOR_VECTOR;
			} else {
//				sig = Signature.INVALID;
				signature = INVALID;
			}
		}
		cosPhi = (float) Math.cos(phiRad);
		sinPhi = (float) Math.sin(phiRad);
		absCosPhi = Math.abs(cosPhi);
		absSinPhi = Math.abs(sinPhi);
	}

	public void doIt(T dst, T src, T ker) {
//		switch (sig) {
		switch (signature) {
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
