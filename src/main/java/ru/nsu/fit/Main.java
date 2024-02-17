package ru.nsu.fit;


import ru.nsu.fit.dymock.BonfireBuilder;
import ru.nsu.fit.dymock.Dymock;
import ru.nsu.fit.dymock.Stick;
import ru.nsu.fit.testclasses.SayHello;

public class Main {
    public static void main(String[] args) {
        SayHello test = Dymock.burn(SayHello.class);
        Object[] arguments = {};
        BonfireBuilder.buildBonfire(test.getClass())
                .addStick(new Stick("sayHello", arguments, "Mocked!"));
        System.out.println(test.sayHello());
    }

}