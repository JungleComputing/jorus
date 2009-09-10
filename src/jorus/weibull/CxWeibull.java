/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra		(fjseins@cs.vu.nl;    Java implementation)
 *  Jan-Mark Geusebroek	(mark@science.uva.nl; C++  implementation)
 *
 */

package jorus.weibull;

import jorus.array.CxArray2dDoubles;
import jorus.array.CxConvert;
import jorus.pixel.CxPixelDouble;
import jorus.util.ConvertableImage;
import jorus.util.FeatureVector;
import jorus.util.RGB24Image;

public class CxWeibull {
    private static int NR_INVAR_IMS = 13;

    // will be derived from (NR_INVAR_IMS - 1) / 2 in initialize;
    private static int NR_INVARS;

    private static int NR_RINGS = 4;

    private static int DENSITY = 1;

    private static int RADIAL_DENSITY = 6;

    // will be derived from NR_RINGS and RADIAL_DENSITY in initialize
    private static int NR_RFIELDS;

    private static int NR_BINS = 1000;

    private static int EX2EY2 = 0;

    private static int WX = 1;

    private static int WR1 = 2;

    private static int WY = 3;

    private static int WR2 = 4;

    private static int CLX = 5;

    private static int CLR1 = 6;

    private static int CLY = 7;

    private static int CLR2 = 8;

    private static int CLLX = 9;

    private static int CLLR1 = 10;

    private static int CLLY = 11;

    private static int CLLR2 = 12;

    private static CxArray2dDoubles Wx, W45, Wy, W135;

    private static CxArray2dDoubles Clx, Cl45, Cly, Cl135;

    private static CxArray2dDoubles Cllx, Cll45, Clly, Cll135;

    private static CxArray2dDoubles Ex2Ey2 = null;

    private static CxArray2dDoubles[] rfIm = new CxArray2dDoubles[NR_RFIELDS];

    private static double[][][] histos;

    private static double[][] betas;

    private static double[][] gammas;

    private static double[][] resbetas;

    private static double[][] resgammas;

    private static boolean initialized = false;

    /** * Public Methods ********************************************** */

    /**
     * Initializes the receptive fields by applying a Gaussian distribution for
     * each field. Initialization may be a time intensive task.
     * 
     * @param imageWidth
     *                the width of the images that are going to be used
     * @param imageHeight
     *                the height of the images that are going to be used
     */
    public static void initialize(int imageWidth, int imageHeight) {
        initialize(imageWidth, imageHeight, NR_INVARS, NR_BINS, NR_RINGS,
                RADIAL_DENSITY);
    }

