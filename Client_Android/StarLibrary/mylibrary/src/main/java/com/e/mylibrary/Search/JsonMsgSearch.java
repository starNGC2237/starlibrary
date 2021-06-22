package com.e.mylibrary.Search;

import com.e.mylibrary.Book;

import java.util.ArrayList;
import java.util.List;

public class JsonMsgSearch {
    private int status;
    private List<Book> data=new ArrayList<>();
    public int getStatus() {
        return status;
    }

    public List<Book> getData() {
        return data;
    }
}
