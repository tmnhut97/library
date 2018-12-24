package com.minhnhut.library.DataObj;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Image {

    public String book_id;
    public String url;

    public Image() {}

    public Image(String book_id, String url) {
        this.book_id = book_id;
        this.url = url;
    }
}
