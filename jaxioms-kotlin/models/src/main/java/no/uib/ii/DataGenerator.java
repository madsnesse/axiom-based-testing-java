package no.uib.ii;

public interface DataGenerator<T> {
    T generate();

    T[] generateMany(int n);

}
