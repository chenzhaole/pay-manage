package com.code.platform.task.util;

public interface RestrictionChain<T extends Comparable<T>> {

    RestrictionChain<T> add(Restriction<T> restriction) throws InvalidInterval;

    Interval<T> getInterval();

}
