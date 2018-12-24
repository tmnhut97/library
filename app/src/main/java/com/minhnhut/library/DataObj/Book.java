package com.minhnhut.library.DataObj;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Book {
    public String book_id;
    public String book_name;
    public String author;
    public String book_image;

    public Book() {}

    public Book(String book_id, String book_name, String author, String book_image) {
        this.book_id = book_id;
        this.book_name = book_name;
        this.author = author;
        this.book_image = book_image;
    }
}
