package com.minhnhut.library.DataObj;

public class BookDetail {
    public String quantity;
    public String category_id;
    public String nxb;
    public String publishing_year;
    public String language;

    public BookDetail() {}

    public BookDetail(String quantity, String category_id, String nxb, String publishing_year, String language) {
        this.quantity = quantity;
        this.category_id = category_id;
        this.nxb = nxb;
        this.publishing_year = publishing_year;
        this.language = language;
    }
}
