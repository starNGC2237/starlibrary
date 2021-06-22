package com.e.mylibrary.JsonPageReview;

import com.e.mylibrary.Book;

import java.util.ArrayList;
import java.util.List;

public class JsonPaginationReview {
    private int pageNum;
    private int pageSize;
    private int size;
    private int startRow;
    private int endRow;
    private int total;
    private int pages;
    private List<JsonReview> list=new ArrayList<>();

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<JsonReview> getList() {
        return list;
    }

    public void setList(List<JsonReview> list) {
        this.list = list;
    }
}
