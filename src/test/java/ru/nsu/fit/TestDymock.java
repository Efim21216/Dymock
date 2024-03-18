package ru.nsu.fit;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.objenesis.ObjenesisStd;
import ru.nsu.fit.dymock.BonfireBuilder;
import ru.nsu.fit.dymock.Dymock;
import ru.nsu.fit.dymock.bytebuddy.Intercepted;
import ru.nsu.fit.dymock.bytebuddy.InterceptionAccessor;
import ru.nsu.fit.dymock.bytebuddy.Interceptor;
import ru.nsu.fit.dymock.matchers.Leaf;
import ru.nsu.fit.dymock.matchers.LeafMatcher;
import ru.nsu.fit.dymock.matchers.Stick;

public class TestDymock {
    @Test
    public void testEmptyMock() {
        Foo original = new Foo();
        Foo mock = Dymock.burn(Foo.class);
        Assertions.assertEquals(original.returnInt(), 42);
        Assertions.assertEquals(mock.returnInt(), 0);
        Assertions.assertEquals(original.echoInt(13), 13);
        Assertions.assertEquals(mock.echoInt(13), 0);
    }
    @Test
    public void testInterfaceEmptyMock() {
        BaseInterface mock = Dymock.burn(BaseInterface.class);
        Assertions.assertNull(mock.hello("Hi"));
    }
    @Test
    public void testStaticEmptyMock() {
        Dymock.burnDown(StaticMethod.class);
        Assertions.assertEquals(StaticMethod.plus(1.0, 1.0), 0.0);
    }
    @Test
    public void testEmptySpy() {
        Foo mock = Dymock.spy(Foo.class);
        Assertions.assertEquals(mock.returnInt(), 42);
        Assertions.assertEquals(mock.echoInt(13), 13);
    }
    @Test
    public void testStaticEmptySpy() {
        Dymock.spyStatic(StaticMethod.class);
        Assertions.assertEquals(StaticMethod.plus(1.0, 1.0), 2.0);
    }
    @Test
    public void testStick() {
        Foo mock = Dymock.burn(Foo.class);
        BonfireBuilder.buildBonfire(mock)
                .addStick(new Stick("returnInt", 2));
        Assertions.assertEquals(mock.returnInt(), 2);
    }
    @Test
    public void testStaticStick() {
        Intercepted<StaticMethod> mock = Dymock.burnDown(StaticMethod.class);
        BonfireBuilder.buildBonfire(mock)
                .addStick(new Stick("plus", 0.0, Leaf.green(), Leaf.green()));
        Assertions.assertEquals(StaticMethod.plus(2.0, 2.0), 0.0);
    }
    @Test
    public void testMatcherEquals() {
        Foo mock = Dymock.burn(Foo.class);
        BonfireBuilder.buildBonfire(mock)
                .addStick(new Stick("echoInt", 2, Leaf.yellow(1)));
        Assertions.assertEquals(mock.echoInt(1), 2);
    }
    @Test
    public void testMatcherByLink() {
        Foo mock = Dymock.burn(Foo.class);
        Foo.Bar arg = new Foo.Bar("Mr");
        BonfireBuilder.buildBonfire(mock)
                .addStick(new Stick("helloBar", "Miss", Leaf.red(arg)));
        Assertions.assertNull(mock.helloBar(new Foo.Bar("-")));
        Assertions.assertEquals("Miss", mock.helloBar(arg));
    }
    @Test
    public void testChainMatchers() {
        Foo mock = Dymock.burn(Foo.class);
        Foo.Bar arg = new Foo.Bar("Mr");
        BonfireBuilder.buildBonfire(mock)
                .addStick(new Stick("helloBar", "Green", Leaf.green()))
                .addStick(new Stick("helloBar", "Yellow", Leaf.yellow(arg)))
                .addStick(new Stick("helloBar", "Red", Leaf.red(arg)));
        Assertions.assertEquals("Green", mock.helloBar(new Foo.Bar("")));
        Assertions.assertEquals("Yellow", mock.helloBar(new Foo.Bar("Mr")));
        Assertions.assertEquals("Red", mock.helloBar(arg));
    }

}
