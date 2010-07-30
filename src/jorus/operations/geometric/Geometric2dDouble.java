package jorus.operations.geometric;

import jorus.array.Matrix;


public class Geometric2dDouble extends Geometric2d<double[]> {

	public Geometric2dDouble(Matrix backwardsTransformationMatrix,
			double[] translationVector, double[] background,
			boolean linearInterpolation) {
		super(backwardsTransformationMatrix, translationVector, background,
				linearInterpolation);
	}

	@Override
	public void doIt(double[] destination, double[] source) {	
		

		int maxSourceX = linearInterpolation ? sourceWidth - 2 : sourceWidth - 1;
		int maxSourceY = linearInterpolation ? sourceHeight - 2 : sourceHeight - 1;

		for (int y = 0; y < destinationHeight; y++) {
			for (int x = 0; x < destinationWidth; x++) {
				int sourceX, sourceY;
				double sourceXf, sourceYf;
				double[] vArg;

				// get location in source image
				double[] vTmp = new double[] { x + translationVector[0],
						y + translationVector[1], 1 + translationVector[2] };
				try {
					vArg = Matrix.multMV(backwardsTransformationMatrix, vTmp);
				} catch (Exception e) {
					// TODO Will not happen
					throw new Error(e);
				}
				

				sourceXf = vArg[0] / vArg[2]; /* homogeneous coordinates */
				sourceYf = vArg[1] / vArg[2];

				sourceX = (int) sourceXf;
				sourceY = (int) sourceYf;

				// check whether it is in the border
				if ((sourceX < 0) || ((int) (sourceXf + 0.5) > maxSourceX)
						|| (sourceY < 0)
						|| ((int) (sourceYf + 0.5) > maxSourceY)) {
					System.arraycopy(background, 0, destination, destinationIndex(x, y), extent);
				} else {
					// apply transformation on pixel (x,y) of the target image
					if (linearInterpolation) {
						int ul, ur, lr, ll, targetPixel;
						double alpha = sourceXf - sourceX;
						double beta = sourceYf - sourceY;

						targetPixel = destinationIndex(x, y);

						ul = sourceIndex(sourceX, sourceY);
						ur = sourceIndex(sourceX + 1, sourceY);
						lr = sourceIndex(sourceX + 1, sourceY + 1);
						ll = sourceIndex(sourceX, sourceY + 1);

						for (int i = 0; i < extent; i++) {
							destination[targetPixel + i] = source[ul + i]
									+ alpha
									* (source[ur + i] - source[ul + i])
									+ beta
									* (source[ll + i] - source[ul + i])
									+ alpha
									* beta
									* (source[ul + i] - source[ur + i]
											- source[ll + i] + source[lr
											+ i]);
						}
					} else { // nearest neighbor
						int sIndex = sourceIndex((int) (sourceXf + 0.5),
								(int) (sourceYf + 0.5));
						int tIndex = destinationIndex(x, y);
						for (int k = 0; k < extent; k++) {
							destination[tIndex + k] = source[sIndex + k];
						}
					}
				}
			}
		}	
	}

}
