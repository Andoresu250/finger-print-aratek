package com.finger.revapplution.registrarconductor.models;

/**
 * Created by Revapplution on 14/03/2018.
 */

public class SuperAdmin extends Profile {

    Integer id;


    public SuperAdmin(String type, Integer id) {
        super(type);
        this.id = id;
    }

    @Override
    public String toString() {
        String s = "\n";
        s += ("     SuperAdmin\n");
        s += ("     id: " + id + "\n");
        s += ("     type: " + type + "\n");
        return s;
    }
}