    /**
     * Initializes the receptive fields by applying a Gaussian distribution for
     * each field. Initialization may be a time intensive task.
     * 
     * @param imageWidth
     *                the width of the images that are going to be used
     * @param imageHeight
     *                the height of the images that are going to be used
     * @param numberOfInvars
     *                the number of invariants that should be used
     * @param numberOfBins
     *                the number of bins that should be used for the histograms
     * @param numberOfRings
     *                the number of rings with receptive fields
     * @param radialDensity
     *                the density of a ring. The n-th ring contains n *
     *                radialDensity receptive fields (the zero-th ring always
     *                contains a 1 receptive field)
     */
    public static void initialize(int imageWidth, int imageHeight,
            int numberOfInvars, int numberOfBins, int numberOfRings,
            int radialDensity) {
        NR_INVARS = numberOfInvars;
        NR_INVAR_IMS = NR_INVARS * 2 + 1;
        NR_RINGS = numberOfRings;
        RADIAL_DENSITY = radialDensity;
        NR_RFIELDS = 1; // there's always one circle
        for (int i = 0; i < NR_RINGS; i++) {
            NR_RFIELDS += i * RADIAL_DENSITY;
        }
        NR_BINS = numberOfBins;

        rfIm = new CxArray2dDoubles[NR_RFIELDS];
        histos = new double[NR_INVAR_IMS][NR_RFIELDS][NR_BINS];
        betas = new double[NR_INVAR_IMS][NR_RFIELDS];
        gammas = new double[NR_INVAR_IMS][NR_RFIELDS];
        resbetas = new double[NR_INVARS][NR_RFIELDS];
        resgammas = new double[NR_INVARS][NR_RFIELDS];

        // Create central Gaussian at high resolution

        double sigma = 30.0 * (double) imageHeight / 576;
        int centerx = imageWidth / 2;
        int centery = imageHeight / 2;

        CxArray2dDoubles pntIm = new CxArray2dDoubles(imageWidth,
                imageHeight, 1);
        CxPixelDouble zero = new CxPixelDouble(new double[] { 0. });
        CxPixelDouble one = new CxPixelDouble(new double[] { 1. });
        pntIm.setVal(zero, true);
        pntIm.setSingleValue(one, centerx, centery, true);
        rfIm[0] = pntIm.gaussDerivative(sigma, 0, 0, 5.);
        pntIm.setSingleValue(zero, centerx, centery, true);

        // Create remaining Gaussians in circular fashion around center

        int idx = 1;
        double r = DENSITY * sigma;

        for (int n = 0; n < NR_RINGS; n++) {
            r += DENSITY * sigma;
            for (int phi = 0; phi < (n + 1) * RADIAL_DENSITY; phi++) {
                double xp = r
                        * Math.cos(2 * Math.PI * phi
                                / ((n + 1) * RADIAL_DENSITY));
                double yp = -r
                        * Math.sin(2 * Math.PI * phi
                                / ((n + 1) * RADIAL_DENSITY));
                int x = (int) (centerx + xp);
                int y = (int) (centery + yp);
                pntIm.setSingleValue(one, x, y, true);
                rfIm[idx] = pntIm.gaussDerivative(sigma, 0, 0, 5.);
                pntIm.setSingleValue(zero, x, y, true);
                idx++;
            }
            r += DENSITY * sigma;
        }
        initialized = true;
    }

