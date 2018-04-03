package net.itca.datastructures.probabilistic.cuckoo;

public class Tuple<T, E> {

    private final T a;
    private final E b;
    public Tuple(T a, E b) {
        this.a = a;
        this.b = b;
    }

    public T getA(){
        return a;
    }

    public E getB(){
        return b;
    }
}
