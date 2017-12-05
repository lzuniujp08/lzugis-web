package com.lzugis.services.utils;

import com.lzugis.helper.CommonConfig;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;

import java.util.*;

import java.nio.charset.*;
import java.io.*;

/**
 * Created by admin on 2017/11/20.
 */
public class ShpUtil {
    private String shpRoot;
    private GeometryFactory geometryFactory;
    private WKTReader wktReader;
    private WKTWriter wktWriter;

    public ShpUtil(){
        super();
        shpRoot = CommonConfig.getVal("geocode.shproot");
        geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
        wktReader = new WKTReader( geometryFactory );
        wktWriter = new WKTWriter();
    }

    /**
     * 获取所有的shp文件
     * @return
     */
    public List listShapeFiles(){
        List<String> result = new ArrayList<String>();
        File file = new File(shpRoot);
        if (!file.isDirectory()) {
            System.out.println(file.getAbsolutePath());
            result.add(file.getAbsolutePath());
        } else {
            File[] directoryList = file.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    if (file.isFile() && file.getName().endsWith(".shp")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            for (int i = 0; i < directoryList.length; i++) {
                result.add(directoryList[i].getPath());
            }
        }
        return result;
    }

    /**
     * 获取shp的features
     * @param shpPath
     * @return
     */
    public List<SimpleFeature> getShapeFeatures(String shpPath){
        List<SimpleFeature> features = new ArrayList<SimpleFeature>();
        try {
            //初始化shape
            File file = new File(shpPath);
            ShapefileDataStore shpDataStore = null;
            shpDataStore = new ShapefileDataStore(file.toURL());
            //设置编码
            Charset charset = Charset.forName("GBK");
            shpDataStore.setCharset(charset);
            String typeName = shpDataStore.getTypeNames()[0];
            SimpleFeatureSource featureSource = null;
            featureSource = shpDataStore.getFeatureSource(typeName);
            SimpleFeatureCollection result = featureSource.getFeatures();
            SimpleFeatureIterator itertor = result.features();
            while (itertor.hasNext()) {
                SimpleFeature feature = itertor.next();
                features.add(feature);
            }
            itertor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return features;
    }

    /**
     * 获取feature信息
     * @param feature
     * @return
     */
    public Object[] getFeatureInfo(SimpleFeature feature){
        String geomType = getGeomtype(feature);
        if(geomType.indexOf("polygon")!=-1){
            return getPolygonInfo(feature);
        }
        else if(geomType.indexOf("polyline")!=-1){
            return getPolylineInfo(feature);
        }
        else{
            return getPointInfo(feature);
        }
    }

    /**
     * 获取geometry类型
     * @param feature
     * @return
     */
    public String getGeomtype(SimpleFeature feature){
        Geometry geom = (Geometry)feature.getAttribute("the_geom");
        return geom.getGeometryType().toLowerCase();
    }

    /**
     * 获取多边形的信息
     * 顺序为：id, pointsnum, areaname, minx, miny, maxx, maxy,
     *         minzoom, maxzoom, pointsdata
     * @param feature
     * @return
     */
    private Object[] getPolygonInfo(SimpleFeature feature){
        Geometry geom = (Geometry)feature.getAttribute("the_geom");

        String id = UUID.randomUUID().toString();
        int pointsnum = geom.getNumPoints();

        String areaname = feature.getAttribute("areaname").toString();

        int minzoom = null==feature.getAttribute("minzoom")?
                0:(Integer) feature.getAttribute("minzoom"),

                maxzoom = null==feature.getAttribute("maxzoom")?
                        21:(Integer) feature.getAttribute("maxzoom");

        Envelope envelope = geom.getEnvelopeInternal();

        String pointsdata = wktWriter.write(geom);
        return new Object[]{
                id,
                pointsnum,
                areaname,
                envelope.getMinX(),
                envelope.getMinY(),
                envelope.getMaxX(),
                envelope.getMaxY(),
                minzoom, maxzoom,
                pointsdata
        };
    }
    /**
     * 获取线的信息
     * 顺序为："id", "pointsnum", "linetype", "linename",
              "minx", "miny", "maxx", "maxy", "minzoom", "maxzoom", "pointsdata"
     * @param feature
     * @return
     */
    private Object[] getPolylineInfo(SimpleFeature feature){
        Geometry geom = (Geometry)feature.getAttribute("the_geom");

        String id = UUID.randomUUID().toString();
        int pointsnum = geom.getNumPoints();

        String linename = feature.getAttribute("linename").toString();

        int minzoom = null==feature.getAttribute("minzoom")?
                0:(Integer) feature.getAttribute("minzoom"),

                maxzoom = null==feature.getAttribute("maxzoom")?
                        21:(Integer) feature.getAttribute("maxzoom"),

                linetype = null==feature.getAttribute("linetype")?
                        0:(Integer) feature.getAttribute("linetype");

        Geometry envelope = geom.getEnvelope();
        Coordinate[] coordinates =  envelope.getCoordinates();
        Coordinate min = coordinates[0],
                max = coordinates[1];

        String pointsdata = wktWriter.write(geom);
        return new Object[]{
                id,
                pointsnum,
                linetype,
                linename,
                min.x, min.y, max.x, max.y,
                minzoom, maxzoom,
                pointsdata
        };
    }
    /**
     * 获取点的信息
     * 顺序为："id", "poiname",
               "x", "y", "minzoom", "maxzoom"
     * @param feature
     * @return
     */
    private Object[] getPointInfo(SimpleFeature feature){
        Point geom = (Point)feature.getAttribute("the_geom");

        String id = UUID.randomUUID().toString();

        String poiname = feature.getAttribute("poiname").toString();

        int minzoom = null==feature.getAttribute("minzoom")?
                0:(Integer) feature.getAttribute("minzoom"),

                maxzoom = null==feature.getAttribute("maxzoom")?
                        21:(Integer) feature.getAttribute("maxzoom");

        return new Object[]{
                id,
                poiname,
                geom.getX(),
                geom.getY(),
                minzoom, maxzoom
        };
    }
}
