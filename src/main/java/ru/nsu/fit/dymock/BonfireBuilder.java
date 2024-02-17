package ru.nsu.fit.dymock;


import java.util.List;

public class BonfireBuilder {
    private static List<Stick> sticks;
    public static Builder buildBonfire(Object mock) {
        return new BonfireBuilder(). new Builder();
    }

    public static List<Stick> getSticks() {
        return sticks;
    }
    public static void setSticks(List<Stick> s) {
        sticks = s;
    }

    public class Builder {
        private Builder() {

        }
        public <R> Builder addStick(Stick stick) {
            sticks.add(stick);
            return this;
        }
    }
}
