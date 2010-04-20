/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

import jorus.operations.bpo.BpoAbsDivLong;
import jorus.operations.bpo.BpoAddLong;
import jorus.operations.bpo.BpoDivLong;
import jorus.operations.bpo.BpoMaxLong;
import jorus.operations.bpo.BpoMinLong;
import jorus.operations.bpo.BpoMulLong;
import jorus.operations.bpo.BpoNegDivLong;
import jorus.operations.bpo.BpoSubLong;
import jorus.operations.bpoval.BpoAbsDivValLong;
import jorus.operations.bpoval.BpoAddValLong;
import jorus.operations.bpoval.BpoDivValLong;
import jorus.operations.bpoval.BpoMaxValLong;
import jorus.operations.bpoval.BpoMinValLong;
import jorus.operations.bpoval.BpoMulValLong;
import jorus.operations.bpoval.BpoNegDivValLong;
import jorus.operations.bpoval.BpoSetValLong;
import jorus.operations.bpoval.BpoSubValLong;
import jorus.operations.svo.SvoAddLong;
import jorus.operations.svo.SvoSetLong;
import jorus.patterns.PatBpo;
import jorus.patterns.PatBpoVal;
import jorus.patterns.PatSvo;
import jorus.pixel.Pixel;

public abstract class Array2dLongs extends Array2d<long[]> {
	/*** Public Methods ***********************************************/
	public Array2dLongs(Array2dLongs orig, int newBW, int newBH) {
		super(orig, newBW, newBH);
	}

	public Array2dLongs(Array2dLongs orig) {
		super(orig);
	}

	public Array2dLongs(int w, int h, int bw, int bh, int e, boolean create) {
		super(w, h, bw, bh, e, create);
	}

	public Array2dLongs(int w, int h, int bw, int bh, int e, long[] array,
			boolean copy) {
		super(w, h, bw, bh, e, array, copy);
	}

	/*** Single Pixel (Value) Operations ******************************/

	public Array2d<long[]> setSingleValue(Pixel<long[]> p, int xidx,
			int yidx, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatSvo.dispatch(this, xidx, yidx, inpl, new SvoSetLong(
				(long[]) p.getValue()));
	}

	public Array2d<long[]> addSingleValue(Pixel<long[]> p, int xidx,
			int yidx, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatSvo.dispatch(this, xidx, yidx, inpl, new SvoAddLong(
				(long[]) p.getValue()));
	}

	/*** Unary Pixel Operations ***************************************/

	/** Binary Pixel Single Value Operations **************************/

	@Override
	public Array2d<long[]> setVal(Pixel<long[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoSetValLong(p.getValue()));
	}

	@Override
	public Array2d<long[]> addVal(Pixel<long[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoAddValLong(p.getValue()));
	}

	@Override
	public Array2d<long[]> subVal(Pixel<long[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoSubValLong(p.getValue()));
	}

	@Override
	public Array2d<long[]> mulVal(Pixel<long[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoMulValLong(p.getValue()));
	}

	@Override
	public Array2d<long[]> divVal(Pixel<long[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoDivValLong(p.getValue()));
	}

	@Override
	public Array2d<long[]> minVal(Pixel<long[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoMinValLong(p.getValue()));
	}

	@Override
	public Array2d<long[]> maxVal(Pixel<long[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoMaxValLong(p.getValue()));
	}

	@Override
	public Array2d<long[]> negDivVal(Pixel<long[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoNegDivValLong(p
				.getValue()));
	}

	@Override
	public Array2d<long[]> absDivVal(Pixel<long[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoAbsDivValLong(p
				.getValue()));
	}
	
	/*** Binary Pixel Operations **************************************/

	public Array2d<long[]> add(Array2d<long[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoAddLong());
	}

	public Array2d<long[]> sub(Array2d<long[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoSubLong());
	}

	public Array2d<long[]> mul(Array2d<long[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMulLong());
	}

	public Array2d<long[]> div(Array2d<long[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoDivLong());
	}

	public Array2d<long[]> min(Array2d<long[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMinLong());
	}

	public Array2d<long[]> max(Array2d<long[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMaxLong());
	}

	public Array2d<long[]> negDiv(Array2d<long[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoNegDivLong());
	}
	
	public Array2d<long[]> absDiv(Array2d<long[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoAbsDivLong());
	}

	public long[] createDataArray(int size) {
		return new long[size];
	}

	protected Class<?> getDataType() {
		return long.class;
	}
}
