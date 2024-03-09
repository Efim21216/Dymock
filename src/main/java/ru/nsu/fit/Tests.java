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
                .addStick(new Stick("testArgs", intArg, "Hi int"))
                .addStick(new Stick("testArgs", dblArg, "Hi double"));

        System.out.println(test.testArgs(1)); // Hi int
        System.out.println(test.testArgs(0.5)); // Hi double
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
        Dymock.ignited(test);
    }
    public static void testStaticMock() {
        System.out.println("Original invocation");
        System.out.println(StaticSayHello.sayHello());
        System.out.println("Mocking class...");
        Intercepted<StaticSayHello> intercepted = Dymock.burnDown(StaticSayHello.class);
        LeafMatcher[] zeroArg = {};
        BonfireBuilder.buildBonfire(intercepted)
                        .addStick(new Stick("sayHello", zeroArg, 42))
                .addStick(new Stick("m", zeroArg, "Intercepted value"));
        System.out.println(StaticSayHello.sayHello());
        System.out.println(StaticSayHello.m());
    }
}
