/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.operations.bpo;


public class BpoNegDivDouble extends Bpo<double[]>
{
	public void doIt(double[] dst, double[] s2)
	{
		for (int j=0; j<height; j++) {
			for (int i=0; i<width; i++) {
				dst[offset1+j*(width+stride1)+i] = dst[offset1+j*(width+stride1)+i] < 0 ?
						-dst[offset1+j*(width+stride1)+i] / s2[offset2+j*(width+stride2)+i]: 0;
			}
		}
	}
}
