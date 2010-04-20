/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.operations.communication;


public class RedOpAddIntArray extends RedOpArray<int[]>
{
	public void doIt(int[] src1, int[] src2)
	{
		for (int i=0; i<src1.length; i++) {
			src1[i] += src2[i];
		}
	}
	
	@Override
	public void doItRange(int[] src1, int[] src2, int startIndex, int length) {
		for (int i=0; i<length; i++) {
			src1[startIndex+i] += src2[i];
		}
	}

}
