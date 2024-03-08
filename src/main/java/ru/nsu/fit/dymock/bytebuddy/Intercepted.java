package ru.nsu.fit.dymock.bytebuddy;

public class Intercepted<T> {
    private final Class<T> clazz;

    public Intercepted(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
