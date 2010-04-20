/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.operations.bpoval;


public class BpoSetValLong extends BpoVal<long[]>
{
	protected long[] value;


	public BpoSetValLong(long[] p)
	{
		value = p;
	}


	public void doIt(long[] dst)
	{
		for (int j=0; j<height; j++) {
			for (int i=0; i<width; i++) {
				dst[offset+j*(width+stride)+i] = value[i%value.length];
			}
		}
	}
}
