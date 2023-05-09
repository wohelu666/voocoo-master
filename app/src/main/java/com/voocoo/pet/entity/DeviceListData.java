package com.voocoo.pet.entity;

import java.util.List;

public class DeviceListData {
    private String page;
    private String pageSize;
    private List<Device> records;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public List<Device> getRecords() {
        return records;
    }

    public void setRecords(List<Device> records) {
        this.records = records;
    }
}
