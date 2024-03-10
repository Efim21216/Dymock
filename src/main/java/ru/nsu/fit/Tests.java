package ru.nsu.fit;

import ru.nsu.fit.dymock.*;
import ru.nsu.fit.dymock.bytebuddy.Intercepted;
import ru.nsu.fit.dymock.matchers.Leaf;
import ru.nsu.fit.dymock.matchers.LeafMatcher;
import ru.nsu.fit.dymock.matchers.Stick;
import ru.nsu.fit.testclasses.*;

public class Tests {
    /*public static void testRedefine() {
        ByteBuddyAgent.install();
        Foo foo = new Foo();
        String className = "ru.nsu.fit.testclasses.Bar";
        TypePool typePool = TypePool.Default.ofSystemLoader();
        TypeDescription typeDescription = typePool.describe(className).resolve();
        new ByteBuddy()
                .redefine(typeDescription, ClassFileLocator.ForClassLoader.ofSystemLoader())
                .method(named("m")).intercept(MethodCall.call((Callable<String>) foo::m))
                .make()
                .load(Foo.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        //.load(Foo.class.getClassLoader(), ClassLoadingStrategy.Default.CHILD_FIRST);
        Bar bar = new Bar();
        System.out.println(bar.m());
        System.out.println(foo.m());
    }
    public static void testAgent() {
        Instrumentation instrumentation = ByteBuddyAgent.install();
        ToStringAgent.premain("", instrumentation);
        Bar bar = new Bar();
        System.out.println(bar);
    }

    public static void testAdvice() {
        ByteBuddyAgent.install();
        new ByteBuddy()
                .redefine(StaticSayHello.class)
                .visit(Advice.to(MethodTracker.class).on(named("sayHello")))
                .make()
                .load(SayHello.class.getClassLoader(), ClassReloadingStrategy.fromInstalledAgent());
        System.out.println(StaticSayHello.sayHello("", 1));
    }*/

   /* public static void testStaticRedefine() {
        ByteBuddyAgent.install();
        String className = "ru.nsu.fit.testclasses.StaticSayHello";
        TypePool typePool = TypePool.Default.ofSystemLoader();
        TypeDescription typeDescription = typePool.describe(className).resolve();
        new ByteBuddy()
                .redefine(typeDescription, ClassFileLocator.ForClassLoader.ofSystemLoader())
                .method(named("sayHello"))
                //.intercept(FixedValue.value("Transformed"))
                .intercept(MethodCall.call(() -> "transformed"))
                .make()
                .load(Bar.class.getClassLoader(), ClassLoadingStrategy.Default.CHILD_FIRST);
        System.out.println(StaticSayHello.sayHello("", 1));
    }*/

    public static void testOverload(){
        SayHello test = Dymock.burn(SayHello.class);
        LeafMatcher[] intArg = {Leaf.green(Integer.class)};
        LeafMatcher[] dblArg = {Leaf.green(Double.class)};

        BonfireBuilder.buildBonfire(test)
                .addStick(new Stick("testArgs", intArg, "Hi int!"))
                .addStick(new Stick("testArgs", dblArg, "Hi double"));

        System.out.println(test.testArgs(1)); // Hi int
        System.out.println(test.testArgs(0.5)); // Hi double
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
    public static void testIgnited() {
        SayHello testA = Dymock.burn(SayHello.class);
        SayHello testB = Dymock.burn(SayHello.class);
        LeafMatcher[] zeroArg = {};
        Stick testStick = new Stick("sayHello", zeroArg, "Mocked!");
        BonfireBuilder.buildBonfire(testA)
                .addStick(testStick);
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
        // Exact
        System.out.println("FALSE");
        System.out.println("\t" + Dymock.ignited(testA, Dymock.exactly(2)));
        System.out.println("\t" + testStick.bask(Dymock.exactly(2)));
        System.out.println("TRUE");
        System.out.println("\t" + Dymock.ignited(testA, Dymock.exactly(1)));
        System.out.println("\t" + testStick.bask(Dymock.exactly(1)));
        // Limit
        System.out.println("FALSE");
        System.out.println("\t" + Dymock.ignited(testA, Dymock.limited().atLeast(2)));
        System.out.println("\t" + testStick.bask(Dymock.limited().atLeast(2)));
        System.out.println("TRUE");
        System.out.println("\t" + Dymock.ignited(testA, Dymock.limited().atMost(2)));
        System.out.println("\t" + testStick.bask(Dymock.limited().atMost(2)));

        // Specific
        System.out.println(Dymock.ignited(testB, testStick)); // false
        testB.sayHello();
        System.out.println(Dymock.ignited(testB, testStick)); // true
        System.out.println(Dymock.ignited(testB, testStick, Dymock.exactly(2))); 
        System.out.println(Dymock.ignited(testB, testStick, Dymock.exactly(1))); 
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
}
