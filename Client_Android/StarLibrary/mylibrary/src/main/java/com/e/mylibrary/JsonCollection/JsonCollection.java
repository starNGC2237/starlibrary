package com.e.mylibrary.JsonCollection;

import com.e.mylibrary.Book;

import java.util.ArrayList;
import java.util.List;

public class JsonCollection {
    private int collectionId;
    private int card;
    private int bookid;
    private List<Book> book=new ArrayList<>();

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    public int getCard() {
        return card;
    }

    public void setCard(int card) {
        this.card = card;
    }

    public int getBookid() {
        return bookid;
    }

    public void setBookid(int bookid) {
        this.bookid = bookid;
    }

    public List<Book> getBook() {
        return book;
    }

    public void setBook(List<Book> book) {
        this.book = book;
    }
}
