package com.lzugis.services.model;

import com.lzugis.dao.jdbc.annotation.JKey;
import com.lzugis.dao.jdbc.annotation.JTable;

/**
 * Created by admin on 2017/11/16.
 */
@JTable("geocode_point")
public class GeocodePoint {
    @JKey
    private String id;

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

    private String name;
}
