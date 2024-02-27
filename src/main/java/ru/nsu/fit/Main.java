package ru.nsu.fit;


import ru.nsu.fit.dymock.BonfireBuilder;
import ru.nsu.fit.dymock.Dymock;
import ru.nsu.fit.dymock.LeafMatcher;
import ru.nsu.fit.dymock.Stick;
import ru.nsu.fit.testclasses.SayHello;

public class Main {
    public static void main(String[] args) {
        SayHello test = Dymock.burn(SayHello.class);
        LeafMatcher[] zeroArg = {};

        Integer num = 2<<10;
        LeafMatcher[] anyArg = {Dymock.green()};
        LeafMatcher[] eqABC = {Dymock.yellow(num)};
        LeafMatcher[] linkEqABC = {Dymock.red(num)};
        BonfireBuilder.buildBonfire(test)
                .addStick(new Stick("sayHello", zeroArg, "Mocked!"))
                .addStick(new Stick("returnInt", zeroArg, 42))
                .addStick(new Stick("testArgs", linkEqABC, "THE NUM"))
                .addStick(new Stick("testArgs", eqABC, "NUM"))
                .addStick(new Stick("testArgs", anyArg, "ANY"))
                .addStick(new Stick("testVoid", anyArg, null));
        System.out.println(test.sayHello());
        System.out.println(test.returnInt());
        System.out.println(test.testArgs(1)); // doesn't match by link or equals
        System.out.println(test.testArgs(2<<10)); // doesn't match by link 
        System.out.println(test.testArgs(num)); // match by link
        test.testVoid(2);
    }

}