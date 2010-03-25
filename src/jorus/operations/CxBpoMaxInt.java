/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.operations;


public class CxBpoMaxInt extends CxBpo<int[]>
{
	public void doIt(int[] dst, int[] s2)
	{
		for (int j=0; j<h; j++) {
			for (int i=0; i<w; i++) {
				if(s2[off2+j*(w+stride2)+i] > dst[off1+j*(w+stride1)+i]) {
					dst[off1+j*(w+stride1)+i] = s2[off2+j*(w+stride2)+i];
				}
			}
		}
	}
}
