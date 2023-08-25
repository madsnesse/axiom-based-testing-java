package com.example.processors;

public interface Generator<T> {
    T generate();

    T[] generateMany(int n);

}
