package main.hr.kcosic.project.models;

import main.hr.kcosic.project.models.enums.DataType;

import java.io.Serializable;

public class DataWrapper implements Serializable {
    private Object data;
    private DataType type;
    private int senderId;

    public DataWrapper() {
    }

    public DataWrapper(DataType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public DataWrapper(DataType type, Object data, int senderId) {
        this.type = type;
        this.data = data;
        this.senderId = senderId;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

}
