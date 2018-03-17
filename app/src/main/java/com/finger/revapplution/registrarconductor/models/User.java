package com.finger.revapplution.registrarconductor.models;

import java.io.Serializable;

/**
 * Created by Revapplution on 14/03/2018.
 */

public class User implements Serializable {

    public Integer id;
    public String username;
    public String email;
    public String state;
    public String type;
    public DateTime createdAt;
    public String token;
    public Profile profile;

    public User(Integer id, String username, String email, String state, String type, DateTime createdAt, String token, Profile profile) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.state = state;
        this.type = type;
        this.createdAt = createdAt;
        this.token = token;
        this.profile = profile;
    }

    @Override
    public String toString() {
        String s = "\n";
        s += ("id: " + id + "\n");
        s += ("username: " + username + "\n");
        s += ("email: " + email + "\n");
        s += ("state: " + state + "\n");
        s += ("type: " + type + "\n");
        s += ("createdA: " + createdAt.toString() + "\n");
        s += ("token: " + token + "\n");
        s += ("profile: " + profile.toString());
        return s;
    }
}
