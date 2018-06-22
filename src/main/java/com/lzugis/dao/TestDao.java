package com.lzugis.dao;

import com.lzugis.dao.jdbc.util.AnnotationUtil;
import com.lzugis.helper.CommonConfig;
import com.lzugis.helper.CommonMethod;
import com.lzugis.services.model.County;
import com.lzugis.services.model.GeocodePoint;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;
import org.apache.commons.lang.StringUtils;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Repository;
import org.sqlite.SQLiteDataSource;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public static void main(String[] args){
        TestDao test = new TestDao();
        String sql = "select id, pid, name, st_astext(geom) as wkt from bj_boundry";
        List list = test.jdbcTemplate.queryForList(sql);
        CommonMethod cm = new CommonMethod();
        cm.append2File("d://bj_boundry.json", JSONArray.toJSONString(list), true);
    }
}
