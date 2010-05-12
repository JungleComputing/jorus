package jorus.parallel.array;

import jorus.parallel.ArrayUtil;
import jorus.parallel.ReduceOp;

public class ByteArrayUtil extends ArrayUtil<byte[]> {

    @Override
    public byte[] clone(byte[] array) {
        return array.clone();
    }

    @Override
    public byte[] create(int length) {
        return new byte[length];
    }

    @Override
    public int getLength(byte[] array) {
        return array.length;
    }

    @Override
    public int typeSize() {
        return 1;
    }

    @Override
    public void release(byte[] array) {
    }

	@Override
	public void reduce(byte[] target, byte[] source, int extent,
			ReduceOp reduceOp) throws UnsupportedOperationException {
		reduce(target, source, extent, 0, target.length / extent, reduceOp);
		
	}

	@Override
	public void reduce(byte[] target, byte[] source, int extent, int offset,
			int pixels, ReduceOp reduceOp) throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}
