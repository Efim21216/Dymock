package ru.nsu.fit.dymock;


public class BonfireBuilder {
    public static Builder buildBonfire(Object mock) {
        return new BonfireBuilder(). new Builder(mock);
    }

    public class Builder {
        private final Object current;
        private Builder(Object mock) {
            this.current = mock;
        }
        public Builder addStick(Stick stick) {
            if (current instanceof Intercepted) {
                stick.setMethodName(((Intercepted) current).getFullName() + "." + stick.getMethodName() + "()");
                StaticInterceptor.addRule(stick);
            }
            else
                ((InterceptionAccessor) current).getInterceptor().addStick(stick);
            return this;
        }
    }
}
