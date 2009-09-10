/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.patterns;

import jorus.array.CxArray2d;
import jorus.parallel.PxSystem;

public abstract class SetBorder<T> {
		
	public void dispatch(CxArray2d<T> s1, int numX, int numY) {
		if (PxSystem.initialized()) { // run parallel
			init(s1, true);
			doIt(s1.getPartialData(), numX, numY);
		} else { // run sequential
			init(s1, false);
			doIt(s1.getData(), numX, numY);
		}
	}
	
	protected abstract void init(CxArray2d<T> s1, boolean parallel);
	protected abstract void doIt(T dst, int numX, int numY);
	
}
