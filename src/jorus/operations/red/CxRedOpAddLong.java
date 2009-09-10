/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations.red;

import jorus.operations.CxRedOp;

public class CxRedOpAddLong extends CxRedOp<Long> {

	@Override
	public Long doIt(Long val1, Long val2) {
		return val1 + val2;
	}
}
