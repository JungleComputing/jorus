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

public class CxRedOpAddShortArray extends CxRedOpArray<short[]> {

	@Override
	public void doIt(short[] src1, short[] src2) {
		for (int i = 0; i < src1.length; i++) {
			src1[i] += src2[i];
		}
	}
}
