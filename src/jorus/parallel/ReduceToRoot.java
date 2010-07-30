package jorus.parallel;

import java.io.IOException;

import jorus.array.Array2d;

public abstract class ReduceToRoot<T> extends Collective<T> {

	protected ReduceToRoot(PxSystem system, Class<T> c) throws Exception {
		super(system, c);
	}

	public <U extends Array2d<T,U>> void reduceToRoot(U data) throws IOException {
		reduceToRoot(data.getData(), data.getExtent(), data
				.getReduceOperation());
	}

	public abstract void reduceToRoot(T array, int extent, ReduceOp reduceOp) throws IOException;
}