    /**
     * Calculates a feature vector from this image.
     * 
     * @param image
     *                the source image
     * @return the calculated feature vector
     */
    public static FeatureVector calculateFeatureVector(ConvertableImage image) {
        RGB24Image rgb24Image = image.toRGB24();

        if (!initialized) {
            initialize(rgb24Image.width, rgb24Image.height);
        }

        // Create CxArray2d from file image data
        CxArray2dDoubles input = new CxArray2dDoubles(rgb24Image.width,
                rgb24Image.height, 3, CxConvert.toDoubles(rgb24Image.pixels));

        // Create all invariant images
        buildInvariantImages(input);

        // Initialize histos, betas, and gammas
        for (int j = 0; j < NR_INVAR_IMS; j++) {
            for (int i = 0; i < NR_RFIELDS; i++) {
                betas[j][i] = 0.;
                gammas[j][i] = 0.;
                for (int k = 0; k < NR_BINS; k++) {
                    histos[j][i][k] = 0.;
                }
            }
        }

        // Calculate all histograms
        for (int i = 0; i < NR_RFIELDS; i++) {
            histos[EX2EY2][i] = Ex2Ey2.impreciseHistogram(rfIm[i], NR_BINS, 0.,
                    1.);

            histos[WX][i] = Wx.impreciseHistogram(rfIm[i], NR_BINS, -1., 1.);
            histos[WR1][i] = W45.impreciseHistogram(rfIm[i], NR_BINS, -1., 1.);
            histos[WY][i] = Wy.impreciseHistogram(rfIm[i], NR_BINS, -1., 1.);
            histos[WR2][i] = W135.impreciseHistogram(rfIm[i], NR_BINS, -1., 1.);

            histos[CLX][i] = Clx.impreciseHistogram(rfIm[i], NR_BINS, -1., 1.);
            histos[CLR1][i] = Cl45
                    .impreciseHistogram(rfIm[i], NR_BINS, -1., 1.);
            histos[CLY][i] = Cly.impreciseHistogram(rfIm[i], NR_BINS, -1., 1.);
            histos[CLR2][i] = Cl135.impreciseHistogram(rfIm[i], NR_BINS, -1.,
                    1.);

            histos[CLLX][i] = Cllx
                    .impreciseHistogram(rfIm[i], NR_BINS, -1., 1.);
            histos[CLLR1][i] = Cll45.impreciseHistogram(rfIm[i], NR_BINS, -1.,
                    1.);
            histos[CLLY][i] = Clly
                    .impreciseHistogram(rfIm[i], NR_BINS, -1., 1.);
            histos[CLLR2][i] = Cll135.impreciseHistogram(rfIm[i], NR_BINS, -1.,
                    1.);
        }

        // Calculate all Weibull fits
        CxWeibullFit fit = new CxWeibullFit(NR_RFIELDS, NR_INVAR_IMS);
        fit.init(new FitWeibull(), histos, betas, gammas, NR_BINS);
        fit.dispatch();

        // Extract all final betas and gammas (ignore EX2EY2)
        double xxb, xyb, yyb;
        double xxg, xyg, yyg;

        for (int j = 1; j < NR_INVAR_IMS; j += 4) {
            for (int i = 0; i < NR_RFIELDS; i++) {
                xxb = 0.;
                xyb = 0.;
                yyb = 0.;
                xxg = 0.;
                xyg = 0.;
                yyg = 0.;

                xxb += betas[j][i];
                yyb += betas[j + 2][i];
                xxg += gammas[j][i];
                yyg += gammas[j + 2][i];

                xxb += 0.5 * betas[j + 1][i] * Math.sqrt(2.0);
                xyb += 0.5 * betas[j + 1][i] * Math.sqrt(2.0);
                yyb += 0.5 * betas[j + 1][i] * Math.sqrt(2.0);

                xxg += 0.5 * gammas[j + 1][i] * Math.sqrt(2.0);
                xyg += 0.5 * gammas[j + 1][i] * Math.sqrt(2.0);
                yyg += 0.5 * gammas[j + 1][i] * Math.sqrt(2.0);

                xyb -= 0.5 * betas[j + 3][i] * Math.sqrt(2.0);
                xxb += 0.5 * betas[j + 3][i] * Math.sqrt(2.0);
                yyb += 0.5 * betas[j + 3][i] * Math.sqrt(2.0);

                xyg -= 0.5 * gammas[j + 3][i] * Math.sqrt(2.0);
                xxg += 0.5 * gammas[j + 3][i] * Math.sqrt(2.0);
                yyg += 0.5 * gammas[j + 3][i] * Math.sqrt(2.0);

                double H = 0.5 * (xxb + yyb);
                double D = H * H - xxb * yyb + xyb * xyb;
                double Ia = H + Math.sqrt(D);
                double Ib = H - Math.sqrt(D);
                resbetas[((j - 1) / 2)][i] = 2.0 * Ia / 4.;
                resbetas[((j - 1) / 2) + 1][i] = 2.0 * Ib / 4.;

                H = 0.5 * (xxg + yyg);
                D = H * H - xxg * yyg + xyg * xyg;
                Ia = H + Math.sqrt(D);
                Ib = H - Math.sqrt(D);
                resgammas[((j - 1) / 2)][i] = 2.0 * Ia / 4.;
                resgammas[((j - 1) / 2) + 1][i] = 2.0 * Ib / 4.;
            }
        }

        // Make invalid all parameters that are not finite or too large

        boolean[] invalids = new boolean[NR_RFIELDS];
        for (int i = 0; i < NR_RFIELDS; i++) {
            invalids[i] = false;
        }
        for (int j = 0; j < NR_INVARS; j++) {
            for (int i = 0; i < NR_RFIELDS; i++) {
                if (Double.isNaN(resbetas[j][i])
                        || Double.isInfinite(resbetas[j][i])
                        || resbetas[j][i] > 100.
                        || Double.isNaN(resgammas[j][i])
                        || Double.isInfinite(resgammas[j][i])
                        || resgammas[j][i] > 100.) {
                    invalids[i] = true;
                }
            }
        }

        // Finalize the set of Weibull parameters and... done!
        FeatureVector result = new FeatureVector(NR_INVARS, NR_RFIELDS);
        for (int i = 0; i < NR_RFIELDS; i++) {
            for (int j = 0; j < NR_INVARS; j++) {
                if (invalids[i]) {
                    resgammas[j][i] = 100.;
                    resbetas[j][i] = 100.;
                }
                result.vector[2 * (i * NR_INVARS + j)] = resgammas[j][i];
                result.vector[2 * (i * NR_INVARS + j) + 1] = resbetas[j][i];
            }
        }
        return result;
    }

