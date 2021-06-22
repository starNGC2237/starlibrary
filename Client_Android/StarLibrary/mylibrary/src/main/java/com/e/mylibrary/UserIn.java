package com.e.mylibrary;

import org.litepal.crud.LitePalSupport;


public class UserIn extends LitePalSupport {
    private String userName;
    private String nickName;
    private String userPass;
    private String userPhone;
    private String createTime;
    private int card;
    private String src;
    private int i;

    public int getCard() {
        return card;
    }

    public void setCard(int card) {
        this.card = card;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getCreateTime() {
        return createTime;
    }

    public int getI() {
        return i;
    }
}
