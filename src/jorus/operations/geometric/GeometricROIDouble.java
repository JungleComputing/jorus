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

public class GeometricROIDouble extends GeometricROI<double[]> {

	public GeometricROIDouble(Pixel<double[]> background) {
		super(background);
	}

	@Override
	public void doIt(double[] destination, double[] source, int beginX,
			int beginY) {
		int top;
		int height;
		int bottom;

		if (beginY < 0) {
			top = -beginY;
			if (sourceHeight < destinationHeight - top) {
				height = sourceHeight;
				bottom = destinationHeight - height - top;
			} else {
				height = destinationHeight - top;
				bottom = 0;
			}
		} else { // beginY >=0
			top = 0;
			if (sourceHeight - beginY < destinationHeight) {
				height = sourceHeight - beginY;
				bottom = destinationHeight - height;
			} else {
				height = destinationHeight;
				bottom = 0;
			}
		}

		int left;
		int width;
		int right;

		if (beginX < 0) {
			left = -beginX;
			if (sourceWidth < destinationWidth - left) {
				width = sourceWidth;
				right = destinationWidth - width - left;
			} else {
				width = destinationWidth - top;
				right = 0;
			}
		} else { // beginX >=0
			left = 0;
			if (sourceWidth - beginX < destinationWidth) {
				width = sourceWidth - beginX;
				right = destinationWidth - width;
			} else {
				width = destinationWidth;
				right = 0;
			}
		}

		if (height < 0 || width < 0) {
			// The new image is in the border completely
			// The image to become the background;
			doBackGround(destination, 0, destination.length);
			return;
		}

		int destinationIndex = 0;
		
		int sourceIndex = sourceOffset + (top - beginY) * sourceRowSize + (left - beginX) * extent; //TODO check this
		for (int y = 0; y < top; y++) {
			doBackGround(destination, destinationIndex, destinationRowSize);
			sourceIndex += sourceRowSize;
			destinationIndex += destinationRowSize;
		}
		for (int y = 0; y < height; y++) {
			doRow(destination, source, destinationIndex, sourceIndex, left,
					width, right);
			sourceIndex += sourceRowSize;
			destinationIndex += destinationRowSize;
		}
		for (int y = 0; y < bottom; y++) {
			doBackGround(destination, destinationIndex, destinationRowSize);
			sourceIndex += sourceRowSize;
			destinationIndex += destinationRowSize;
		}
	}

	private void doRow(double[] destination, double[] source,
			int destinationIndex, int sourceIndex, int left, int width,
			int right) {
		if (left > 0) {
			doBackGround(destination, destinationIndex, left);
			destinationIndex += left * extent;
		}
		if (width > 0) {
			doForeGround(destination, destinationIndex, source, sourceIndex,
					width);
			destinationIndex += width * extent;
		}
		if (right > 0) {
			doBackGround(destination, destinationIndex, right);
		}
	}

	private void doBackGround(double[] destination, int destinationIndex,
			int length) {
		int len = background.length;
		for (int x = 0; x < length * extent; x++) {
			destination[destinationIndex + x] = background[x % len];
		}
	}

	private void doForeGround(double[] destination, int destinationIndex,
			double[] source, int sourceIndex, int length) {
		for (int x = 0; x < length * extent; x++) {
			destination[destinationIndex + x] = source[sourceIndex + x];
		}
	}

}
