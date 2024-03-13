package ru.nsu.fit;

import ru.nsu.fit.dymock.*;
import ru.nsu.fit.dymock.bytebuddy.Intercepted;
import ru.nsu.fit.dymock.matchers.Leaf;
import ru.nsu.fit.dymock.matchers.LeafMatcher;
import ru.nsu.fit.dymock.matchers.Stick;
import ru.nsu.fit.testclasses.*;

public class Tests {
    public static void testOverload(){
        SayHello test = Dymock.burn(SayHello.class);
        LeafMatcher[] intArg = {Leaf.green(Integer.class)};
        LeafMatcher[] twoIntArg = {Leaf.yellow(2)};
        LeafMatcher[] dblArg = {Leaf.green(Double.class)};

        BonfireBuilder.buildBonfire(test)
                .addStick(new Stick("testArgs", intArg, "Hi int!"))
                .addStick(new Stick("testArgs", dblArg, "Hi double"))
                .addStick(new Stick("testArgs", twoIntArg, "Hi 2"));

        System.out.println(test.testArgs(1)); // Hi int
        System.out.println(test.testArgs(0.5)); // Hi double
        System.out.println(test.testArgs(2.0)); // Hi double (doesn't match implicit Integer type)
        System.out.println(test.testArgs(2)); // Hi 2
    }

