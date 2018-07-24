package com.hdmon.uaa.domain.enumeration;

/**
 * Created by UserName on 7/24/2018.
 */
public enum UserDataMarriage {
    UNKNOW(0),
    NO(1),
    YES(2);

    private final int value;

    UserDataMarriage(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
