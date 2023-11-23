package com.sy.im.model;

public class Person {
    String userId;
    String nickname;
    String imgUrl;
    String gender;
    String remark;
    String signature;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }



    @Override
    public String toString() {
        return "Person{" +
                "userId='" + userId + '\'' +
                ", nickname='" + nickname + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", gender='" + gender + '\'' +
                ", remark='" + remark + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
