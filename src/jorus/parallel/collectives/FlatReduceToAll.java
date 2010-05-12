package jorus.parallel.collectives;

import java.io.IOException;

import jorus.parallel.PxSystem;
import jorus.parallel.ReduceOp;
import jorus.parallel.ReduceToAll;

public final class FlatReduceToAll<T> extends ReduceToAll<T> {

    public FlatReduceToAll(PxSystem system, Class<T> c) throws Exception {
        super(system, c);
    }

	@Override
	public void reduceToAll(T array, int extent, ReduceOp reduceOp)
			throws IOException {
		final int length = util.getLength(array);
        
        if (rank == 0) {
       
            final T tmp = (T) util.create(length);
           
            for (int partner = 1; partner < size; partner++) {
                comm.receive(partner, tmp, 0, length);
                util.reduce(array, tmp, extent, reduceOp);
            }

            for (int partner = 1; partner < size; partner++) {
                comm.send(partner, array, 0, length);
            }
            
            util.release(tmp);
      
        } else {
            comm.send(0, array, 0, length);
            comm.receive(0, array, 0, length);
        }		
	}

}
