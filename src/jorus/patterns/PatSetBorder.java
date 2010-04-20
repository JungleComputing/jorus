/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.patterns;


import jorus.array.Array2d;
import jorus.operations.communication.SetBorder;
import jorus.parallel.PxSystem;


public class PatSetBorder
{
    public static <T> void dispatch(Array2d<T> s1,
            int numX, int numY, SetBorder<T> sbo)
    {
        if (PxSystem.initialized()) {				// run parallel
            sbo.init(s1, true);
            sbo.doIt(s1.getPartialDataReadWrite(), numX, numY);
        } else {									// run sequential
            sbo.init(s1, false);
            sbo.doIt(s1.getDataReadWrite(), numX, numY);
        }
    }
}
