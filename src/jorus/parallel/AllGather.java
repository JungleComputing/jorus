package jorus.parallel;

import java.io.IOException;

public abstract class AllGather<T> extends Collective<T> {
   
    protected AllGather(PxSystem system, Class<T> c) throws Exception {
        super(system, c);
    }
    
    public abstract void allGather(T out, int offset, int length,
    		T in, int [] offsets, int [] sizes) throws IOException ;   
}
