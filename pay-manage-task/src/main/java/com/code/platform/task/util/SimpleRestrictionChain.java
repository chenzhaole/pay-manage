package com.code.platform.task.util;

/*
 * PER_ORDER, last PER_ACCOUNT and last UNIVERSAL matter.
 */
public class SimpleRestrictionChain<T extends Comparable<T>> implements RestrictionChain<T> {

    private Interval<T> perOrder = new Interval<T>();
    private Interval<T> perAccount = new Interval<T>();
    private Interval<T> universal = new Interval<T>();
    private Interval<T> intv = new Interval<T>();

    public RestrictionChain<T> add(Restriction<T> restriction) throws InvalidInterval {
        switch (restriction.getScope()) {
            case PER_ORDER:
                perOrder.join(restriction.getInterval());
                break;
            case PER_ACCOUNT:
                perAccount = restriction.getInterval();
                break;
            case UNIVERSAL:
                universal = restriction.getInterval();
                break;
        }
        intv = new Interval<T>().join(perOrder).join(perAccount).join(universal);
        return this;
    }

    public Interval<T> getInterval() {
        return intv;
    }

}
