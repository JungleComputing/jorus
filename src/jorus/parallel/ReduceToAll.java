package jorus.parallel;

import java.io.IOException;

import jorus.array.Array2d;

public abstract class ReduceToAll<T> extends Collective<T> {

	protected ReduceToAll(PxSystem system, Class<T> c) throws Exception {
		super(system, c);
	}

	public <U extends Array2d<T,U>> void reduceToAll(U data) throws IOException {
		reduceToAll(data.getData(), data.getExtent(), data
				.getReduceOperation());
	}

	public abstract void reduceToAll(T array, int extent, ReduceOp reduceOp)
			throws IOException;

}
