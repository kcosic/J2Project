package main.hr.kcosic.project.models;

import main.hr.kcosic.project.models.enums.DataType;

import java.io.Serializable;

public class DataWrapper implements Serializable {
    private Object data;
    private DataType type;

    public DataWrapper() {
    }

    public DataWrapper(DataType type, Object data) {
        this.type = type;
        this.data = data;
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
}
