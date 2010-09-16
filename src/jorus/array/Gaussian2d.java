package jorus.array;

import jorus.parallel.PxSystem;

public class Gaussian2d {
	public static Array2dScalarDouble createGaussianKernel2dDouble(double sigmaT,
			double sigmaR, double phiDegrees, int derivativeT, double n) {
		int width, height;
		double phiRadians = phiDegrees * Math.PI / 180.0;

		if (phiRadians == 0.0) {
			width = (int) (n * sigmaT);
			height = (int) (n * sigmaR);
		} else {
			double l;
			l = Math.sqrt(sigmaR * sigmaR
					/ (Math.cos(phiRadians) * Math.cos(phiRadians)) + sigmaT
					* sigmaT / (Math.sin(phiRadians) * Math.sin(phiRadians)));
			width = (int) (n
					* Math.abs(sigmaT * sigmaT / Math.tan(phiRadians) + sigmaR
							* sigmaR * Math.tan(phiRadians)) / l + 0.5);
			l = Math.sqrt(sigmaT * sigmaT
					/ (Math.cos(phiRadians) * Math.cos(phiRadians)) + sigmaR
					* sigmaR / (Math.sin(phiRadians) * Math.sin(phiRadians)));
			height = (int) (n
					* Math.abs(sigmaR * sigmaR / Math.tan(phiRadians) + sigmaT
							* sigmaT * Math.tan(phiRadians)) / l + 0.5);
		}
		double divido = 1. / (2 * Math.PI * sigmaR * sigmaT);

		double[] kernelData = new double[(2 * width + 1) * (2 * height + 1)];
		int kernelIndex = 0;

		for (int yy = 0; yy < 2 * height + 1; yy++) {
			for (int xx = 0; xx < 2 * width + 1; xx++) {
				if (derivativeT == 2) {
					divido = (Math.pow(
							(Math.cos(phiRadians) * (xx - width) + Math
									.sin(phiRadians) * (yy - height))
									/ sigmaT, 2.0) - 1.)
							/ (2 * Math.PI * sigmaR * sigmaT * sigmaT);
				}
				kernelData[kernelIndex] = (divido * Math.exp(-0.5
						* (Math.pow(
								(Math.cos(phiRadians) * (xx - width) + Math
										.sin(phiRadians) * (yy - height))
										/ sigmaT, 2.0) + Math.pow(
								(-Math.sin(phiRadians) * (xx - width) + Math
										.cos(phiRadians) * (yy - height))
										/ sigmaR, 2.0))));
				kernelIndex++;
			}
		}
		Array2dScalarDouble result = new Array2dScalarDouble(2 * width + 1, 2 * height + 1,
				kernelData, false);
		if(PxSystem.initialized()) {
			result.setState(Array2d.LOCAL_FULL);	
		}
		return result;
	}

	public static Array2dScalarFloat createGaussianKernel2dFloat(double sigmaT,
			double sigmaR, double phiDegrees, int derivativeT, double n) {
		int width, height;
		double phiRadians = phiDegrees * Math.PI / 180.0;
	
		if (phiRadians == 0.0) {
			width = (int) (n * sigmaT);
			height = (int) (n * sigmaR);
		} else {
			double l;
			l = Math.sqrt(sigmaR * sigmaR
					/ (Math.cos(phiRadians) * Math.cos(phiRadians)) + sigmaT
					* sigmaT / (Math.sin(phiRadians) * Math.sin(phiRadians)));
			width = (int) (n
					* Math.abs(sigmaT * sigmaT / Math.tan(phiRadians) + sigmaR
							* sigmaR * Math.tan(phiRadians)) / l + 0.5);
			l = Math.sqrt(sigmaT * sigmaT
					/ (Math.cos(phiRadians) * Math.cos(phiRadians)) + sigmaR
					* sigmaR / (Math.sin(phiRadians) * Math.sin(phiRadians)));
			height = (int) (n
					* Math.abs(sigmaR * sigmaR / Math.tan(phiRadians) + sigmaT
							* sigmaT * Math.tan(phiRadians)) / l + 0.5);
		}
		double divido = 1. / (2 * Math.PI * sigmaR * sigmaT);
	
		float[] kernelData = new float[(2 * width + 1) * (2 * height + 1)];
		int kernelIndex = 0;
	
		for (int yy = 0; yy < 2 * height + 1; yy++) {
			for (int xx = 0; xx < 2 * width + 1; xx++) {
				if (derivativeT == 2) {
					divido = (Math.pow(
							(Math.cos(phiRadians) * (xx - width) + Math
									.sin(phiRadians) * (yy - height))
									/ sigmaT, 2.0) - 1.)
							/ (2 * Math.PI * sigmaR * sigmaT * sigmaT);
				}
				kernelData[kernelIndex] = (float) (divido * Math.exp(-0.5
						* (Math.pow(
								(Math.cos(phiRadians) * (xx - width) + Math
										.sin(phiRadians) * (yy - height))
										/ sigmaT, 2.0) + Math.pow(
								(-Math.sin(phiRadians) * (xx - width) + Math
										.cos(phiRadians) * (yy - height))
										/ sigmaR, 2.0))));
				kernelIndex++;
			}
		}
	
		Array2dScalarFloat result = new Array2dScalarFloat(2 * width + 1, 2 * height + 1,
				kernelData, false);
		if(PxSystem.initialized()) {
			result.setState(Array2d.LOCAL_FULL);	
		}
		return result;
	}

}
