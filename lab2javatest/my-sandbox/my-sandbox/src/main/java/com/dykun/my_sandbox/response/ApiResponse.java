package com.dykun.my_sandbox.response;

import java.util.List;

public class ApiResponse<M, T> {
    private M meta;
    private List<T> data;

    public M getMeta() { return meta; }
    public void setMeta(M meta) { this.meta = meta; }
    
    public List<T> getData() { return data; }
    public void setData(List<T> data) { this.data = data; }
}
