package com.finger.revapplution.registrarconductor.models;

/**
 * Created by Revapplution on 15/03/2018.
 */

public class TransportCompany extends Profile{

    public Integer id;
    public String name;
    public String nit;
    public String address;

    public TransportCompany(String type, Integer id, String name, String nit, String address) {
        super(type);
        this.id = id;
        this.name = name;
        this.nit = nit;
        this.address = address;
    }

    @Override
    public String toString() {
        String s = "Empresa de transporte: \n";
        s += "id: " + id + "\n";
        s += "name: " + name + "\n";
        s += "nit: " + nit + "\n";
        s += "address: " + address + "\n";
        return s;
    }
}
