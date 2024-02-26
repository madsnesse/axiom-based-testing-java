package org.example;

public interface Example<T> {

    default T a(T a, T b) {
        return a;
    }

}
