package com.lzugis.services.model;

import com.lzugis.dao.jdbc.annotation.JIgnore;
import com.lzugis.dao.jdbc.annotation.JKey;
import com.lzugis.dao.jdbc.annotation.JTable;

@JTable("county")
public class County {
    @JKey
    private int gid;

    private double area, perimeter, x, y;
    private String name;

    @JIgnore
    private Object geom;

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getPerimeter() {
        return perimeter;
    }

    public void setPerimeter(double perimeter) {
        this.perimeter = perimeter;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getGeom() {
        return geom;
    }

    public void setGeom(Object geom) {
        this.geom = geom;
    }
}
