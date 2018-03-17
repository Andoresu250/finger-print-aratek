package com.finger.revapplution.registrarconductor.models;

import java.util.Date;

/**
 * Created by Revapplution on 14/03/2018.
 */

public class DateTime extends java.util.Date {

    public DateTime(long readLong) {
        super(readLong);
    }

    public DateTime(Date date) {
        super(date.getTime());
    }
}