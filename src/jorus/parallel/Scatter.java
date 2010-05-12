package jorus.parallel;

import java.io.IOException;

public abstract class Scatter<T> extends Collective<T> {
   
    protected Scatter(PxSystem system, Class<T> c) throws Exception {
        super(system, c);
    }
    
    public abstract void scatter(T in, int [] offsets, int [] sizes, T out, 
            int offset, int size) throws IOException;
}
