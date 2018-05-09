package com.code.platform.task.util;

/*
 * closed interval, [a, b].
 */
public class Interval<T extends Comparable<T>> {

    private T lower = null;
    private T upper = null;

    /*
     * ( - infinite, + infinite ), infinite always open
     */
    public Interval() { }

    public Interval(T lower, T upper) throws InvalidInterval {
        setLower(lower);
        setUpper(upper);
    }

    public T getLower() {
        return lower;
    }

    public Interval<T> setLower(T lower) throws InvalidInterval {
        validate(lower, upper);
        this.lower = lower;
        return this;
    }

    public T getUpper() {
        return upper;
    }

    public Interval<T> setUpper(T upper) throws InvalidInterval {
        validate(lower, upper);
        this.upper = upper;
        return this;
    }

    public boolean contains(T t) {
        return (lower == null || lower.compareTo(t) <= 0)
                && (upper == null || upper.compareTo(t) >= 0);
    }

    public Interval<T> join(Interval<T> interval) throws InvalidInterval {
        T lo = interval.getLower();
        if (lo != null) {
            if (lower != null && lower.compareTo(lo) > 0)
                lo = lower;
        } else
            lo = lower;
        setLower(lo);

        T up = interval.getUpper();
        if (up != null) {
            if (upper != null && upper.compareTo(up) < 0)
                up = upper;
        } else
            up = upper;
        setUpper(up);

        return this;
    }

    private void validate(T lower, T upper) throws InvalidInterval {
        if (lower != null &&
                upper != null &&
                lower.compareTo(upper) > 0)
            throw new InvalidInterval(lower, upper);
    }

}
