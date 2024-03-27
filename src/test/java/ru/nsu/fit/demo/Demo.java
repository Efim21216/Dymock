package ru.nsu.fit.demo;

import java.math.BigInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ru.nsu.fit.FinalClass;
import ru.nsu.fit.StaticMethod;
import ru.nsu.fit.dymock.BonfireBuilder;
import ru.nsu.fit.dymock.Dymock;
import ru.nsu.fit.dymock.bytebuddy.Intercepted;
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

        //Порядок применения пересекающихся правил на аргументы:
        //От последнего заданного к наиболее ранее заданному
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
        //Такие правила будут применены ко всем перегруженным функциям, чьи аргументы удовлетворяют условию
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
        //emptyArgs - если у метода нет аргументов
        Assertions.assertTrue(Dymock.ignited(mock, "echoInt", Dymock.exactly(1), Dymock.emptyArgs()));
        Assertions.assertTrue(Dymock.ignited(mock, "echoInt", Dymock.exactly(0), int.class));
        Assertions.assertTrue(Dymock.ignited(mock, "echoInt", Dymock.exactly(0), int.class, int.class));

        //Кроме того, вызовы можно посчитать у конкретного Stick
        Stick stick = new Stick("echoInt", 2, Leaf.any());
        BonfireBuilder.buildBonfire(mock)
                .addStick(stick);
        mock.echoInt(2);
        mock.echoInt(2.0);

        Assertions.assertTrue(stick.bask(Dymock.exactly(2)));
    }

    @Test
    void demoStaticAndFinal() {
        //Final классы и классы со Static методами похожы тем, что нельзя 
        //наследоваться от них, переопределив необходимые методы

        //Тем не менее, все описанные операции можно проделывать с ними
        //В случае класса со статичными методами пользователи могут получить объекта 
        //класса Intercepted<>, чтобы управлять и проверять поведения мока 
        Intercepted<AddOperations> mock = Dymock.burnDown(AddOperations.class);

        //Правила сложения для целых чисел
        Stick intStick = new Stick("add", 1, Leaf.any(Integer.class), Leaf.any(Integer.class));
        //чисел c плавающей точкой 
        Stick doubleStick = new Stick("add", 1.1, Leaf.any(Double.class), Leaf.any(Double.class));
        //и bigInteger
        BigInteger bigIntValue = BigInteger.valueOf(1);
        Stick bigIntStick = new Stick("add", bigIntValue, Leaf.eq(bigIntValue), Leaf.eq(bigIntValue));

        BonfireBuilder.buildBonfire(mock)
                .addStick(intStick)
                .addStick(doubleStick)
                .addStick(bigIntStick);


        //Вызовы происходят как обычно
        Assertions.assertEquals(1, AddOperations.add(1,1));
        Assertions.assertEquals(1.1, AddOperations.add(1.,1.));
        Assertions.assertEquals(BigInteger.valueOf(1), AddOperations.add(bigIntValue,bigIntValue));
        

        //Проверка числа вызовов также аналогична
        Assertions.assertTrue(Dymock.ignited(mock, "add", Dymock.exactly(1), Integer.class, Integer.class));
        Assertions.assertTrue(Dymock.ignited(mock, "add", Dymock.exactly(1), Double.class, Double.class));
        Assertions.assertTrue(Dymock.ignited(mock, "add", Dymock.exactly(3)));


        //Мок Final тоже работает, ничем не отличаясь от обычной процедуры 
        Rectangle mockFinal = Dymock.burn(Rectangle.class);
        BonfireBuilder.buildBonfire(mockFinal)
                .addStick(new Stick("area", 100));
        
        mockFinal.setHeight(0);
        mockFinal.setWidth(0);
        Assertions.assertEquals(100, mockFinal.area());
        Assertions.assertTrue(Dymock.ignited(mockFinal, "area"));
    }

    @Test
    void demoSpy(){
        //Если мы хотим сохранить поведение объекта или класса,
        //но изменить в нём конкретные детали - используем spy
        CallCounting mockFinal = Dymock.spy(CallCounting.class);
        BonfireBuilder.buildBonfire(mockFinal)
        //Задаём условие на все вызовы с одним аргументом
                .addStick(new Stick("echoInt", 100, Leaf.any()))
        //Задаём условие на конкретные аргументы. Для всех остальных значений, поведение не изменится
                .addStick(new Stick("echoInt", 2, Leaf.eq(0), Leaf.eq(0)));
        
        Assertions.assertEquals(100, mockFinal.echoInt(1));
        Assertions.assertEquals(2, mockFinal.echoInt(0, 0));
        Assertions.assertEquals(20, mockFinal.echoInt(10, 10));


        //Даже если наш вызов не удовлетворил ни одному их условий (stick),
        //этот вызов учтётся при подсчёте общего числа
        Assertions.assertTrue(Dymock.ignited(mockFinal, "echoInt", Dymock.exactly(3)));


        //Классы со статичными методами также можно не "разрушать"
        Intercepted<AddOperations> mockStatic = Dymock.spyStatic(AddOperations.class);

        BonfireBuilder.buildBonfire(mockStatic)
        //Добавляем правило для сложения только целых чисел 
                .addStick(new Stick("add", 1, Leaf.any(Integer.class), Leaf.any(Integer.class)));
        
        Assertions.assertEquals(1, AddOperations.add(1,1));
        Assertions.assertEquals(2, AddOperations.add(1.,1.));
    }

    @Test
    void demoCustomLeaf(){
        //Матчер, который ищет, есть ли в переданной строке "плохие" символы
        HelloWorld helloWorld = Dymock.spy(HelloWorld.class);
        String result = "Unsupported numbers";
        BonfireBuilder.buildBonfire(helloWorld)
                .addStick(new Stick("concat", result,
                        //Задаём правило, когда в первом аргументе числа
                        new StringMatcher("1234567890"), 
                        Leaf.any()))
                .addStick(new Stick("concat", result,
                        Leaf.any(), 
                        //Задаём правило, когда во втором аргументе числа
                        new StringMatcher("1234567890")));

                        
        Assertions.assertEquals("ab", helloWorld.concat("a", "b"));
        Assertions.assertEquals(result, helloWorld.concat("a2", "b"));
        Assertions.assertEquals(result, helloWorld.concat("a", "b2"));
    }
}
