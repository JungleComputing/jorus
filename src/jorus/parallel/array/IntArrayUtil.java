package jorus.parallel.array;

import jorus.parallel.ArrayUtil;
import jorus.parallel.ReduceOp;

public class IntArrayUtil extends ArrayUtil<int []> {

    @Override
    public int[] clone(int[] array) {
        return array.clone();
    }

    @Override
    public int[] create(int length) {
        return new int[length];
    }

    @Override
    public int getLength(int[] array) {
        return array.length;
    }

    @Override
    public int typeSize() {
        return 4;
    }

    @Override
    public void release(int[] array) {
    }

    @Override
	public void reduce(int[] target, int[] source, int extent,
			ReduceOp reduceOp) throws UnsupportedOperationException {
		reduce(target, source, extent, 0, target.length / extent, reduceOp);
		
	}

	@Override
	public void reduce(int[] target, int[] source, int extent, int offset,
			int pixels, ReduceOp reduceOp) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}

