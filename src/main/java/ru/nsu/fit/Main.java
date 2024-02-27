package ru.nsu.fit;


import ru.nsu.fit.dymock.BonfireBuilder;
import ru.nsu.fit.dymock.Dymock;
import ru.nsu.fit.dymock.LeafMatcher;
import ru.nsu.fit.dymock.Stick;
import ru.nsu.fit.testclasses.SayHello;

public class Main {
    public static void main(String[] args) {
        SayHello test = Dymock.burn(SayHello.class);
        LeafMatcher[] arguments = {Dymock.green(), Dymock.green()};
        BonfireBuilder.buildBonfire(test)
                .addStick(new Stick("sayHello", arguments, "Mocked!"))
                .addStick(new Stick("returnInt", arguments, 42))
                .addStick(new Stick("testArgs", arguments, 42))
                .addStick(new Stick("testVoid", arguments, 42));
        System.out.println(test.sayHello());
        System.out.println(test.returnInt());
        System.out.println(test.testArgs(2));
        test.testVoid(2);
    }

}