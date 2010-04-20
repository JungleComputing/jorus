/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.operations.svo;


public class SvoAddInt extends Svo<int[]>
{
	protected int[] value;


	public SvoAddInt(int[] p)
	{
		value = p;
	}


	public void doIt(int[] dst, int x, int y)
	{
		for (int i=0; i<e; i++) {
			dst[off+y*(w+stride)+x*e+i] += value[i];
		}
	}
}
