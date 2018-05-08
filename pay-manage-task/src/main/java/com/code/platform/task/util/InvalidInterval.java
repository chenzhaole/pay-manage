package com.code.platform.task.util;

public class InvalidInterval extends Exception {

    private static final long serialVersionUID = -7376091276788787024L;

    private Object lower = null, upper = null;

    public InvalidInterval(Object lower, Object upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public Object getLower() {
        return lower;
    }

    public Object getUpper() {
        return upper;
    }

}
