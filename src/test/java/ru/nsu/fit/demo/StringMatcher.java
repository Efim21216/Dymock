package ru.nsu.fit.demo;

import ru.nsu.fit.dymock.matchers.LeafMatcher;

public class StringMatcher extends LeafMatcher{
    private String badChars;

    //Конструктор может быть любым
    public StringMatcher(String badChars){
        //Необходимо вызвать конструктор LeafMatcher, проверяющий тип аргумента
        //Если проверять тип не нужно нужно написать super(Object.class);
        super(String.class);
        this.badChars = badChars;
    }

    //Смысловая часть - переопределение метода matches
    @Override
    public boolean matches(Object actual) {
        //Если нужно проверить тип аргумента - вызываем родительский метод
        if(!super.matches(actual))
            return false;

        //Основная логика матчера: Никакой из символов строки не "плохой"
        return ((String) actual).codePoints().anyMatch(c -> badChars.indexOf((char) c) != -1);
    }
}
