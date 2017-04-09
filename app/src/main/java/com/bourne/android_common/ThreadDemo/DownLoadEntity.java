package com.bourne.android_common.ThreadDemo;


public class DownLoadEntity {
    public DownLoadEntity(String name) {
        this.name = name;
    }

    private String name;
    private int count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
