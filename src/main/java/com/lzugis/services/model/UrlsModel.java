package com.lzugis.services.model;

import com.lzugis.dao.jdbc.annotation.JKey;
import com.lzugis.dao.jdbc.annotation.JTable;

@JTable("base_urls")
public class UrlsModel {
    @JKey
    private String id;

    private String pid, title, url, expand;

    private boolean isshow, isopen, issenior;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExpand() {
        return expand;
    }

    public void setExpand(String expand) {
        this.expand = expand;
    }

    public boolean isIsshow() {
        return isshow;
    }

    public void setIsshow(boolean isshow) {
        this.isshow = isshow;
    }

    public boolean isIsopen() {
        return isopen;
    }

    public void setIsopen(boolean isopen) {
        this.isopen = isopen;
    }

    public boolean isIssenior() {
        return issenior;
    }

    public void setIssenior(boolean issenior) {
        this.issenior = issenior;
    }

}
