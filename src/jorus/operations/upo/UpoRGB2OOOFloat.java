/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.upo;

public class UpoRGB2OOOFloat extends Upo<float[]> {

	public void doIt(float[] a) {
		// NOTE: here we assume array 'a' to be in RGB color space
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i += 3) {
				final int id = off + j * (w + stride) + i;
				final float x = a[id];
				final float y = a[id + 1];
				final float z = a[id + 2];
				a[id] = x * (float) 0.000233846 + y * (float) 0.00261968 + z
						* (float) 0.00127135;
				a[id + 1] = x * (float) 0.000726333 + y * (float) 0.000718106
						+ z * (float) -0.00121377;
				a[id + 2] = x * (float) 0.000846833 + y * (float) -0.00173932
						+ z * (float) 0.000221515;
			}
		}
	}
}
