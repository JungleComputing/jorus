/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.operations.svo;


public class SvoAddShort extends Svo<short[]>
{
	protected short[] value;


	public SvoAddShort(short[] p)
	{
		value = p;
	}


	public void doIt(short[] dst, int x, int y)
	{
		for (int i=0; i<e; i++) {
			dst[off+y*(w+stride)+x*e+i] += value[i];
		}
	}
}
