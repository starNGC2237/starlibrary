package com.e.JavaClass.JsonOrder;

import java.util.ArrayList;
import java.util.List;

public class JsonMsgOrder {
    private int status;
    private List<JsonOrder> data=new ArrayList<>();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<JsonOrder> getData() {
        return data;
    }

    public void setData(List<JsonOrder> data) {
        this.data = data;
    }
}
