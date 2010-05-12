package jorus.parallel.collectives;

import java.io.IOException;

import jorus.parallel.PxSystem;
import jorus.parallel.ReduceToRoot;
import jorus.parallel.ReduceOp;

public class BinomialReduceToRoot<T> extends ReduceToRoot<T> {

	public BinomialReduceToRoot(PxSystem system, Class<T> c) throws Exception {
		super(system, c);
	}

	@Override
	public void reduceToRoot(T array, int extent, ReduceOp reduceOp) throws IOException {
		// TODO Auto-generated method stub
		final int length = util.getLength(array);
		final T tmp = (T) util.create(length);

		int mask = 0x1;

		while (mask < size) {

			/* Receive */
			if ((mask & rank) == 0) {
				final int source = (rank | mask);

				if (source < size) {
					comm.receive(source, tmp, 0, length);
					util.reduce(array, tmp, extent, reduceOp);
				}
			} else {
				/* Done receiving. Now send my result to parent */
				final int dst = ((rank & (~mask))) % size;
				comm.send(dst, array, 0, length);
			}

			mask <<= 1;
		}

		util.release(tmp);
	}
}
