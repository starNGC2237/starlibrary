package com.e.JavaClass;

import org.litepal.crud.LitePalSupport;

public class UserMangers extends LitePalSupport {
    private String userName;
    private String nickName;
    private String userPass;
    private String userPhone;
    private String createTime;
    private String floor;
    private int i;

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }


    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

}
