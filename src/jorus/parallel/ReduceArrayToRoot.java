package jorus.parallel;

import jorus.operations.communication.RedOpArray;

public abstract class ReduceArrayToRoot<T> extends Collective {
   
    protected ReduceArrayToRoot(PxSystem system, Class c) throws Exception {
        super(system, c);
    }
    
    public abstract T reduceArrayToRoot(T data, RedOpArray<T> op) throws Exception;
}
