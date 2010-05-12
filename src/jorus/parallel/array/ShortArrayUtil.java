package jorus.parallel.array;

import jorus.parallel.ArrayUtil;
import jorus.parallel.ReduceOp;

public class ShortArrayUtil extends ArrayUtil<short []> {

    @Override
    public short[] clone(short[] array) {
        return array.clone();
    }

    @Override
    public short[] create(int length) {
        return new short[length];
    }

    @Override
    public int getLength(short[] array) {
        return array.length;
    }

    @Override
    public int typeSize() {
        return 8;
    }

    @Override
    public void release(short[] array) {
    }

    @Override
	public void reduce(short[] target, short[] source, int extent,
			ReduceOp reduceOp) throws UnsupportedOperationException {
		reduce(target, source, extent, 0, target.length / extent, reduceOp);
		
	}

	@Override
	public void reduce(short[] target, short[] source, int extent, int offset,
			int pixels, ReduceOp reduceOp) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}
