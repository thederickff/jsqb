package io.github.str4ng3r.common;

public class Template<T> extends Pagination {
    T data;

    public Template(SqlParameter sqlParameter, T data) {
        super(sqlParameter.p);
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        String t = super.toString().substring(1);
        t = "{\n\tdata: " + data + "," + t;
        return t;
    }
}
