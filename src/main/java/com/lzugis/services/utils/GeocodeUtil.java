package com.lzugis.services.utils;

import com.lzugis.dao.CommonDao;
import com.lzugis.dao.GeocodeDao;
import com.lzugis.services.model.GeocodePoints;
import com.lzugis.services.model.GeocodePolygon;
import com.lzugis.services.model.GeocodePolyline;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import org.geotools.geometry.jts.JTSFactoryFinder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2017/11/20.
 */
public class GeocodeUtil {

    private GeocodeDao geoDao;
    private GeocodePolygon polygon;
    private GeocodePolyline line;
    private GeocodePoints point;
    private GeometryFactory geometryFactory;
    private WKTReader wktReader;
    private WKTWriter wktWriter;

    public GeocodeUtil(){
        super();
        geoDao = new GeocodeDao();
        polygon = new GeocodePolygon();
        line = new GeocodePolyline();
        point = new GeocodePoints();

        geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
        wktReader = new WKTReader( geometryFactory );
        wktWriter = new WKTWriter();
    }
    /**
     * 根据经纬度获取点
     * @param lon
     * @param lat
     * @param zoom
     * @return
     */
    public Map getAreaFromLonlat(double lon, double lat, int zoom){
        Map result = new HashMap();
        try {
            String filters = "where minx<= ? and maxx>=? AND miny<=? and maxy>=? and minzoom<=? and maxzoom>=? order by maxzoom";
            String[] fields = new String[]{"id", "areaname", "pointsdata"};
            Object[] values = new Object[]{lon, lon, lat, lat, zoom, zoom};
            List list = geoDao.getDataByFilter(polygon.getTableName(), fields, filters, values);
            if (list.size() > 1) {
                Geometry _point = new GeometryFactory().createPoint(new Coordinate(lon, lat));
                for (int i = 0; i < list.size(); i++) {
                    Map map = (Map) list.get(i);
                    String wkt = map.get("pointsdata").toString();
                    Geometry _area = (Geometry) wktReader.read(wkt);
                    if(_area.contains(_point)){
                        result = map;
                        break;
                    }
                }
            } else {
                result = list.size()>0?(Map) list.get(0):null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据经纬度获取点
     * @param lon
     * @param lat
     * @param zoom
     * @return
     */
    public Map getPoiFromLonlat(double lon, double lat, int zoom){
        Map result = new HashMap();
        try {
            String fields = "id, poiname, x, y";
            String filter = " where minzoom<=? and maxzoom>=?";
            Object[] values = new Object[]{zoom, zoom};
            result = geoDao.getNearestPoint(point.getTableName(),
                    fields, lon, lat, filter, values);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
