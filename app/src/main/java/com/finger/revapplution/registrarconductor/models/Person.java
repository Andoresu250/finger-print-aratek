package com.finger.revapplution.registrarconductor.models;

/**
 * Created by Revapplution on 15/03/2018.
 */

public class Person extends Profile{
    public Integer id;
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public String identification;
    public String identificationType;
    public Double rating;
    public PersonProfile profile;

    public Person(String type, Integer id, String firstName, String lastName, String phoneNumber, String identification, String identificationType, Double rating, PersonProfile profile) {
        super(type);
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.identification = identification;
        this.identificationType = identificationType;
        this.rating = rating;
        this.profile = profile;
    }

    public String getFullName(){
        return this.firstName + " " + this.lastName;
    }

    @Override
    public String toString() {

        String s = "Person: \n";
        s += "id: " + id + "\n";
        s += "firstName: " + firstName + "\n";
        s += "lastName: " + lastName + "\n";
        s += "phoneNumber: " + phoneNumber + "\n";
        s += "identification: " + identification + "\n";
        s += "identificationType: " + identificationType + "\n";
        s += "rating: " + rating + "\n";
        s += "profile: " + profile + "\n";
        return s;
    }
}
