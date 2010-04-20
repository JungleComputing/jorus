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

public abstract class GeneralizedConvolution1d<T> {
	private enum Signature {
		VECTOR_VECTOR, VECTOR_SCALAR, SCALAR_SCALAR, INVALID;
	}

	protected Signature signature;
	protected int dimension; //0 = horizontal, 1 = vertical
	protected int width; // width of image (in pixels)
	protected int height; // height of image (in pixels)
	protected int extent; // pixel extent
	protected int offset; // location of first pixel in source array
	protected int stride; // number of elements between two rows (corrected for extent) 
	protected int kernelWidth; // kernel size for filtering
	
	protected int rowSize; //width of a row, including borders, corrected for extent

	/***
	 * 
	 * @param source
	 * @param kernel
	 * @param dimension 0 = horizontal, 1 = vertical
	 * @param parallel
	 */
	public void init(Array2d<T> source, Array2d<T> kernel, int dimension,
			boolean parallel) {
		
		this.dimension = dimension;

		extent = source.getExtent();
		width = parallel ? source.getPartialWidth() : source.getWidth();
		height = parallel ? source.getPartialHeight() : source.getHeight();
		
		int borderWidth = source.getBorderWidth();
		int borderHeight = source.getBorderHeight();
		
		stride = borderWidth * extent * 2;
		rowSize = width * extent + stride;
		offset = rowSize * borderHeight + (borderWidth * extent);
		
		kernelWidth = kernel.getWidth();

		if (extent == 1) {
			if (kernel.getExtent() == 1) {
				signature = Signature.SCALAR_SCALAR;
			} else {
				signature = Signature.INVALID;
			}
		} else {
			if (kernel.getExtent() == 1) {
				signature = Signature.VECTOR_SCALAR;
			} else if (extent == kernel.getExtent()) {
				signature = Signature.VECTOR_VECTOR;
			} else {
				signature = Signature.INVALID;
			}
		}
	}

	public void doIt(T dst, T src, T ker) {
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
