package ru.nsu.fit.dymock;


import ru.nsu.fit.dymock.bytebuddy.FinalInterceptor;
import ru.nsu.fit.dymock.bytebuddy.Intercepted;
import ru.nsu.fit.dymock.bytebuddy.InterceptionAccessor;
import ru.nsu.fit.dymock.bytebuddy.StaticInterceptor;
import ru.nsu.fit.dymock.matchers.Stick;

import java.lang.reflect.Modifier;

public class BonfireBuilder {
    public static Builder buildBonfire(Object mock) {
        if(!(mock instanceof Intercepted || mock instanceof InterceptionAccessor ||
                Modifier.isFinal(mock.getClass().getModifiers()))){
            throw new IllegalStateException("Object " + mock + " is not a burned (mocked) object");
        }
        return new BonfireBuilder(). new Builder(mock);
    }

    public class Builder {
        private final Object current;
        private Builder(Object mock) {
            this.current = mock;
        }
        public Builder addStick(Stick stick) {
            if (current instanceof Intercepted) {
                StaticInterceptor.addStick(stick, ((Intercepted<?>) current).getClazz());
            }
            else if (current instanceof InterceptionAccessor) {
                ((InterceptionAccessor) current).getInterceptor().addStick(stick);
            }
            else
                FinalInterceptor.addStick(stick, current);

            return this;
        }
    }
}
