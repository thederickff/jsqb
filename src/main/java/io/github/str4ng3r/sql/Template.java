package io.github.str4ng3r.sql;

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
}
