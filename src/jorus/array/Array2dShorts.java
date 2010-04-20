/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

import jorus.operations.bpo.BpoAbsDivShort;
import jorus.operations.bpo.BpoAddShort;
import jorus.operations.bpo.BpoDivShort;
import jorus.operations.bpo.BpoMaxShort;
import jorus.operations.bpo.BpoMinShort;
import jorus.operations.bpo.BpoMulShort;
import jorus.operations.bpo.BpoNegDivShort;
import jorus.operations.bpo.BpoSubShort;
import jorus.operations.bpoval.BpoAbsDivValShort;
import jorus.operations.bpoval.BpoAddValShort;
import jorus.operations.bpoval.BpoDivValShort;
import jorus.operations.bpoval.BpoMaxValShort;
import jorus.operations.bpoval.BpoMinValShort;
import jorus.operations.bpoval.BpoMulValShort;
import jorus.operations.bpoval.BpoNegDivValShort;
import jorus.operations.bpoval.BpoSetValShort;
import jorus.operations.bpoval.BpoSubValShort;
import jorus.operations.svo.SvoAddShort;
import jorus.operations.svo.SvoSetShort;
import jorus.patterns.PatBpo;
import jorus.patterns.PatBpoVal;
import jorus.patterns.PatSvo;
import jorus.pixel.Pixel;

public abstract class Array2dShorts extends Array2d<short[]> {
	/*** Public Methods ***********************************************/
	public Array2dShorts(Array2dShorts orig, int newBW, int newBH) {
		super(orig, newBW, newBH);
	}

	public Array2dShorts(Array2dShorts orig) {
		super(orig);
	}

	public Array2dShorts(int w, int h, int bw, int bh, int e, boolean create) {
		super(w, h, bw, bh, e, create);
	}

	public Array2dShorts(int w, int h, int bw, int bh, int e, short[] array,
			boolean copy) {
		super(w, h, bw, bh, e, array, copy);
	}

	/*** Single Pixel (Value) Operations ******************************/

	@Override
	public Array2d<short[]> setSingleValue(Pixel<short[]> p, int xidx,
			int yidx, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatSvo.dispatch(this, xidx, yidx, inpl, new SvoSetShort(p
				.getValue()));
	}

	@Override
	public Array2d<short[]> addSingleValue(Pixel<short[]> p, int xidx,
			int yidx, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatSvo.dispatch(this, xidx, yidx, inpl, new SvoAddShort(p
				.getValue()));
	}

	/*** Unary Pixel Operations ***************************************/

	
	/** Binary Pixel Single Value Operations **************************/

	@Override
	public Array2d<short[]> setVal(Pixel<short[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoSetValShort(p.getValue()));
	}

	@Override
	public Array2d<short[]> addVal(Pixel<short[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoAddValShort(p.getValue()));
	}

	@Override
	public Array2d<short[]> subVal(Pixel<short[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoSubValShort(p.getValue()));
	}

	@Override
	public Array2d<short[]> mulVal(Pixel<short[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoMulValShort(p.getValue()));
	}

	@Override
	public Array2d<short[]> divVal(Pixel<short[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoDivValShort(p.getValue()));
	}

	@Override
	public Array2d<short[]> minVal(Pixel<short[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoMinValShort(p.getValue()));
	}

	@Override
	public Array2d<short[]> maxVal(Pixel<short[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoMaxValShort(p.getValue()));
	}

	@Override
	public Array2d<short[]> negDivVal(Pixel<short[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoNegDivValShort(p
				.getValue()));
	}

	@Override
	public Array2d<short[]> absDivVal(Pixel<short[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoAbsDivValShort(p
				.getValue()));
	}

	/*** Binary Pixel Operations **************************************/

	@Override
	public Array2d<short[]> add(Array2d<short[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoAddShort());
	}

	@Override
	public Array2d<short[]> sub(Array2d<short[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoSubShort());
	}

	@Override
	public Array2d<short[]> mul(Array2d<short[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMulShort());
	}

	@Override
	public Array2d<short[]> div(Array2d<short[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoDivShort());
	}

	@Override
	public Array2d<short[]> min(Array2d<short[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMinShort());
	}

	@Override
	public Array2d<short[]> max(Array2d<short[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMaxShort());
	}

	@Override
	public Array2d<short[]> negDiv(Array2d<short[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoNegDivShort());
	}
	
	@Override
	public Array2d<short[]> absDiv(Array2d<short[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoAbsDivShort());
	}

	@Override
	public short[] createDataArray(int size) {
		return new short[size];
	}

	@Override
	protected Class<?> getDataType() {
		return short.class;
	}
}
