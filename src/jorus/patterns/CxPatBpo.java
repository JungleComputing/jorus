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
import jorus.operations.CxBpo;
import jorus.parallel.PxSystem;


public class CxPatBpo
{
    public static CxArray2d dispatch(CxArray2d s1, CxArray2d s2,
            boolean inplace, CxBpo bpo)
    {
        CxArray2d dst = s1;
       
        if (PxSystem.initialized()) {				// run parallel
       
            final PxSystem px = PxSystem.get();
            final int rank = px.myCPU();
            
            try {

                if (s1.getLocalState() != CxArray2d.VALID ||
                        s1.getDistType() != CxArray2d.PARTIAL) {

                    if (s1.getGlobalState() != CxArray2d.NONE) { 

//                        if (rank == 0) System.out.println("BPO SCATTER 1...");
                        px.scatter(dst);
                   
                    } else { 
                        // Added -- J
                        //
                        // A hack that assumes dst is a target data structure which we do not need to 
                        // scatter. We only initialize the local partitions.

                        final int pHeight = px.getPartHeight(s1.getHeight(), rank);

                        final double[] pData = new double[(s1.getWidth() + s1.getBorderWidth() * 2)
                                                          * (pHeight + s1.getBorderHeight() * 2) * s1.getExtent()];

                        s1.setPartialData(s1.getWidth(), pHeight, pData, CxArray2d.VALID, CxArray2d.PARTIAL);
                    }
                }
                
                if (s2.getLocalState() != CxArray2d.VALID ||
                        s2.getDistType() != CxArray2d.PARTIAL) {
//                    if (rank == 0) System.out.println("BPO SCATTER 2...");
                    px.scatter(s2);
                }
                
                if (!inplace) dst = s1.clone();
                
                bpo.init(s1, s2, true);
                bpo.doIt(dst.getPartialDataReadWrite(), 
                		s2.getPartialDataReadOnly());

                dst.setGlobalState(CxArray2d.INVALID);
                
//              if (PxSystem.myCPU() == 0) System.out.println("BPO GATHER...");
//              PxSystem.gatherOFT(dst);

            } catch (Exception e) {
                System.err.println("Failed to perform operation!");
                e.printStackTrace(System.err);
            }

        } else {
            if (s1.getGlobalState() == CxArray2d.NONE) {
                // Added -- J
                //
                // A hack that assumes dst is a target data structure which we
                // do not need to
                // scatter. We only initialize the local partitions.

                final double[] data = new double[(s1.getWidth() + s1
                        .getBorderWidth() * 2)
                        * (s1.getHeight() + s1.getBorderHeight() * 2)
                        * s1.getExtent()];

                s1.setData(s1.getWidth(), s1.getHeight(), data, CxArray2d.VALID);
            }

            
            if (!inplace) dst = s1.clone();
            
            // run sequential
            bpo.init(s1, s2, false);
            bpo.doIt(dst.getDataReadWrite(), s2.getDataReadOnly());
        }

        return dst;
    }
}
