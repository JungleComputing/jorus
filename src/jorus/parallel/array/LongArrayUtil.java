package jorus.parallel.array;

import jorus.parallel.ArrayUtil;
import jorus.parallel.ReduceOp;

public class LongArrayUtil extends ArrayUtil<long []> {

    @Override
    public long[] clone(long[] array) {
        return array.clone();
    }

    @Override
    public long[] create(int length) {
        return new long[length];
    }

    @Override
    public int getLength(long[] array) {
        return array.length;
    }

    @Override
    public int typeSize() {
        return 8;
    }

    @Override
    public void release(long[] array) {
    }

    @Override
	public void reduce(long[] target, long[] source, int extent,
			ReduceOp reduceOp) throws UnsupportedOperationException {
		reduce(target, source, extent, 0, target.length / extent, reduceOp);
		
	}

	@Override
	public void reduce(long[] target, long[] source, int extent, int offset,
			int pixels, ReduceOp reduceOp) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}

