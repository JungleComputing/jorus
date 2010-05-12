package jorus.parallel.collectives;

import java.io.IOException;

import jorus.parallel.Gather;
import jorus.parallel.PxSystem;

public final class FlatGather<T> extends Gather<T> {

    public FlatGather(PxSystem system, Class<T> c) throws Exception {
        super(system, c);
    }

    @Override
    public void gather(T out, int offset, int length, T in, int[] offsets, int[] sizes) throws IOException {
  
        if (rank == 0) { 
            for (int partner = 1; partner < size; partner++) {
                comm.receive(partner, in, offsets[partner], sizes[partner]);
            }
    
            System.arraycopy(out, offset, in, offsets[0], length);
        } else { 
            comm.send(0, out, offset, length);
        }
    }

}
