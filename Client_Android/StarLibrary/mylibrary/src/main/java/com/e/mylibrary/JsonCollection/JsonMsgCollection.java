package com.e.mylibrary.JsonCollection;

import java.util.ArrayList;
import java.util.List;

public class JsonMsgCollection {
    private int status;
    private List<JsonCollection> data=new ArrayList<>();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<JsonCollection> getData() {
        return data;
    }

    public void setData(List<JsonCollection> data) {
        this.data = data;
    }
}
