package com.lzugis.services.model;

/**
 * Created by admin on 2017/11/20.
 */
public class GeocodeShpInfo {
    private String id, shppath, geomtype, memo;
    private Integer isparse;

    public Integer getGeomcount() {
        return geomcount;
    }

    public void setGeomcount(Integer geomcount) {
        this.geomcount = geomcount;
    }

    private Integer geomcount;

    public String getTableName(){return "geocode_shpinfo";}

    public String[] getTableFields(){
        return new String[]{"id", "shppath",
            "geomtype", "geomcount", "isparse", "memo"};
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShppath() {
        return shppath;
    }

    public void setShppath(String shppath) {
        this.shppath = shppath;
    }

    public String getGeomtype() {
        return geomtype;
    }

    public void setGeomtype(String geomtype) {
        this.geomtype = geomtype;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getIsparse() {
        return isparse;
    }

    public void setIsparse(Integer isparse) {
        this.isparse = isparse;
    }
}
