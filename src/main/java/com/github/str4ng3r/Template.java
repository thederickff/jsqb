package com.github.str4ng3r;

public class Template<T> extends Pagination {
    T data;

    public Template(SqlParameter sqlParameter, T data) {
        super(sqlParameter.p);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
