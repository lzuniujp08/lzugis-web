package com.lzugis.dao;

import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.lzugis.helper.CommonConfig;
import com.lzugis.helper.CommonMethod;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;
import org.apache.commons.lang.StringUtils;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.json.simple.JSONArray;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;
import org.sqlite.SQLiteDataSource;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/11/14.
 */
@Repository
public class UniversityDao extends CommonDao {

    public UniversityDao(){
        jdbcTemplate = new JdbcTemplate();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(CommonConfig.getVal("database.driverclassname"));
        dataSource.setUrl(CommonConfig.getVal("database.url"));
        dataSource.setUsername(CommonConfig.getVal("database.username"));
        dataSource.setPassword(CommonConfig.getVal("database.password"));
        jdbcTemplate.setDataSource(dataSource);
    }

    public String[] getLonLatByName(String name){
        String[] lonlat = new String[]{"99","99"};
        StringBuffer url = new StringBuffer();
        url.append("http://api.tianditu.com/apiserver/ajaxproxy?proxyReqUrl=")
                .append("http://map.tianditu.com/query.shtml?postStr={'keyWord':'"+name+"',")
                .append("'level':'9','mapBound':'114.6089,39.5392,118.7040,40.9562','queryType':'7','start':'0','count':'1'}&type=query");
        String bdUrl = "http://api.map.baidu.com/?qt=gc&wd=%E6%96%B0%E7%AB%99%E5%8C%BA%E8%83%9C%E5%88%A9%E8%B7%AF89&cn=%E4%B8%AD%E5%9B%BD&fromproduct=jsapi&res=api&callback=lzugis&ak=DD279b2a90afdf0ae7a3796787a0742e";
        InputStream is = null;
        try {
            is = new URL(url.toString()).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            int cp;
            while ((cp = rd.read()) != -1) {
                sb.append((char) cp);
            }
            String strJson = sb.toString().substring(19,sb.toString().length()-1);
            JSONObject json = new JSONObject(strJson);
            com.amazonaws.util.json.JSONArray arr = new com.amazonaws.util.json.JSONArray();
            if(!json.isNull("pois")){
                arr = json.getJSONArray("pois");
                JSONObject poiinfo = (JSONObject) arr.get(0);
                lonlat = poiinfo.get("lonlat").toString().split(" ");
                is.close();
            }
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return lonlat;
    }

    public static void main(String[] args) throws IOException{
        UniversityDao test = new UniversityDao();
        String sql = "SELECT * FROM university";
        List list = test.jdbcTemplate.queryForList(sql);
        for(int i=0,len=list.size();i<len;i++){
            Map map = (Map) list.get(i);
            String name = map.get("location").toString()+ map.get("name").toString();
            Object[] lonlat = test.getLonLatByName(name);
            String _info = i+"条，"+name+", lonlat:"+lonlat[0]+","+lonlat[1];
            System.out.println(_info);
            String updatesql = "update university set lon=?, lat=? where name=?";
            test.jdbcTemplate.update(updatesql, new Object[]{Double.parseDouble(lonlat[0].toString()),
                    Double.parseDouble(lonlat[1].toString()),
                    map.get("name").toString()});
        }

    }
}
