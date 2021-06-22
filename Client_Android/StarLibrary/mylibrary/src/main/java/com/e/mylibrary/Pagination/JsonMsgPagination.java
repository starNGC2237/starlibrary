package com.e.mylibrary.Pagination;


public class JsonMsgPagination {
    private int status;
    private JsonPagination data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public JsonPagination getData() {
        return data;
    }

    public void setData(JsonPagination data) {
        this.data = data;
    }
}
