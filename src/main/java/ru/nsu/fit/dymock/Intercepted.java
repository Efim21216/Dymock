package ru.nsu.fit.dymock;

public class Intercepted {
    private final String fullName;

    public Intercepted(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }
}
