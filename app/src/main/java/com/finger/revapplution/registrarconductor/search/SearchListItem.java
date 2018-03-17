package com.finger.revapplution.registrarconductor.search;

/**
 * Created by Revapplution on 15/03/2018.
 */


public class SearchListItem {
    int id;
    String title;

    public SearchListItem(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String toString() {
        return this.title;
    }
}
