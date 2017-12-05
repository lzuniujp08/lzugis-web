package com.lzugis.services.model;

/**
 * Created by admin on 2017/11/16.
 */
public class GeocodePoints {
    private String id, poiname, pointsdata;
    private Double x;

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    private Double y;
    private Double minzoom;

    public String getTableName(){
        return "geocode_point";
    }

    public String[] getTableFields(){
        return new String[]{"id", "poiname",
                "x", "y", "minzoom", "maxzoom"};
    }

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPoiname(String poiname) {
        this.poiname = poiname;
    }

    public String getPointsdata() {
        return pointsdata;
    }

    public void setPointsdata(String pointsdata) {
        this.pointsdata = pointsdata;
    }

    public String getPoiname() {
        return poiname;
    }
}
