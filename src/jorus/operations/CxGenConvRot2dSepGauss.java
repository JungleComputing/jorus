/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.operations;

public class CxGenConvRot2dSepGauss extends CxGenConvRot2dSep<double[]> {
	// NOTE: As these methods are only used inside our library we can
	// be a little bit less safe. Here we assume 'dst' and 'src' to be
	// of identical signature, such that 'offset' and 'stride' apply
	// to both these structures.

	public void doIt(double[] dst, double[] src, double[] ker) {
		int dstPtr, srcPtr, kerPtr;
	    int pup, ppup, pdown, ppdown;
	    
	    double val;
		
	    int i, j, jj;
	    
	    dstPtr = 0;
		srcPtr = 0;

	    for (i=0; i< imHeight; i++) {
			kerPtr = halfKerSize;		// only half of kernel used

			/*** start at center of filter ***/

			pup = srcPtr + (i+borHeight)*srcWidth + borWidth; //TODO extent
			// TODO loop over extent
			val = ker[kerPtr];
			for (j=0; j<imWidth; j++) {
				dst[dstPtr] = val * src[pup];
				pup++;
				dstPtr++;
			}
			//end loop over extent

			/*** do rest of filter ***/

			for (jj = 1; jj <=halfKerSize; jj++) {
				double	xf = Math.abs(cosTheta)*jj;
				double	yf = Math.abs(sinTheta)*jj;
				int		x = (int)xf;
				int		y = (int)yf;
				double	a = xf-(double)x;
				double	b = 1.0-a;
				double	c = yf-(double)y;
				double	d = 1.0-c;
				double	pu, pd, ppu, ppd;
				double	vu, vd, vvu, vvd;

				pd = d;
				d = b*c;
				c = a*c;
				a = a*pd;
				b = b*pd;

				dstPtr -= imWidth;
				kerPtr++;

				if (sinTheta/cosTheta <= 0.0) {
					pup    = srcPtr + (i+borHeight+y)*srcWidth   + borWidth;
					pdown  = srcPtr + (i+borHeight-y)*srcWidth   + borWidth;
					ppup   = srcPtr + (i+borHeight+y+1)*srcWidth + borWidth;
					ppdown = srcPtr + (i+borHeight-y-1)*srcWidth + borWidth;
				} else {
					pdown  = srcPtr + (i+borHeight+y)*srcWidth   + borWidth;
					pup    = srcPtr + (i+borHeight-y)*srcWidth   + borWidth;
					ppdown = srcPtr + (i+borHeight+y+1)*srcWidth + borWidth;
					ppup   = srcPtr + (i+borHeight-y-1)*srcWidth + borWidth;
				}

				a *= ker[kerPtr];
				b *= ker[kerPtr];
				c *= ker[kerPtr];
				d *= ker[kerPtr];

				/*** do the real filtering ***/

				pup    += x;     ppup   += x;
				pdown  -= (x+1); ppdown -= (x+1);
				pu  = src[pup];
				ppu = src[ppup];
				pd  = src[pdown];
				ppd = src[ppdown];
				pup++;   ppup++;
				pdown++; ppdown++;

				for (j=0; j<imWidth; j++) {
					vu  = src[pup];
					vvu = src[ppup];
					vd  = src[pdown];
					vvd = src[ppdown];
					pup++;   ppup++;
					pdown++; ppdown++;
					dst[dstPtr] += a*(vu+pd)+b*(pu+vd)+c*(vvu+ppd)+d*(ppu+vvd);
					dstPtr++;
					pu = vu; ppu = vvu;
					pd = vd; ppd = vvd;
				}
			}
	    }
	}
}
