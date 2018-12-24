package com.minhnhut.library.DataObj;

import java.io.Serializable;

public class BorrowBook implements Serializable {
    public String book_id;
    public String book_name;
    public String author;
    public String book_image;
    public int quantity;

    public BorrowBook() {}

    public BorrowBook(String book_id, String book_name, String author, String book_image, int quantity) {
        this.book_id = book_id;
        this.book_name = book_name;
        this.author = author;
        this.book_image = book_image;
        this.quantity = quantity;
    }
}
