package jorus.parallel.array;

import jorus.parallel.ArrayUtil;
import jorus.parallel.ReduceOp;

public class FloatArrayUtil extends ArrayUtil<float []> {

    @Override
    public float[] clone(float[] array) {
        return array.clone();
    }

    @Override
    public float[] create(int length) {
        return new float[length];
    }

    @Override
    public int getLength(float[] array) {
        return array.length;
    }

    @Override
    public int typeSize() {
        return 4;
    }

    @Override
    public void release(float[] array) {
    }

    @Override
	public void reduce(float[] target, float[] source, int extent,
			ReduceOp reduceOp) throws UnsupportedOperationException {
		reduce(target, source, extent, 0, target.length / extent, reduceOp);
		
	}

	@Override
	public void reduce(float[] target, float[] source, int extent, int offset,
			int pixels, ReduceOp reduceOp) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}
