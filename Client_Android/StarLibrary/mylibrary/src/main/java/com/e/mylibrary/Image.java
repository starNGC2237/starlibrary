package com.e.mylibrary;

public class Image {
    private int status;
    private DataImage data;

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setData(DataImage data) {
        this.data = data;
    }

    public DataImage getData() {
        return data;
    }
}
