package jorus.parallel.collectives;

import java.io.IOException;

import jorus.parallel.PxSystem;
import jorus.parallel.ReduceOp;
import jorus.parallel.ReduceToRoot;

public final class FlatReduceToRoot<T> extends ReduceToRoot<T> {

	public FlatReduceToRoot(PxSystem system, Class<T> c) throws Exception {
		super(system, c);
	}

	@Override
	public void reduceToRoot(T array, int extent, ReduceOp reduceOp)
			throws IOException {
		final int length = util.getLength(array);

		if (rank == 0) {

			final T tmp = (T) util.create(length);

			for (int partner = 1; partner < size; partner++) {
				comm.receive(partner, tmp, 0, length);
				util.reduce(array, tmp, extent, reduceOp);
			}

			util.release(tmp);

		} else {
			comm.send(0, array, 0, length);
		}
	}
}
