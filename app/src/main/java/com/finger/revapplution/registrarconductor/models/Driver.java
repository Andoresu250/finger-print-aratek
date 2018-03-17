package com.finger.revapplution.registrarconductor.models;

/**
 * Created by Revapplution on 15/03/2018.
 */

public class Driver extends PersonProfile {

    public Integer id;
    public String fingerprint;
    public TransportCompany transportCompany;

    public Driver(String type, Integer id, String fingerprint, TransportCompany transportCompany) {
        super(type);
        this.id = id;
        this.fingerprint = fingerprint;
        this.transportCompany = transportCompany;
    }

    @Override
    public String toString() {
        String s = "Driver: \n";
        s += "id: " + id + "\n";
        s += "fingerprint: " + fingerprint + "\n";
        s += "transportCompany: " + transportCompany + "\n";
        return s;
    }
}
