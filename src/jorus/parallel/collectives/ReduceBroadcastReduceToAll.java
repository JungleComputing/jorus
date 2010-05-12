package jorus.parallel.collectives;

import java.io.IOException;

import jorus.parallel.PxSystem;
import jorus.parallel.ReduceOp;
import jorus.parallel.ReduceToAll;

public class ReduceBroadcastReduceToAll<T> extends ReduceToAll<T> {

    public ReduceBroadcastReduceToAll(PxSystem system, Class<T> c) throws Exception {
        super(system, c);
    }

    @Override
	public void reduceToAll(T array, int extent, ReduceOp reduceOp)
			throws IOException {
		 // This is a simple binomial reduce-to-root followed by a binomial 
        // broadcast. 
        
        // Reduce phase
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
                /* Done receiveing. Now send my result to parent */                    
                final int dst = ((rank & (~ mask))) % size; 
                comm.send(dst, array, 0, length);
            }
            
            mask <<= 1;
        }
        
        // Broadcast phase
        // First handle the receive phase
        mask = 0x1;
        
        while (mask < size) { 

            if ((rank & mask) != 0) {
                
                final int source = rank - mask; 
                comm.receive(source, array, 0, length);
                break;
            }
            mask <<= 1;
        }
        
        // Next handle the send phase
        mask >>= 1;
        
        while (mask > 0) { 

            if ((rank + mask) < size) {
                
                final int dst = rank + mask;
                comm.send(dst, array, 0, length);
            }     
            mask >>= 1;
        }
        
        util.release(tmp);		
	}

}
