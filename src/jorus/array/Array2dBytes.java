/*
 *  Copyright (c) 2008, Vrije Universiteit, Amsterdam, The Netherlands.
 *  All rights reserved.
 *
 *  Author(s)
 *  Frank Seinstra	(fjseins@cs.vu.nl)
 *
 */

package jorus.array;

import jorus.operations.bpo.BpoAbsDivByte;
import jorus.operations.bpo.BpoAddByte;
import jorus.operations.bpo.BpoDivByte;
import jorus.operations.bpo.BpoMaxByte;
import jorus.operations.bpo.BpoMinByte;
import jorus.operations.bpo.BpoMulByte;
import jorus.operations.bpo.BpoNegDivByte;
import jorus.operations.bpo.BpoSubByte;
import jorus.operations.bpoval.BpoAbsDivValByte;
import jorus.operations.bpoval.BpoAddValByte;
import jorus.operations.bpoval.BpoDivValByte;
import jorus.operations.bpoval.BpoMaxValByte;
import jorus.operations.bpoval.BpoMinValByte;
import jorus.operations.bpoval.BpoMulValByte;
import jorus.operations.bpoval.BpoNegDivValByte;
import jorus.operations.bpoval.BpoSetValByte;
import jorus.operations.bpoval.BpoSubValByte;
import jorus.operations.svo.SvoAddByte;
import jorus.operations.svo.SvoSetByte;
import jorus.patterns.PatBpo;
import jorus.patterns.PatBpoVal;
import jorus.patterns.PatSvo;
import jorus.pixel.Pixel;

public abstract class Array2dBytes extends Array2d<byte[]> {
	/*** Public Methods ***********************************************/

	public Array2dBytes(Array2dBytes orig, int newBW, int newBH) {
		super(orig, newBW, newBH);
	}

	public Array2dBytes(Array2dBytes orig) {
		super(orig);
	}

	public Array2dBytes(int w, int h, int bw, int bh, int e, boolean create) {
		super(w, h, bw, bh, e, create);
	}

	public Array2dBytes(int w, int h, int bw, int bh, int e, byte[] array,
			boolean copy) {
		super(w, h, bw, bh, e, array, copy);
	}

	/*** Single Pixel (Value) Operations ******************************/

	@Override
	public Array2d<byte[]> setSingleValue(Pixel<byte[]> p, int xidx,
			int yidx, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatSvo.dispatch(this, xidx, yidx, inpl, new SvoSetByte(p
				.getValue()));
	}

	@Override
	public Array2d<byte[]> addSingleValue(Pixel<byte[]> p, int xidx,
			int yidx, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatSvo.dispatch(this, xidx, yidx, inpl, new SvoAddByte(p
				.getValue()));
	}

	/*** Unary Pixel Operations ***************************************/

	/** Binary Pixel Single Value Operations **************************/

	@Override
	public Array2d<byte[]> setVal(Pixel<byte[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoSetValByte(p.getValue()));
	}

	@Override
	public Array2d<byte[]> addVal(Pixel<byte[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoAddValByte(p.getValue()));
	}

	@Override
	public Array2d<byte[]> subVal(Pixel<byte[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoSubValByte(p.getValue()));
	}

	@Override
	public Array2d<byte[]> mulVal(Pixel<byte[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoMulValByte(p.getValue()));
	}

	@Override
	public Array2d<byte[]> divVal(Pixel<byte[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoDivValByte(p.getValue()));
	}

	@Override
	public Array2d<byte[]> minVal(Pixel<byte[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoMinValByte(p.getValue()));
	}

	@Override
	public Array2d<byte[]> maxVal(Pixel<byte[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl, new BpoMaxValByte(p.getValue()));
	}

	@Override
	public Array2d<byte[]> negDivVal(Pixel<byte[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl,
				new BpoNegDivValByte(p.getValue()));
	}

	@Override
	public Array2d<byte[]> absDivVal(Pixel<byte[]> p, boolean inpl) {
		if (!equalExtent(p))
			return null;
		return PatBpoVal.dispatch(this, inpl,
				new BpoAbsDivValByte(p.getValue()));
	}

	/*** Binary Pixel Operations **************************************/

	@Override
	public Array2d<byte[]> add(Array2d<byte[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoAddByte());
	}

	@Override
	public Array2d<byte[]> sub(Array2d<byte[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoSubByte());
	}

	@Override
	public Array2d<byte[]> mul(Array2d<byte[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMulByte());
	}

	@Override
	public Array2d<byte[]> div(Array2d<byte[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoDivByte());
	}

	@Override
	public Array2d<byte[]> min(Array2d<byte[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMinByte());
	}

	@Override
	public Array2d<byte[]> max(Array2d<byte[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoMaxByte());
	}

	@Override
	public Array2d<byte[]> negDiv(Array2d<byte[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoNegDivByte());
	}

	@Override
	public Array2d<byte[]> absDiv(Array2d<byte[]> a, boolean inpl) {
		if (!equalSignature(a))
			return null;
		return PatBpo.dispatch(this, a, inpl, new BpoAbsDivByte());
	}

	@Override
	public final byte[] createDataArray(int size) {
		return new byte[size];
	}

	@Override
	protected final Class<?> getDataType() {
		return byte.class;
	}
}
