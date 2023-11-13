package org.example;

import no.uib.ii.annotation.GenerateGenerator;

import java.util.Objects;


@GenerateGenerator
public class Person {

    private final int age; //TODO create rules for classes, how do we determine rules?

    private final String name; //TODO how can I create rules for strings?

    public Person(String name) {
        this.name = name;
        this.age = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(age, name);
    }

    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}
