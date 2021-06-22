package com.e.javaBean;

import java.util.Date;

public class Data {

        private String nickName;
        private String userName;
        private String passWord;
        private String phone;
        private Date createTime;
        private int card;

    public int getCard() {
        return card;
    }

    public void setCard(int card) {
        this.card = card;
    }

    public void setNickName(String nickName) {
            this.nickName = nickName;
        }
        public String getNickName() {
            return nickName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
        public String getUserName() {
            return userName;
        }

        public void setPassWord(String passWord) {
            this.passWord = passWord;
        }
        public String getPassWord() {
            return passWord;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
        public String getPhone() {
            return phone;
        }

        public void setCreateTime(Date createTime) {
            this.createTime = createTime;
        }
        public Date getCreateTime() {
            return createTime;
        }
}
