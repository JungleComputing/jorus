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
import jorus.array.Array2dScalarDouble;
import jorus.parallel.PxSystem;
import jorus.parallel.ReduceOp;
import jorus.weibull.CxWeibullFit;


public class PatTask
{
    // This must be generalized to allow any type of task to be executed
    
    public static void dispatch(CxWeibullFit task, int iMax, int jMax)
    {
        if (PxSystem.initialized()) {				// run parallel
            
            final PxSystem px = PxSystem.get();
            final int rank = px.myCPU();
            final int size = px.nrCPUs();
            
            
            try {
                int taskNr = 0;
                for (int j=0; j<jMax; j++) {
                    for (int i=0; i<iMax; i++) {
                        if (taskNr % size == rank) {
                            task.doIt(i, j);
                        }

                        // Added -- Jason
                        taskNr++;						
                    }
                }
                for (int j=0; j<jMax; j++) {
                	double[] betaArray = task.getBetas(j);
                	Array2dScalarDouble betas = new Array2dScalarDouble(betaArray.length, 1, betaArray, false);
                	betas.setPartialData(betaArray.length, 1, betaArray, Array2d.LOCAL_NOT_REDUCED);
                	betas.setReduceOperation(ReduceOp.SUM);
                	px.reduceToRoot(betas);
                	
                	double[] gammaArray = task.getGammas(j);
                	Array2dScalarDouble gammas = new Array2dScalarDouble(gammaArray.length, 1, gammaArray, false);
                	betas.setPartialData(gammaArray.length, 1, gammaArray, Array2d.LOCAL_NOT_REDUCED);
                	gammas.setReduceOperation(ReduceOp.SUM);
                	px.reduceToRoot(gammas);
                }
            } catch (Exception e) {
                System.err.println("Failed to perform operation!");
                e.printStackTrace(System.err);
            }
        } else {									// run sequential
            for (int j=0; j<jMax; j++) {
                for (int i=0; i<iMax; i++) {
                    task.doIt(i, j);
                }
            }
        }
    }
}
