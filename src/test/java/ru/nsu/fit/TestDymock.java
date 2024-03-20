package ru.nsu.fit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.fit.dymock.BonfireBuilder;
import ru.nsu.fit.dymock.Dymock;
import ru.nsu.fit.dymock.bytebuddy.Intercepted;
import ru.nsu.fit.dymock.matchers.Leaf;
import ru.nsu.fit.dymock.matchers.PartialStick;
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
                .addStick(new Stick("plus", 0.0, Leaf.any(), Leaf.any()));
        Assertions.assertEquals(StaticMethod.plus(2.0, 2.0), 0.0);
    }
    @Test
    public void testFinalStick() {
        FinalClass mock = Dymock.burn(FinalClass.class);
        BonfireBuilder.buildBonfire(mock)
                        .addStick(new Stick("isDivisor", true, Leaf.any(), Leaf.any()));
        Assertions.assertTrue(mock.isDivisor(2, 3));
        Assertions.assertFalse(new FinalClass().isDivisor(2, 3));
    }
    @Test
    public void testMatcherEquals() {
        Foo mock = Dymock.burn(Foo.class);
        BonfireBuilder.buildBonfire(mock)
                .addStick(new Stick("echoInt", 2, Leaf.eq(1)));
        Assertions.assertEquals(mock.echoInt(1), 2);
    }
    @Test
    public void testMatcherByLink() {
        Foo mock = Dymock.burn(Foo.class);
        Foo.Bar arg = new Foo.Bar("Mr");
        BonfireBuilder.buildBonfire(mock)
                .addStick(new Stick("helloBar", "Miss", Leaf.linkEq(arg)));
        Assertions.assertNull(mock.helloBar(new Foo.Bar("-")));
        Assertions.assertEquals("Miss", mock.helloBar(arg));
    }
    @Test
    public void testChainMatchers() {
        Foo mock = Dymock.burn(Foo.class);
        Foo.Bar arg = new Foo.Bar("Mr");
        BonfireBuilder.buildBonfire(mock)
                .addStick(new Stick("helloBar", "Green", Leaf.any()))
                .addStick(new Stick("helloBar", "Yellow", Leaf.eq(arg)))
                .addStick(new Stick("helloBar", "Red", Leaf.linkEq(arg)));
        Assertions.assertEquals("Green", mock.helloBar(new Foo.Bar("")));
        Assertions.assertEquals("Yellow", mock.helloBar(new Foo.Bar("Mr")));
        Assertions.assertEquals("Red", mock.helloBar(arg));
    }
    @Test
    public void testSpyAndChainMatchers() {
        Foo mock = Dymock.spy(Foo.class);
        Foo.Bar arg = new Foo.Bar("Mr");
        BonfireBuilder.buildBonfire(mock)
                .addStick(new Stick("helloBar", "Green", Leaf.any()))
                .addStick(new Stick("helloBar", "Yellow", Leaf.eq(arg)))
                .addStick(new Stick("helloBar", "Red", Leaf.linkEq(arg)));
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
                .addStick(new Stick("plus", 1.0, Leaf.eq(1.0), Leaf.eq(1.0)));
        Assertions.assertEquals(2.0, useStaticMethod.advancedSum(1.0, 1.0));
    }
    @Test
    public void testCompositeOrCondition(){
        Foo mock = Dymock.burn(Foo.class);
        BonfireBuilder.buildBonfire(mock)
                .addStick(new Stick("echoInt", 1, Leaf.or(Leaf.eq(0), Leaf.eq(1))));
        Assertions.assertEquals(mock.echoInt(0), 1);
        Assertions.assertEquals(mock.echoInt(1), 1);
        Assertions.assertEquals(mock.echoInt(2), 0);
    }
    @Test
    public void testCompositeAndCondition(){
        Foo mock = Dymock.burn(Foo.class);
        BonfireBuilder.buildBonfire(mock)
                .addStick(new Stick("echoInt", 1, Leaf.and(Leaf.limit().from(0), Leaf.limit().to(1))));
        Assertions.assertEquals(1, mock.echoInt(0));
        Assertions.assertEquals(1, mock.echoInt(1));
        Assertions.assertEquals(0, mock.echoInt(2));
    }
    @Test
    public void testEmptyCompositeCondition(){
        Foo mock = Dymock.burn(Foo.class);
        BonfireBuilder.buildBonfire(mock)
                .addStick(new Stick("echoInt", 1, Leaf.or()));
        Assertions.assertEquals(mock.echoInt(0), 1);
        Assertions.assertEquals(mock.echoInt(12345678), 1);
    }
    @Test
    public void testOverloadBask(){
        Foo mock = Dymock.burn(Foo.class);
        Stick intStick = new Stick("echoInt", 0, Leaf.any(Integer.class));
        Stick doubleStick = new Stick("echoInt", 1, Leaf.any(Double.class));
        Stick twoArgsStick = new Stick("echoInt", 2, Leaf.eq(0), Leaf.eq(0));
        BonfireBuilder.buildBonfire(mock)
                .addStick(intStick)
                .addStick(doubleStick)
                .addStick(twoArgsStick);

        mock.echoInt(0);
        mock.echoInt(1);
        mock.echoInt(0.1);
        mock.echoInt(0, 0);
        mock.echoInt(1, 1);

        Assertions.assertTrue(intStick.bask(Dymock.exactly(2)));
        Assertions.assertTrue(doubleStick.bask(Dymock.exactly(1)));
        Assertions.assertTrue(twoArgsStick.bask(Dymock.exactly(1)));
        Assertions.assertTrue(Dymock.ignited(mock, "echoInt", Dymock.exactly(5))); // counts missed calls

        Foo anotherMock = Dymock.burn(Foo.class);
        Assertions.assertFalse(Dymock.ignited(anotherMock, intStick));
        Assertions.assertTrue(Dymock.ignited(mock, intStick));
        Assertions.assertFalse(Dymock.ignited(anotherMock, doubleStick));
        Assertions.assertTrue(Dymock.ignited(mock, doubleStick));
        Assertions.assertFalse(Dymock.ignited(anotherMock, twoArgsStick));
        Assertions.assertTrue(Dymock.ignited(mock, twoArgsStick));
    }
    @Test
    public void testStaticOverload(){
        Intercepted<StaticMethod> mock = Dymock.burnDown(StaticMethod.class);
        Stick intStick = new Stick("plus", 0, Leaf.any(Integer.class), Leaf.any(Integer.class));
        Stick doubleStick = new Stick("plus", 1.1, Leaf.any(Double.class), Leaf.any(Double.class));
        BonfireBuilder.buildBonfire(mock)
                .addStick(intStick)
                .addStick(doubleStick);

        Assertions.assertEquals(0,  StaticMethod.plus(10, 1));
        Assertions.assertEquals(1.1,  StaticMethod.plus(10.0, 1));
        StaticMethod.sub(0, 0);

        Assertions.assertTrue(intStick.bask(Dymock.exactly(1)));
        Assertions.assertTrue(doubleStick.bask(Dymock.exactly(1)));
        Assertions.assertTrue(Dymock.ignited(mock, "plus", Dymock.exactly(2)));
        Assertions.assertTrue(Dymock.ignited(mock, Dymock.exactly(3)));
    }
    @Test
    public void testStringFail(){
        StringFoo mock = Dymock.burn(StringFoo.class);
        Stick stringStick = new Stick("echoInt", 1, Leaf.any(String.class));
        Stick intStringStick = new Stick("echoInt", 2, Leaf.any(Integer.class), Leaf.any(String.class));
        BonfireBuilder.buildBonfire(mock)
                .addStick(stringStick)
                .addStick(intStringStick);
        Assertions.assertEquals(1, mock.echoInt("321"));
        Assertions.assertEquals(2, mock.echoInt(1, "321"));
        Assertions.assertTrue(intStringStick.bask(Dymock.exactly(1)));
        Assertions.assertTrue(stringStick.bask(Dymock.exactly(1)));
    }
    @Test
    public void testPartial(){
        Foo mock = Dymock.burn(Foo.class);

        PartialStick firstArgStick = new PartialStick("echoInt", 1, Leaf.partial("a", Leaf.eq(1)));
        PartialStick secondArgStick = new PartialStick("echoInt", 2, Leaf.partial("b", Leaf.eq(1)));
        BonfireBuilder.buildBonfire(mock)
            .addPartialStick(firstArgStick)
            .addPartialStick(secondArgStick);
        Assertions.assertEquals(1, mock.echoInt(1));
        Assertions.assertEquals(1, mock.echoInt(1, 0));
        Assertions.assertEquals(2, mock.echoInt(1, 1));
    }
    @Test
    public void testPartialStatic(){
        Intercepted<StaticMethod> mock = Dymock.burnDown(StaticMethod.class);
        PartialStick doubleStick = new PartialStick("plus", 1.1, Leaf.partial("a", Leaf.eq(1.0)));
        BonfireBuilder.buildBonfire(mock).addPartialStick(doubleStick);

        Assertions.assertEquals(1.1, StaticMethod.plus(1.0));
        Assertions.assertEquals(1.1, StaticMethod.plus(1.0, 0));
    }
}
