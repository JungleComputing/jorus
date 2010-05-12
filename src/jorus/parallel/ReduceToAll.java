package jorus.parallel;

import java.io.IOException;

import jorus.array.Array2d;

public abstract class ReduceToAll<T> extends Collective<T> {

	protected ReduceToAll(PxSystem system, Class<T> c) throws Exception {
		super(system, c);
	}

	public void reduceToAll(Array2d<T> data) throws IOException {
		reduceToAll(data.getPartialDataReadWrite(), data.getExtent(), data
				.getReduceOperation());
	}

	public abstract void reduceToAll(T array, int extent, ReduceOp reduceOp)
			throws IOException;

}
