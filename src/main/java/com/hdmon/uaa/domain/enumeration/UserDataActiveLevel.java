package com.hdmon.uaa.domain.enumeration;

/**
 * Created by UserName on 7/24/2018.
 */
public enum UserDataActiveLevel {
    NONE(0),
    MOBILE(1),
    EMAIL(2),
    MOBILE_EMAIL(3),
    OPEN_ID(4);

    private final int value;

    UserDataActiveLevel(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
