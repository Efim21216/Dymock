package ru.nsu.fit;

public class UseStaticMethod {
    private final boolean multiplyByTwo;

    public UseStaticMethod(boolean multiplyByTwo) {
        this.multiplyByTwo = multiplyByTwo;
    }
    public double advancedSum(double a, double b) {
        if (multiplyByTwo)
            return StaticMethod.plus(a, b) * 2;
        return StaticMethod.plus(a, b);
    }
}
