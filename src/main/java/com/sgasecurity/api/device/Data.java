package com.sgasecurity.api.device;

import com.sgasecurity.api.DeviceRow;

import java.util.List;

public class Data {
    private int pageSize;
    private List<DeviceRow> rows;
    private int total;
    private int totalPage;
    private int page;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<DeviceRow> getRows() {
        return rows;
    }

    public void setRows(List<DeviceRow> rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
