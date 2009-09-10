/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.red;

import jorus.operations.CxRedOpArray;

public class CxRedOpAddIntArray extends CxRedOpArray<int[]> {

	@Override
	public void doIt(int[] src1, int[] src2) {
		for (int i = 0; i < src1.length; i++) {
			src1[i] += src2[i];
		}
	}
}
