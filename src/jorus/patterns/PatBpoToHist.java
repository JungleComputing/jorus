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
import jorus.operations.bpo.BpoToHist;
import jorus.operations.communication.RedOpAddDoubleArray;
import jorus.parallel.PxSystem;
import jorus.parallel.ReduceOp;


public class PatBpoToHist
{
    private static final RedOpAddDoubleArray reduceOp = new RedOpAddDoubleArray();
    
    public static double[] dispatch(Array2d s1, Array2d s2,
            int nBins, double minVal,
            double maxVal, BpoToHist bpo)
    {
        double [] dst = new double[nBins];

        if (PxSystem.initialized()) {		
            
            final PxSystem px = PxSystem.get();
            final boolean root = px.isRoot();

      
            try {

                if (s1.getLocalState() != Array2d.LOCAL_PARTIAL) {
                    if (root) System.out.println("BPO2HIST SCATTER 1...");
                    px.scatter(s1);
                }
                if (s2.getLocalState() != Array2d.LOCAL_PARTIAL) {
                    if (root) System.out.println("BPO2HIST SCATTER 2...");
                    px.scatter(s2);
                }

                bpo.init(s1, s2, true);

                bpo.doIt(dst, s1.getPartialDataReadOnly(),
                        s2.getPartialDataReadOnly(), nBins, minVal, maxVal);

//              if (PxSystem.myCPU() == 0) System.out.println("BPO2HIST ALLREDUCE..");
                px.reduceToAll(dst, 1, ReduceOp.SUM);

            } catch (Exception e) {
                System.err.println("Failed to perform operation!");
                e.printStackTrace(System.err);
            }

        } else {									// run sequential
            bpo.init(s1, s2, false);
            bpo.doIt(dst, s1.getDataReadOnly(),
                    s2.getDataReadOnly(), nBins, minVal, maxVal);
        }
        return dst;
    }
}
