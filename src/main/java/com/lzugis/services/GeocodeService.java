package com.lzugis.services;

import com.lzugis.dao.GeocodeDao;
import com.lzugis.helper.*;
import com.lzugis.services.model.*;
import com.lzugis.services.utils.GeoHash;
import com.lzugis.services.utils.GeocodeUtil;
import com.lzugis.services.utils.ShpUtil;
import org.json.simple.JSONArray;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by admin on 2017/11/13.
 */
@Service
public class GeocodeService {
    @Autowired
    private GeocodeDao geoDao;
    private static ShpUtil shpUtil;
    private static GeocodeUtil geoUtil;
    private static GeoHash geoHash;

    private static GeocodePolygon polygon;
    private static GeocodePolyline polyline;
    private static GeocodePoints points;
    private static GeocodeShpInfo shpInfo;
    private static List rows;
    private static String saveTime = "";

    public GeocodeService(){
        super();
        shpUtil = new ShpUtil();
        geoUtil = new GeocodeUtil();
        geoHash = new GeoHash();

        polygon = new GeocodePolygon();
        polyline = new GeocodePolyline();
        points = new GeocodePoints();
        shpInfo = new GeocodeShpInfo();
        rows = new ArrayList();
    }

    public Map getLonlatGeocode(double lon, double lat, int zoom){
        return geoUtil.getAreaFromLonlat(lon, lat, zoom);
    }

    public Map getPoiByLonlat(double lon, double lat, int zoom){
        return geoUtil.getPoiFromLonlat(lon, lat, zoom);
    }

    public Map getGeocodeArea(int limit, int offset){
        String filter = " limit ? offset ?";
        Object[] paras = new Object[]{limit, offset};
        rows = geoDao.getDataByFilter(polygon.getTableName(), polygon.getTableFields(), filter, paras);
        int total = geoDao.getCountByFilter(
                polygon.getTableName(),
                new String[]{},
                new String[]{}
        );
        Map map = new HashMap();
        map.put("total", total);
        map.put("rows", rows);
        return map;
    }

    public Map getGeocodePoi(int limit, int offset){
        String filter = " ORDER BY RANDOM() limit ? offset ?";
        Object[] paras = new Object[]{limit, offset};
        if(rows.size()==0) {
            rows = geoDao.getDataByFilter(points.getTableName(), points.getTableFields(), filter, paras);
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmm");
            saveTime = formatter.format(date);
//            writeData2File();
        }
        int total = geoDao.getCountByFilter(
                points.getTableName(),
                new String[]{},
                new String[]{}
        );
        Map map = new HashMap();
        map.put("total", total);
        map.put("rows", rows);

        return map;
    }

    public String getDownFile(String type){
        String savePath = CommonConfig.getVal("geocode.savepath");
        String filePath = savePath+ File.separator+saveTime+"."+type;
        File file = new File(filePath);
        if(!file.exists())writeData2File(type);

        type ="shp".equals(type)?"zip":type;
        return savePath+ File.separator+saveTime+"."+type;
    }

    private void writeData2File(String type){
        CsvHelper csvHelper = new CsvHelper();
        ExcelHelper xlsHelper = new ExcelHelper();
        ShapeHelper shpHelper = new ShapeHelper();

        String savePath = CommonConfig.getVal("geocode.savepath"),
                shpPath = savePath + saveTime + ".shp",
                csvPath = savePath + saveTime + ".csv",
                xlsPath = savePath + saveTime + ".xls";

        if("shp".equals(type)) {
            shpHelper.write2ShapeFile(shpPath, points.getTableFields(), rows);
        }else if("xls".equals(type)) {
            xlsHelper.write2ExcelFile(xlsPath, points.getTableFields(), rows);
        }else{
            csvHelper.write2CsvFile(csvPath, points.getTableFields(), rows);
        }
    }

    public Map saveQueryList(QueryList qlist){
        Map result = new HashMap();
        try{
            QueryList _qlist = (QueryList)geoDao.get(QueryList.class, qlist.getName(), "name");
            if(null == _qlist){
                qlist.setId(UUID.randomUUID().toString());
                int r = geoDao.save(qlist);
                if(r>0){
                    result.put("code", "200");
                    result.put("msg", qlist.getName()+"保存成功！");
                    result.put("data",null);
                }else{
                    result.put("code", "500");
                    result.put("msg", qlist.getName()+"保存失败！");
                    result.put("data",null);
                }
            }
            else{
                result.put("code", "200");
                result.put("msg", qlist.getName()+"保存成功！");
                result.put("data",null);
            }
        }catch (Exception e){
            result.put("code", "500");
            result.put("msg", e.getMessage());
            result.put("data",null);
        }
        return result;
    }

