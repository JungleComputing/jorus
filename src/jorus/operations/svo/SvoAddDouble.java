/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.operations.svo;


public class SvoAddDouble extends Svo<double[]>
{
	protected double[] value;


	public SvoAddDouble(double[] p)
	{
		value = p;
	}


	public void doIt(double[] dst, int x, int y)
	{
		for (int i=0; i<e; i++) {
			dst[off+y*(w+stride)+x*e+i] += value[i];
		}
	}
}
