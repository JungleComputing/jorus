package jorus.parallel;

import java.io.IOException;

public abstract class Gather<T> extends Collective<T> {
   
    protected Gather(PxSystem system, Class<T> c) throws Exception {
        super(system, c);
    }
    
    public abstract void gather(T out, int offset, int length, 
            T in, int [] offsets, int [] sizes) throws IOException ;   
}
