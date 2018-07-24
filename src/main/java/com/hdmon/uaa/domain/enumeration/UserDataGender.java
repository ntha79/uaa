package com.hdmon.uaa.domain.enumeration;

/**
 * Created by UserName on 7/24/2018.
 */
public enum UserDataGender {
    UNKNOW(0),
    MALE(1),
    FEMALE(2);

    private final int value;

    UserDataGender(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
