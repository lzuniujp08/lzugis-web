package com.lzugis.services;

import com.lzugis.dao.TestDao;
import com.lzugis.dao.jdbc.util.AnnotationUtil;
import com.lzugis.services.model.GeocodePoint;
import org.apache.commons.lang.StringUtils;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.DirectPosition2D;
import org.json.simple.JSONObject;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.ResultSet;
import java.util.*;

/**
 * Created by admin on 2017/11/13.
 */
@Service
public class TestService {
    @Autowired
    private TestDao testDao;

    private String demPath = "D:\\lzugis\\geotools\\data\\img\\beijing1.tif";

    public Double calcNums(double num1, double num2, String type){
        Map<String, Double> mapR = new HashMap();
        mapR.put("plus",num1+num2);
        mapR.put("minus",num1-num2);
        mapR.put("multi",num1*num2);
        mapR.put("division",num2==0?0:num1/num2);
        return mapR.get(type);
    }

    public List getDbData() throws Exception{
        List list = testDao.getDbData();
        return list;
    }

    public List getGpsData(){
        if(testDao.equals(null)) testDao = new TestDao();
        return testDao.getGpsData();
    }

    public Map addGeoPOI(GeocodePoint poi){
        Map result = new HashMap();
        if(!StringUtils.isNotBlank(poi.getId())){
            poi.setId(UUID.randomUUID().toString());
        }
        result.put("flag", testDao.save(poi));
        return result;
    }


    public List getLineDem(String points_data){
        List list = new ArrayList();
        try {
            File file = new File(demPath);
            GeoTiffReader tifReader = new GeoTiffReader(file);
            GridCoverage2D coverage = tifReader.read(null);
            CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem2D();

            String[] points = points_data.split(";");
            for(int i=0;i<points.length;i++){
                String strLonlat = points[i];
                String[] strLonlats = strLonlat.split(",");

                double lon = Double.parseDouble(strLonlats[0]),
                        lat = Double.parseDouble(strLonlats[1]);

                DirectPosition position = new DirectPosition2D(crs, lon, lat);
                int[] results = (int[]) coverage.evaluate(position);
                results = coverage.evaluate(position, results);
                Map map = new HashMap();
                map.put("lon", lon);
                map.put("lat", lon);
                map.put("dem", results[0]);
                list.add(map);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }
}
