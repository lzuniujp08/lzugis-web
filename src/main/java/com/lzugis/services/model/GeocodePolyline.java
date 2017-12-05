package com.lzugis.services.model;

/**
 * Created by admin on 2017/11/16.
 */
public class GeocodePolyline {
    private String id, linename, pointsdata;
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
    private Integer pointsnum, linetype;

    public String getTableName(){
        return "geocode_polyline";
    }

    public String[] getTableFields(){
        return new String[]{"id", "pointsnum", "linetype", "linename",
                "minx", "miny", "maxx", "maxy", "minzoom", "maxzoom", "pointsdata"};
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLinename() {
        return linename;
    }

    public void setLinename(String linename) {
        this.linename = linename;
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

    public Integer getPointsnum() {
        return pointsnum;
    }

    public void setPointsnum(Integer pointsnum) {
        this.pointsnum = pointsnum;
    }

    public Integer getLinetype() {
        return linetype;
    }

    public void setLinetype(Integer linetype) {
        this.linetype = linetype;
    }
}
