/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.operations;


public class CxBpoNegDivShort extends CxBpo<short[]>
{
	public void doIt(short[] dst, short[] s2)
	{
		for (int j=0; j<h; j++) {
			for (int i=0; i<w; i++) {
				dst[off1+j*(w+stride1)+i] = dst[off1+j*(w+stride1)+i] < 0 ?
						(short)(-dst[off1+j*(w+stride1)+i] / s2[off2+j*(w+stride2)+i]): 0;
			}
		}
	}
}
