package com.e.mylibrary.JsonPageReview;

import com.e.mylibrary.Pagination.JsonPagination;

public class JsonMsgReview {
    private int status;
    private JsonPaginationReview data;
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public JsonPaginationReview getData() {
        return data;
    }
    public void setData(JsonPaginationReview data) {
        this.data = data;
    }
}
