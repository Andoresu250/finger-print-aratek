package com.finger.revapplution.registrarconductor.models;

import java.io.Serializable;

/**
 * Created by Revapplution on 14/03/2018.
 */

public class Profile implements Serializable{

    String type;

    public Profile(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        String s = "\n";
        s += ("     type: " + type);
        return s;
    }
}
