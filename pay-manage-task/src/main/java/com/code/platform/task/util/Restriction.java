package com.code.platform.task.util;

public class Restriction<T extends Comparable<T>> {

    public enum Scope {
        PER_ORDER,
        PER_ACCOUNT,
        UNIVERSAL
    }

    private Interval<T> interval;
    private Scope scope;

    public Restriction(Interval<T> interval) {
        this(interval, Scope.PER_ORDER);
    }

    public Restriction(Interval<T> interval, Scope scope) {
        this.interval = interval;
        this.scope = scope;
    }

    public Interval<T> getInterval() {
        return interval;
    }

    public Scope getScope() {
        return scope;
    }

}
