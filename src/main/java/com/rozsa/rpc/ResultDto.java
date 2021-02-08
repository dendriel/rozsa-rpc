package com.rozsa.rpc;

class ResultDto {
    Object res;

    public ResultDto() {}

    public ResultDto(Object res) {
        this.res = res;
    }

    public Object getRes() {
        return res;
    }

    public void setRes(Object res) {
        this.res = res;
    }
}
