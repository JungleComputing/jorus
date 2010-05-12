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
import jorus.operations.svo.Svo;
import jorus.parallel.PxSystem;


public class PatSvo
{
    public static <T> Array2d<T> dispatch(Array2d<T> s1, int x, int y,
            boolean inplace, Svo<T> svo)
    {
        Array2d<T> dst = s1;

        if (PxSystem.initialized()) {				// run parallel
            
            final PxSystem px = PxSystem.get();
            final int rank = px.myCPU();
            
            try {

                if (s1.getLocalState() != Array2d.VALID ||
                        s1.getDistType() != Array2d.PARTIAL) {

                    // The data structure has not been distibuted yet, or is no 
                    // longer valid

                    if (s1.getGlobalState() != Array2d.NONE) { 

                        if (rank == 0) System.out.println("SVO SCATTER 1...");
                        px.scatter(dst);

                    } else { 
                        // Added -- J
                        //
                        // A hack that assumes dst is a target data structure which we do not need to 
                        // scatter. We only initialize the local partitions.

                        final int pHeight = px.getPartHeight(s1.getHeight(),rank);

                        final int size = (s1.getWidth() + s1.getBorderWidth() * 2)
                        * (pHeight + s1.getBorderHeight() * 2) 
                        * s1.getExtent();

                        s1.setPartialData(s1.getWidth(), pHeight, s1.createDataArray(size), 
                                Array2d.VALID, Array2d.PARTIAL);
                    }                    
                }

                if (!inplace) dst = s1.clone();

                svo.init(s1, true);

                int start = px.getLclStartY(s1.getHeight(), rank);

                if ((y >= start) && (y < start+s1.getPartialHeight())) {
                    svo.doIt(dst.getPartialDataWriteOnly(), x, y - start);
                }

                if (dst.getGlobalState() != Array2d.NONE) { 
                    dst.setGlobalState(Array2d.INVALID);
                }

//              if (PxSystem.myCPU() == 0) System.out.println("SVO GATHER...");
//              PxSystem.gatherOFT(dst);

            } catch (Exception e) {
                System.err.println("Failed to perform operation!");
                e.printStackTrace(System.err);
            }

        } else {									// run sequential

            if (!inplace) dst = s1.clone();

            svo.init(s1, false);
            svo.doIt(dst.getDataWriteOnly(), x, y);
        }

        return dst;
    }
}