    public static void testStaticOverload(){
        Intercepted<StaticSayHello> intercepted = Dymock.burnDown(StaticSayHello.class);
        LeafMatcher[] intArg = {Leaf.green(Integer.class)};
        LeafMatcher[] dblArg = {Leaf.green(Double.class)};

        BonfireBuilder.buildBonfire(intercepted)
                .addStick(new Stick("testArgs", intArg, "Hi int!"))
                .addStick(new Stick("testArgs", dblArg, "Hi double"));

        System.out.println(StaticSayHello.testArgs(1)); // Hi int
        System.out.println(StaticSayHello.testArgs(0.5)); // Hi double
    }
    public static void testIgnited() {
        SayHello testA = Dymock.burn(SayHello.class);
        SayHello testB = Dymock.burn(SayHello.class);
        LeafMatcher[] zeroArg = {};
        LeafMatcher[] oneArg = {Leaf.yellow(1)};
        LeafMatcher[] twoArg = {Leaf.yellow(2)};
        Stick testStick = new Stick("sayHello", zeroArg, "Mocked!");
        Stick stickOne = new Stick("testArgs", oneArg, "One");
        Stick stickTwo = new Stick("testArgs", twoArg, "Two");
        BonfireBuilder.buildBonfire(testA)
                .addStick(testStick)
                .addStick(stickOne)
                .addStick(stickTwo);
        BonfireBuilder.buildBonfire(testB)
                .addStick(testStick);

        // FALSE
        System.out.println("FALSE");
        System.out.println("\t" + Dymock.ignited(testA));
        System.out.println("\t" + testStick.bask());

        testA.sayHello();

        // TRUE
        System.out.println("TRUE");
        System.out.println("\t" + Dymock.ignited(testA));
        System.out.println("\t" + testStick.bask());
        System.out.println("\t" + Dymock.ignited(testA, testStick));
        System.out.println("\t" + Dymock.ignited(testA, "sayHello"));
        // Exact
        System.out.println("FALSE");
        System.out.println("\t" + Dymock.ignited(testA, Dymock.exactly(2)));
        System.out.println("\t" + testStick.bask(Dymock.exactly(2)));
        System.out.println("\t" + Dymock.ignited(testA, testStick, Dymock.exactly(2)));
        System.out.println("\t" + Dymock.ignited(testA, "sayHello", Dymock.exactly(2)));
        System.out.println("TRUE");
        System.out.println("\t" + Dymock.ignited(testA, Dymock.exactly(1)));
        System.out.println("\t" + testStick.bask(Dymock.exactly(1)));
        System.out.println("\t" + Dymock.ignited(testA, testStick, Dymock.exactly(1)));
        System.out.println("\t" + Dymock.ignited(testA, "sayHello", Dymock.exactly(1)));
        // Limit
        System.out.println("FALSE");
        System.out.println("\t" + Dymock.ignited(testA, Dymock.atLeast(2)));
        System.out.println("\t" + testStick.bask(Dymock.atLeast(2)));
        System.out.println("\t" + Dymock.ignited(testA, testStick, Dymock.atLeast(2)));
        System.out.println("\t" + Dymock.ignited(testA, "sayHello", Dymock.atLeast(2)));
        System.out.println("TRUE");
        System.out.println("\t" + Dymock.ignited(testA, Dymock.atMost(2)));
        System.out.println("\t" + testStick.bask(Dymock.atMost(2)));
        System.out.println("\t" + Dymock.ignited(testA, testStick, Dymock.atMost(2)));
        System.out.println("\t" + Dymock.ignited(testA, "sayHello", Dymock.atMost(2)));

        // Specific
        System.out.println(Dymock.ignited(testB, testStick)); // false
        testB.sayHello();
        System.out.println(Dymock.ignited(testB, testStick)); // true
        System.out.println(Dymock.ignited(testB, testStick, Dymock.exactly(2)));
        System.out.println(Dymock.ignited(testB, testStick, Dymock.exactly(1)));

        // Multiple sticks
        System.out.println("Multiple sticks");
        testA.testArgs(1);
        System.out.println(Dymock.ignited(testA, stickOne));
        System.out.println(Dymock.ignited(testA, stickTwo));
        System.out.println(Dymock.ignited(testA, "testArgs"));
    }
    public static void testStaticMock() {
        System.out.println("Original invocation");
        System.out.println(StaticSayHello.sayHello());
        System.out.println("Mocking class...");
        Intercepted<StaticSayHello> intercepted = Dymock.burnDown(StaticSayHello.class);
        LeafMatcher[] zeroArg = {};

        Integer num = 2<<10;
        LeafMatcher[] anyArg = {Leaf.green(Integer.class)};
        LeafMatcher[] eqNum = {Leaf.yellow(num, Integer.class)};
        LeafMatcher[] linkEqNum = {Leaf.red(num, Integer.class)};
        BonfireBuilder.buildBonfire(intercepted)
                        .addStick(new Stick("sayHello", zeroArg, 42))
                .addStick(new Stick("m", zeroArg, "Intercepted value"))
                .addStick(new Stick("testArgs", anyArg, "ANY"))
                .addStick(new Stick("testArgs", eqNum, "NUM"))
                .addStick(new Stick("testArgs", linkEqNum, "THE NUM"));
        System.out.println(StaticSayHello.sayHello());
        System.out.println(StaticSayHello.m());
        System.out.println(StaticSayHello.testArgs(1));
        System.out.println(StaticSayHello.testArgs(2<<10));
        System.out.println(StaticSayHello.testArgs(num)); // doesn't match
    }
    public static void testStaticNameConflict() {
        Intercepted<StaticSayHello> interceptedHello = Dymock.burnDown(StaticSayHello.class);    
        Intercepted<StaticSayHi> interceptedHi = Dymock.burnDown(StaticSayHi.class);  

        LeafMatcher[] zeroArg = {};
        BonfireBuilder.buildBonfire(interceptedHello)
                        .addStick(new Stick("m", zeroArg, "Intercepted hello"));
        BonfireBuilder.buildBonfire(interceptedHi)
                        .addStick(new Stick("m", zeroArg, "Intercepted hi"));
        
        System.out.println(StaticSayHello.m());
        System.out.println(StaticSayHi.m());
    }
    public static void testFPComparison(){
        SayHello test = Dymock.burn(SayHello.class);
        double f1 = .0;
        for (int i = 1; i <= 11; i++) {
            f1 += .1;
        }
        LeafMatcher[] arg = {Leaf.fleaf(f1, .0001)};
        BonfireBuilder.buildBonfire(test).addStick(new Stick("testArgs", arg, "Hell yeah"));

        double f2 = .1 * 11;
        System.out.println(test.testArgs(f2)); // works
        System.out.println(test.testArgs(f2+1)); // shouldn't
    }
    public static void testSimpleCase() {
        SayHello test = Dymock.burn(SayHello.class);
        LeafMatcher[] zeroArg = {};

        Integer num = 2<<10;
        LeafMatcher[] anyArg = {Leaf.green()};
        LeafMatcher[] eqNum = {Leaf.yellow(num)};
        LeafMatcher[] linkEqNum = {Leaf.red(num)};
        BonfireBuilder.buildBonfire(test)
                .addStick(new Stick("sayHello", zeroArg, "Mocked!"))
                .addStick(new Stick("returnInt", zeroArg, 42))
                .addStick(new Stick("testArgs", anyArg, "ANY"))
                .addStick(new Stick("testArgs", eqNum, "NUM"))
                .addStick(new Stick("testArgs", linkEqNum, "THE NUM"))
                .addStick(new Stick("testVoid", anyArg, null));
        System.out.println(test.sayHello());
        System.out.println(test.returnInt());
        System.out.println(test.testArgs(1)); // doesn't match by link or equals
        System.out.println(test.testArgs(2<<10)); // doesn't match by link
        System.out.println(test.testArgs(num)); // match by link
        test.testVoid(2);
    }
}
