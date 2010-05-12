package jorus.parallel;

import java.io.IOException;

import jorus.array.Array2d;

public abstract class ReduceToRoot<T> extends Collective<T> {

	protected ReduceToRoot(PxSystem system, Class<T> c) throws Exception {
		super(system, c);
	}

	public void reduceToRoot(Array2d<T> data) throws IOException {
		reduceToRoot(data.getPartialDataReadWrite(), data.getExtent(), data
				.getReduceOperation());
	}

	public abstract void reduceToRoot(T array, int extent, ReduceOp reduceOp) throws IOException;
}
