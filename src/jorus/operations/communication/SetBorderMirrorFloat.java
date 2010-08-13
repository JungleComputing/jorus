/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.communication;

import jorus.parallel.PxSystem;

public class SetBorderMirrorFloat extends SetBorder<float[]> {
	public void doIt(float[] destination, int numX, int numY) {
		if (doParallel) {

			// long start = System.currentTimeMillis();

			doItParallel(destination, numX, numY);

			// long end = System.currentTimeMillis();

			// System.out.println("Exchange time " + (end-start));

		} else {
			doItSequential(destination, numX, numY);
		}
	}

	private void doItSequential(float[] destination, int numX, int numY) {
		if (numX > 0) {

			// NOTE: Horus has code here to exchange the top/bottom
			// data. In the dog demo this is nog triggered due to
			// the data layout.

			// Timo: you mean left/right border??

			// Mirror left part NOT including upper and lower "corners"

			for (int j = 0; j < height; j++) {
				int imageIndex = offset + j * rowSize;
				int borderIndex = imageIndex - extent;
				for (int i = 0; i < numX; i++) {
					for (int k = 0; k < extent; k++) {
						destination[borderIndex - i * extent + k] = destination[imageIndex
								+ i * extent + k];
					}
				}
			}

			// Mirror right part NOT including upper and lower "corners"

			for (int j = 0; j < height; j++) {
				int borderIndex = offset + j * rowSize + width * extent;
				int imageIndex = borderIndex - extent;
				for (int i = 0; i < numX; i++) {
					for (int k = 0; k < extent; k++) {
						destination[borderIndex + i * extent + k] = destination[imageIndex
								- i * extent + k];
					}
				}
			}
		}

		if (numY > 0) {
			// Mirror top part including left and right "corners"

			final int baseRow = offset - stride / 2;

			for (int j = 0; j < numY; j++) {
				int borderIndex = baseRow - (1 + j) * rowSize;
				int imageIndex = baseRow + j * rowSize;
				for (int i = 0; i < rowSize; i++) {
					destination[borderIndex + i] = destination[imageIndex + i];
				}
			}

			// Mirror bottom part including left and right "corners"

			for (int j = 0; j < numY; j++) {
				int borderIndex = baseRow + (height + j) * rowSize;
				int imageIndex = baseRow + (height - 1 - j) * rowSize;
				for (int i = 0; i < rowSize; i++) {
					destination[borderIndex + i] = destination[imageIndex + i];
				}
			}
		}
	}

	private void doItParallel(float[] destination, int numX, int numY) {
		PxSystem px = PxSystem.get();

		if (numX > 0) {

			// NOTE: Horus has code here to exchange the top/bottom
			// data. In the dog demo this is nog triggered due to
			// the data layout.

			// Timo: you mean left/right border??

			if (extent == 1) {
				// Mirror left part NOT including upper and lower "corners"
				for (int j = 0; j < height; j++) {
					int imageIndex = offset + j * rowSize;
					int borderIndex = imageIndex - 1;
					for (int i = 0; i < numX; i++) {
						destination[borderIndex - i] = destination[imageIndex
								+ i];
					}
				}

				// Mirror right part NOT including upper and lower "corners"
				for (int j = 0; j < height; j++) {
					int borderIndex = offset + j * rowSize + width;
					int imageIndex = borderIndex - 1;
					for (int i = 0; i < numX; i++) {
						destination[borderIndex + i] = destination[imageIndex
								- i];
					}
				}

			} else { //extent != 1
				// Mirror left part NOT including upper and lower "corners"
				for (int j = 0; j < height; j++) {
					int imageIndex = offset + j * rowSize;
					int borderIndex = imageIndex - extent;
					for (int i = 0; i < numX; i++) {
						for (int k = 0; k < extent; k++) {
							destination[borderIndex - i * extent + k] = destination[imageIndex
									+ i * extent + k];
						}
					}
				}

				// Mirror right part NOT including upper and lower "corners"

				for (int j = 0; j < height; j++) {
					int borderIndex = offset + j * rowSize + width * extent;
					int imageIndex = borderIndex - extent;
					for (int i = 0; i < numX; i++) {
						for (int k = 0; k < extent; k++) {
							destination[borderIndex + i * extent + k] = destination[imageIndex
									- i * extent + k];
						}
					}
				}
			}
		}

		if (numY > 0) {
			try {
				px.borderExchange(destination, width * extent,
						height, offset, stride, numY);
				// PxSystem.get().borderExchangeTimo(destination, width *
				// extent, height, offset - stride/2, rowSize, numY);
			} catch (Exception e) {
				//
			}

			// Mirror top part including left and right "corners"

			

			if (px.myCPU() == 0) { // the first CPU
				final int baseRow = offset - stride / 2;
				for (int j = 0; j < numY; j++) {
					int borderIndex = baseRow - (1 + j) * rowSize;
					int imageIndex = baseRow + j * rowSize;
					for (int i = 0; i < rowSize; i++) {
						destination[borderIndex + i] = destination[imageIndex
								+ i];
					}
				}
			}

			// Mirror bottom part including left and right "corners"

			if (px.myCPU() == px.nrCPUs() - 1) {
				final int baseRow = offset - stride / 2 + (height) * rowSize;
				for (int j = 0; j < numY; j++) {
					int borderIndex = baseRow + j * rowSize;
					int imageIndex = baseRow - (j + 1) * rowSize;
					for (int i = 0; i < rowSize; i++) {
						destination[borderIndex + i] = destination[imageIndex
								+ i];
					}
				}
			}
		}
	}
}
