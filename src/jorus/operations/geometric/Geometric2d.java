/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.geometric;

import jorus.array.Array2d;
import jorus.array.Matrix;
import jorus.parallel.PxSystem;

public abstract class Geometric2d<T> {
	protected int extent = 0;
	
	protected int sourceWidth = 0; //in pixels
	protected int sourceHeight = 0;
	protected int sourceOffset = 0;
	protected int sourceStride = 0;
	protected int sourceRowWidth = 0;

	protected int destinationWidth = 0; //in pixels
	protected int destinationHeight = 0;
	protected int destinationOffset = 0;
	protected int destinationStride = 0;
	protected int destinationRowWidth = 0;

	protected Matrix backwardsTransformationMatrix;
	protected double[] translationVector;
	protected T background;
	protected boolean linearInterpolation;

	public Geometric2d(Matrix backwardsTransformationMatrix,
			double[] translationVector, T background, boolean linearInterpolation) {
		this.linearInterpolation = linearInterpolation;
		this.backwardsTransformationMatrix = backwardsTransformationMatrix;
		this.translationVector = translationVector;
		this.background = background;
	}

	protected final int destinationIndex(int x, int y) {
		return destinationOffset + y * destinationRowWidth + x * extent;
	}
	
	protected final int sourceIndex(int x, int y) {
		return sourceOffset + y * sourceRowWidth + x * extent;
	}
	
	public void init(Array2d<T,?> destination, Array2d<T,?> source, boolean parallel) {
		extent = source.getExtent();
		
		int borderWidth = source.getBorderWidth();
		sourceWidth = parallel ? source.getPartialWidth() : source.getWidth();
		sourceHeight = parallel ? source.getPartialHeight() : source
				.getHeight();
		sourceStride = 2 * borderWidth * extent;
		sourceRowWidth = sourceWidth * extent + sourceStride;
		sourceOffset = sourceRowWidth * source.getBorderHeight()
				+ (borderWidth * extent);

		
		borderWidth = destination.getBorderWidth();
		destinationWidth = parallel ? destination.getPartialWidth() : destination.getWidth();
		destinationHeight = parallel ? destination.getPartialHeight()
				: destination.getHeight();
		destinationStride = 2 * borderWidth * extent;
		destinationRowWidth = destinationWidth * extent + destinationStride;
		destinationOffset = destinationRowWidth * destination.getBorderHeight()
				+ (borderWidth * extent);
		
		
		if(parallel) {
//			We do not have this one: only vertical data partitioning
			PxSystem px = PxSystem.get();
//			translationVector[0] += px.getLclStartX(source.getHeight(), px.myCPU());
			translationVector[1] += px.getLclStartY(source.getHeight(), px.myCPU());
		}

	}

	public abstract void doIt(T destination, T source);
}
