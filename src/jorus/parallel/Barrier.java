package jorus.parallel;

import java.io.IOException;

public abstract class Barrier<T> extends Collective<T> {

    protected Barrier(PxSystem system, Class<T> c) throws Exception {
        super(system, c);
    }

    public abstract void barrier() throws IOException; 
    
    /*
    public static Barrier create(String type, PxSystem s, Class c) throws Exception { 
        
        // FIXME: This should be done using reflection, but I'm not sure how to mix this 
        // with generics -- J.
        if (type.equals("Flat")) { 
            return new FlatBarrier(s, c);
        } else { 
            throw new Exception("Unknown barrier implementation selected!");
        }
    }*/
    
    
}
