package com.minhnhut.library.DataObj;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Category {
    public String category_id;
    public String category_name;

    public Category() { }

    public Category(String category_id, String category_name) {
        this.category_id = category_id;
        this.category_name = category_name;
    }
}
