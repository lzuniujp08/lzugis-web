package com.lzugis.dao;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.lzugis.dao.jdbc.util.AnnotationUtil;
import com.lzugis.helper.CommonConfig;
import com.lzugis.helper.CommonMethod;
import com.lzugis.services.model.District;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;
import org.apache.commons.lang.StringUtils;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;
import org.sqlite.SQLiteDataSource;

import java.util.*;

/**
 * Created by admin on 2017/11/14.
 */
@Repository
public class TestDao extends CommonDao {

    public TestDao(){
        jdbcTemplate = new JdbcTemplate();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(CommonConfig.getVal("database.driverclassname"));
        dataSource.setUrl(CommonConfig.getVal("database.url"));
        dataSource.setUsername(CommonConfig.getVal("database.username"));
        dataSource.setPassword(CommonConfig.getVal("database.password"));
        jdbcTemplate.setDataSource(dataSource);

        SQLiteDataSource source = new SQLiteDataSource();
        String dbPath = CommonConfig.getVal("geocode.dbpath");
        source.setUrl("jdbc:sqlite:"+dbPath);
        sqliteJdbcTemplate = new JdbcTemplate(source);
    }

    public List getDbData() throws Exception{
        List list = jdbcTemplate.queryForList("SELECT gid, st_astext(geom) from layer_segment");
        return list;
    }

