/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.geometric;

import jorus.pixel.Pixel;

public class GeometricROIFloat extends GeometricROI<float[]> {

	public GeometricROIFloat(Pixel<float[]> background) {
		super(background);
	}

	@Override
	public void doIt(float[] destination, float[] source, int beginX, int beginY) {

		if (beginX >= destinationWidth || beginX < -destinationWidth
				|| beginY >= destinationHeight || beginY < -destinationHeight) {
			// The new image is in the border completely
			// The image to become the background;
			doBackGround(destination, 0, destination.length);
			return;
		}

		int destinationIndex = 0;
		int sourceIndex = sourceOffset;
		int rowsDone = 0;

		// do the top border
		if (beginY > 0) { // start with 'beginY' background rows
			doBackGround(destination, 0, destinationRowSize * beginY);
			destinationIndex += destinationRowSize * beginY;
			rowsDone += beginY;
		} else { // 'beginY' rows are cut off the source image
			sourceIndex -= sourceRowSize * beginY;
		}

		// do the source rows

		//first determine the column layout
		int leftBorder; // size of left border
		int sourceColumns; // number of source columns to be copied
		if (beginX > 0) {
			leftBorder = beginX;
			sourceColumns = Math.min(sourceWidth, destinationWidth - beginX);
		} else {
			leftBorder = 0;
			// correct the source index for the missing first columns
			sourceIndex -= beginX * extent;
			sourceColumns = Math.min(sourceWidth - beginX, destinationWidth);
		}
		int rightBorder = destinationWidth - sourceColumns - leftBorder; // size of right border

		// determine which rows to copy
		int sourceRows;
		if (sourceHeight <= destinationHeight - rowsDone) {
			// all source rows fits in the destination image
			sourceRows = sourceHeight;
		} else {
			// source image too big, cut off bottom source rows
			sourceRows = destinationHeight - rowsDone;
		}
		for (int i = 0; i < sourceRows; i++) {
			doRow(destination, source, destinationIndex, sourceIndex,
					leftBorder, sourceColumns, rightBorder);
			sourceIndex += sourceRowSize;
			destinationIndex += destinationRowSize;
		}
		rowsDone += sourceRows;

		// do the bottom border
		if (rowsDone < destinationHeight) {
			doBackGround(destination, rowsDone * destinationRowSize,
					(destinationHeight - rowsDone) * destinationRowSize);
		}

		
	}

	private void doRow(float[] destination, float[] source,
			int destinationIndex, int sourceIndex, int leftBorder, int sourceColumns,
			int rightBorder) {
		if (leftBorder > 0) {
			doBackGround(destination, destinationIndex, leftBorder * extent);
			destinationIndex += leftBorder * extent;
		}
		if (sourceColumns > 0) {
			doForeGround(destination, destinationIndex, source, sourceIndex,
					sourceColumns * extent);
			destinationIndex += sourceColumns * extent;
		}
		if (rightBorder > 0) {
			doBackGround(destination, destinationIndex, rightBorder * extent);
		}
	}

	/**
	 * @param destination
	 * @param destinationIndex
	 * @param length
	 *            the number of pixels elements to become background pixel
	 *            elements
	 */
	private void doBackGround(float[] destination, int destinationIndex,
			int length) {
		int len = background.length;
		for (int x = 0; x < length; x++) {
			destination[destinationIndex + x] = background[x % len];
		}
	}

	/**
	 * @param destination
	 * @param destinationIndex
	 * @param length
	 *            the number of pixels elements to become background pixel
	 *            elements
	 */
	private void doForeGround(float[] destination, int destinationIndex,
			float[] source, int sourceIndex, int length) {
		for (int x = 0; x < length; x++) {
			destination[destinationIndex + x] = source[sourceIndex + x];
		}
	}

}
