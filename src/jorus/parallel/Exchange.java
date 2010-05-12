package jorus.parallel;

public abstract class Exchange<T> extends Collective<T> {

    protected Exchange(PxSystem system, Class<T> c) throws Exception {
        super(system, c);
    }

    public abstract void exchange(T in, int offIn, int lenIn, T out, int offOut, int lenOut);
}
