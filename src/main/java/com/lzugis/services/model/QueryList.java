package com.lzugis.services.model;

import com.lzugis.dao.jdbc.annotation.JKey;
import com.lzugis.dao.jdbc.annotation.JTable;

@JTable("as_query_list")
public class QueryList {
    @JKey
    private String id;

    private String name;
    private String user_id;
    private String query_xml;
    private String status;
    private String remarks;

    public String getAutorun() {
        return autorun;
    }

    public void setAutorun(String autorun) {
        this.autorun = autorun;
    }

    private String autorun;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getQuery_xml() {
        return query_xml;
    }

    public void setQuery_xml(String query_xml) {
        this.query_xml = query_xml;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDel_flag() {
        return del_flag;
    }

    public void setDel_flag(String del_flag) {
        this.del_flag = del_flag;
    }

    private String del_flag;
}