    /** * Private Methods********************************************** */

    /**
     * r
     * @param input input image. Should have an extent of 1
     */
    private static void buildInvariantImages(CxArray2dDoubles input) {
    	if(input.getExtent() != 1) {
    		return; //FIXME error handling
    	}
    	
        double s = 1.0; // sigma

        // Convert to opponent color space (and stretch)

        input.convertRGB2OOO(true);
        input.mulVal(new CxPixelDouble(new double[] { 255., 255., 255. }),
                true);

        CxArray2dDoubles plane = input.getPlane(0);
        CxArray2dDoubles E = plane.gaussDerivative(s, 0, 0, 3.);
        CxArray2dDoubles Ex = plane.gaussDerivative(s, 1, 0, 3.);
        CxArray2dDoubles Ey = plane.gaussDerivative(s, 0, 1, 3.);

        plane = input.getPlane(1);
        CxArray2dDoubles El = plane.gaussDerivative(s, 0, 0, 3.);
        CxArray2dDoubles Elx = plane.gaussDerivative(s, 1, 0, 3.);
        CxArray2dDoubles Ely = plane.gaussDerivative(s, 0, 1, 3.);

        plane = input.getPlane(2);
        CxArray2dDoubles Ell = plane.gaussDerivative(s, 0, 0, 3.);
        CxArray2dDoubles Ellx = plane.gaussDerivative(s, 1, 0, 3.);
        CxArray2dDoubles Elly = plane.gaussDerivative(s, 0, 1, 3.);

        // Intensity contrast

        Wx = (CxArray2dDoubles) Ex.div(E, false);
        Wy = (CxArray2dDoubles) Ey.div(E, false);
        W45 = (CxArray2dDoubles) Wx.add(Wy, false);
        W135 = (CxArray2dDoubles) Wx.sub(Wy, false);

        // Chromatic C invarient

        CxArray2dDoubles E2;

        E2 = (CxArray2dDoubles) E.mul(E, false);
        Clx = (CxArray2dDoubles) El.mul(Ex, false);
        Elx.mul(E, true);
        Clx = (CxArray2dDoubles) Elx.sub(Clx, false);
        Clx.div(E2, true);

        Cly = (CxArray2dDoubles) El.mul(Ey, false);
        Ely.mul(E, true);
        Cly = (CxArray2dDoubles) Ely.sub(Cly, false);
        Cly.div(E2, true);

        Cllx = (CxArray2dDoubles) Ell.mul(Ex, false);
        Ellx.mul(E, true);
        Cllx = (CxArray2dDoubles) Ellx.sub(Cllx, false);
        Cllx.div(E2, true);

        Clly = (CxArray2dDoubles) Ell.mul(Ey, false);
        Elly.mul(E, true);
        Clly = (CxArray2dDoubles) Elly.sub(Clly, false);
        Clly.div(E2, true);

        Cl45 = (CxArray2dDoubles) Clx.add(Cly, false);
        Cl135 = (CxArray2dDoubles) Clx.sub(Cly, false);
        Cll45 = (CxArray2dDoubles) Cllx.add(Clly, false);
        Cll135 = (CxArray2dDoubles) Cllx.sub(Clly, false);

        // Squared gradient

        Ex.mul(Ex, true);
        Ey.mul(Ey, true);
        Ex2Ey2 = (CxArray2dDoubles) Ey.add(Ex, false);
    }

}
