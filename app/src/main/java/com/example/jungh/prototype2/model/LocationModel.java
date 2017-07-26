package com.example.jungh.prototype2.model;

public class LocationModel {
    private int code;
    private String msg;
    private LocationDetailModel data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public LocationDetailModel getData() {
        return data;
    }

    public void setData(LocationDetailModel data) {
        this.data = data;
    }
}

