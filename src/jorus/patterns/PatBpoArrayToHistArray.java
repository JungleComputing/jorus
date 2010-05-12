package jorus.patterns;

import java.util.Arrays;

import jorus.array.Array2d;
import jorus.operations.bpo.BpoToHist;
import jorus.parallel.PxSystem;
import jorus.parallel.ReduceOp;

public class PatBpoArrayToHistArray {

	// private static double [] buffer;
	// private static double [] dst;

	public static double[][] dispatch(Array2d s1, Array2d[] a2, int nBins,
			double minVal, double maxVal, BpoToHist bpo) {

		double[][] result = new double[a2.length][nBins];

		if (PxSystem.initialized()) { // run parallel

			final PxSystem px = PxSystem.get();
			final boolean root = px.isRoot();

			double[] buffer = new double[a2.length * nBins];
			double[] dst = new double[nBins];

			try {
				if (s1.getLocalState() != Array2d.LOCAL_PARTIAL) {
					if (root)
						System.out.println("BPO2HIST SCATTER 1...");
					px.scatter(s1);
				}

				for (int i = 0; i < a2.length; i++) {

					Array2d s2 = a2[i];

					if (s2.getLocalState() != Array2d.LOCAL_PARTIAL) {
						if (root)
							System.out.println("BPO2HIST SCATTER 2...");
						px.scatter(s2);
					}

					bpo.init(s1, s2, true);

					bpo.doIt(dst, s1.getPartialDataReadOnly(), s2
							.getPartialDataReadOnly(), nBins, minVal, maxVal);

					System.arraycopy(dst, 0, buffer, i * nBins, nBins);

					// Clear the dst array for the next pass!
					Arrays.fill(dst, 0.0);
				}

				/*
				 * double sum = 0.0;
				 * 
				 * for (int i=0;i<buffer.length;i++) { sum += buffer[i]; }
				 * 
				 * System.out.println("PRE: " + sum);
				 */

				// HACK to synchronize all machines. If we don't do this,
				// any load imbalance in the histogram calculation shows up as
				// communication overhead. On small numbers of machines this
				// imbalance can be quit big (e.g., some 200ms on 2 nodes).
				px.silentBarrier();

				// if (PxSystem.myCPU() == 0)
				// System.out.println("BPO2HIST ALLREDUCE..");

				// Timo: Not sure whether the extent is correct, but does not
				// matter for the addition anyways
				px.reduceToAll(buffer, 1, ReduceOp.SUM);

				/*
				 * sum = 0.0;
				 * 
				 * for (int i=0;i<buffer.length;i++) { sum += buffer[i]; }
				 * 
				 * System.out.println("POST: " + sum);
				 */

				// FIXME: inefficient
				for (int i = 0; i < result.length; i++) {
					System.arraycopy(buffer, i * nBins, result[i], 0, nBins);
				}

			} catch (Exception e) {
				System.err.println("Failed to perform operation!");
				e.printStackTrace(System.err);
			}

		} else { // run sequential
			for (int i = 0; i < a2.length; i++) {
				Array2d s2 = a2[i];

				bpo.init(s1, s2, false);
				bpo.doIt(result[i], s1.getDataReadOnly(), s2.getDataReadOnly(),
						nBins, minVal, maxVal);
			}
		}

		return result;
	}
}
