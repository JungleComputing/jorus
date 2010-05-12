package jorus.parallel.collectives;

import java.io.IOException;

import jorus.parallel.Barrier;
import jorus.parallel.PxSystem;

public class FlatBarrier<T> extends Barrier<T> {

    public FlatBarrier(PxSystem system, Class<T> c) throws Exception {
        super(system, c);
    }

    @Override
    public void barrier() throws IOException {
        
        if (rank == 0) { 
            for (int partner = 1; partner < size; partner++) {
                comm.receive(partner);
            }
     
            for (int partner = 1; partner < size; partner++) {
                comm.send(partner);
            }
        } else { 
            comm.send(0);
            comm.receive(0);
        }
    }

}
