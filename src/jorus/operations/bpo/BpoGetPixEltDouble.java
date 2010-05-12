/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.operations.bpo;


public class BpoGetPixEltDouble extends Bpo<double[]>
{
	protected int index;


	public BpoGetPixEltDouble(int idx)
	{
		index = idx;
		if (index <  0) {
			index = 1;
		}
		index %= 3;
	}


	public void doIt(double[] dst, double[] src)
	{
		int e2 = src.length / dst.length;
		for (int j=0; j<height; j++) {
			for (int i=0; i<width; i++) {
				dst[offset1+j*(width+stride1)+i] =
							src[offset2+j*(width*e2+stride2) + i*e2 + index];
			}
		}
	}
}
