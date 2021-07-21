package main.hr.kcosic.project.models;

import java.io.Serializable;

public class Snake implements Serializable
{

    public static int getSnakeStartColumn() {
        return 0;
    }

    public static int getSnakeStartRow() {
        return 2;
    }

    public static int getSnakeEndColumn() {
        return 2;
    }

    public static int getSnakeEndRow() {
        return 0;
    }

}
