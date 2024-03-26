package ru.nsu.fit.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.nsu.fit.dymock.BonfireBuilder;
import ru.nsu.fit.dymock.Dymock;
import ru.nsu.fit.dymock.matchers.Leaf;
import ru.nsu.fit.dymock.matchers.PartialStick;
import ru.nsu.fit.dymock.matchers.Stick;
import ru.nsu.fit.dymock.matchers.WetStick;

public class Demo {
    @Test
    void demoSticks() {
        //После этого все методы будут возвращать default value
        //Object -> null, number -> 0
        HelloWorld helloWorld = Dymock.burn(HelloWorld.class);
        Assertions.assertNull(helloWorld.hello());
        Assertions.assertEquals(0.0, helloWorld.echoDouble(11.1));


        //Stick - определяет при каких входных аргументах
        //какое возвращать значение для метода
        //Stick(String methodName, Object returnValue) если нет аргументов
        Stick hi = new Stick("hello", "Hi!");
        BonfireBuilder.buildBonfire(helloWorld)
                .addStick(hi);
        Assertions.assertEquals("Hi!", helloWorld.hello());

        //Можно не только возвращать значение, но и кидать исключение
        //Для этого есть класс WetStick
        BonfireBuilder.buildBonfire(helloWorld)
                .addStick(new WetStick("hello", new IllegalArgumentException()));
        Assertions.assertThrows(IllegalArgumentException.class, helloWorld::hello);

        //Чтобы задать аргументы необходим наследник класса
        //LeafMatcher. Есть несколько наших реализаций
        Double d = 1.0;
        BonfireBuilder.buildBonfire(helloWorld)
                //Сначало идут более общие правило, а затем более специфичные
                //any - для любого аргумента
                .addStick(new Stick("echoDouble", 1.0, Leaf.any()))
                //eq - сравнение через equals
                .addStick(new Stick("echoDouble", 2.0, Leaf.eq(2.0)))
                //linkEq - сравнение по ссылке
                .addStick(new Stick("echoDouble", 20.0, Leaf.linkEq(d)));

        Assertions.assertEquals(1.0, helloWorld.echoDouble(1.0));
        Assertions.assertEquals(20.0, helloWorld.echoDouble(d));
        Assertions.assertEquals(2.0, helloWorld.echoDouble(2.0));


        //Matchers для работы с числами
        BonfireBuilder.buildBonfire(helloWorld)
                //from - числа от 100 включительно
                .addStick(new Stick("echoDouble", 100.0, Leaf.limit().from(100.0)))
                //to - числа до 99 включитально
                .addStick(new Stick("echoDouble", 99.0, Leaf.limit().to(99.0)))
                //fpEq - приблизительное равенство. 0.001 - точность
                .addStick(new Stick("echoDouble", 3.0, Leaf.fpEq(3.0, 0.001)));

        Assertions.assertEquals(99.0, helloWorld.echoDouble(3.1));
        Assertions.assertEquals(100.0, helloWorld.echoDouble(110.0));
        Assertions.assertEquals(3.0, helloWorld.echoDouble(3.0001));


        //Также есть возможность задавать более сложные условия через and и or
        BonfireBuilder.buildBonfire(helloWorld)
                .addStick(new Stick("echoDouble", 10.0, Leaf.or(
                        Leaf.and(Leaf.limit().from(10.0), Leaf.limit().to(15.0)),
                        Leaf.and(Leaf.limit().from(20.0), Leaf.limit().to(25.0))
                )));
        Assertions.assertEquals(10.0, helloWorld.echoDouble(11.0));
        Assertions.assertEquals(10.0, helloWorld.echoDouble(21.0));


        //Есть возможность частичного задания аргументов
        //Для этого нужно указать название аргумента и его значение
        //Leaf.partial(String parameterName, LeafMatcher matcher)
        BonfireBuilder.buildBonfire(helloWorld)
                .addPartialStick(new PartialStick("concat", "B is important", Leaf.partial("b", Leaf.eq("param2"))));
        Assertions.assertEquals("B is important", helloWorld.concat("any argument here", "param2"));

        //Если и этого не достаточно, то есть возможность создать свой Matcher
        //Здесь добавить пример
    }

    @Test
    void demoCallCounting() {
        CallCounting mock =  Dymock.burn(CallCounting.class);
        //Проверим, что мы ещё не вызывали методы экземпляра
        //Для проверки количества вызовов есть метод Dymock.ignited
        Assertions.assertFalse(Dymock.ignited(mock));

        mock.echoInt();
        Assertions.assertTrue(Dymock.ignited(mock));

        //Для более точной проверки количества вызовов есть методы
        //exactly, atLeast, atMost, inRange
        Assertions.assertTrue(Dymock.ignited(mock, Dymock.exactly(1)));
        Assertions.assertFalse(Dymock.ignited(mock, Dymock.exactly(2)));

        Assertions.assertTrue(Dymock.ignited(mock, Dymock.atLeast(0)));
        Assertions.assertFalse(Dymock.ignited(mock, Dymock.atLeast(2)));

        Assertions.assertTrue(Dymock.ignited(mock, Dymock.atMost(2)));
        Assertions.assertFalse(Dymock.ignited(mock, Dymock.atMost(1)));

        Assertions.assertTrue(Dymock.ignited(mock, Dymock.inRange(0, 2)));
        Assertions.assertFalse(Dymock.ignited(mock, Dymock.inRange(1, 10)));


        //Можно отслеживать вызовы методов с определённым названием
        Assertions.assertTrue(Dymock.ignited(mock, "doSomething", Dymock.exactly(0)));
        Assertions.assertTrue(Dymock.ignited(mock, "echoInt", Dymock.exactly(1)));

        //Можно отслеживать вызовы по сигнатуре метода
        Assertions.assertTrue(Dymock.ignited(mock, "echoInt", Dymock.exactly(1), new Class[0]));

    }

}
