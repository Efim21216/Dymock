package ru.nsu.fit;

public class Foo {
    public int returnInt() {
        return 42;
    }
    public int echoInt(int a) {
        return a;
    }
    public int echoInt(int a, int b) {
        return a + b;
    }
    public int echoInt(double d){
        return (int)d;
    }
    public String helloBar(Bar bar) {
        return "Hello, " + bar.name;
    }
    public static class Bar {
        private String name;

        public Bar(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Bar that = (Bar) o;
            return name.equals(that.name);
        }
    }
}
