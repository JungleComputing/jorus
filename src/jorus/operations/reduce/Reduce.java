package jorus.operations.reduce;

import jorus.array.Array2d;
import jorus.parallel.ReduceOp;

public abstract class Reduce<T> {
	protected int width = 0;
	protected int height = 0;
	protected int offset = 0;
	protected int stride = 0;
	protected int extent = 0;
	protected int rowWidth = 0;

	public void init(Array2d<T,?> s1, boolean parallel) {
		int bw1 = s1.getBorderWidth();

		width = parallel ? s1.getPartialWidth() : s1.getWidth();
		extent = s1.getExtent();
		height = parallel ? s1.getPartialHeight() : s1.getHeight();
		stride = bw1 * extent * 2;
		rowWidth = width * extent + stride;
		offset = rowWidth * s1.getBorderHeight() + bw1 * extent;
	}

	public abstract void doIt(T dst, T src);
	
	public abstract ReduceOp getOpcode();
}