    public Map getShortRoute(double startx, double starty, double endx, double endy){
        try {
            String sql = "SELECT ST_Astext(pgr_fromatob) AS wkt FROM pgr_fromAtoB('road', ?, ?, ?, ?)";
            return jdbcTemplate.queryForMap(sql, new Object[]{startx, starty, endx, endy});
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    public List getGpsData(){
        String sql = "select lon||','||lat as lonlat from location_track order by create_time";
        return this.jdbcTemplate.queryForList(sql);
    }

    public void jdbcTempTest() {
        // sql语句
        String dsql ="delete from capital_buffer where id=?";
        jdbcTemplate.update(dsql, new Object[]{44});
        String sql = "select name, lon, lat, id from capital_buffer";
        List num = jdbcTemplate.queryForList(sql);
        System.out.println(num.toString());
    }

    public void getPointsData(){
        List list = jdbcTemplate.queryForList("SELECT gid, st_astext(geom) as wkt from layer_segment");
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
        WKTReader reader = new WKTReader( geometryFactory );
       try {
           for(int i=0;i<list.size();i++){
               Map map = (Map)list.get(i);
               String wkt = map.get("wkt").toString();
               Geometry geom = (Geometry) reader.read(wkt);
               Coordinate[] coords = geom.getCoordinates();
               String[] pointData = new String[coords.length];
               for(int j=0;j<coords.length;j++){
                   Coordinate coord = coords[j];
                   pointData[j] =  "["+coord.x+", "+coord.y+"]";
               }
               String sql = "update layer_segment set points_data = ? where gid = ?";
               jdbcTemplate.update(sql, new Object[]{StringUtils.join(pointData, ","), map.get("gid")});

               System.out.println(map.get("gid").toString());
           }
       }catch (Exception e){
           e.printStackTrace();
       }

    }

    private static String url = "http://restapi.amap.com/v3/config/district?level=#level#&extensions=all&subdistrict=1&key=c002f8d098d53ba8815a61fd11b67627&s=rsv3&output=json&keywords=#keyword#&platform=JS";
    private static CommonMethod cm = new CommonMethod();
    private static TestDao test = new TestDao();

    public static void main(String[] args){
        long start = System.currentTimeMillis();
        //全国
        test.insertData("", "");
        long end = System.currentTimeMillis();
        System.out.println("total cost"+(end - start)+"MS");
    }

    public void insertData(String _level, String _keyword){
        _level = StringUtils.isNotBlank(_level)?_level:"country";
        _keyword = StringUtils.isNotBlank(_keyword)?_keyword:"中国";
        try {
            String _url = url.replaceAll("#level#", _level);
            _url = _url.replaceAll("#keyword#", _keyword);
            JSONObject json = cm.getUrlJSON(_url);
            JSONArray jsonArray = json.getJSONArray("districts");
            json = jsonArray.getJSONObject(0);

            String id = UUID.randomUUID().toString(),
                    adcode=json.getString("adcode"),
                    center=json.getString("center"),
                    level=json.getString("level"),
                    name=json.getString("name"),
                    polyline=json.getString("polyline");
            polyline = polyline2Wkt(polyline);
            double centerx = Double.parseDouble(center.split(",")[0]),
                    centery = Double.parseDouble(center.split(",")[1]);
            District district = new District(id, adcode,centerx, centery, level, name,polyline);
            test.save(district);

            System.out.println(name);

            jsonArray = json.getJSONArray("districts");

            //省
            insertProvinceData(jsonArray);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void insertProvinceData(JSONArray _jsonArray) throws Exception{
        for(int i=0;i<_jsonArray.length();i++){
            JSONObject pDis = _jsonArray.getJSONObject(i);
            String _level = pDis.getString("level"),
                    _keyword = pDis.getString("name");
            String _url = url.replaceAll("#level#", _level);
            _url = _url.replaceAll("#keyword#", _keyword);
            JSONObject json = cm.getUrlJSON(_url);
            JSONArray jsonArray = json.getJSONArray("districts");
            json = jsonArray.getJSONObject(0);

            String id = UUID.randomUUID().toString(),
                    adcode=json.getString("adcode"),
                    center=json.getString("center"),
                    level=json.getString("level"),
                    name=json.getString("name"),
                    polyline=json.getString("polyline");
            polyline = polyline2Wkt(polyline);
            double centerx = Double.parseDouble(center.split(",")[0]),
                    centery = Double.parseDouble(center.split(",")[1]);
            District district = new District(id, adcode,centerx, centery, level, name,polyline);
            test.save(district);
            System.out.println(name);

            //市
            insertCityData(json.getJSONArray("districts"));
        }
    }

    public void insertCityData(JSONArray _jsonArray) throws Exception{
        for(int i=0;i<_jsonArray.length();i++){
            JSONObject pDis = _jsonArray.getJSONObject(i);
            String _level = pDis.getString("level"),
                    _keyword = pDis.getString("name");
            String _url = url.replaceAll("#level#", _level);
            _url = _url.replaceAll("#keyword#", _keyword);
            JSONObject json = cm.getUrlJSON(_url);
            JSONArray jsonArray = json.getJSONArray("districts");
            json = jsonArray.getJSONObject(0);

            String id = UUID.randomUUID().toString(),
                    adcode=json.getString("adcode"),
                    center=json.getString("center"),
                    level=json.getString("level"),
                    name=json.getString("name"),
                    polyline=json.getString("polyline");
            polyline = polyline2Wkt(polyline);
            double centerx = Double.parseDouble(center.split(",")[0]),
                    centery = Double.parseDouble(center.split(",")[1]);
            District district = new District(id, adcode,centerx, centery, level, name,polyline);
            test.save(district);
            System.out.println(name);

            //县
//            insertDistrictData(json.getJSONArray("districts"));
        }
    }

    public void insertDistrictData(JSONArray _jsonArray) throws Exception{
        for(int i=0;i<_jsonArray.length();i++){
            JSONObject pDis = _jsonArray.getJSONObject(i);
            String _level = pDis.getString("level"),
                    _keyword = pDis.getString("name");
            String _url = url.replaceAll("#level#", _level);
            _url = _url.replaceAll("#keyword#", _keyword);
            JSONObject json = cm.getUrlJSON(_url);
            JSONArray jsonArray = json.getJSONArray("districts");
            json = jsonArray.getJSONObject(0);

            String id = UUID.randomUUID().toString(),
                    adcode=json.getString("adcode"),
                    center=json.getString("center"),
                    level=json.getString("level"),
                    name=json.getString("name"),
                    polyline=json.getString("polyline");
            polyline = polyline2Wkt(polyline);
            double centerx = Double.parseDouble(center.split(",")[0]),
                    centery = Double.parseDouble(center.split(",")[1]);
            District district = new District(id, adcode,centerx, centery, level, name,polyline);
            test.save(district);
        }
    }

    public String polyline2Wkt(String polyline) {
        String[] points = polyline.split(";");
        StringBuffer wkt = new StringBuffer();
        //LINESTRING(3 4,10 50,20 25)
        wkt.append("LINESTRING(");
        for(int i=0, len=points.length;i<len;i++){
            String[] point = points[i].split(",");
            wkt.append(point[0]+" "+point[1]);
            if(i!=len-1) wkt.append(", ");
        }
        wkt.append(")");
        return wkt.toString();
    }
}
