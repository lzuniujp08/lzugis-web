package com.lzugis.services.model;

/**
 * Created by admin on 2017/11/16.
 */
public class GeocodePolygon {
    private String id, areaname, pointsdata;
    private Double minx;
    private Double miny;
    private Double maxx;
    private Double maxy;
    private Double minzoom;

    public Double getMinzoom() {
        return minzoom;
    }

    public void setMinzoom(Double minzoom) {
        this.minzoom = minzoom;
    }

    public Double getMaxzoom() {
        return maxzoom;
    }

    public void setMaxzoom(Double maxzoom) {
        this.maxzoom = maxzoom;
    }

    private Double maxzoom;
    private Integer pointsnum;

    public String getTableName(){
        return "geocode_polygon";
    }

    public String[] getTableFields(){
        return new String[]{"id", "pointsnum", "areaname",
            "minx", "miny", "maxx", "maxy", "minzoom", "maxzoom", "pointsdata"};
    }

    public String getAreaname() {
        return areaname;
    }

    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }

    public Integer getPointsnum() {
        return pointsnum;
    }

    public void setPointsnum(Integer pointsnum) {
        this.pointsnum = pointsnum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPointsdata() {
        return pointsdata;
    }

    public void setPointsdata(String pointsdata) {
        this.pointsdata = pointsdata;
    }

    public Double getMinx() {
        return minx;
    }

    public void setMinx(Double minx) {
        this.minx = minx;
    }

    public Double getMiny() {
        return miny;
    }

    public void setMiny(Double miny) {
        this.miny = miny;
    }

    public Double getMaxx() {
        return maxx;
    }

    public void setMaxx(Double maxx) {
        this.maxx = maxx;
    }

    public Double getMaxy() {
        return maxy;
    }

    public void setMaxy(Double maxy) {
        this.maxy = maxy;
    }
}
