package com.zwyl.myfile.http;

public class HttpResult<T> {
    public int resultCode;
    public String resultMsg;

    public T resultInfo;

    public int getResultCode() {
        return resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public T getResultInfo() {
        return resultInfo;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public void setResultInfo(T resultInfo) {
        this.resultInfo = resultInfo;
    }
}