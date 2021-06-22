package com.e.JavaClass;

import java.util.ArrayList;
import java.util.List;

public class JsonMsgSearchManagers {
    private int status;
    private List<Manager> data=new ArrayList<>();
    public int getStatus() {
        return status;
    }

    public List<Manager> getData() {
        return data;
    }
}
