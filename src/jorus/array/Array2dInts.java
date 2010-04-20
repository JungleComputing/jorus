/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */


package jorus.array;


import jorus.operations.bpo.BpoAbsDivInt;
import jorus.operations.bpo.BpoAddInt;
import jorus.operations.bpo.BpoDivInt;
import jorus.operations.bpo.BpoMaxInt;
import jorus.operations.bpo.BpoMinInt;
import jorus.operations.bpo.BpoMulInt;
import jorus.operations.bpo.BpoNegDivInt;
import jorus.operations.bpo.BpoSubInt;
import jorus.operations.bpoval.BpoAbsDivValInt;
import jorus.operations.bpoval.BpoAddValInt;
import jorus.operations.bpoval.BpoDivValInt;
import jorus.operations.bpoval.BpoMaxValInt;
import jorus.operations.bpoval.BpoMinValInt;
import jorus.operations.bpoval.BpoMulValInt;
import jorus.operations.bpoval.BpoNegDivValInt;
import jorus.operations.bpoval.BpoSetValInt;
import jorus.operations.bpoval.BpoSubValInt;
import jorus.operations.svo.SvoAddInt;
import jorus.operations.svo.SvoSetInt;
import jorus.patterns.PatBpo;
import jorus.patterns.PatBpoVal;
import jorus.patterns.PatSvo;
import jorus.pixel.Pixel;


public abstract class Array2dInts extends Array2d<int[]>
{
    /*** Public Methods ***********************************************/
    public Array2dInts(Array2dInts orig, int newBW, int newBH) {
        super(orig, newBW, newBH);
    }

    public Array2dInts(Array2dInts orig) {
        super(orig);
    }

    public Array2dInts(int w, int h, int bw, int bh, int e, boolean create) {
        super(w, h, bw, bh, e, create);
    }

    public Array2dInts(int w, int h, int bw, int bh, int e, int[] array, boolean copy) {
        super(w, h, bw, bh, e, array, copy);
    }

    /*** Single Pixel (Value) Operations ******************************/

    @Override
    public Array2d<int[]> setSingleValue(Pixel<int[]> p,
            int xidx, int yidx, boolean inpl)
    {
        if (!equalExtent(p)) return null;
        return PatSvo.dispatch(this, xidx, yidx, inpl,
                new SvoSetInt(p.getValue()));
    }
    
    @Override
    public Array2d<int[]> addSingleValue(Pixel<int[]> p,
            int xidx, int yidx, boolean inpl)
    {
        if (!equalExtent(p)) return null;
        return PatSvo.dispatch(this, xidx, yidx, inpl,
                new SvoAddInt(p.getValue()));
    }


    /*** Unary Pixel Operations ***************************************/

	/** Binary Pixel Single Value Operations **************************/

	@Override
	public Array2d<int[]> setVal(Pixel<int[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoSetValInt(p.getValue()));
	}

	@Override
	public Array2d<int[]> addVal(Pixel<int[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoAddValInt(p.getValue()));
	}

	@Override
	public Array2d<int[]> subVal(Pixel<int[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoSubValInt(p.getValue()));
	}

	@Override
	public Array2d<int[]> mulVal(Pixel<int[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoMulValInt(p.getValue()));
	}

	@Override
	public Array2d<int[]> divVal(Pixel<int[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoDivValInt(p.getValue()));
	}

	@Override
	public Array2d<int[]> minVal(Pixel<int[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoMinValInt(p.getValue()));
	}

	@Override
	public Array2d<int[]> maxVal(Pixel<int[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoMaxValInt(p.getValue()));
	}

	@Override
	public Array2d<int[]> negDivVal(Pixel<int[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoNegDivValInt(p
				.getValue()));
	}

	@Override
	public Array2d<int[]> absDivVal(Pixel<int[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoAbsDivValInt(p
				.getValue()));
	}
	
    /*** Binary Pixel Operations **************************************/
    
    @Override
    public Array2d<int[]> add(Array2d<int[]> a, boolean inpl)
    {
        if (!equalSignature(a)) return null;
        return PatBpo.dispatch(this, a, inpl, new BpoAddInt());
    }

    @Override
    public Array2d<int[]> sub(Array2d<int[]> a, boolean inpl)
    {
        if (!equalSignature(a)) return null;
        return PatBpo.dispatch(this, a, inpl, new BpoSubInt());
    }

    @Override
    public Array2d<int[]> mul(Array2d<int[]> a, boolean inpl)
    {
        if (!equalSignature(a)) return null;
        return PatBpo.dispatch(this, a, inpl, new BpoMulInt());
    }

    @Override
    public Array2d<int[]> div(Array2d<int[]> a, boolean inpl)
    {
        if (!equalSignature(a)) return null;
        return PatBpo.dispatch(this, a, inpl, new BpoDivInt());
    }
    
    @Override
    public Array2d<int[]> min(Array2d<int[]> a, boolean inpl)
	{
		if (!equalSignature(a)) return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMinInt());
	}
    
    @Override
    public Array2d<int[]> max(Array2d<int[]> a, boolean inpl)
	{
		if (!equalSignature(a)) return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMaxInt());
	}
	
    @Override
	public Array2d<int[]> negDiv(Array2d<int[]> a, boolean inpl)
	{
		if (!equalSignature(a)) return null;
		return PatBpo.dispatch(this, a, inpl, new BpoNegDivInt());
	}
	
	@Override
	public Array2d<int[]> absDiv(Array2d<int[]> a, boolean inpl)
	{
		if (!equalSignature(a)) return null;
		return PatBpo.dispatch(this, a, inpl, new BpoAbsDivInt());
	}
	
	@Override
    public int[] createDataArray(int size) { 
        return new int[size];
    }
   
	@Override
    protected Class<?> getDataType() { 
        return int.class;
    }
}
