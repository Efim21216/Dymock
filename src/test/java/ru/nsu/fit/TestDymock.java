package ru.nsu.fit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.fit.dymock.BonfireBuilder;
import ru.nsu.fit.dymock.Dymock;
import ru.nsu.fit.dymock.bytebuddy.Intercepted;
import ru.nsu.fit.dymock.matchers.Leaf;
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
    public void testFinalEmptyMock() {
        FinalClass mock = Dymock.burn(FinalClass.class);
        Assertions.assertFalse(mock.isDivisor(2, 1));
        FinalClass origin = new FinalClass();
        Assertions.assertTrue(origin.isDivisor(2, 1));
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
    public void testFinalStick() {
        FinalClass mock = Dymock.burn(FinalClass.class);
        BonfireBuilder.buildBonfire(mock)
                        .addStick(new Stick("isDivisor", true, Leaf.green(), Leaf.green()));
        Assertions.assertTrue(mock.isDivisor(2, 3));
        Assertions.assertFalse(new FinalClass().isDivisor(2, 3));
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
    @Test
    public void testSpyAndChainMatchers() {
        Foo mock = Dymock.spy(Foo.class);
        Foo.Bar arg = new Foo.Bar("Mr");
        BonfireBuilder.buildBonfire(mock)
                .addStick(new Stick("helloBar", "Green", Leaf.green()))
                .addStick(new Stick("helloBar", "Yellow", Leaf.yellow(arg)))
                .addStick(new Stick("helloBar", "Red", Leaf.red(arg)));
        Assertions.assertEquals("Green", mock.helloBar(new Foo.Bar("")));
        Assertions.assertEquals("Yellow", mock.helloBar(new Foo.Bar("Mr")));
        Assertions.assertEquals("Red", mock.helloBar(arg));
        Assertions.assertEquals(12, mock.echoInt(12));
    }
    @Test
    public void testUseStaticInAnotherClass() {
        UseStaticMethod useStaticMethod = new UseStaticMethod(true);
        Assertions.assertEquals(4.0, useStaticMethod.advancedSum(1.0, 1.0));
        Intercepted<StaticMethod> mock = Dymock.burnDown(StaticMethod.class);
        BonfireBuilder.buildBonfire(mock)
                .addStick(new Stick("plus", 1.0, Leaf.yellow(1.0), Leaf.yellow(1.0)));
        Assertions.assertEquals(2.0, useStaticMethod.advancedSum(1.0, 1.0));
    }
}
