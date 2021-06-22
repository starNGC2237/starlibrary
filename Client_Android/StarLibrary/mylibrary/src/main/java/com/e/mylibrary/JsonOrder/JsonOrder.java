package com.e.mylibrary.JsonOrder;

import com.e.mylibrary.Book;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JsonOrder {
    private String orderName;
    private int borrowId;
    private String floor;
    private int card;
    private Date rgTime;
    private Date unrgTime;
    private Date boTime;
    private Date unboTime;
    private List<Book> book=new ArrayList<>();
    private int readed;
    private int type=-1;
    private int readyed;
    private int sweeped;
    private int showed;
    private int recall;
    private int remind;

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public int getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(int borrowId) {
        this.borrowId = borrowId;
    }

    public int getCard() {
        return card;
    }

    public void setCard(int card) {
        this.card = card;
    }

    public Date getRgTime() {
        return rgTime;
    }

    public void setRgTime(Date rgTime) {
        this.rgTime = rgTime;
    }

    public Date getUnrgTime() {
        return unrgTime;
    }

    public void setUnrgTime(Date unrgTime) {
        this.unrgTime = unrgTime;
    }

    public Date getBoTime() {
        return boTime;
    }

    public void setBoTime(Date boTime) {
        this.boTime = boTime;
    }

    public Date getUnboTime() {
        return unboTime;
    }

    public void setUnboTime(Date unboTime) {
        this.unboTime = unboTime;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public List<Book> getBook() {
        return book;
    }

    public void setBook(List<Book> book) {
        this.book = book;
    }

    public int getReaded() {
        return readed;
    }

    public void setReaded(int readed) {
        this.readed = readed;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getReadyed() {
        return readyed;
    }

    public void setReadyed(int readyed) {
        this.readyed = readyed;
    }

    public int getSweeped() {
        return sweeped;
    }

    public void setSweeped(int sweeped) {
        this.sweeped = sweeped;
    }

    public int getShowed() {
        return showed;
    }

    public void setShowed(int showed) {
        this.showed = showed;
    }

    public int getRecall() {
        return recall;
    }

    public void setRecall(int recall) {
        this.recall = recall;
    }

    public int getRemind() {
        return remind;
    }

    public void setRemind(int remind) {
        this.remind = remind;
    }
}
