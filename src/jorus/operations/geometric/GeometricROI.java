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
import jorus.pixel.Pixel;

/**
 * @author timo
 *
 * @param <T>
 */
public abstract class GeometricROI<T> {
	/**
	 * the pixel extent
	 */
	protected int extent;

	/**
	 * height of the source image in pixels
	 */
	protected int sourceHeight;

	/**
	 * width of the source image in pixels
	 */
	protected int sourceWidth;

	/**
	 * the number of array elements in a row of the image
	 */
	protected int sourceRowSize;

	/**
	 * the offset to the first real image pixel (and not a border value) in the
	 * array of the source image
	 */
	protected int sourceOffset;
	/**
	 * height of the destination image in pixels
	 */
	protected int destinationHeight;
	/**
	 * width of the destination image in pixels
	 */
	protected int destinationWidth;
	/**
	 * the number of array elements in a row of the image
	 */
	protected int destinationRowSize;

	/**
	 * the background pixel
	 */
	protected T background;

	/**
	 * @param background
	 *            the background pixel
	 */
	GeometricROI(Pixel<T> background) {
		if(background == null) {
			this.background = null;
		} else {
			this.background = background.getValue();
		}
	}

	/**
	 * @param destination
	 * @param source
	 * @param parallel
	 */
	public void init(Array2d<T,?> destination, Array2d<T,?> source, boolean parallel) {
		extent = source.getExtent();
		sourceWidth = parallel ? source.getPartialWidth() : source.getWidth();
		sourceHeight = parallel ? source.getPartialHeight() : source.getHeight();
		sourceRowSize = (sourceWidth + 2 * source.getBorderWidth()) * extent;
		sourceOffset = source.getBorderHeight() * sourceRowSize
				+ source.getBorderWidth() * extent;

		destinationWidth = parallel ? destination.getPartialWidth()
				: destination.getWidth();
		destinationHeight = parallel ? destination.getPartialHeight()
				: destination.getHeight();
		destinationRowSize = (destinationWidth + 2 * destination.getBorderWidth())
				* extent;
		// destinationOffset = destination.getBorderHeight() * sourceRowSize +
		// destination.getBorderWidth() * extent;
	}

	
	/**
	 * @param destination The destination array
	 * @param source The source array
	 * @param beginX The x coordinate of the source pixel (0,0) in the destination image
	 * @param beginY The y coordinate of the source pixel (0,0) in the destination image
	 */
	public abstract void doIt(T destination, T source, int beginX, int beginY);
}