    public Map getQueryList(){
        Map result = new HashMap();
        try{
            List list = geoDao.selectForList("select * from "+geoDao.table(QueryList.class));
            result.put("code", "200");
            result.put("msg", "查询成功！");
            result.put("data", JSONArray.toJSONString(list));
        }catch (Exception e){
            result.put("code", "500");
            result.put("msg", e.getMessage());
            result.put("data",null);
        }
        return result;
    }

    public List searchByDist(double lon, double lat, double dist){
        List list = new ArrayList();
        int precision = geoHash.effectnum(dist);
        String strGeohash = geoHash.encode(lat, lon, 0);
        String filters = "where geohash like '"+strGeohash.substring(0, precision)+"%'";
        List _list = geoDao.getDataByFilter(points.getTableName(), points.getTableFields(), filters, new Object[]{});
        for(int i=0;i<_list.size();i++){
            Map map = (Map)_list.get(i);
            String _geohash = map.get("geohash").toString();
            double _dist = geoHash.distance(strGeohash, _geohash);
            if(_dist<dist)list.add(map);
        }
        return list;
    }

    /**
     * 程序主函数，主要处理shp数据的入库
     * @param args
     */
    public static void main(String[] args){
        GeocodeService geoS = new GeocodeService();
//        geoDao = new GeocodeDao();
        long start = System.currentTimeMillis();

//        Map result = geoUtil.getAreaFromLonlat(105.83521, 23.42288, 7 );
//        System.out.println(result.get("areaname").toString());

        List list = geoS.searchByDist(116.399, 39.969, 5000);
        System.out.println(JSONArray.toJSONString(list));

//        parseData2Db();

        System.out.println("共耗时"+(System.currentTimeMillis() - start)+"ms");
    }
    /**
     * shp数据解析入库
     */
    private static void parseData2Db(){
        GeocodeDao dao = new GeocodeDao();
        List listShps = shpUtil.listShapeFiles();
        for(int i = 0 ; i < listShps.size() ; i++) {
            String shpPath = listShps.get(i).toString();

            int dataSize = dao.getCountByFilter(
                    shpInfo.getTableName(),
                    new String[]{"shppath", "isparse"},
                    new Object[]{shpPath, 1}
            );

            //如果还未扫描
            if (dataSize==0) {
                System.out.println(shpPath+"扫描入库中。。。");

                List<SimpleFeature> features = shpUtil.getShapeFeatures(shpPath);

                String geomType = shpUtil.getGeomtype(features.get(0));
                StringBuffer unionSql = new StringBuffer();
                int count = 0;
                for (int j = 0; j < features.size(); j++) {
                    count++;
                    Object[] values = shpUtil.getFeatureInfo(features.get(j));
                    unionSql.append("select ");
                    for(int k=0;k<values.length;k++){
                        Object val = values[k];
                        unionSql.append(val.getClass()==String.class?"'"+val+"'":val);
                        if(k!=values.length-1)unionSql.append(", ");
                    }
                    if(count%500==0){
                        saveData2Table(geomType, dao, unionSql);
                        unionSql = new StringBuffer();
                        count = 0;
                    }
                    else{
                        if(j!=features.size()-1)unionSql.append(" union ");
                    }
                }
                saveData2Table(geomType, dao, unionSql);
                //插入shpinfo, "id", "shppath", "geomtype", "isparse", "memo"
                Object[] shpValues = new Object[]{
                        UUID.randomUUID().toString(),
                        shpPath,
                        geomType,
                        features.size(),
                        1,
                        ""
                };
                dao.insert(shpInfo.getTableName(), shpInfo.getTableFields(), shpValues);
            }else{
                System.out.println(shpPath+"已入库！");
            }
        }
    }

    /**
     * 保存数据
     * @param geomType
     * @param dao
     * @param unionSql
     */
    private static void saveData2Table(String geomType, GeocodeDao dao, StringBuffer unionSql){
        if(geomType.indexOf("polygon")!=-1){
            dao.insert(polygon.getTableName(), polygon.getTableFields(), unionSql.toString());
        }
        else if(geomType.indexOf("polyline")!=-1){
            dao.insert(polyline.getTableName(), polyline.getTableFields(), unionSql.toString());
        }
        else{
            dao.insert(points.getTableName(), points.getTableFields(), unionSql.toString());
        }
    }
}
